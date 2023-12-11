package ymsli.com.cmsg.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ymsli.com.cmsg.BuildConfig
import java.util.concurrent.TimeUnit

object Networking{

    var couriemateNetworkService = SMSAppNetworkService::class.java
    lateinit var cns:SMSAppNetworkService

    fun createCouriemateNetworkService()
            : SMSAppNetworkService {
        val loggingInterceptor = HttpLoggingInterceptor()
       loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(
                        OkHttpClient.Builder()
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .readTimeout(60, TimeUnit.SECONDS)
                                .writeTimeout(60, TimeUnit.SECONDS)
                                .addInterceptor(loggingInterceptor)
                                .retryOnConnectionFailure(true)
                                .build()
                )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(couriemateNetworkService)
            .apply {
                cns = this
            }
    }
}