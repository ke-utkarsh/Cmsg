/**
 * Project Name : CMsg App
 * @company YMSLI
 * @author  Hitesh (VE00YM128)
 * @date   December 26, 2021
 * Copyright (c) 2019, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * Constants : This class contains all constants value for CMsg App application
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 *
 * -----------------------------------------------------------------------------------
 */

package ymsli.com.cmsg.common

class Constants {


    companion object {

        const val COUNTRY_CODE_UGANDA = "+256-"

        const val PASSWORD_LENGTH_MINIMUM = 5
        const val EMPTY_STRING = ""
        const val SPLASH_DELAY = 1500L
        const val NOTIFICATION_TIME_FORMAT = "MM/dd/yyyy hh:mm a"
        const val FORMAT_COURIEMATE_DATE = "d MMMM, HH:mm 'Hrs'"
        const val FORMAT_COURIEMATE_CURRENCY = "#,###.00"
        const val DATE_PLACE_HOLDER = "____/__/__"

        const val SHARED_PREF_KEY_DRIVER_ID = "driverId"
        const val SHARED_PREF_KEY_USER_ID = "userId"
        const val SHARED_PREF_KEY_USER_NAME = "userName"
        const val SHARED_PREF_KEY_PASSWORD = "password"
        const val SHARED_PREF_KEY_IS_LOGGED = "isLoggedIn"
        const val SHARED_PREF_KEY_USER_TYPE = "userType"
        const val SHARED_PREF_KEY_API_AUTH_TOKEN = "apiAuthToken"
        const val SHARED_PREF_KEY_COMPANY_CONTACT = "companyContact"
        const val SHARED_PREF_KEY_COMPANY_ADDRESS = "companyAddress"
        const val SHARED_PREF_KEY_CUSTOMER_NOT_REACHABLE = "customerUnreachable"

        const val DAY_1_SUFFIX = "st"
        const val DAY_2_SUFFIX = "nd"
        const val DAY_3_SUFFIX = "rd"
        const val DAY_DEFAULT_SUFFIX = "th"

        const val NA_KEY = "NA"
        const val PROFILE_SOURCE_KEY = "mobile"

        const val MESSAGE_DETAIL_INTENT = "MESSAGE_DETAIL_INTENT"
        const val READ_ONLY_MESSAGES_INTENT = "READ_ONLY_MESSAGES_INTENT"
        const val MESSAGE_TYPE_INTENT = "MESSAGE_TYPE_INTENT"
        const val LOCAL_SENT_INTENT = "LOCAL_SENT_INTENT"
        const val PROVIDER_TYPE_INTENT = "PROVIDER_TYPE_INTENT"
        const val SMS_WORKER_TAG = "SMS_WORKER_TAG"
        const val SENT_MESSAGES_HISTORY_DAYS = 6

        const val NO_NETWORK_AVAILABLE ="No Network Available"
        const val ACTION_OK ="OK"
    }
}
