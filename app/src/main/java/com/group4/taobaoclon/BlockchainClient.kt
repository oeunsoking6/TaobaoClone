package com.group4.taobaoclon

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BlockchainClient {
    // CRITICAL: Pointing to Port 8084 for the Blockchain Service
    private const val BASE_URL = "http://10.0.2.2:8084/"

    val instance: BlockchainApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(BlockchainApi::class.java)
    }
}