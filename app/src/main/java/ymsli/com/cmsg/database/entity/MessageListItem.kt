package ymsli.com.cmsg.database.entity

abstract class MessageListItem {

    companion object{
        const val TYPE_HEADER = 0
        const val TYPE_MESSAGE = 1
    }

    abstract fun getType():Int
}