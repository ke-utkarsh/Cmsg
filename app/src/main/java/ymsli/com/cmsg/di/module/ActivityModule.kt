package ymsli.com.cmsg.di.module

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.common.ViewModelProviderFactory
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.views.changepassword.ChangePasswordViewModel
import ymsli.com.cmsg.views.dashboard.DashboardViewModel
import ymsli.com.cmsg.views.forgotpassword.EnterOTPViewModel
import ymsli.com.cmsg.views.forgotpassword.ForgotPasswordViewModel
import ymsli.com.cmsg.views.forgotpassword.ResetPasswordViewModel
import ymsli.com.cmsg.views.login.LoginViewModel
import ymsli.com.cmsg.views.messagedetail.MessageDetailsViewModel
import ymsli.com.cmsg.views.pendingsms.list.PendingMsgListViewModel
import ymsli.com.cmsg.views.profile.ProfileViewModel
import ymsli.com.cmsg.views.sentsms.dashboard.SentDashboardViewModel
import ymsli.com.cmsg.views.sentsms.list.SentMsgViewModel
import ymsli.com.cmsg.views.settings.SettingsViewModel
import ymsli.com.cmsg.views.sync.SyncViewModel
import ymsli.com.cmsg.views.userengagement.ProgressFragment
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.smsapp.views.splash.SplashViewModel

@Module
class ActivityModule {

    @Provides
    fun provideSplashViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): SplashViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SplashViewModel::class) {
            SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository)
        }).get(SplashViewModel::class.java)
    }


    @Provides
    fun provideLoginViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): LoginViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(LoginViewModel::class) {
            LoginViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository)
        }).get(LoginViewModel::class.java)
    }

    @Provides
    fun provideDashboardViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): DashboardViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(DashboardViewModel::class) {
            DashboardViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(DashboardViewModel::class.java)
    }

    @Provides
    fun provideSentDashboardViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): SentDashboardViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SentDashboardViewModel::class) {
            SentDashboardViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(SentDashboardViewModel::class.java)
    }


    @Provides
    fun providePendingMsgListViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): PendingMsgListViewModel {
        return ViewModelProvider(
            activity,
            ViewModelProviderFactory(PendingMsgListViewModel::class) {
                PendingMsgListViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    smsAppRepository
                )
            }).get(PendingMsgListViewModel::class.java)
    }

    @Provides
    fun provideSentMsgListViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): SentMsgViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SentMsgViewModel::class) {
            SentMsgViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(SentMsgViewModel::class.java)
    }


    @Provides
    fun provideMessageDetailsViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): MessageDetailsViewModel {
        return ViewModelProvider(
            activity,
            ViewModelProviderFactory(MessageDetailsViewModel::class) {
                MessageDetailsViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    smsAppRepository
                )
            }).get(MessageDetailsViewModel::class.java)
    }


    @Provides
    fun provideForgotPasswordViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): ForgotPasswordViewModel {
        return ViewModelProvider(
            activity,
            ViewModelProviderFactory(ForgotPasswordViewModel::class) {
                ForgotPasswordViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    smsAppRepository
                )
            }).get(ForgotPasswordViewModel::class.java)
    }

    @Provides
    fun provideEnterOTPViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): EnterOTPViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(EnterOTPViewModel::class) {
            EnterOTPViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(EnterOTPViewModel::class.java)
    }

    @Provides
    fun provideResetPasswordViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): ResetPasswordViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(ResetPasswordViewModel::class) {
            ResetPasswordViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(ResetPasswordViewModel::class.java)
    }

    @Provides
    fun provideChangePasswordViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): ChangePasswordViewModel {
        return ViewModelProvider(
            activity,
            ViewModelProviderFactory(ChangePasswordViewModel::class) {
                ChangePasswordViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    smsAppRepository
                )
            }).get(ChangePasswordViewModel::class.java)
    }

    @Provides
    fun provideProfileViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): ProfileViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(ProfileViewModel::class) {
            ProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(ProfileViewModel::class.java)
    }

    /**
     * creates instance of ProgressFragment
     * using dagger2 framework
     */
    @Provides
    fun provideProgressFragment(): ProgressFragment = ProgressFragment.newInstance()

    @Provides
    fun provideSyncViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): SyncViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SyncViewModel::class) {
            SyncViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(SyncViewModel::class.java)
    }

    @Provides
    fun provideSettingsViewModel(
        activity: AppCompatActivity,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        smsAppRepository: SMSAppRepository
    ): SettingsViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SettingsViewModel::class) {
            SettingsViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                smsAppRepository
            )
        }).get(SettingsViewModel::class.java)
    }
}