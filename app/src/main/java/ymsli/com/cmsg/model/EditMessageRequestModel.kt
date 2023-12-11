package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class EditMessageRequestModel (
    @SerializedName("updatedBy")
    val updatedBy: String?,

    @SerializedName("smsModelList")
    val smsModelList: List<SMSModelList>
        )

data class SMSModelList(
    @SerializedName("smsId")
    val smsId: Long,

    @SerializedName("messageStatus")
    val messageStatus: String?,

    @SerializedName("updatedBy")
    val updateBy: String?,

    @SerializedName("serviceProvider")
    var serviceProvider: String?,

    @SerializedName("failureReason")
    var failureReason: String?
)