package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestModel (
    @SerializedName("emailId")
    val emailId: String
    )