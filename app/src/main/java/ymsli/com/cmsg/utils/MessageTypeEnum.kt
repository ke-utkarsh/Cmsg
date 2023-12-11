package ymsli.com.cmsg.utils

enum class MessageTypeEnum(val messageType: Int) {
    PENDING(1),
    SENT_OFFLINE(2),
    SENT_API(3),
    DELIVERED(4),
    NOT_DELIVERED(5)
}