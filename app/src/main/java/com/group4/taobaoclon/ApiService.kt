package com.group4.taobaoclon

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// --- Data Classes ---
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)
data class AddToCartRequest(val productId: Int, val quantity: Int)
data class HistoryEvent(val timestamp: Long, val description: String)
data class RegisterRequest(val email: String, val password: String)

// --- YOUR LIVE RENDER URLS (NOW CORRECTED) ---
private const val USER_SERVICE_URL = "https://user-service-677i.onrender.com/"
private const val PRODUCT_SERVICE_URL = "https://product-service-0v4l.onrender.com/"
private const val RECOMMENDATION_SERVICE_URL = "https://recommendation-service-ig7f.onrender.com/"
private const val CART_SERVICE_URL = "https://cart-service-y16i.onrender.com/" // <-- THE CORRECT URL
private const val BLOCKCHAIN_SERVICE_URL = "http://10.0.2.2:8084/" // Connects to local Ganache

// --- Retrofit Instances ---
private val userRetrofit = Retrofit.Builder().baseUrl(USER_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val productRetrofit = Retrofit.Builder().baseUrl(PRODUCT_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val recommendationRetrofit = Retrofit.Builder().baseUrl(RECOMMENDATION_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val cartRetrofit = Retrofit.Builder().baseUrl(CART_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()
private val blockchainRetrofit = Retrofit.Builder().baseUrl(BLOCKCHAIN_SERVICE_URL).addConverterFactory(GsonConverterFactory.create()).build()


interface ApiService {
    // --- User Service ---
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    // --- Product Service ---
    @GET("products")
    suspend fun getProducts(): List<Product>
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Product

    // --- Recommendation Service ---
    @GET("recommendations/{productId}")
    suspend fun getRecommendations(@Path("productId") productId: Int): List<Product>

    // --- Cart Service ---
    @GET("cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<List<CartItem>>

    @POST("cart")
    suspend fun addToCart(@Header("Authorization") token: String, @Body request: AddToCartRequest): Response<Unit>

    @retrofit2.http.HTTP(method = "DELETE", path = "cart", hasBody = true)
    suspend fun removeCartItem(@Header("Authorization") token: String, @Body request: AddToCartRequest): Response<Unit>

    // --- Blockchain Service ---
    @GET("history/{productId}")
    suspend fun getHistory(@Path("productId") productId: Int): Response<List<HistoryEvent>>
}

// --- Public API Client ---
object ApiClient {
    val userApiService: ApiService by lazy { userRetrofit.create(ApiService::class.java) }
    val productApiService: ApiService by lazy { productRetrofit.create(ApiService::class.java) }
    val recommendationApiService: ApiService by lazy { recommendationRetrofit.create(ApiService::class.java) }
    val cartApiService: ApiService by lazy { cartRetrofit.create(ApiService::class.java) }
    val blockchainApiService: ApiService by lazy { blockchainRetrofit.create(ApiService::class.java) }
}