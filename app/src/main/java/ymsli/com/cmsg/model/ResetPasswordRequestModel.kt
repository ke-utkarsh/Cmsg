package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequestModel (
    @SerializedName("newPassword")
    val newPassword: String,

    @SerializedName("emailId")
    val emailId: String,

    @SerializedName("otpCode")
    val otpCode: Int
        )