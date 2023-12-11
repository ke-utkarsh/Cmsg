package ymsli.com.cmsg.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.BindsInstance
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.CMsgApplication
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.database.SMSAppDatabase
import ymsli.com.cmsg.di.module.ApplicationModule
import ymsli.com.cmsg.network.SMSAppNetworkService
import ymsli.com.cmsg.preference.CMsgAppPreferences
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import javax.inject.Singleton

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ApplicationComponent : Dagger component to provide application level dependencies.
 * objects provided by this component are created once and exist for the lifetime of application.
 * this component is a dependency for all the other components.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun getContext(): Context
    fun provideSMsgAppPrefs(): CMsgAppPreferences
    fun provideSMsgAppDatabase(): SMSAppDatabase

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app: Application): ApplicationComponent
    }

    fun inject(app: CMsgApplication)

    fun getSharedPreferences(): SharedPreferences

    fun getNetworkHelper(): NetworkHelper

    fun getSMSAppRepository(): SMSAppRepository

    fun getSchedulerProvider(): SchedulerProvider

    fun getCompositeDisposable(): CompositeDisposable

    fun getSMSAppNetworkService(): SMSAppNetworkService

    fun getSMSAppSharedPreferences(): CMsgAppPreferences

    fun getSMSDatabase(): SMSAppDatabase

}