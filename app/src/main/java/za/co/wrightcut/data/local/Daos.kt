package za.co.wrightcut.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)
}

@Dao
interface BarberDao {
    @Query("SELECT * FROM barbers")
    suspend fun getAllBarbers(): List<BarberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarbers(barbers: List<BarberEntity>)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY date DESC, time DESC")
    suspend fun getAllBookings(): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun deleteBooking(bookingId: Int)
}

@Dao
interface GalleryDao {
    @Query("SELECT * FROM gallery")
    suspend fun getAllGalleryItems(): List<GalleryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItems(items: List<GalleryEntity>)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)
}
