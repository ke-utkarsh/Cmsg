package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponseModel (

    @SerializedName("responseMessage")
    val message: String? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null,
    @SerializedName("responseData")
    val responseData: Any? = null,
        )


data class UserManagementUpdateModel (
    @SerializedName("userImageString")
    val userImageString: String?,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("userName")
    val userName: String?,

    @SerializedName("source")
    val source: String?
)