package be.odisee.veilingplatform.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuctionApiServiceFactory {
    private const val BASE_URL = "https://odiseeauction.azurewebsites.net/"

    fun create(): AuctionApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuctionApiService::class.java)
    }
}

// Dit heeft me geholpen
// https://www.delasign.com/blog/android-studio-kotlin-api-call/