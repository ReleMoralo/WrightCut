package za.co.wrightcut.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val category: String
)

@Entity(tableName = "barbers")
data class BarberEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val title: String,
    val rating: Double,
    val bio: String
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: Int,
    val status: String,
    val barber: String,
    val service: String,
    val price: Double,
    val date: String,
    val time: String
)

@Entity(tableName = "gallery")
data class GalleryEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val imageUrl: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val phone: String,
    val preferredLanguage: String,
    val notificationsEnabled: Boolean = true
)
