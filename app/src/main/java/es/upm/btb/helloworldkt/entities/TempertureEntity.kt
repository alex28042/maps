package es.upm.btb.helloworldkt.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TempertureEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val city: String
)
