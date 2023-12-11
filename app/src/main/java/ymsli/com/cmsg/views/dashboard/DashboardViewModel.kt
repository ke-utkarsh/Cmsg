package ymsli.com.cmsg.views.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.LocalSentMessageEntity
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.MessageStatusEnum
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.NetworkHelper

class DashboardViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    fun isLoggedIn():Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    /**
     * gets count of pending messages
     */
    fun getMessageTypeCountPending(): LiveData<Long> =  smsAppRepository.getMessageTypeCount(MessageTypeEnum.PENDING.messageType)

    fun getPendingMessagesCount(messageStatus: String,messageType: Int): LiveData<Long> =
        smsAppRepository.getPendingMessagesCount(messageStatus, messageType)

    /**
     * gets count of sent messages
     */
    fun getMessageTypeCountSent(): LiveData<Long> =  smsAppRepository.messageCountByStatus(MessageTypeEnum.SENT_OFFLINE.name)

    fun getMessageTypeCountSentAPI(): LiveData<Long> =  smsAppRepository.getMessageTypeCount(MessageTypeEnum.SENT_API.messageType)

    fun getSentMessagesOfflineCount(): LiveData<Long> = smsAppRepository.getSentMessagesOfflineCount()

    fun isInternetConnected(): Boolean = checkInternetConnection()
}

