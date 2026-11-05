package za.co.wrightcut.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import za.co.wrightcut.data.local.BarberEntity
import za.co.wrightcut.data.local.BookingEntity
import za.co.wrightcut.data.local.GalleryEntity
import za.co.wrightcut.data.local.ServiceEntity
import za.co.wrightcut.data.local.UserProfileEntity
import za.co.wrightcut.data.local.WrightCutDatabase
import za.co.wrightcut.data.model.AuthResponse
import za.co.wrightcut.data.model.CreateBookingRequest
import za.co.wrightcut.data.model.LoginRequest
import za.co.wrightcut.data.model.RegisterRequest
import za.co.wrightcut.data.model.UpdateBookingRequest
import za.co.wrightcut.data.model.UpdateUserRequest
import za.co.wrightcut.data.network.ApiClient
import za.co.wrightcut.data.network.AuthTokenManager

class WrightCutRepository(context: Context) {
    private val db = WrightCutDatabase.getDatabase(context)
    private val api = ApiClient.getApiService(context)
    private val tokenManager = AuthTokenManager(context)

    // AUTH

    suspend fun login(email: String, pass: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, pass))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    val token = body.token

                    if (token.isNullOrBlank()) {
                        return@withContext Result.failure(
                            Exception("Login failed: No authentication token received")
                        )
                    }

                    val fullName = body.fullName ?: email.substringBefore("@")

                    tokenManager.saveToken(token)
                    tokenManager.saveUser(email, fullName)

                    db.userDao().insertUserProfile(
                        UserProfileEntity(
                            1,
                            fullName,
                            email,
                            "071 234 5678",
                            "English"
                        )
                    )

                    Result.success(body)

                } else {
                    Result.failure(
                        Exception("Login failed. Check your email and password.")
                    )
                }

            } catch (e: Exception) {
                Result.failure(
                    Exception("Unable to connect to the server: ${e.message}", e)
                )
            }
        }



    // Replaced the whole register code to this code
    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        pass: String,
        lang: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {

        try {
            val response = api.register(
                RegisterRequest(fullName, email, phone, pass, lang)
            )

            if (response.isSuccessful && response.body() != null) {

                val body = response.body()!!

                val token = body.token

                if (token.isNullOrBlank()) {
                    return@withContext Result.failure(
                        Exception("Registration failed: No authentication token received")
                    )
                }

                tokenManager.saveToken(token)
                tokenManager.saveUser(email, fullName)

                db.userDao().insertUserProfile(
                    UserProfileEntity(
                        1,
                        fullName,
                        email,
                        phone,
                        lang
                    )
                )

                Result.success(body)

            } else {
                Result.failure(
                    Exception(
                        "Registration failed. Server returned ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "Registration could not be completed: ${e.message}",
                    e
                )
            )
        }
    }


    // SERVICES
    suspend fun getServices(): List<ServiceEntity> = withContext(Dispatchers.IO) {
        var localServices = db.serviceDao().getAllServices()
        if (localServices.isEmpty()) {
            val defaultServices = listOf(
                ServiceEntity(1, "Fade Only", 100.0, 45, "Cuts"),
                ServiceEntity(2, "Fade + Black Dye", 150.0, 60, "Cuts"),
                ServiceEntity(3, "Chiskop (Clipper)", 80.0, 30, "Cuts"),
                ServiceEntity(4, "Chiskop (Blade)", 100.0, 40, "Cuts"),
                ServiceEntity(5, "Clipper Shave", 80.0, 25, "Shaves"),
                ServiceEntity(6, "Blade Shave", 100.0, 30, "Shaves"),
                ServiceEntity(7, "Beard Black Colour", 80.0, 25, "Shaves"),
                ServiceEntity(8, "Big Design", 150.0, 60, "Designs"),
                ServiceEntity(9, "Trim", 50.0, 20, "Cuts")
            )
            db.serviceDao().insertServices(defaultServices)
            localServices = defaultServices
        }

        try {
            val response = api.getServices()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val apiEntities = response.body()!!.map {
                    ServiceEntity(it.id, it.name, it.price, it.durationMinutes, it.category)
                }
                db.serviceDao().insertServices(apiEntities)
                return@withContext apiEntities
            }
        } catch (_: Exception) {}

        return@withContext localServices
    }

    // BARBERS
    suspend fun getBarbers(): List<BarberEntity> = withContext(Dispatchers.IO) {
        var localBarbers = db.barberDao().getAllBarbers()
        if (localBarbers.isEmpty()) {
            val defaultBarbers = listOf(
                BarberEntity(1, "KG", "Senior Barber", 4.9, "Master of fade cuts and hair art."),
                BarberEntity(2, "Boikhutso", "Master Stylist", 4.8, "Specialist in razor shaves and beard design.")
            )
            db.barberDao().insertBarbers(defaultBarbers)
            localBarbers = defaultBarbers
        }

        try {
            val response = api.getBarbers()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val apiEntities = response.body()!!.map {
                    BarberEntity(it.id, it.name, it.title, it.rating, it.bio)
                }
                db.barberDao().insertBarbers(apiEntities)
                return@withContext apiEntities
            }
        } catch (_: Exception) {}

        return@withContext localBarbers
    }

    // BOOKINGS
    suspend fun getBookings(): List<BookingEntity> = withContext(Dispatchers.IO) {
        val localBookings = db.bookingDao().getAllBookings()

        try {
            val response = api.getMyBookings()
            if (response.isSuccessful && response.body() != null) {
                val apiBookings = response.body()!!.map {
                    BookingEntity(it.bookingId, it.status, it.barber, it.service, it.price, it.date, it.time)
                }
                db.bookingDao().insertBookings(apiBookings)
                return@withContext apiBookings
            }
        } catch (_: Exception) {}

        return@withContext localBookings
    }

    suspend fun createBooking(barberId: Int, serviceId: Int, date: String, time: String): Result<BookingEntity> = withContext(Dispatchers.IO) {
        val allServices = getServices()
        val foundService = allServices.find { it.id == serviceId }
        val barberName = if (barberId == 1) "KG" else "Boikhutso"
        val serviceName = foundService?.name ?: "Fade Only"
        val price = foundService?.price ?: 100.0

        try {
            val response = api.createBooking(CreateBookingRequest(barberId, serviceId, date, time))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val entity = BookingEntity(body.bookingId, body.status, body.barber, body.service, body.price, body.date, body.time)
                db.bookingDao().insertBooking(entity)
                return@withContext Result.success(entity)
            }
        } catch (_: Exception) {}

        val newBookingId = (100..999).random()
        val localEntity = BookingEntity(newBookingId, "Confirmed", barberName, serviceName, price, date, time)
        db.bookingDao().insertBooking(localEntity)
        Result.success(localEntity)
    }

    suspend fun cancelBooking(bookingId: Int) = withContext(Dispatchers.IO) {
        try {
            api.updateBooking(bookingId, UpdateBookingRequest(status = "Cancelled"))
        } catch (_: Exception) {}
        db.bookingDao().deleteBooking(bookingId)
    }

    // GALLERY
    suspend fun getGallery(): List<GalleryEntity> = withContext(Dispatchers.IO) {
        var localItems = db.galleryDao().getAllGalleryItems()
        if (localItems.isEmpty()) {
            val defaultItems = listOf(
                GalleryEntity(1, "High Skin Fade with Lineup", "Fades", "http://example.com/fade.jpg"),
                GalleryEntity(2, "Custom Razor Hair Design", "Designs", "http://example.com/design.jpg"),
                GalleryEntity(3, "Beard Sculpt & Dye", "Contour", "http://example.com/beard.jpg")
            )
            db.galleryDao().insertGalleryItems(defaultItems)
            localItems = defaultItems
        }
        return@withContext localItems
    }

    // USER PROFILE
    suspend fun getUserProfile(): UserProfileEntity = withContext(Dispatchers.IO) {
        val profile = db.userDao().getUserProfile()
            ?: UserProfileEntity(1, tokenManager.getUserName(), tokenManager.getUserEmail(), "071 234 5678", "English")
        return@withContext profile
    }

    suspend fun updateUserProfile(fullName: String, lang: String, notifications: Boolean) = withContext(Dispatchers.IO) {
        val email = tokenManager.getUserEmail()
        val updated = UserProfileEntity(1, fullName, email, "071 234 5678", lang, notifications)
        db.userDao().insertUserProfile(updated)
        tokenManager.saveUser(email, fullName)

        try {
            api.updateUserProfile(UpdateUserRequest(fullName, lang, notifications))
        } catch (_: Exception) {}
    }
}
