package ymsli.com.cmsg.views.profile

import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.model.UserProfileRequestModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import java.net.HttpURLConnection

class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    var profilePic: MutableLiveData<ByteArray> = MutableLiveData()
    var uploadSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun isLoggedIn(): Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }


    fun getUserPhoneNum() = smsAppRepository.getUserPhoneNum()

    fun getUserDisplayName() = smsAppRepository.getUserDisplayName()

    fun getUserProfile() {
        val userProfileRequestModel = UserProfileRequestModel(
            userId = smsAppRepository.getUserId(),
            userName = smsAppRepository.getUserName(),
            source = Constants.PROFILE_SOURCE_KEY,
            userImage = null
        )
        if (checkInternetConnection()) {
            compositeDisposable.addAll(
                smsAppRepository.getUserProfile(userProfileRequestModel)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        Log.d("res", "here")
                    }, {
                        Log.d("res", "here")
                    })
            )
        }

    }


    fun saveUserProfile() {
        val userProfileRequestModel = UserProfileRequestModel(
            userId = smsAppRepository.getUserId(),
            userName = smsAppRepository.getUserName(),
            source = Constants.PROFILE_SOURCE_KEY,
            userImage = profilePic.value
        )
        if (checkInternetConnection()) {
            showProgress.postValue(Event(true))
            compositeDisposable.addAll(
                smsAppRepository.getUserProfile(userProfileRequestModel)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        Log.d("res", it.responseData.toString())
                        if (it.responseCode == HttpURLConnection.HTTP_OK) {
                            smsAppRepository.setUserImage(
                                Base64.encodeToString(
                                    profilePic.value,
                                    Base64.DEFAULT
                                )
                            )
                        }
                        // show successful ack to user
                        uploadSuccess.postValue(Event(true))
                        showProgress.postValue(Event(false))
                    }, {
                        showProgress.postValue(Event(false))
                        Log.d("res", "exception")
                    })
            )
        }
    }

    fun getUserImage(): String? = smsAppRepository.getUserImage()
}

