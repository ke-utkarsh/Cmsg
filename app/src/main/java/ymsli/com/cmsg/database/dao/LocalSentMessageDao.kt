package ymsli.com.cmsg.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ymsli.com.cmsg.database.entity.LocalSentMessageEntity


@Dao
interface LocalSentMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessageInDB(messages: LocalSentMessageEntity)

    @Query("Select COUNT(*) from local_sent_message_table")
    fun getSentMessagesOfflineCount(): LiveData<Long>

    @Query("Select * from local_sent_message_table ORDER BY createdOn DESC")
    fun getMessagesOfType(): LiveData<List<LocalSentMessageEntity>>

    @Query("Delete from local_sent_message_table")
    fun cleanMessageOfType()
}