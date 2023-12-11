package ymsli.com.cmsg.base

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.CMsgApplication
import ymsli.com.cmsg.di.component.DaggerServiceComponent
import ymsli.com.cmsg.di.component.ServiceComponent
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import javax.inject.Inject

abstract class BaseService(): Service() {
    @Inject
    lateinit var smsAppRepository: SMSAppRepository

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    abstract fun inject(jobServiceComponent: ServiceComponent)

    private fun buildServiceComponent() =
        DaggerServiceComponent
            .builder()
            .applicationComponent((applicationContext as CMsgApplication).appComponent)
            .build()

    fun init(){
        inject(buildServiceComponent())
    }

    /**
     * determines if internet is there or not
     */
    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}