package be.odisee.veilingplatform.userInterface.ui.viewmodel

import AuthManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.odisee.veilingplatform.network.AuctionApiService

class UserAuthViewModelFactory(
    private val authManager: AuthManager,
    private val apiService: AuctionApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAuthenticationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserAuthenticationViewModel(authManager, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


//Mijn UserAuthViewModelFactory is eigenlijk een slimme tool in mijn app die helpt bij het maken voor,
// serAuthenticationViewModel. Deze ViewModel is belangrijk omdat het helpt met gebruikersinformatie en inloggen.
// Wanneer mijn app een UserAuthenticationViewModel nodig heeft, gaat het naar deze fabriek,


//mijn bron:
// https://developer.android.com/reference/kotlin/androidx/lifecycle/ViewModelProvider.Factory