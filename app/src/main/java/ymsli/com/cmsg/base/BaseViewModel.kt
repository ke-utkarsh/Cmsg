package ymsli.com.cmsg.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.common.Constants
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.database.entity.ServiceProviderEntity
import ymsli.com.cmsg.model.EditMessageRequestModel
import ymsli.com.cmsg.model.SMSModelList
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.*
import java.net.HttpURLConnection
import java.sql.Timestamp

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * BaseViewModel : Base class for all the ViewModels in the project.
 *                 Common functionality should be refactored into this class.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
abstract class BaseViewModel(
    protected val schedulerProvider: SchedulerProvider,
    protected val compositeDisposable: CompositeDisposable,
    protected val networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository

) : ViewModel() {
    val message: MutableLiveData<Event<Int>> = MutableLiveData()

    var showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()

    val messageString: MutableLiveData<Event<String>> = MutableLiveData()

    var pendingSMSList: MutableLiveData<List<MessageEntity>> = MutableLiveData()

    var smsAPICallCompleted: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var smsAPICallSentMessagesCompleted: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var openNextActivity: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var isMessageDeleted: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var sentMessageDashboardModel: MutableLiveData<Event<ArrayList<SentMessageDashboardModel>>> = MutableLiveData()
    /**
     * checks instantaneous internet connectivity of the device
     */
    public fun checkInternetConnection(): Boolean = networkHelper.isNetworkConnected()

    fun setLoggedIn(status: Boolean) = smsAppRepository.setLoggedIn(status)

    abstract fun onCreate()

    /**
     * gets pending messages from API
     */
    fun getPendingMessages() {
        if (checkInternetConnection()) {
            showProgress.postValue(Event(true))
            compositeDisposable.addAll(
                smsAppRepository.getPendingMessages()
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        Log.d("API", "Response")
                        if(it.responseData!=null && it.responseData.isNotEmpty()) {
                            pendingSMSList.postValue(it.responseData.sortedByDescending { it.createdOn })
                            it.responseData.forEach {
                                it.messageType = MessageTypeEnum.PENDING.messageType
                            }
                            // store the list locally
                            smsAppRepository.insertMessageInDB(it.responseData.toTypedArray())
                        }
                        showProgress.postValue(Event(false))
                        smsAPICallCompleted.postValue(Event(true))
                    }, {
                        smsAPICallCompleted.postValue(Event(false))
                        showProgress.postValue(Event(false))
                        Log.d("API", "Response")
                    })
            )
        } else {
            //TODO show network error message
            //messageStringId.postValue(Resource.error(R.string.network_connection_error))
        }
    }

    /**
     * gets sent messages from API
     */
    fun getSentMessages() {
        if (checkInternetConnection()) {
            showProgress.postValue(Event(true))
            compositeDisposable.addAll(
                smsAppRepository.getSentMessagesProvider(Constants.SENT_MESSAGES_HISTORY_DAYS)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        smsAppRepository.cleanMessageOfType(MessageTypeEnum.SENT_API.messageType)
                        if (it.responseData!=null && it.responseCode.toInt() == HttpURLConnection.HTTP_OK) {
                            //pendingSMSList.postValue(it.responseData)
                            pendingSMSList.postValue(it.responseData?.sortedByDescending { it.createdOn })
                            it.responseData.forEach {
                                it.messageType = MessageTypeEnum.SENT_API.messageType
                            }
                            // clear the older data and store the list locally
                            smsAppRepository.insertMessageInDB(it.responseData.toTypedArray())
                        }
                        showProgress.postValue(Event(false))
                        openNextActivity.postValue(Event(true))
                    }, {
                        openNextActivity.postValue(Event(false))
                        showProgress.postValue(Event(false))
                        Log.d("API", "Response")
                    })
            )
        } else {
            //TODO show network error message
            //messageStringId.postValue(Resource.error(R.string.network_connection_error))
        }
    }

    fun getMessagesOfType(messageType: Int): LiveData<List<MessageEntity>> =
        smsAppRepository.getMessagesOfType(messageType)

    fun getPendingMessages(messageStatus: String,messageType: Int): LiveData<List<MessageEntity>> =
        smsAppRepository.getPendingMessages(messageStatus, messageType)

    fun getMessagesOfProvider(messageType: Int, provider: String): LiveData<List<MessageEntity>> =
        smsAppRepository.getMessagesOfProvider(messageType, provider)

    fun cleanMessageOfType(messageType: Int) = smsAppRepository.cleanMessageOfType(messageType)

    fun getSIMInfo(): List<PhoneSIMEntity> = smsAppRepository.getSIMInfo()

    fun getServiceProviders(): List<ServiceProviderEntity> = smsAppRepository.getServiceProviders()

    fun getUserName(): String? = smsAppRepository.getUserName()

    /**
     * deletes the corresponding message
     */
    fun deleteMessage(messages: ArrayList<MessageEntity>) {
        if (checkInternetConnection()) {
            showProgress.postValue(Event(true))
            val updatedBy = smsAppRepository.getUserName()
            val smsModelList: ArrayList<SMSModelList> = ArrayList()
            for (msg in messages) {
                Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \tDelete message api triggered for smsId: "+msg.smsId)
                val smsModel = SMSModelList(
                    smsId = msg.smsId, messageStatus = msg.messageStatus,
                    updateBy = updatedBy,
                    serviceProvider = null,
                    failureReason = null
                )
                smsModelList.add(smsModel)
            }
            val deleteRequest =
                EditMessageRequestModel(updatedBy = updatedBy, smsModelList = smsModelList)
            compositeDisposable.addAll(
                smsAppRepository.deleteMessages(deleteRequest)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        Log.d("Here", "Point")
                        if(it.responseCode==HttpURLConnection.HTTP_OK){
                            // delete message from local DB
                            for(msg in messages){
                                smsAppRepository.deleteMessage(msg.smsId)
                            }
                            isMessageDeleted.postValue(Event(true))
                        }else isMessageDeleted.postValue(Event(false))

                        showProgress.postValue(Event(false))
                    }, {
                        isMessageDeleted.postValue(Event(false))
                        Log.d("Here", "Point")
                        showProgress.postValue(Event(false))
                    })
            )
        } else {

        }
    }

    fun updateMessageStatus(smsId: Long,messageStatus: String): Int = smsAppRepository.updateMessageStatus(smsId, messageStatus)

    fun createMessageSentModel(messages: List<MessageEntity>){
        val simInfoModelList: ArrayList<SentMessageDashboardModel> = ArrayList()
        val simMap: HashMap<String,Int> = HashMap()
        for(msg in messages){
            if(simMap.containsKey(msg.serviceProvider?.uppercase())){
                simMap.put(msg.serviceProvider?.uppercase()!!,simMap.get(msg.serviceProvider?.uppercase())!!+1)
            }
            else{
                simMap.put(msg.serviceProvider?.uppercase()?:Constants.NA_KEY,1)
            }
        }
        val keys = simMap.keys
        for(carrier in keys){
            val carrierMessages = messages.filter { it.serviceProvider.equals(carrier,true) }
            if(carrierMessages.isNotEmpty()){
                val deliveredMessages =
                    carrierMessages.filter { it.messageStatus.equals(MessageStatusEnum.SENT.status, true) }
                val sentMessageDashboardModel = SentMessageDashboardModel(
                    carrierName = carrier,
                    totalMessages = carrierMessages.size,
                    deliveredMessages = deliveredMessages.size,
                    undeliveredMessages = carrierMessages.size - deliveredMessages.size
                )
                simInfoModelList.add(sentMessageDashboardModel)
            }
        }

        sentMessageDashboardModel.postValue(Event(simInfoModelList))
    }

    //region to get settings about order
    fun getShowReceiverName() = smsAppRepository.getShowReceiverName()

    fun getShowReceiverNumber() = smsAppRepository.getShowReceiverNumber()

    fun getShowReceiverMessage() = smsAppRepository.getShowReceiverMessage()
    //endregion to get settings about order
}