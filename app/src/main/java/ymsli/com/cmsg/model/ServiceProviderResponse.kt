package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName
import ymsli.com.cmsg.database.entity.ServiceProviderEntity

data class ServiceProviderResponse (
    @SerializedName("responseCode")
    val responseCode: Int,

    @SerializedName("responseMessage")
    val responseMessage: String,

    @SerializedName("responseData")
    val responseData: List<ServiceProviderEntity>?
    )