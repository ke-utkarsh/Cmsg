package ymsli.com.cmsg.model

data class SentMessageDashboardModel(

    val carrierName: String?,

    val totalMessages: Int?,

    val deliveredMessages: Int?,

    val undeliveredMessages: Int?
)