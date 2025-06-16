package be.odisee.veilingplatform.userInterface.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.veilingplatform.data.AuctionRepository
import be.odisee.veilingplatform.model.Item
import be.odisee.veilingplatform.network.AuctionApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ItemsViewModel(private val repository: AuctionRepository) : ViewModel() {
    private val _items = MutableStateFlow<Result<List<Item>>>(Result.success(emptyList()))
    val items: StateFlow<Result<List<Item>>> = _items.asStateFlow()

    val categories = MutableStateFlow<List<String>>(emptyList())

    private val _cancelItemStatus = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val cancelItemStatus: StateFlow<Result<Boolean>> = _cancelItemStatus.asStateFlow()
    var toastMessage by mutableStateOf<String?>(null)


    init {
        loadItems()
        loadCategories()
    }

    private fun loadCategories() {
        categories.value = listOf("Kunst en Antiek", "Elektronica", "Mode",
            "Sport en Vrije Tijd","Huis en Tuin", "Auto's en Voertuigen","Sieraden en Horloges","Boeken en Collectibles",
        "Onroerend Goed")

    }

    fun getFilteredItems(maxPrice: String, selectedCategory: String?) {
        viewModelScope.launch {
            val price = maxPrice.toDoubleOrNull()
            Log.d("ItemsViewModel", "Filtering items with category: $selectedCategory and max price: $maxPrice")
            val itemsResult = if (selectedCategory != null && price != null) {
                Log.d("ItemsViewModel", "Searching items with filters")
                repository.searchItems(listOf(selectedCategory), price)
            } else {
                Log.d("ItemsViewModel", "Getting all items without filters")
                repository.getItems()
            }

            _items.value = itemsResult
        }
    }

    fun loadItems() {
        viewModelScope.launch {
            val result = repository.getItems()
            _items.value = result.map { items ->
                items.sortedByDescending { it.startDateTime }
            }
        }
    }

    fun refreshItemsAndResetFilters() {
        resetFilters()
        loadItems()
    }

    fun resetFilters() {
        viewModelScope.launch {
            _items.value = repository.getItems()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addItem(
        name: String, description: String, startingPrice: Double,
        startDateTime: String, endDateTime: String, category: String
    ) {
        viewModelScope.launch {
            try {
                Log.d("ItemsViewModel", "Starting to add item: $name")
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val newItemRequest = AuctionApiService.NewItemRequest(
                    name = name,
                    description = description,
                    startingPrice = startingPrice,
                    startDateTime = startDateTime.format(formatter),
                    endDateTime = endDateTime.format(formatter),
                    category = category
                )

                val response = repository.addItem(newItemRequest)
                if (response.isSuccess) {
                    Log.d("ItemsViewModel", "Item succesvol toegevoegd")
                    loadItems()
                    toastMessage = "Item '$name' succesvol toegevoegd"
                } else {
                    Log.e("ItemsViewModel", "Fout bij het toevoegen van item")
                    toastMessage = "Fout bij het toevoegen van item: ${response.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("ItemsViewModel", "Exception bij het toevoegen van item", e)
                toastMessage = "Exception bij het toevoegen van item: ${e.message}"
            }
        }
    }



    fun cancelItem(itemId: String) {
        viewModelScope.launch {
            val result = repository.cancelItem(itemId)
            _cancelItemStatus.value = result
            if (result.isSuccess) {
                Log.d("ItemsViewModel", "Item succesvol geannuleerd")
                loadItems()
                toastMessage = "Item succesvol geannuleerd"
            } else {
                Log.e("ItemsViewModel", "Fout bij het annuleren van item: ${result.exceptionOrNull()?.message}")
                toastMessage = "Fout bij annuleren item: ${result.exceptionOrNull()?.message}"
            }
        }
    }




    fun placeBid(itemId: String, bidAmount: Double) {
        viewModelScope.launch {
            val result = repository.placeBidOnItem(itemId, bidAmount)
            if (result.isSuccess) {
                toastMessage = "Bod van $bidAmount geplaatst op item $itemId"
            } else {
                result.exceptionOrNull()?.let { exception ->

                    val errorMessage = if (exception.message?.contains("Biddings not started") == true) {
                        "Biedingen zijn nog niet gestart voor dit item."
                    } else {
                        "Fout bij het plaatsen van bod: ${exception.message}"
                    }
                    toastMessage = errorMessage
                }
            }
        }
    }
// == true ervoor zorgen dat de app niet crasht door een null-waarde en tegelijkertijd
// een specifieke foutmelding kan genereren indien de voorwaarde voldaan is.


}



