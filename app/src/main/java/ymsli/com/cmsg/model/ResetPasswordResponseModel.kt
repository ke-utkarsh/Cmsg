package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponseModel (
    @SerializedName("responseMessage")
    val message: String? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null
        )