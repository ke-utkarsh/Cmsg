package ymsli.com.cmsg.views.sync

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.database.entity.PhoneSIMEntity
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import javax.net.ssl.HttpsURLConnection

class SyncViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository){


    override fun onCreate() {

    }

    fun getServiceProvider(){
        if(checkInternetConnection()){
            compositeDisposable.addAll(
            smsAppRepository.getServiceProvider()
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    if(it.responseCode==HttpsURLConnection.HTTP_OK && it.responseData!=null && it.responseData.size>0){
                        smsAppRepository.insertServiceProviders(it.responseData.toTypedArray())
                    }
                    getPendingMessages()
                },{
                    openNextActivity.postValue(Event(true))
                })
            )
        }
    }

    /**
     * stores sim info in local DB
     */
    fun storeSIMInfo(simInfoList: Array<PhoneSIMEntity>){
        smsAppRepository.deleteSIMInfo()
        smsAppRepository.storeSIMInfo(simInfoList)
    }

    fun clearRoomData() = smsAppRepository.clearMessages()
}