package ymsli.com.cmsg.di.module

import android.app.IntentService
import dagger.Module

/**
 * Project Name : CMSG
 * @company YMSLI
 * @author  Sushant Somani (VE00YM129)
 * @date   January 10, 2022
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ServiceModule : This is the service module of dagger2 framework. This is
 *                      responsible for providing objects with @Inject annotation
 *                      in the service.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */
@Module
class ServiceModule(private val intentService:IntentService) {
}