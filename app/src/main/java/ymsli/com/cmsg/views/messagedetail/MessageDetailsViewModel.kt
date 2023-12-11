package ymsli.com.cmsg.views.messagedetail

import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper

class MessageDetailsViewModel(
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
     * stores sim info in local DB
     */
    fun storeSIMInfo(simInfoList: Array<PhoneSIMEntity>){
        smsAppRepository.deleteSIMInfo()
        smsAppRepository.storeSIMInfo(simInfoList)
    }
}

