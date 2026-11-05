package za.co.wrightcut.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.wrightcut.WrightCutApi
import java.util.concurrent.TimeUnit

object ApiClient {
    private var apiService: WrightCutApiService? = null

    fun getApiService(context: Context): WrightCutApiService {
        if (apiService == null) {
            val authTokenManager = AuthTokenManager(context.applicationContext)
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                // This line is changed to the line after it
                // level = HttpLoggingInterceptor.Level.BODY
                // level.BODY can log request and response bodies including login passwords and other sensitive information
                level = HttpLoggingInterceptor.Level.NONE
                // This disable HTTP loggin through that interceptor.
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(authTokenManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val baseUrl = if (WrightCutApi.baseUrl.startsWith("http")) {
                if (WrightCutApi.baseUrl.endsWith("/")) WrightCutApi.baseUrl else "${WrightCutApi.baseUrl}/"
            } else {
                "https://your-api.example.com/api/"
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(WrightCutApiService::class.java)
        }
        return apiService!!
    }
}
