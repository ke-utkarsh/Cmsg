package ymsli.com.cmsg.preference

import android.content.SharedPreferences
import ymsli.com.cmsg.common.Constants
import ymsli.com.cmsg.model.CompanyDetails
import ymsli.com.cmsg.model.UserMaster
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CMsgAppPreferences @Inject constructor(
    private val smsAppSharedPreferences: SharedPreferences)
{
    companion object{
        const val PREFS_NAME = "CMSG-APP-SHARED-PREFS"
        private const val TRIP_ID = "TRIP_ID"
        private const val PREF_KEY_COMPANY_CODE = "COMPANY_CODE"
        private const val SHOW_NAME_KEY = "SHOW_NAME_KEY"
        private const val SHOW_NUMBER_KEY = "SHOW_NUMBER_KEY"
        private const val SHOW_MESSAGE_KEY = "SHOW_MESSAGE_KEY"
        private const val USER_NAME_KEY = "USER_NAME_KEY"
        private const val USER_MOBILE_KEY = "USER_MOBILE_KEY"
        private const val USER_IMAGE_KEY = "USER_IMAGE_KEY"
    }

    fun isLoggedIn():Boolean{
        return smsAppSharedPreferences.getBoolean(Constants.SHARED_PREF_KEY_IS_LOGGED,false)
    }

    fun setLoggedIn(initStatus : Boolean){
        smsAppSharedPreferences.edit().putBoolean(Constants.SHARED_PREF_KEY_IS_LOGGED,initStatus).commit()
    }

    fun setAuthorizationToken(authorizationToken : String){
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_API_AUTH_TOKEN,authorizationToken).commit()
    }

    fun getAuthorizationToken():String?{
        return smsAppSharedPreferences.getString(Constants.SHARED_PREF_KEY_API_AUTH_TOKEN,null)
    }

    fun saveCompanyDetails(companyDetails: CompanyDetails){
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_COMPANY_CONTACT, companyDetails.contactNumber).apply()
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_COMPANY_ADDRESS, companyDetails.address).apply()
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_CUSTOMER_NOT_REACHABLE,companyDetails.defaultMsgToCustomer).apply()
        smsAppSharedPreferences.edit().putString(PREF_KEY_COMPANY_CODE, companyDetails.companyCode).apply()
    }

    fun setUserDataInSharedPref(user: UserMaster){
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_DRIVER_ID, user.driverId.toString()).apply()
        smsAppSharedPreferences.edit().putLong(Constants.SHARED_PREF_KEY_USER_ID, user.userId?:0).apply()
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_USER_NAME,user.username).apply()
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_PASSWORD,user.password).apply()
        smsAppSharedPreferences.edit().putString(USER_NAME_KEY,user.displayName).apply()
        smsAppSharedPreferences.edit().putString(USER_MOBILE_KEY,user.mobileNo).apply()
        smsAppSharedPreferences.edit().putString(USER_IMAGE_KEY,user.userImageString).apply()
    }

    fun getUserId():Long = smsAppSharedPreferences.getLong(Constants.SHARED_PREF_KEY_USER_ID,0)

    fun getUserImage(): String? = smsAppSharedPreferences.getString(USER_IMAGE_KEY,null)

    fun setUserImage(userImage: String) = smsAppSharedPreferences.edit().putString(USER_IMAGE_KEY,userImage).apply()

    fun getUserName(): String?{
        return smsAppSharedPreferences.getString(
            Constants.SHARED_PREF_KEY_USER_NAME,
            Constants.EMPTY_STRING)
    }

    fun setShowReceiverName(show: Boolean) = smsAppSharedPreferences.edit().putBoolean(SHOW_NAME_KEY,show).apply()

    fun setShowReceiverNumber(show: Boolean) = smsAppSharedPreferences.edit().putBoolean(SHOW_NUMBER_KEY,show).apply()

    fun setShowReceiverMessage(show: Boolean) = smsAppSharedPreferences.edit().putBoolean(SHOW_MESSAGE_KEY,show).apply()

    fun getShowReceiverName() = smsAppSharedPreferences.getBoolean(SHOW_NAME_KEY,true)

    fun getShowReceiverNumber() = smsAppSharedPreferences.getBoolean(SHOW_NUMBER_KEY,true)

    fun getShowReceiverMessage() = smsAppSharedPreferences.getBoolean(SHOW_MESSAGE_KEY,true)

    fun getUserDisplayName() = smsAppSharedPreferences
        .getString(USER_NAME_KEY, Constants.EMPTY_STRING)

    fun getUserPhoneNum() = smsAppSharedPreferences
        .getString(USER_MOBILE_KEY, Constants.EMPTY_STRING)

    fun getUserPassword(): String?{
        return smsAppSharedPreferences.getString(
            Constants.SHARED_PREF_KEY_PASSWORD,
            Constants.EMPTY_STRING)
    }

    fun setUserPassword(password: String) =
        smsAppSharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_PASSWORD,
        password).apply()

}