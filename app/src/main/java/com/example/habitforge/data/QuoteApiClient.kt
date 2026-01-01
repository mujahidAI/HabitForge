package com.example.habitforge.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * QuoteApiClient - Singleton Retrofit client for ZenQuotes API
 *
 * Provides a configured Retrofit instance for making HTTP requests to the ZenQuotes API.
 * Uses the Singleton pattern to ensure only one Retrofit instance exists throughout the app.
 *
 * Configuration:
 * - Base URL: https://zenquotes.io/api/
 * - Converter: Gson (for JSON serialization/deserialization)
 * - HTTP Client: OkHttp (default)
 *
 * Why Singleton?
 * - Retrofit instances are expensive to create
 * - Reuses connection pool and thread pool
 * - Ensures consistent configuration across the app
 *
 * Usage:
 * ```kotlin
 * val quotes = QuoteApiClient.api.getQuote()
 * ```
 */
object QuoteApiClient {

    /**
     * Configured Retrofit instance
     *
     * Built once and reused for all API calls.
     * Automatically converts JSON responses to Kotlin objects using Gson.
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/api/")  // ZenQuotes API base URL
        .addConverterFactory(GsonConverterFactory.create())  // JSON converter
        .build()

    /**
     * QuoteApiService instance for making API calls
     *
     * Provides access to all API endpoints defined in QuoteApiService.
     * This is the main entry point for fetching quotes.
     */
    val api: QuoteApiService = retrofit.create(QuoteApiService::class.java)
}
