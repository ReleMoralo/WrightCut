package za.co.wrightcut.data.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface FirestoreRestApi {

    @GET("customers/{userId}")
    suspend fun getCustomerProfile(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String
    ): Response<JsonObject>
}
