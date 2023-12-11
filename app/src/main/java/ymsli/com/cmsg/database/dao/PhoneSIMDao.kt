package ymsli.com.cmsg.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ymsli.com.cmsg.database.entity.PhoneSIMEntity

@Dao
interface PhoneSIMDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg simInfo: PhoneSIMEntity)

    @Query("Delete from phone_sim_table")
    fun deleteSIMInfo()

    @Query("Select * from phone_sim_table")
    fun getSIMInfo(): List<PhoneSIMEntity>
}