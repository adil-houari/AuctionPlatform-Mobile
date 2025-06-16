package be.odisee.veilingplatform.userInterface.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.odisee.veilingplatform.data.AuctionRepository

class ItemsViewModelFactory(private val repository: AuctionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


// Dit zorgt voor mijn ViewModel, tis belangrijk
// omdat het de informatie over veilingitems beheert. Dit gebruikt een AuctionRepository,

// https://developer.android.com/reference/kotlin/androidx/lifecycle/ViewModelProvider.Factory