package ymsli.com.cmsg.utils

enum class MessageStatusEnum(val status: String) {
    SENT("sent"),
    FAILED("failed"),
    PENDING("pending"),
    OPERATOR_NOT_SUPPORTED("unsupportedOperator")
}