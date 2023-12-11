package ymsli.com.cmsg.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ymsli.com.cmsg.database.dao.*
import ymsli.com.cmsg.database.entity.*

@Database(entities = [SMSAppEntity::class,MessageEntity::class,ServiceProviderEntity::class,PhoneSIMEntity::class,LocalSentMessageEntity::class], version = 1, exportSchema = false)
abstract class SMSAppDatabase: RoomDatabase() {
    companion object { const val DB_NAME = "SMS_APP_ROOM_DB" }
    abstract val smsAppDao: SMSAppDao
    abstract val messageDao: MessageDao
    abstract val serviceProviderDao: ServiceProviderDao
    abstract val phoneSIMDao: PhoneSIMDao
    abstract val localSentMessageDao: LocalSentMessageDao
}