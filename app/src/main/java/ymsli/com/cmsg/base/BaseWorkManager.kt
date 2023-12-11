package ymsli.com.cmsg.base

import android.content.Context
import android.net.ConnectivityManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.CMsgApplication
import ymsli.com.cmsg.di.component.ServiceComponent
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.di.component.DaggerServiceComponent
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import javax.inject.Inject

abstract class BaseWorkManager(private val context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {
    @Inject
    lateinit var smsAppRepository: SMSAppRepository

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    abstract fun inject(jobServiceComponent: ServiceComponent)

    private fun buildWorkManagerComponent() =
        DaggerServiceComponent
            .builder()
            .applicationComponent((context.applicationContext as CMsgApplication).appComponent)
            .build()

    fun init(){
        inject(buildWorkManagerComponent())
    }

    /**
     * determines if internet is there or not
     */
    fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}