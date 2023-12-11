package ymsli.com.cmsg.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "service_provider")
data class ServiceProviderEntity(

    @PrimaryKey
    @SerializedName("simPrefix")
    val simPrefix: String,

    @SerializedName("serviceProvider")
    val serviceProvider: String?

)