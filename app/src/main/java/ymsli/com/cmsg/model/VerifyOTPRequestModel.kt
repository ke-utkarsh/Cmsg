package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class VerifyOTPRequestModel (
    @SerializedName("otpCode")
    val otpCode: Int,
    @SerializedName("emailId")
    val emailId: String
        )
