package ymsli.com.cmsg.di.component

import androidx.appcompat.app.AppCompatActivity
import dagger.BindsInstance
import dagger.Component
import ymsli.com.cmsg.di.ActivityScope
import ymsli.com.cmsg.di.module.ActivityModule
import ymsli.com.cmsg.views.changepassword.ChangePasswordActivity
import ymsli.com.cmsg.views.dashboard.DashboardActivity
import ymsli.com.cmsg.views.forgotpassword.EnterOTPActivity
import ymsli.com.cmsg.views.forgotpassword.ForgotPasswordActivity
import ymsli.com.cmsg.views.forgotpassword.ResetPasswordActivity
import ymsli.com.cmsg.views.login.LoginActivity
import ymsli.com.cmsg.views.messagedetail.MessageDetailsActivity
import ymsli.com.cmsg.views.pendingsms.list.PendingMsgListActivity
import ymsli.com.cmsg.views.profile.ProfileActivity
import ymsli.com.cmsg.views.sentsms.dashboard.SentDashboardActivity
import ymsli.com.cmsg.views.sentsms.list.SentMsgListActivity
import ymsli.com.cmsg.views.settings.SettingsActivity
import ymsli.com.cmsg.views.splash.SplashActivity
import ymsli.com.cmsg.views.sync.SyncActivity

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 21, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * ActivityComponent : Dagger component for activity dependency injection
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(dashboardActivity: DashboardActivity)
    fun inject(pendingMsgListActivity: PendingMsgListActivity)
    fun inject(sentMsgListActivity: SentMsgListActivity)
    fun inject(sentDashboardActivity: SentDashboardActivity)
    fun inject(messageDetailsActivity: MessageDetailsActivity)
    fun inject(forgotPasswordActivity: ForgotPasswordActivity)
    fun inject(enterOTPActivity: EnterOTPActivity)
    fun inject(resetPasswordActivity: ResetPasswordActivity)
    fun inject(changePasswordActivity: ChangePasswordActivity)
    fun inject(profileActivity: ProfileActivity)
    fun inject(syncActivity: SyncActivity)
    fun inject(settingsActivity: SettingsActivity)



    @Component.Factory
    interface Factory {
        fun create(
            appComponent: ApplicationComponent,
            @BindsInstance activity: AppCompatActivity
        ): ActivityComponent
    }
}