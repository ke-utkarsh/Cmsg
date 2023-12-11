package ymsli.com.cmsg.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Project Name : CMSG
 * @company  YMSLI
 * @author   VE00YM023
 * @date     October 22, 2021
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * CMSGEntity : Place holder entity for initial room database setup.
 * this will be removed once project development starts.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@Entity
class MessageEntityResponseModel(

    @SerializedName("responseCode")
    val responseCode: String,

    @SerializedName("responseData")
    val responseData: List<MessageEntity>?,

    @SerializedName("responseMessage")
    val responseMessage: String
)