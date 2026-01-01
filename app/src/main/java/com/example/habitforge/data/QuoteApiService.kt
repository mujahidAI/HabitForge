package com.example.habitforge.data

import retrofit2.http.GET

/**
 * QuoteResponse - Data model for quotes from ZenQuotes API
 *
 * Represents a single quote returned by the ZenQuotes API.
 * The API returns an array of quotes, but we only use the first one.
 *
 * JSON Structure:
 * ```json
 * [
 *   {
 *     "q": "Adventure is worthwhile in itself.",
 *     "a": "Amelia Earhart"
 *   }
 * ]
 * ```
 *
 * @property q The quote text (short for "quote")
 * @property a The author name (short for "author")
 */
data class QuoteResponse(
    val q: String,  // quote text
    val a: String   // author
)

/**
 * QuoteApiService - Retrofit interface for ZenQuotes API
 *
 * Defines the API endpoint for fetching random inspirational quotes.
 * The API is free and doesn't require authentication.
 *
 * Base URL: https://zenquotes.io/api/
 * Endpoint: /random
 *
 * API Documentation: https://zenquotes.io/
 */
interface QuoteApiService {
    /**
     * Fetches a random inspirational quote
     *
     * Returns a list containing a single quote object.
     * The list format is maintained to match the API response structure.
     *
     * Example Response:
     * ```json
     * [{"q": "Believe you can and you're halfway there.", "a": "Theodore Roosevelt"}]
     * ```
     *
     * @return List containing one QuoteResponse object
     * @throws IOException if network request fails
     */
    @GET("random")
    suspend fun getQuote(): List<QuoteResponse>
}
