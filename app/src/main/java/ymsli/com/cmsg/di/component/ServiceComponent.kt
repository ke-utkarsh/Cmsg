package ymsli.com.cmsg.di.component

import dagger.Component
import ymsli.com.cmsg.common.SendSMSService
import ymsli.com.cmsg.common.UpdateMessageWorkManager
import ymsli.com.cmsg.di.IntentServiceScope
import ymsli.com.cmsg.di.module.ServiceModule

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author  Sushant Somani (VE00YM129)
 * @date   January 10, 2022
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ServiceComponent : This is the service component of dagger2 framework. This is
 *                      responsible for injecting objects in the service.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
@IntentServiceScope
@Component(dependencies = [ApplicationComponent::class],modules = [ServiceModule::class])
interface ServiceComponent {

    fun inject(sendSMSService: SendSMSService)

    fun inject(updateMessageWorkManager: UpdateMessageWorkManager)

}