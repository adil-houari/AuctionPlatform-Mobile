package be.odisee.veilingplatform.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Item(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("startingPrice") val startingPrice: Double,
    @SerializedName("startDateTime") val startDateTime: String,
    @SerializedName("endDateTime") val endDateTime: String,
    @SerializedName("category") val category: String,
    @SerializedName("highestBid") val highestBid: Double,
    @SerializedName("seller") val seller: String
)


