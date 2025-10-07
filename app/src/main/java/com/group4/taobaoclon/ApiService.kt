package com.group4.taobaoclon

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Define the data classes for login request and response
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)


// Base URLs for our services
private const val USER_SERVICE_URL = "http://10.0.2.2:8080/"
private const val PRODUCT_SERVICE_URL = "http://10.0.2.2:8081/"
private const val RECOMMENDATION_SERVICE_URL = "http://10.0.2.2:8082/"

// Create Retrofit instances for each service
private val userRetrofit = Retrofit.Builder().baseUrl(USER_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val productRetrofit = Retrofit.Builder().baseUrl(PRODUCT_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val recommendationRetrofit = Retrofit.Builder().baseUrl(RECOMMENDATION_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()


interface ApiService {
    // --- User Service functions ---
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // --- Product Service functions ---
    @GET("products")
    suspend fun getProducts(): List<Product>

    // --- Recommendation Service functions ---
    @GET("recommendations/{productId}")
    suspend fun getRecommendations(@Path("productId") productId: Int): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Product
}

// Public object to access our services
object ApiClient {
    val userApiService: ApiService by lazy { userRetrofit.create(ApiService::class.java) }
    val productApiService: ApiService by lazy { productRetrofit.create(ApiService::class.java) }
    val recommendationApiService: ApiService by lazy { recommendationRetrofit.create(ApiService::class.java) }
}