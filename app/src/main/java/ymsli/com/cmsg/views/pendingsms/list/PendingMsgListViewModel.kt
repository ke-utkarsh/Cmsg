package ymsli.com.cmsg.views.pendingsms.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.LocalSentMessageEntity
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.MessageTypeEnum
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Utils

class PendingMsgListViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    var changeSelectionCount : MutableLiveData<Int> = MutableLiveData()
    fun isLoggedIn(): Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    fun getSentMessagesOffline(): LiveData<List<LocalSentMessageEntity>> = smsAppRepository.getSentMessagesOffline()

    fun isInternetConnected(): Boolean = checkInternetConnection()

    /**
     * stores sim info in local DB
     */
    fun storeSIMInfo(simInfoList: Array<PhoneSIMEntity>){
        smsAppRepository.deleteSIMInfo()
        smsAppRepository.storeSIMInfo(simInfoList)
    }
}

