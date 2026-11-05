package za.co.wrightcut.data.model

import com.google.gson.annotations.SerializedName

data class ServiceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("durationMinutes") val durationMinutes: Int = 30,
    @SerializedName("category") val category: String = "Cuts"
)

data class BarberDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("rating") val rating: Double = 5.0,
    @SerializedName("bio") val bio: String = ""
)

data class AvailabilityResponse(
    @SerializedName("barberId") val barberId: Int,
    @SerializedName("date") val date: String,
    @SerializedName("availableSlots") val availableSlots: List<String>
)

data class CreateBookingRequest(
    @SerializedName("barberId") val barberId: Int,
    @SerializedName("serviceId") val serviceId: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("galleryReferenceId") val galleryReferenceId: Int? = null
)

data class BookingResponse(
    @SerializedName("bookingId") val bookingId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("barber") val barber: String,
    @SerializedName("service") val service: String,
    @SerializedName("price") val price: Double,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String
)

data class UpdateBookingRequest(
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("status") val status: String? = null
)

data class GalleryItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String
)

data class UpdateUserRequest(
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("preferredLanguage") val preferredLanguage: String?,
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean?
)

data class FcmTokenRequest(
    @SerializedName("token") val token: String
)
