package ymsli.com.cmsg.views.forgotpassword

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.R
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.common.Validation
import ymsli.com.cmsg.common.Validator
import ymsli.com.cmsg.model.ForgotPasswordRequestModel
import ymsli.com.cmsg.model.VerifyOTPRequestModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.utils.Status
import java.net.HttpURLConnection

class EnterOTPViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()
    var isOTPVerified: MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun isLoggedIn():Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    fun verifyOTP(email: String?, otp: String?){
        // validate OTP entered by user
        val validations = Validator.validateOTP(otp)
        validationsList.postValue(validations)
        val successValidation = validations.filter { it.resource.status == Status.SUCCESS }
        when {
            /** Validation failed, notify user */
            successValidation.size != validations.size -> {
                val failedValidation = validations.filter { it.resource.status == Status.ERROR }[0]
                messageStringId.postValue(failedValidation.resource)
            }

            /** Network is not available, notify user */
            !checkInternetConnection() -> {
                messageStringId.postValue(Resource.error(R.string.network_connection_error))
            }

            else -> {
                if(checkInternetConnection()){
                    showProgress.postValue(Event(true))
                    val verifyOTPModel = VerifyOTPRequestModel(emailId = email!!, otpCode = otp!!.toInt())
                    compositeDisposable.addAll(
                        smsAppRepository.verifyOTP(verifyOTPModel)
                            .subscribeOn(schedulerProvider.io())
                            .subscribe({
                                Log.d("res","here")
                                if(it.responseCode== HttpURLConnection.HTTP_OK){
                                    isOTPVerified.postValue(Event(true))
                                }
                                else{
                                    isOTPVerified.postValue(Event(false))
                                }
                                showProgress.postValue(Event(false))
                            },{
                                isOTPVerified.postValue(Event(false))
                                showProgress.postValue(Event(false))
                                Log.d("res","here")
                            })
                    )
                }
            }
        }
    }
}

