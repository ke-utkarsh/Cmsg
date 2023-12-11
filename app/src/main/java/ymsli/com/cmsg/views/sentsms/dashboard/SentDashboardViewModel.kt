package ymsli.com.cmsg.views.sentsms.dashboard

import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper

class SentDashboardViewModel(
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

}

