package ymsli.com.cmsg.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "local_sent_message_table")
data class LocalSentMessageEntity(
    @SerializedName("messageBody")
    val messageBody: String?,

    @SerializedName("messageStatus")
    var messageStatus: String?,

    @SerializedName("orderId")
    val orderId: Long?,

    @SerializedName("receiverMobileNo")
    val receiverMobileNo: String?,

    @SerializedName("receiverName")
    val receiverName: String?,

    @SerializedName("serviceProvider")
    val serviceProvider: String?,

    @PrimaryKey
    @SerializedName("smsId")
    val smsId: Long,

    @SerializedName("subject")
    val subject: String?,

    @SerializedName("updateBy")
    val updateBy: String?,

    @SerializedName("updatedOn")
    val updatedOn: String?,

    @SerializedName("createdOn")
    val createdOn: String?,

    @SerializedName("orderNo")
    val orderNo: String?,

    var messageType: Int
) : Serializable