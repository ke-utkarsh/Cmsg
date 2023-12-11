package ymsli.com.cmsg.views.settings

import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.smsapp.utils.rx.SchedulerProvider

class SettingsViewModel(schedulerProvider: SchedulerProvider,
compositeDisposable: CompositeDisposable,
networkHelper: NetworkHelper,
private val smsAppRepository: SMSAppRepository): BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    override fun onCreate() {

    }

    fun setShowReceiverName(show: Boolean) = smsAppRepository.setShowReceiverName(show)

    fun setShowReceiverNumber(show: Boolean) = smsAppRepository.setShowReceiverNumber(show)

    fun setShowReceiverMessage(show: Boolean) = smsAppRepository.setShowReceiverMessage(show)
}