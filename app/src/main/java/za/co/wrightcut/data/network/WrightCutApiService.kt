package za.co.wrightcut.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import za.co.wrightcut.data.model.AuthResponse
import za.co.wrightcut.data.model.AvailabilityResponse
import za.co.wrightcut.data.model.BarberDto
import za.co.wrightcut.data.model.BookingResponse
import za.co.wrightcut.data.model.CreateBookingRequest
import za.co.wrightcut.data.model.FcmTokenRequest
import za.co.wrightcut.data.model.GalleryItemDto
import za.co.wrightcut.data.model.GoogleAuthRequest
import za.co.wrightcut.data.model.LoginRequest
import za.co.wrightcut.data.model.RegisterRequest
import za.co.wrightcut.data.model.ServiceDto
import za.co.wrightcut.data.model.UpdateBookingRequest
import za.co.wrightcut.data.model.UpdateUserRequest

interface WrightCutApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleAuthRequest): Response<AuthResponse>

    @GET("services")
    suspend fun getServices(): Response<List<ServiceDto>>

    @GET("barbers")
    suspend fun getBarbers(): Response<List<BarberDto>>

    @GET("barbers/{id}/availability")
    suspend fun getBarberAvailability(
        @Path("id") barberId: Int,
        @Query("date") date: String
    ): Response<AvailabilityResponse>

    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<BookingResponse>

    @GET("bookings/me")
    suspend fun getMyBookings(): Response<List<BookingResponse>>

    @PUT("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: Int,
        @Body request: UpdateBookingRequest
    ): Response<BookingResponse>

    @GET("gallery")
    suspend fun getGallery(@Query("category") category: String? = null): Response<List<GalleryItemDto>>

    @PUT("users/me")
    suspend fun updateUserProfile(@Body request: UpdateUserRequest): Response<Unit>

    @POST("notifications/register-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<Unit>
}
