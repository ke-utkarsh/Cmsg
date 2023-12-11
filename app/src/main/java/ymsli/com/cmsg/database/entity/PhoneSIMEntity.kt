package ymsli.com.cmsg.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_sim_table")
data class PhoneSIMEntity (
    @PrimaryKey
    val slot: Int,

    val carrierName: String,

    val subscriptionId: Int
    )