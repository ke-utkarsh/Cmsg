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
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.utils.Status
import java.net.HttpURLConnection

class ForgotPasswordViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    var otpTriggered: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var otpDuration: Int = 10
    var emailIdResponse : String? = null
    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    fun isLoggedIn():Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    fun triggerOTP(email: String?){
        // add email validations here
        val validations = Validator.validateEmail(email)
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
                    val forgotPassModel = ForgotPasswordRequestModel(emailId = email!!)
                    compositeDisposable.addAll(
                        smsAppRepository.triggerOTP(forgotPassModel)
                            .subscribeOn(schedulerProvider.io())
                            .subscribe({
                                Log.d("res","here")
                                if(it.responseCode==HttpURLConnection.HTTP_OK){
                                    otpDuration = ((it.responseData)?.otpValidityDuration)?:10
                                    emailIdResponse = ((it.responseData)?.emailId)
                                    otpTriggered.postValue(Event(true))
                                }
                                else{
                                    otpTriggered.postValue(Event(false))
                                }
                                showProgress.postValue(Event(false))
                            },{
                                otpTriggered.postValue(Event(false))
                                showProgress.postValue(Event(false))
                                Log.d("res","here")
                            })
                    )
                }
            }
        }


    }

}

