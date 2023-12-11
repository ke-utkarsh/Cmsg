package ymsli.com.cmsg.views.forgotpassword

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.R
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.common.Validation
import ymsli.com.cmsg.common.Validator
import ymsli.com.cmsg.model.ResetPasswordRequestModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.utils.Status
import java.net.HttpURLConnection

class ResetPasswordViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()
    var isPasswordReset: MutableLiveData<Event<Boolean>> = MutableLiveData()
    fun isLoggedIn():Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    fun resetPassword(newPassword: String?,retypeNewPassword: String?,email: String,otp: Int){
        // validate new password length & re-type mismatch if any
        // validate OTP entered by user
        val validations = Validator.validateResetPasswordFields(newPassword,retypeNewPassword)
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
                showProgress.postValue(Event(true))
                val resetPasswordRequestModel = ResetPasswordRequestModel(newPassword = newPassword!!,emailId = email, otpCode = otp)
                compositeDisposable.addAll(
                    smsAppRepository.resetPassword(resetPasswordRequestModel)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({
                            if(it.responseCode==HttpURLConnection.HTTP_OK){
                                isPasswordReset.postValue(Event(true))
                            }
                            else{
                                isPasswordReset.postValue(Event(false))
                            }
                            showProgress.postValue(Event(false))
                        },{
                            isPasswordReset.postValue(Event(false))
                            showProgress.postValue(Event(false))
                        })
                )
            }
        }
    }

}

