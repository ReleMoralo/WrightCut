package za.co.wrightcut.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("preferredLanguage") val preferredLanguage: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class GoogleAuthRequest(
    @SerializedName("idToken") val idToken: String
)

data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("message") val message: String? = null
)
