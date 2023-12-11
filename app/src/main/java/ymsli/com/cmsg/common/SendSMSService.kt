package ymsli.com.cmsg.common

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.SystemClock
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.WorkManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ymsli.com.cmsg.CMsgApplication
import ymsli.com.cmsg.R
import ymsli.com.cmsg.base.BaseService
import ymsli.com.cmsg.database.entity.LocalSentMessageEntity
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.di.component.ServiceComponent
import ymsli.com.cmsg.model.EditMessageRequestModel
import ymsli.com.cmsg.model.SMSModelList
import ymsli.com.cmsg.utils.MessageStatusEnum
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.Utils
import java.lang.Exception
import java.sql.Timestamp

class SendSMSService : BaseService() {
    var SENT = "SMS_SENT"
    var DELIVERED = "SMS_DELIVERED"
    private var pickCarrierName: String = ""


    override fun inject(jobServiceComponent: ServiceComponent) = jobServiceComponent.inject(this)

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @DelicateCoroutinesApi
    override fun onStart(intent: Intent?, startId: Int) {
        var sentMessageCount = 0
        val smsId = intent?.getLongExtra("SMSID", -1) ?: -1
        if (isNetworkConnected()) {
            if (smsId > 0) {
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t1A. Triggerring Service for single SMS with SMS ID $smsId")
                Log.d("Service Started for SMSID ", smsId.toString())
                GlobalScope.launch(Dispatchers.IO) {
                    val messageEntity = smsAppRepository.getSpecificMessage(smsId)
                    sendSMS(messageEntity)
                }
            } else {
                val messages = intent?.getSerializableExtra("SMS_LIST") as ArrayList<MessageEntity>
                val messageType = intent?.getIntExtra("MESSAGE_TYPE", 0)
                val providerType = intent?.getStringExtra("PROVIDER_TYPE")
                Log.i("messageType2", messageType.toString())
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t1B. Triggerring Service for bulk/multiple SMS")
                GlobalScope.launch(Dispatchers.IO) {
                    for (sms in messages) {
                        sentMessageCount++
                        //SystemClock.sleep(1000)
                        if (sentMessageCount == 3) {
                            stopSelf()
                            sentMessageCount = 0
                            val broadcastIntent = Intent("ymsli.com.cmsg.SMS_SENT_COMPLETE")
                            broadcastIntent.putExtra("MESSAGE_TYPE", messageType)
                            broadcastIntent.putExtra("PROVIDER_TYPE", providerType)
                            sendBroadcast(broadcastIntent)
                            break
                        }else{

                        }
                        Utils.writeToFile(
                            "\n${Timestamp(System.currentTimeMillis())} \t1C. Total Bulk Messaging Sending Count" + messages.size
                                    + "Message Sent No." + sms.smsId
                        )
                        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t2. Sending SMS to ${sms.smsId}")
                        Log.d("Triggering new SMS ", sms.smsId.toString())
                        sendSMS(sms)
                        val TIMEOUT_INTERVAL = 20 * 1000 // 20 seconds
                        val startTime = Utils.getTimeInMilliSec()
                        while (!triggerNextSMS) {
                            SystemClock.sleep(2000)
                            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## WAITING TO TRIGGER PREVIOUS SMS BEFORE TRIGGERING NEW SMS ###########")
                            Log.d("Waiting for ", "past message")
                            // add timeout
                            val currentTime = Utils.getTimeInMilliSec()
                            if (currentTime - startTime > TIMEOUT_INTERVAL) {
                                // mark the message as successful
                                sms.messageStatus = MessageStatusEnum.SENT.status
                                sms.messageType = MessageTypeEnum.SENT_OFFLINE.messageType
                                //storeSentMessageOffline(sms)//update this message locally here
                                if (sms.serviceProvider.isNullOrEmpty() && pickCarrierName != "") {
                                    sms.serviceProvider = pickCarrierName
                                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## Assign Carrier 1 ${sms.smsId} ###########")
                                }
                                updateMessageStatus(sms)
                                sendBroadcast(sms.smsId, MessageStatusEnum.SENT.status)
                                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## SENT ACTION TIMEOUT, TRIGGERING NEXT SMS IN THE QUEUE & PREVIOUS IS MARKED SUCCESSFUL WITH SMS ID ${sms.smsId} ###########")
                                //unregister broadcast receivers
                                unregisterBroadcastReceivers()
                                break
                            }
                        }
                        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t********** WAIT OVER, TRIGGERING NEXT SMS IN THE QUEUE **********")
                        triggerNextSMS = false
                    }
                }
            }
        }
    }

