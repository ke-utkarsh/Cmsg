package ymsli.com.cmsg.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "messages_table")
data class MessageEntity(
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
    var serviceProvider: String?,

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

    @SerializedName("failureReason")
    var failureReason: String? = null,

    var messageType: Int
): MessageListItem(),Serializable {

    override fun getType(): Int {
        return MessageListItem.TYPE_MESSAGE
    }
}

