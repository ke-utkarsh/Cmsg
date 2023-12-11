package ymsli.com.cmsg.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ymsli.com.cmsg.database.entity.ServiceProviderEntity

@Dao
interface ServiceProviderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServiceProviders(vararg serviceProviders: ServiceProviderEntity)

    @Query("Select * from service_provider")
    fun getServiceProviders(): List<ServiceProviderEntity>
}