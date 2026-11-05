package za.co.wrightcut.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ServiceEntity::class,
        BarberEntity::class,
        BookingEntity::class,
        GalleryEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WrightCutDatabase : RoomDatabase() {

    abstract fun serviceDao(): ServiceDao
    abstract fun barberDao(): BarberDao
    abstract fun bookingDao(): BookingDao
    abstract fun galleryDao(): GalleryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: WrightCutDatabase? = null

        fun getDatabase(context: Context): WrightCutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WrightCutDatabase::class.java,
                    "wrightcut_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
