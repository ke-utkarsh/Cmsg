package ymsli.com.cmsg.itemviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.base.BaseItemViewModel
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.model.SentMessageDashboardModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import javax.inject.Inject

class SentMessageDashboardItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    smsAppRepository: SMSAppRepository
): BaseItemViewModel<SentMessageDashboardModel>(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {
    override fun onCreate() {

    }

    val carrierName : LiveData<String> = Transformations.map(data){ it.carrierName }
    val totalMessages: LiveData<String> = Transformations.map(data) { it.totalMessages.toString() }
    val deliveredMessages: LiveData<String> = Transformations.map(data) { it.deliveredMessages.toString() }
    val undeliveredMessages: LiveData<String> = Transformations.map(data) { it.undeliveredMessages.toString() }
}