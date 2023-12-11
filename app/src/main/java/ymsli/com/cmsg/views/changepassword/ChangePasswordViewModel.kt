package ymsli.com.cmsg.views.changepassword

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ymsli.com.cmsg.R
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.common.Validation
import ymsli.com.cmsg.common.Validator
import ymsli.com.cmsg.model.ChangePasswordRequestDTO
import ymsli.com.cmsg.model.ChangePasswordValidationModel
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.utils.Status
import ymsli.com.cmsg.utils.Utils

class ChangePasswordViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    /** Live data fields for all the text input fields on the UI */
    var currentPassword = smsAppRepository.getUserPassword()
    var enteredCurrentPassword: MutableLiveData<String> = MutableLiveData()
    var newPassword: MutableLiveData<String> = MutableLiveData()
    var retypeNewPassword: MutableLiveData<String> = MutableLiveData()
    var apiRequestActive: MutableLiveData<Boolean> = MutableLiveData()

    fun onCurrentPasswordChange(fieldValue: String) = enteredCurrentPassword.postValue(fieldValue)
    fun onNewPasswordChange(fieldValue: String) = newPassword.postValue(fieldValue)
    fun onRetypeNewPasswordChange(fieldValue: String) = retypeNewPassword.postValue(fieldValue)
    fun isLoggedIn():Boolean {
        return smsAppRepository.isLoggedIn()
    }

    override fun onCreate() {

    }

    /**
     * Used to perform the change password operation.
     * If all validations are passed and network is available then API call is performed.
     * @author Sushant VE00YM129
     */
    fun changePassword(){
        val validationModel = getValidationModel()
        val validations = Validator.validateChangePasswordFields(validationModel)
        validationsList.postValue(validations)
        val successValidation = validations.filter { it.resource.status == Status.SUCCESS }

        when{
            /** Validation failed, notify user */
            successValidation.size != validations.size -> {
                val failedValidation = validations.filter { it.resource.status == Status.ERROR }[0]
                messageStringId.postValue(failedValidation.resource)
            }

            /** Network is not available, notify user */
            !checkInternetConnection() -> {
                messageStringId.postValue(Resource.error(R.string.network_connection_error))
            }

            /** Network is available and validations passed, continue with API call */
            else -> {
                showProgress.postValue(Event(true))
                compositeDisposable.addAll(
                    smsAppRepository.changePassword(getRequestModel())
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({
                            apiRequestActive.postValue(true)
                            currentPassword = validationModel.newPassword
                            messageStringId.postValue(Resource.success(R.string.PASSWORD_CHANGE_SUCCESS))
                            showProgress.postValue(Event(false))
                            smsAppRepository.setUserPassword(currentPassword)

                        }, {
                            showProgress.postValue(Event(false))
                            apiRequestActive.postValue(false)
                            messageStringId.postValue(Resource.error(R.string.ERROR_PASSWORD_CHANGE_FAILED))
                        })
                )
            }
        }
    }

    /**
     * Prepare a model for the validator.
     * set all the fields which are required by the validator.
     * @author Sushant VE00YM129
     */
    private fun getValidationModel(): ChangePasswordValidationModel {
        return ChangePasswordValidationModel(
            currentPassword = currentPassword!!,
            enteredCurrentPassword = enteredCurrentPassword.value,
            newPassword = newPassword.value,
            retypeNewPassword = retypeNewPassword.value
        )
    }

    /**
     * Prepare a model for the request API.
     * @author Sushant VE00YM129
     */
    private fun getRequestModel(): ChangePasswordRequestDTO {
        return ChangePasswordRequestDTO(
            currentPassword = currentPassword,
            newPassword = newPassword.value, userId = smsAppRepository.getUserName(),
            timezoneOffset = Utils.getGMTOffset()
        )
    }
}

