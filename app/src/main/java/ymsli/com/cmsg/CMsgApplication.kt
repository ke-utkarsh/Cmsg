package ymsli.com.cmsg

import android.app.Application
import ymsli.com.cmsg.di.component.ApplicationComponent
import ymsli.com.cmsg.di.component.DaggerApplicationComponent
import ymsli.com.cmsg.repository.SMSAppRepository
import javax.inject.Inject

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * CMSGApp : Application class for app level configurations (DI, Loggers etc.)
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
class CMsgApplication: Application() {

    lateinit var appComponent: ApplicationComponent private set

    @Inject
    lateinit var smsAppRepository: SMSAppRepository

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
        clearOfflineSentMessages()
    }

    private fun initAppComponent(){
        appComponent = DaggerApplicationComponent.factory().create(this)
        appComponent.inject(this)
    }

    private fun clearOfflineSentMessages(){
        smsAppRepository.cleanMessageOfType()
    }
}