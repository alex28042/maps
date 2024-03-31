package es.upm.btb.helloworldkt.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.upm.btb.helloworldkt.entities.TempertureEntity

@Dao
interface ITempertureDao {
    @Insert
    fun insertTemperture(tempertureEntity: TempertureEntity);

    @Query("SELECT * FROM TempertureEntity")
    fun getAllTempertures(): List<TempertureEntity>
}