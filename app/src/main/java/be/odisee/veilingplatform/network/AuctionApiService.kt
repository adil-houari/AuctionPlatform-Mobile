package be.odisee.veilingplatform.network

import be.odisee.veilingplatform.model.Bid
import be.odisee.veilingplatform.model.Item
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.*




interface AuctionApiService {

    @POST("api/Authentication/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<TokenResponse>

    @GET("api/auction/Items/search")
    suspend fun getItems(): List<Item>


    @GET("api/auction/Items/search")
    suspend fun searchItems(
        @Query("Categories") categories: List<String>?,
        @Query("MaxPrice") maxPrice: Double?
    ): List<Item>

    data class TokenResponse(
        val token: String,
        val expiration: String,
        val accountType: String
    )

    @POST("api/auction/Items")
    suspend fun addItem(@Header("Authorization") authToken: String, @Body newItemRequest: NewItemRequest): Response<Unit>
    data class NewItemRequest(
        val name: String,
        val description: String,
        val startingPrice: Double,
        val startDateTime: String,
        val endDateTime: String,
        val category: String
    )

    @DELETE("api/auction/Items/{itemId}/cancel")
    suspend fun cancelItem(@Header("Authorization") authHeaderValue: String, @Path("itemId") itemId: String): Response<Unit>

    @POST("/api/auction/Items/{itemId}/bids")
    suspend fun placeBid(@Header("Authorization") authToken: String, @Path("itemId") itemId: String, @Body bid: Bid): Response<Unit>

}

