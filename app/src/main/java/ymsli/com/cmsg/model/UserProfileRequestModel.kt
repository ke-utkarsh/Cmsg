package ymsli.com.cmsg.model

import com.google.gson.annotations.SerializedName

data class UserProfileRequestModel (
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("userImage")
    val userImage: ByteArray?,

    @SerializedName("userName")
    val userName: String?,

    @SerializedName("source")
    val source: String?
        )