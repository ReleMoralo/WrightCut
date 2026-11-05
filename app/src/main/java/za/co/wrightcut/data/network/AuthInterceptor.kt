package za.co.wrightcut.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authTokenManager: AuthTokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = authTokenManager.getToken()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}
