package ymsli.com.cmsg.repository


import androidx.lifecycle.LiveData
import io.reactivex.Single
import retrofit2.Response
import ymsli.com.cmsg.database.SMSAppDatabase
import ymsli.com.cmsg.database.entity.*
import ymsli.com.cmsg.model.*
import ymsli.com.cmsg.network.SMSAppNetworkService
import ymsli.com.cmsg.preference.CMsgAppPreferences
import ymsli.com.cmsg.utils.MessageTypeEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSAppRepository @Inject constructor(
    private val smsAppNetworkService: SMSAppNetworkService,
    private val smsAppPreferences: CMsgAppPreferences,
    private val smsAppDatabase: SMSAppDatabase
){

    fun isLoggedIn():Boolean{
        return smsAppPreferences.isLoggedIn()
    }

    fun setLoggedIn(status : Boolean) = smsAppPreferences.setLoggedIn(status)

    fun getPendingMessages(): Single<MessageEntityResponseModel> = smsAppNetworkService.getPendingMessages()

    fun getSentMessagesProvider(numDays: Int):Single<MessageEntityResponseModel> = smsAppNetworkService.getSentMessagesProvider(numDays)

    fun deleteMessages(deleteMessageRequestModel: EditMessageRequestModel): Single<DeleteMessageResponseModel> =
        smsAppNetworkService.deleteMessages(deleteMessageRequestModel,smsAppPreferences.getAuthorizationToken())

    fun updateMessages(editMessageRequestModel: EditMessageRequestModel): Single<DeleteMessageResponseModel> =
        smsAppNetworkService.updateMessages(editMessageRequestModel,smsAppPreferences.getAuthorizationToken())

    fun setUserDataInSharedPref(user: UserMaster){
        smsAppPreferences.setUserDataInSharedPref(user)
    }

    fun getUserName(): String?{
        return smsAppPreferences.getUserName()
    }

    fun insertMessageInDB(localSentMessageEntity: LocalSentMessageEntity) = smsAppDatabase.localSentMessageDao.insertMessageInDB(localSentMessageEntity)

    fun cleanMessageOfType() = smsAppDatabase.localSentMessageDao.cleanMessageOfType()

    fun getSentMessagesOffline(): LiveData<List<LocalSentMessageEntity>> = smsAppDatabase.localSentMessageDao.getMessagesOfType()

    fun getSentMessagesOfflineCount(): LiveData<Long> = smsAppDatabase.localSentMessageDao.getSentMessagesOfflineCount()

    fun getServiceProvider():Single<ServiceProviderResponse> = smsAppNetworkService.getServiceProvider()

    fun insertServiceProviders(serviceProviders: Array<ServiceProviderEntity>) = smsAppDatabase.serviceProviderDao.insertServiceProviders(*serviceProviders)

    fun getServiceProviders(): List<ServiceProviderEntity> = smsAppDatabase.serviceProviderDao.getServiceProviders()

    fun storeSIMInfo(simInfoList: Array<PhoneSIMEntity>) = smsAppDatabase.phoneSIMDao.insert(*simInfoList)

    fun deleteSIMInfo() = smsAppDatabase.phoneSIMDao.deleteSIMInfo()

    fun getSIMInfo(): List<PhoneSIMEntity> = smsAppDatabase.phoneSIMDao.getSIMInfo()

    fun doLogin(user: UserMaster):Single<Response<APIResponse>>{
        return smsAppNetworkService.doLogin(user)
    }

    fun setAuthorizationToken(authorizationToken : String){
        smsAppPreferences.setAuthorizationToken(authorizationToken)
    }

    fun userInfo(userId: Long):Single<List<UserMaster>>{
        return smsAppNetworkService.userInfo(userId)
    }

    fun getCompanyDetails():Single<CompanyDetails>{
        return smsAppNetworkService.getCompanyDetails()
    }

    fun saveCompanyDetails(companyDetails: CompanyDetails)
            = smsAppPreferences.saveCompanyDetails(companyDetails)

    fun insertMessageInDB(messages: Array<MessageEntity>) = smsAppDatabase.messageDao.insertMessageInDB(*messages)

    fun updateStoredMessageInDB(messages: Array<MessageEntity>) = smsAppDatabase.messageDao.insertMessageInDB(*messages)

    fun getMessageTypeCount(messageType: Int): LiveData<Long> = smsAppDatabase.messageDao.getMessageTypeCount(messageType)

    fun getMessagesOfType(messageType: Int): LiveData<List<MessageEntity>>{
        if(messageType==2){
            return smsAppDatabase.messageDao.getMessagesOfStatus(MessageTypeEnum.SENT_OFFLINE.name)
        }
        else{
            return smsAppDatabase.messageDao.getMessagesOfType(messageType)
        }
    }

    fun getMessagesOfStatusList(messageStatus: String): List<MessageEntity> = smsAppDatabase.messageDao.getMessagesOfStatusList(messageStatus)

    fun getPendingMessages(messageStatus: String,messageType: Int): LiveData<List<MessageEntity>> =
        smsAppDatabase.messageDao.getPendingMessages(messageStatus, messageType)

    fun getPendingMessagesCount(messageStatus: String,messageType: Int): LiveData<Long> =
        smsAppDatabase.messageDao.getPendingMessagesCount(messageStatus, messageType)

    fun getSpecificMessage(smsId: Long): MessageEntity = smsAppDatabase.messageDao.getSpecificMessage(smsId)

    fun getMessagesOfProvider(messageType: Int,provider: String): LiveData<List<MessageEntity>> = smsAppDatabase.messageDao.getMessagesOfProvider(messageType, provider)

    fun updateMessageStatus(smsId: Long,messageStatus: String): Int {
        return smsAppDatabase.messageDao.updateMessageStatus(smsId, messageStatus)
    }

    fun messageCountByStatus(messageStatus: String):LiveData<Long> = smsAppDatabase.messageDao.messageCountByStatus(messageStatus)

    fun deleteMessage(smsId: Long) = smsAppDatabase.messageDao.deleteMessage(smsId)

    fun updateServiceProvider(smsId: Long,serviceProvider: String) =
        smsAppDatabase.messageDao.updateServiceProvider(smsId, serviceProvider)

    fun updateFailureReason(smsId: Long,failureReason: String) =
        smsAppDatabase.messageDao.updateFailureReason(smsId, failureReason)

    fun clearMessages() = smsAppDatabase.messageDao.clearMessages()

    fun cleanMessageOfType(messageType: Int) = smsAppDatabase.messageDao.cleanMessageOfType(messageType)

    //region settings shared prefs
    fun setShowReceiverName(show: Boolean) = smsAppPreferences.setShowReceiverName(show)

    fun setShowReceiverNumber(show: Boolean) = smsAppPreferences.setShowReceiverNumber(show)

    fun setShowReceiverMessage(show: Boolean) = smsAppPreferences.setShowReceiverMessage(show)

    fun getShowReceiverName() = smsAppPreferences.getShowReceiverName()

    fun getShowReceiverNumber() = smsAppPreferences.getShowReceiverNumber()

    fun getShowReceiverMessage() = smsAppPreferences.getShowReceiverMessage()
    //endregion settings shared prefs

    fun getUserPhoneNum() = smsAppPreferences.getUserPhoneNum()

    fun getUserDisplayName() = smsAppPreferences.getUserDisplayName()

    fun changePassword(request: ChangePasswordRequestDTO): Single<ChangePasswordResponseDTO>{
        return smsAppNetworkService.changePassword(request,
            smsAppPreferences.getAuthorizationToken())
    }

    fun getUserPassword():String?{
        return smsAppPreferences.getUserPassword()
    }

    fun setUserPassword(currentPassword: String?) =smsAppPreferences.setUserPassword(currentPassword!!)

    fun clearRoomData() = smsAppDatabase.clearAllTables()

    fun getUserId():Long = smsAppPreferences.getUserId()

    fun getUserImage(): String? = smsAppPreferences.getUserImage()

    fun setUserImage(userImage: String) = smsAppPreferences.setUserImage(userImage)

    fun getUserProfile(userProfileRequestModel: UserProfileRequestModel):Single<UserProfileResponseModel>
        = smsAppNetworkService.getUserProfile(userProfileRequestModel,smsAppPreferences.getAuthorizationToken())

    fun triggerOTP(forgotPasswordRequestModel: ForgotPasswordRequestModel): Single<ForgotPasswordResponseModel>
        = smsAppNetworkService.triggerOTP(forgotPasswordRequestModel)

    fun verifyOTP(verifyOTPRequestModel: VerifyOTPRequestModel): Single<VerifyOTPResponseModel>
        = smsAppNetworkService.verifyOTP(verifyOTPRequestModel)

    fun resetPassword(resetPasswordRequestModel: ResetPasswordRequestModel): Single<ResetPasswordResponseModel>
        = smsAppNetworkService.resetPassword(resetPasswordRequestModel)
}