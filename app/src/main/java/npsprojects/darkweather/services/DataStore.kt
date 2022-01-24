package npsprojects.darkweather.services

import android.content.Context
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Database(entities = [(SavedLocation::class)], version = 1)
abstract  class LocationsDatabase: RoomDatabase() {
    abstract fun locationsDao(): LocationsDao

    companion object {

        private var INSTANCE: LocationsDatabase? = null

        internal fun getInstance(context: Context):LocationsDatabase? {
            if (INSTANCE == null) {
                synchronized(LocationsDatabase::class.java) {

                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            LocationsDatabase::class.java,
                            "Locations_db"
                        ).fallbackToDestructiveMigration()
                            .build()

                    }
                }

            }
            return INSTANCE
        }
    }
}

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg obj: T)

    @Update
    suspend fun update(vararg obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

}



@Dao
abstract class LocationsDao:BaseDao<SavedLocation> {

    @Query("Select * from Locations")
    abstract fun getAll():List<SavedLocation>


    @Query("DELETE FROM Locations")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM Locations WHERE latitude = :latitude AND longitude = :longitude")
    abstract suspend fun deleteWhere(latitude:Double,longitude: Double)

}

@Entity(tableName = "Locations")
data class SavedLocation (
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id:Long = 0L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double
)