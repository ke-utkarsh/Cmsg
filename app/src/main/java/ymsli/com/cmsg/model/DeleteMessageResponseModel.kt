package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class DeleteMessageResponseModel(
    @SerializedName("responseCode")
    val responseCode: Int,

    @SerializedName("responseMessage")
    val responseMessage: String,
)