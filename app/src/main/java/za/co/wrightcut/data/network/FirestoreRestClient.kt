
package za.co.wrightcut.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FirestoreRestClient {

    private const val BASE_URL =
        "https://firestore.googleapis.com/v1/projects/wrightcutbarbershop/databases/(default)/documents/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FirestoreRestApi =
        retrofit.create(FirestoreRestApi::class.java)
}
