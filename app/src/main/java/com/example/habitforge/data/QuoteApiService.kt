package com.example.habitforge.data

import retrofit2.http.GET

data class QuoteResponse(
    val q: String,  // quote text
    val a: String   // author
)

interface QuoteApiService {
    @GET("random")
    suspend fun getQuote(): List<QuoteResponse> // <- important, matches call in ViewModel
}
