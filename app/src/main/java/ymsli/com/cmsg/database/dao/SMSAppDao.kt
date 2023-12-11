package ymsli.com.cmsg.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ymsli.com.cmsg.database.entity.SMSAppEntity

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 22, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * CMSGDao : Place holder dao for initial room database setup.
 * this will be removed once project development starts.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@Dao
interface SMSAppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(smsAppEntity: SMSAppEntity)

    @Query("SELECT * FROM smsappentity")
    suspend fun fetchAll(): List<SMSAppEntity>
}