    private fun showCompletionDialog() {

        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        builder.setMessage("50 SMS messages have been sent. Click OK to send the next batch.")
            .setPositiveButton("OK") { _, _ ->
                dialog.dismiss() // Dismiss the dialog
            }
        dialog.show()
    }

    private var triggerNextSMS: Boolean = false

    private lateinit var message: MessageEntity
    private fun sendSMS(messageEntity: MessageEntity) {
        val phoneNum = messageEntity.receiverMobileNo
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t3. Phone number from API is $phoneNum")
        if (phoneNum.isNullOrBlank() || !phoneNum.contains("-")) {
            // notify user that SMS cannot be sent and return
            Toast.makeText(
                this,
                this.resources.getString(R.string.phone_number_format_incorrect),
                Toast.LENGTH_SHORT
            ).show()
            triggerNextSMS = true
            return
        }
        this.message = messageEntity
        sendBroadcast(messageEntity.smsId, MessageStatusEnum.PENDING.status)
        val simInfoList = smsAppRepository.getSIMInfo()
        val serviceProviders = smsAppRepository.getServiceProviders()
        val receiverPhoneNo = messageEntity.receiverMobileNo.split("-")
        val receiverPrefix: String
        if (receiverPhoneNo.get(1).get(0) != '0') {
            // append 0
            receiverPrefix = "0" + receiverPhoneNo.get(1).substring(0, 2)
        } else {
            receiverPrefix = receiverPhoneNo.get(1).substring(0, 2)
        }

        val relevantProviders = serviceProviders.filter { it.simPrefix.equals(receiverPrefix) }
        // if relevantProviders is 0, then mark message as Failed
        if (relevantProviders.isEmpty()) {
            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tERROR: Relevant provider not found")
            sendBroadcast(messageEntity.smsId, MessageStatusEnum.OPERATOR_NOT_SUPPORTED.status)
            smsAppRepository.updateMessageStatus(
                messageEntity.smsId,
                MessageStatusEnum.FAILED.status
            )
            messageEntity.failureReason =
                resources.getString(R.string.failure_reason_relevant_providers)
            updateMessageStatus(messageEntity)
            triggerNextSMS = true
            return
        }

        var subId: Int = Int.MIN_VALUE
        var carrierIndex: Int = 1
        for (carriers in simInfoList) {
            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tCarrier " + carrierIndex + " in your device " + carriers.carrierName)
            carrierIndex += 1
            Log.i("Carrier", "Carrier " + carrierIndex + " in your device " + carriers.carrierName);
        }

        var simToUse = false
        var deviceCarrierName: String? = null
        for (serviceProvider in relevantProviders) {
            //  val simToUse = simInfoList.filter { it.carrierName.equals(serviceProvider.serviceProvider,true) }
            for (carriers in simInfoList) {
                deviceCarrierName = carriers.carrierName;
                simToUse = deviceCarrierName.contains(
                    serviceProvider.serviceProvider.toString(),
                    ignoreCase = true
                )
                if (simToUse) {
                    subId = carriers.subscriptionId
                    messageEntity.serviceProvider = carriers.carrierName
                    break
                }
            }
            if (simToUse) {
                // subId = simInfoList[0].subscriptionId
                //messageEntity.serviceProvider = simInfoList[0].carrierName
                // update service provider in DB
                //This pickCarrierName is store for future, If service provider blank update in the RoomDatabase
                pickCarrierName = messageEntity.serviceProvider.toString()
                smsAppRepository.updateServiceProvider(
                    messageEntity.smsId,
                    messageEntity.serviceProvider!!
                )
            } else {
                // show user that sms cannot be sent
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tERROR: Valid service provider not found")
                messageEntity.failureReason =
                    resources.getString(R.string.failure_reason_relevant_providers)
                sendBroadcast(messageEntity.smsId, MessageStatusEnum.OPERATOR_NOT_SUPPORTED.status)
                smsAppRepository.updateMessageStatus(
                    messageEntity.smsId,
                    MessageStatusEnum.FAILED.status
                )
                smsAppRepository.updateFailureReason(
                    messageEntity.smsId,
                    resources.getString(R.string.failure_reason_relevant_providers)
                )
                smsAppRepository.updateServiceProvider(
                    messageEntity.smsId,
                    serviceProvider.serviceProvider ?: Constants.NA_KEY
                )
                updateMessageStatus(messageEntity)
                triggerNextSMS = true
                return
            }
            break
        }

        val sentPIIntent = Intent(SENT)
        sentPIIntent.putExtra("SMS_ID", messageEntity.smsId)
        val sentPI = PendingIntent.getBroadcast(this, 0, sentPIIntent, PendingIntent.FLAG_IMMUTABLE)

        val deliveredPIIntent = Intent(DELIVERED)
        deliveredPIIntent.putExtra("SMS_ID", messageEntity.smsId)
        val deliveredPI =
            PendingIntent.getBroadcast(this, 0, deliveredPIIntent, PendingIntent.FLAG_IMMUTABLE)

        this.registerReceiver(sentReceiver, IntentFilter(SENT))
        this.registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))

        Log.d(
            "SendSMSService: **** Receiver Phone number",
            receiverPhoneNo.get(1).toString() + " ###SMSID### " + messageEntity.smsId
        )
        val smsManager = SmsManager.getSmsManagerForSubscriptionId(subId)
        val smsParts = smsManager.divideMessage(messageEntity.messageBody)
        val list1 = ArrayList<PendingIntent>()
        val list2 = ArrayList<PendingIntent>()
        list1.add(sentPI)
        list2.add(deliveredPI)
        var phoneNumberFormatted = receiverPhoneNo.get(1)
        if (phoneNumberFormatted.length == 9) {//if phone number length is 9, append 0 in phone number
            phoneNumberFormatted = "0$phoneNumberFormatted"
        }
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t4. Sending SMS to Phone number $phoneNumberFormatted")
        smsManager.sendMultipartTextMessage(phoneNumberFormatted, null, smsParts, list1, list2)
    }

    private fun sendBroadcast(smsId: Long, smsStatus: String) {
        val intent = Intent("cmsg_send_msg")
        val appli = (this.applicationContext as CMsgApplication)
        intent.setClass(appli, SendSMSCallback::class.java)
        intent.putExtra("sms_update", smsId)
        intent.putExtra("sms_status", smsStatus)
        appli.sendBroadcast(intent)
    }

    /**
     * broadcast receiver for sms sent
     */
    private val sentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val smsId = intent?.getLongExtra("SMS_ID", -1) ?: -1
            Log.d("Sent Intent SMS ID is ", smsId.toString())
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t5. Broadcast receiver for SMS Sent acknowledgement with SMSID ${message.smsId}")
                }

                else -> {
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tERROR: Unable to sent SMS probably network provider error")
                    message.failureReason = resources.getString(R.string.cannot_send_sms)
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tERROR: Result Data" + resultData.toString());
                    smsAppRepository.updateFailureReason(
                        message.smsId,
                        resources.getString(R.string.cannot_send_sms)
                    )
                    sendBroadcast(message.smsId, MessageStatusEnum.FAILED.status)
                    message.messageStatus = MessageStatusEnum.FAILED.status
                    if (message.serviceProvider.isNullOrEmpty() && pickCarrierName != "") {
                        message.serviceProvider = pickCarrierName
                        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## Assign Carrier 2 ${message.smsId} ###########")
                    }
                    updateMessageStatus(message)
                    triggerNextSMS = true
                }
            }
        }
    }

    /**
     * broadcast receiver for sms delivered
     */
    private val deliveredReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val smsId = intent?.getLongExtra("SMS_ID", -1) ?: -1
            Log.d("Delivery Intent SMS ID is ", smsId.toString())
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t6. Broadcast receiver fot SMS Delivered acknowledgement with SMSID ${message.smsId}")
                    sendBroadcast(message.smsId, MessageStatusEnum.SENT.status)
                    message.messageStatus = MessageStatusEnum.SENT.status
                    message.messageType = MessageTypeEnum.SENT_OFFLINE.messageType
                    if (message.serviceProvider.isNullOrEmpty() && pickCarrierName != "") {
                        message.serviceProvider = pickCarrierName
                        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## Assign Carrier 3 ${message.smsId} ###########")
                    }
                    updateMessageStatus(message)
                }

                AppCompatActivity.RESULT_CANCELED -> {
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tERROR: Unable to deliver SMS probably receiver phone is switched off")
                    message.failureReason = resources.getString(R.string.sent_but_undelivered)
                    smsAppRepository.updateFailureReason(
                        message.smsId,
                        resources.getString(R.string.sent_but_undelivered)
                    )
                    sendBroadcast(message.smsId, MessageStatusEnum.FAILED.status)
                    message.messageStatus = MessageStatusEnum.FAILED.status

                    if (message.serviceProvider.isNullOrEmpty() && pickCarrierName != "") {
                        message.serviceProvider = pickCarrierName
                        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t########## Assign Carrier 4 ${message.smsId} ###########")
                    }
                    updateMessageStatus(message)
                }
            }
            intent?.data = null
            triggerNextSMS = true
            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t7. Pickup next SMS from the list to be sent")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastReceivers()
    }

    private fun unregisterBroadcastReceivers() {
        try {
            unregisterReceiver(sentReceiver)
        } catch (ex: Exception) {

        }
        try {
            unregisterReceiver(deliveredReceiver)
        } catch (ex: Exception) {

        }
    }

    override fun stopService(name: Intent?): Boolean {
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tSERVICE STOPPED")
        return super.stopService(name)
    }

    /**
     * updates the status of the message
     * at remote database
     */
    private fun updateMessageStatus(messageEntity: MessageEntity) {
        storeSentMessageOffline(messageEntity)
        Log.d("Calling WorkManager for ", messageEntity.smsId.toString())
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t8. Update SMS status. Calling API..." + messageEntity.smsId)
        Utils.startSMSUpdateWorkManager(this, messageEntity.smsId)
    }

    private fun storeSentMessageOffline(messageEntity: MessageEntity) {
        if (messageEntity.messageStatus.equals(MessageStatusEnum.FAILED.status) ||
            messageEntity.messageStatus.equals(MessageStatusEnum.PENDING.status)
        ) return
        val localSentMessageEntity = LocalSentMessageEntity(
            messageBody = messageEntity.messageBody,
            messageStatus = messageEntity.messageStatus,
            orderId = messageEntity.orderId,
            receiverMobileNo = messageEntity.receiverMobileNo,
            receiverName = messageEntity.receiverName,
            serviceProvider = messageEntity.serviceProvider,
            smsId = messageEntity.smsId,
            subject = messageEntity.subject,
            updateBy = messageEntity.updateBy,
            updatedOn = messageEntity.updatedOn,
            createdOn = messageEntity.createdOn,
            messageType = messageEntity.messageType,
            orderNo = messageEntity.orderNo
        )
        Log.d("Updating message in locally", messageEntity.smsId.toString())
        smsAppRepository.insertMessageInDB(localSentMessageEntity)
        smsAppRepository.updateStoredMessageInDB(arrayOf(messageEntity))
    }

}