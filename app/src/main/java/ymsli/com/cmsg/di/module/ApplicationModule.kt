package ymsli.com.cmsg.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.RxSchedulerProvider
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.database.SMSAppDatabase
import ymsli.com.cmsg.database.SMSAppDatabase.Companion.DB_NAME
import ymsli.com.cmsg.network.Networking
import ymsli.com.cmsg.network.SMSAppNetworkService
import ymsli.com.cmsg.preference.CMsgAppPreferences
import ymsli.com.cmsg.preference.CMsgAppPreferences.Companion.PREFS_NAME
import ymsli.com.cmsg.utils.NetworkHelper
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()


    @Singleton
    @Provides
    fun provideSharedPref(app: Application): SharedPreferences{
        return app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSMsgAppPrefs(sharedPreferences: SharedPreferences): CMsgAppPreferences = CMsgAppPreferences(sharedPreferences)

    @Singleton
    @Provides
    fun provideSMsgAppDatabase(app: Application): SMSAppDatabase{
        return Room.databaseBuilder(app, SMSAppDatabase::class.java, DB_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNetworkHelper(application: Application): NetworkHelper =
        NetworkHelper(application)

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider =
        RxSchedulerProvider()



    @Provides
    @Singleton
    fun provideSMSNetworkService()
            : SMSAppNetworkService = Networking.createCouriemateNetworkService()



}