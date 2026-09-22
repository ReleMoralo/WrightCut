package za.co.wrightcut

/** Kotlin endpoint contract for the Wright Cut backend. Replace baseUrl with your hosted API. */
object WrightCutApi {
    const val baseUrl = "https://your-api.example.com/api"
    const val register = "/auth/register"
    const val login = "/auth/login"
    const val googleSignIn = "/auth/google"
    const val services = "/services"
    const val barbers = "/barbers"
    const val availability = "/availability"
    const val bookings = "/bookings"
    const val gallery = "/gallery"
    const val profile = "/profile"
    const val fcmToken = "/notifications/token"
    const val loyalty = "/loyalty"
} 
