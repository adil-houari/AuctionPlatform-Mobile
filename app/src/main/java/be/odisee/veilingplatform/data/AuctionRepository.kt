package be.odisee.veilingplatform.data

import AuthManager
import android.util.Log
import be.odisee.veilingplatform.model.Bid
import be.odisee.veilingplatform.model.Item
import be.odisee.veilingplatform.network.LoginRequest
import be.odisee.veilingplatform.network.AuctionApiService
import be.odisee.veilingplatform.network.AuctionApiService.TokenResponse

import retrofit2.HttpException
import java.io.IOException

class AuctionRepository(
    private val apiService: AuctionApiService,
    private val authManager: AuthManager,

) {

    suspend fun getItems(): Result<List<Item>> = try {
        val response = apiService.getItems()
        Log.d("AuctionRepository", "Get items successful: $response")
        Result.success(response)
    } catch (e: Exception) {
        Log.e("AuctionRepository", "Get items failed", e)
        Result.failure(e)
    }

    suspend fun searchItems(categories: List<String>?, maxPrice: Double?): Result<List<Item>> {
        return try {
            Result.success(apiService.searchItems(categories, maxPrice))
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }


    suspend fun login(username: String, password: String): Result<TokenResponse> {
        return try {
            val loginRequest = LoginRequest(username, password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                val tokenResponse = response.body()!!
                authManager.saveAuthToken(tokenResponse.token)
                authManager.saveAccountType(tokenResponse.accountType)
                Result.success(tokenResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addItem(item: AuctionApiService.NewItemRequest): Result<Boolean> {

        val authToken = authManager.getAuthToken()
        if (authToken.isNullOrEmpty() || !authManager.isTokenValid()) {
            Log.e("AuctionRepository", "Geen auth token beschikbaar of token is verlopen")
            return Result.failure(Exception("Niet ingelogd of token is verlopen"))
        }
        val authHeaderValue = "Bearer $authToken"

        //Checken als alles correct is
        Log.d("AuctionRepository", "Item Naam: ${item.name}")
        Log.d("AuctionRepository", "Item Beschrijving: ${item.description}")
        Log.d("AuctionRepository", "Item Startprijs: ${item.startingPrice}")
        Log.d("AuctionRepository", "Item StartDatum: ${item.startDateTime}")
        Log.d("AuctionRepository", "Item EindDatum: ${item.endDateTime}")
        Log.d("AuctionRepository", "Item Categorie: ${item.category}")


        Log.d("AuctionRepository", "Verzenden item: $item")

        return try {
            val response = apiService.addItem(authHeaderValue, item)
            if (response.isSuccessful) {
                Log.d("AuctionRepository", "Item succesvol toegevoegd")
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Onbekende fout"
                Log.e("AuctionRepository", "Fout bij toevoegen item: $errorBody")
                Result.failure(Exception("Fout bij toevoegen item: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AuctionRepository", "Exception bij toevoegen item", e)
            Result.failure(e)
        }
    }


    suspend fun cancelItem(itemId: String): Result<Boolean> {
        val authToken = authManager.getAuthToken()
        if (authToken.isNullOrEmpty()) {
            return Result.failure(Exception("Geen geldige token beschikbaar"))
        }
        val authHeaderValue = "Bearer $authToken"
        return try {
            val response = apiService.cancelItem(authHeaderValue, itemId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Fout bij het annuleren van het item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeBidOnItem(itemId: String, bidAmount: Double): Result<Unit> {
        val authToken = authManager.getAuthToken()
        if (authToken.isNullOrEmpty()) {
            Log.d("AuctionRepository", "Niet ingelogd of token is verlopen")
            return Result.failure(Exception("Niet ingelogd of token is verlopen"))
        }
        return try {
            Log.d("AuctionRepository", "API call: plaats bod $bidAmount op item $itemId met token $authToken")
            val response = apiService.placeBid("Bearer $authToken", itemId, Bid(bidAmount))
            if (response.isSuccessful) {
                Log.d("AuctionRepository", "Bod succesvol geplaatst: ${response.body()}")
                Result.success(Unit)
            } else {
                val errorResponse = response.errorBody()?.string()
                Log.d("AuctionRepository", "Fout bij het plaatsen van bod: $errorResponse")
                Result.failure(Exception("Fout bij het plaatsen van bod: $errorResponse"))
            }
        } catch (e: Exception) {
            Log.e("AuctionRepository", "Exception bij API call: ${e.message}")
            Result.failure(e)
        }
    }



}

