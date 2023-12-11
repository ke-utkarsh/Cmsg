package ymsli.com.cmsg.views.login

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ymsli.com.cmsg.R
import ymsli.com.smsapp.utils.rx.SchedulerProvider
import ymsli.com.cmsg.base.BaseViewModel
import ymsli.com.cmsg.common.Event
import ymsli.com.cmsg.common.Validation
import ymsli.com.cmsg.common.Validator
import ymsli.com.cmsg.model.UserMaster
import ymsli.com.cmsg.repository.SMSAppRepository
import ymsli.com.cmsg.utils.NetworkHelper
import ymsli.com.cmsg.utils.Resource
import ymsli.com.cmsg.utils.Status
import ymsli.com.cmsg.utils.Utils
import java.sql.Timestamp

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val smsAppRepository: SMSAppRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper, smsAppRepository) {

    val loggingIn: MutableLiveData<Boolean> = MutableLiveData()
    val loggedIn: MutableLiveData<Boolean> = MutableLiveData()
    val appDataLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var usernameField: MutableLiveData<String> = MutableLiveData()
    var passwordField: MutableLiveData<String> = MutableLiveData()

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    //error live dataList
    val invalidUsername: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    val invalidPassword: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    fun onUsernameChange(username: String) = usernameField.postValue(username)
    fun onPasswordChange(password: String) = passwordField.postValue(password)

    private lateinit var userInfo: UserMaster

    override fun onCreate() {

    }

    fun doLogin(countryCode: String) {
        smsAppRepository.cleanMessageOfType()// clear offline SENT messages
        val username = usernameField.value
        val password = passwordField.value
        val user =
            UserMaster(username = username, password = password)
        val validations = Validator.validateLoginFields(user)
        validationsList.postValue(validations)
        val successValidation = validations.filter { it.resource.status == Status.SUCCESS }

        when {
            /** Validations failed, notify user */
            successValidation.size != validations.size -> {
                val failedValidation =
                    validations.filter { it.resource.status == Status.ERROR }[0].field
                loginErrorUserAcknowledgement(failedValidation.toString())
            }

            /** Validations passed but no internet, notify user */
            (!checkInternetConnection()) -> {
                messageStringId.postValue(Resource.error(R.string.network_connection_error))
            }

            /** Validations passed and internet is available, continue with API call */
            else -> { //same user is logging in again
                user.username = "$username$countryCode"
                showProgress.postValue(Event(true))
                loginUser(countryCode)
            }
        }
    }

    private fun loginUser(countryCode: String){
        val username = usernameField.value
        val password = passwordField.value
        val user =
            UserMaster(username = "$username$countryCode", password = password)
        loggingIn.postValue(true)
        compositeDisposable.addAll(
            smsAppRepository.doLogin(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    //var jwtToken = it.headers().get("Authorization").toString()
                    var jwtToken = it.headers().get("x-amzn-Remapped-Authorization").toString()
                    if(it.headers().get("x-amzn-Remapped-Authorization").isNullOrBlank()){
                        //if(it.headers().get("Authorization").isNullOrBlank()){
                            showProgress.postValue(Event(false))
                            loggingIn.postValue(false)
                        messageStringId.postValue(Resource.error(R.string.ERROR_AUTH_FAILED))
                    }
                    else {
                        if(jwtToken.equals("null",true)){
                            FirebaseCrashlytics.getInstance().recordException(Exception("JWT Token is $jwtToken"))
                        }
                        else{
                            smsAppRepository.setAuthorizationToken(jwtToken)
                            val userId = it.body()!!.responseData!!.userName.toLong()
                            var apiLevel = Build.VERSION.SDK_INT
                            Utils.writeToFile("\n${Timestamp(System.currentTimeMillis())} \t10A. New Login by userId: "+userId+"***Android Api Level: "+apiLevel)
                            getUserInfo(userId)
                        }
                    }
                }, {
                    showProgress.postValue(Event(false))
                    loggingIn.postValue(false)
                    messageStringId.postValue(Resource.error(R.string.ERROR_AUTH_FAILED))
                })
        )
    }

    /**
     * if the username and password is correct,
     * info is fetched about corresponding user
     */
    private fun getUserInfo(userId: Long) {
        if (checkInternetConnection()) {
            compositeDisposable.addAll(
                smsAppRepository.userInfo(userId)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        userInfo = it[0]
                        userInfo.password = passwordField.value
                        usernameField.postValue(userInfo.username)
                        loggedIn.postValue(true)
                        // store parameters locally
                        smsAppRepository.setUserDataInSharedPref(userInfo)
                        getCompanyDetails(userId)
                    }, {
                        loggingIn.postValue(false)
                        showProgress.postValue(Event(false))
                        messageStringId.postValue(Resource.error(R.string.network_default_error))
                    })
            )
        } else messageStringId.postValue(Resource.error(R.string.network_connection_error))
    }

    /**
     * fetch company details like phone number
     * and stores them locally
     */
    private fun getCompanyDetails(userId: Long) {
        if (checkInternetConnection()) {
            compositeDisposable.addAll(
                smsAppRepository.getCompanyDetails()
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        smsAppRepository.saveCompanyDetails(it)
                        smsAppRepository.setLoggedIn(true)
                        showProgress.postValue(Event(false))
                        appDataLoaded.postValue(Event(true))
                    }, {
                        loggingIn.postValue(false)
                        showProgress.postValue(Event(false))
                        messageStringId.postValue(Resource.error(R.string.network_internal_error))
                    })
            )
        } else {
            loggingIn.postValue(false)
            messageStringId.postValue(Resource.error(R.string.network_connection_error))
        }
    }

    /**
     * called during invalid input
     * by the user
     */
    private fun loginErrorUserAcknowledgement(invalidField: String?) =
        when (invalidField) {
            Validation.Field.USERNAME.toString() ->
                invalidUsername.postValue(Event(emptyMap()))
            Validation.Field.EMPTY_PASSWORD.toString() ->
                invalidPassword.postValue(Event(emptyMap()))
            //messageStringId.postValue(Resource.error(R.string.password_field_empty))
            Validation.Field.PASSWORD.toString() ->
                invalidPassword.postValue(Event(emptyMap()))
            else -> {}
        }
}

