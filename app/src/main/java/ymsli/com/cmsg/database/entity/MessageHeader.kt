package ymsli.com.cmsg.database.entity

class MessageHeader(private var headerType: Int): MessageListItem() {

    fun getHeaderType(): Int = headerType

    override fun getType(): Int {
        return MessageListItem.TYPE_HEADER
    }
}