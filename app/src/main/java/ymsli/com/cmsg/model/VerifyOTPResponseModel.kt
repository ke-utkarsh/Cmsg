package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class VerifyOTPResponseModel (
    @SerializedName("responseMessage")
    val message: String? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null

        )
