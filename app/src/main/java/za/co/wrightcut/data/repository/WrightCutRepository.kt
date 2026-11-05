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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import za.co.wrightcut.data.network.FirestoreRestClient
import com.google.gson.JsonObject
import com.google.firebase.auth.GoogleAuthProvider



class WrightCutRepository(context: Context) {
    private val db = WrightCutDatabase.getDatabase(context)
    private val api = ApiClient.getApiService(context)
    private val tokenManager = AuthTokenManager(context)

    //Firebase authentication

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    // AUTH


    suspend fun login(
        email: String,
        pass: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {

        try {
            // Authenticate the customer using Firebase
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email.trim(), pass)
                .await()

            val user = authResult.user
                ?: return@withContext Result.failure(
                    Exception("Login failed: Firebase user not found")
                )

            val uid = user.uid

            // Retrieve the customer's profile from Firestore
            val document = firestore.collection("customers")
                .document(uid)
                .get()
                .await()

            if (!document.exists()) {
                firebaseAuth.signOut()
                return@withContext Result.failure(
                    Exception("Customer profile not found")
                )
            }

            val fullName = document.getString("fullName")
                ?: email.substringBefore("@")

            val phone = document.getString("phone") ?: ""
            val language = document.getString("language") ?: "English"

            // Retrieve a valid Firebase ID token
            val idToken = user.getIdToken(false).await().token
                ?: return@withContext Result.failure(
                    Exception("Unable to retrieve Firebase token")
                )

            // Save the login information locally
            tokenManager.saveToken(idToken)
            tokenManager.saveUser(email.trim(), fullName)

            // Update the local customer profile
            db.userDao().insertUserProfile(
                UserProfileEntity(
                    1,
                    fullName,
                    email.trim(),
                    phone,
                    language
                )
            )

            Result.success(
                AuthResponse(
                    idToken,
                    1,
                    fullName,
                    email.trim()
                )
            )

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.localizedMessage ?: "Login failed. Check your credentials.",
                    e
                )
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
            // Create the customer account in Firebase Authentication
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email.trim(), pass)
                .await()

            val user = authResult.user
                ?: return@withContext Result.failure(
                    Exception("Firebase did not return a user")
                )

            val uid = user.uid

            // Store the customer's profile in Firestore
            val customerProfile = hashMapOf(
                "uid" to uid,
                "fullName" to fullName,
                "email" to email.trim(),
                "phone" to phone,
                "language" to lang
            )

            firestore.collection("customers")
                .document(uid)
                .set(customerProfile)
                .await()

            // Obtain a real Firebase ID token
            val idToken = user.getIdToken(false).await().token
                ?: return@withContext Result.failure(
                    Exception("Unable to retrieve Firebase token")
                )

            tokenManager.saveToken(idToken)
            tokenManager.saveUser(email.trim(), fullName)

            // Keep the customer's profile available locally
            db.userDao().insertUserProfile(
                UserProfileEntity(
                    1,
                    fullName,
                    email.trim(),
                    phone,
                    lang
                )
            )

            Result.success(
                AuthResponse(
                    idToken,
                    1,
                    fullName,
                    email.trim()
                )
            )

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.localizedMessage ?: "Registration failed",
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


    suspend fun getCustomerProfileViaRest(): Result<JsonObject> =
        withContext(Dispatchers.IO) {
            try {
                val user = firebaseAuth.currentUser
                    ?: return@withContext Result.failure(
                        Exception("Please log in first")
                    )

                val idToken = user.getIdToken(false).await().token
                    ?: return@withContext Result.failure(
                        Exception("Unable to get Firebase ID token")
                    )

                val response = FirestoreRestClient.api.getCustomerProfile(
                    authorization = "Bearer $idToken",
                    userId = user.uid
                )

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(
                        Exception(
                            "REST API request failed: HTTP ${response.code()}"
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(
                    Exception(
                        "REST API error: ${e.localizedMessage}",
                        e
                    )
                )
            }
        }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()

                val user = authResult.user
                    ?: return@withContext Result.failure(
                        Exception("Google authentication failed")
                    )

                val uid = user.uid
                val email = user.email ?: ""
                val fullName = user.displayName ?: email.substringBefore("@")

                val profileRef = firestore.collection("customers").document(uid)
                val existingProfile = profileRef.get().await()

                if (!existingProfile.exists()) {
                    val profile = hashMapOf(
                        "uid" to uid,
                        "fullName" to fullName,
                        "email" to email,
                        "phone" to "",
                        "language" to "English"
                    )

                    profileRef.set(profile).await()
                }

                val savedProfile = profileRef.get().await()
                val savedName = savedProfile.getString("fullName") ?: fullName
                val savedPhone = savedProfile.getString("phone") ?: ""
                val savedLanguage = savedProfile.getString("language") ?: "English"

                val firebaseToken = user.getIdToken(false).await().token
                    ?: return@withContext Result.failure(
                        Exception("Unable to retrieve Firebase token")
                    )

                tokenManager.saveToken(firebaseToken)
                tokenManager.saveUser(email, savedName)

                db.userDao().insertUserProfile(
                    UserProfileEntity(
                        1,
                        savedName,
                        email,
                        savedPhone,
                        savedLanguage
                    )
                )

                Result.success(
                    AuthResponse(firebaseToken, 1, savedName, email)
                )

            } catch (e: Exception) {
                Result.failure(
                    Exception(e.localizedMessage ?: "Google sign-in failed", e)
                )
            }
        }

}
