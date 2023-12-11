package ymsli.com.cmsg.itemviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.base.BaseItemViewModel
import ymsli.com.cmsg.database.entity.MessageEntity
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import javax.inject.Inject

class MessageItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    smsAppRepository: SMSAppRepository
): BaseItemViewModel<MessageEntity>(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository){

    val orderNo: LiveData<String> = Transformations.map(data) { it.orderNo.toString() }
    val receiverName : LiveData<String> = Transformations.map(data){ it.receiverName }
    val contactNo: LiveData<String> = Transformations.map(data) { it.receiverMobileNo }
    val messageBody: LiveData<String> = Transformations.map(data) { it.messageBody }
    override fun onCreate() {
        
    }
}