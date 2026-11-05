package za.co.wrightcut.data.network

import android.content.Context
import android.content.SharedPreferences

class AuthTokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("wrightcut_auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }

    fun saveUser(email: String, fullName: String) {
        prefs.edit().putString("user_email", email).putString("user_name", fullName).apply()
    }

    fun getUserEmail(): String {
        return prefs.getString("user_email", "lerato@gmail.com") ?: "lerato@gmail.com"
    }

    fun getUserName(): String {
        return prefs.getString("user_name", "Lerato Mokoena") ?: "Lerato Mokoena"
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
