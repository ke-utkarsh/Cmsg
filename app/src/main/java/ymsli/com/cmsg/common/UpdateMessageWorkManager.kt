package ymsli.com.cmsg.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import com.google.gson.Gson
import ymsli.com.cmsg.base.BaseWorkManager
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

class UpdateMessageWorkManager(private val context: Context, workerParameters: WorkerParameters)
    : BaseWorkManager(context, workerParameters) {

    override fun inject(jobServiceComponent: ServiceComponent) = jobServiceComponent.inject(this)

    override fun doWork(): Result {
        init()
        return try{
            if(isNetworkConnected()){
                val smsId = inputData.getLong(Constants.SMS_WORKER_TAG,-1)
                if (smsId > 0) {
                    // update SMS status using API
                    Log.d("Updating message in API", smsId.toString())
                    updateSMSStatus(smsAppRepository.getSpecificMessage(smsId))
                }
            }
            Result.success()
        }
        catch (ex: Exception){
            Result.success()
        }
    }

    private fun updateSMSStatus(messageEntity: MessageEntity){
        storeSentMessageOffline(messageEntity) // update local DB as well
        val updatedBy = smsAppRepository.getUserName()
        val smsModelList: ArrayList<SMSModelList> = ArrayList()
        var status = messageEntity.messageStatus

        if(!status.isNullOrBlank() && status.equals(MessageTypeEnum.SENT_OFFLINE.toString())){
            status = MessageStatusEnum.SENT.status
        }
        var failureReason: String? = null
        // update failureReason if required
        if(messageEntity.messageStatus.equals(MessageStatusEnum.FAILED.status)){
            failureReason = messageEntity.failureReason
        }

        val smsModel = SMSModelList(
            smsId = messageEntity.smsId, messageStatus = status,
            updateBy = updatedBy,
            serviceProvider = messageEntity.serviceProvider, failureReason = failureReason)
        smsModelList.add(smsModel)

        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t101. Calling API to update SMS status for SMSId "+messageEntity.smsId)
        val editRequest =
            EditMessageRequestModel(updatedBy = updatedBy, smsModelList = smsModelList)
        val req = Gson().toJson(editRequest)
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t102. API request body "+req)
        compositeDisposable.addAll(
            smsAppRepository.updateMessages(editRequest)
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t103A. API call successful for SMSId "+messageEntity.smsId)
                    Log.d("API","Success")
                }, {
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t103 B1. &&&&****&*&*&*& ERROR: API call failed for SMSId "+messageEntity.smsId)
                    Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t103 B2. &&&&****&*&*&*& ERROR: detail SMSId "+messageEntity.smsId+" ${it.message} ${it.cause}")
                    Log.d("API","Failed")
                })
        )
    }

    /**
     * stores sent messages locally
     */
    private fun storeSentMessageOffline(messageEntity: MessageEntity){
        Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t100. Updating local DB of SMSId "+messageEntity.smsId)
        if(messageEntity.messageStatus.equals(MessageStatusEnum.FAILED.status) ||
            messageEntity.messageStatus.equals(MessageStatusEnum.PENDING.status)) return
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
    }
}