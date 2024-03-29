package es.upm.btb.helloworldkt.database
import androidx.room.Database
import androidx.room.RoomDatabase
import es.upm.btb.helloworldkt.entities.LocationEntity
import es.upm.btb.helloworldkt.repositories.ILocationDao

@Database(entities = [LocationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): ILocationDao
}
