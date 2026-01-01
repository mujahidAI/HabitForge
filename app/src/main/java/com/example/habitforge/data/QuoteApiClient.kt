package com.example.habitforge.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QuoteApiClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: QuoteApiService = retrofit.create(QuoteApiService::class.java)
}
