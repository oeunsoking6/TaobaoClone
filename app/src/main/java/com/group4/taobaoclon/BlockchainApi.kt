package com.group4.taobaoclon

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BlockchainApi {
    // 1. Get History (We used this for the Timeline)
    @GET("history/{id}")
    fun getProductHistory(@Path("id") productId: String): Call<List<HistoryItem>>

    // 2. Add History (We need this for the new Admin Screen)
    @POST("history")
    fun addHistory(@Body request: AddHistoryRequest): Call<Void>
}