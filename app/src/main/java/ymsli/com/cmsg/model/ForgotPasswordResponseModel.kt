package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponseModel (

    @SerializedName("responseMessage")
    val responseMessage: String? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null,
    @SerializedName("responseData")
    val responseData: ForgotPasswordResponseData? = null
        )

data class ForgotPasswordResponseData(
    @SerializedName("emailId")
    val emailId: String,

    @SerializedName("otpValidityDuration")
    val otpValidityDuration: Int,
)