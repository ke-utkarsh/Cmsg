package ymsli.com.cmsg.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ymsli.com.cmsg.database.entity.MessageEntity

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessageInDB(vararg messages: MessageEntity)

    @Query("Select COUNT(*) from messages_table where messageType =:messageType")
    fun getMessageTypeCount(messageType: Int): LiveData<Long>

    @Query("Select * from messages_table where messageType =:messageType ORDER BY createdOn DESC")
    fun getMessagesOfType(messageType: Int): LiveData<List<MessageEntity>>

    @Query("Select * from messages_table where messageType =:messageType AND messageStatus LIKE :messageStatus ORDER BY createdOn DESC")
    fun getPendingMessages(messageStatus: String,messageType: Int): LiveData<List<MessageEntity>>

    @Query("Select COUNT(*) from messages_table where messageType =:messageType AND messageStatus =:messageStatus")
    fun getPendingMessagesCount(messageStatus: String,messageType: Int): LiveData<Long>

    @Query("Delete from messages_table where messageType =:messageType")
    fun cleanMessageOfType(messageType: Int)

    @Query("Select * from messages_table where messageType =:messageType AND serviceProvider LIKE :provider")
    fun getMessagesOfProvider(messageType: Int,provider: String): LiveData<List<MessageEntity>>

    @Query("Update messages_table SET messageStatus =:messageStatus WHERE smsId = :smsId")
    fun updateMessageStatus(smsId: Long,messageStatus: String): Int

    @Query("Select COUNT(*) from messages_table WHERE messageStatus =:messageStatus")
    fun messageCountByStatus(messageStatus: String):LiveData<Long>

    @Query("Select * from messages_table WHERE messageStatus =:messageStatus")
    fun getMessagesOfStatus(messageStatus: String): LiveData<List<MessageEntity>>

    @Query("Select * from messages_table WHERE messageStatus =:messageStatus")
    fun getMessagesOfStatusList(messageStatus: String): List<MessageEntity>

    @Query("Select * from messages_table WHERE smsId =:smsId")
    fun getSpecificMessage(smsId: Long): MessageEntity

    @Query("Delete from messages_table where smsId =:smsId")
    fun deleteMessage(smsId: Long)

    @Query("Update messages_table SET serviceProvider =:serviceProvider WHERE smsId = :smsId")
    fun updateServiceProvider(smsId: Long,serviceProvider: String)

    @Query("Update messages_table SET failureReason =:failureReason WHERE smsId = :smsId")
    fun updateFailureReason(smsId: Long,failureReason: String)

    @Query("Delete from messages_table")
    fun clearMessages()
}