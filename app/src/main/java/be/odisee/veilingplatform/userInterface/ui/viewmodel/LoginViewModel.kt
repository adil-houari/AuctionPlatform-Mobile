package be.odisee.veilingplatform.userInterface.ui.viewmodel

import AuthManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.veilingplatform.data.AuctionRepository
import be.odisee.veilingplatform.network.AuctionApiServiceFactory
import kotlinx.coroutines.launch

open class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val apiService = AuctionApiServiceFactory.create()
    private val repository = AuctionRepository(apiService, authManager)


    // misschien nog gebruiken
    open fun login(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val response = repository.login(username, password)
            if (response.isSuccess) {
                response.getOrNull()?.token?.let {
                    authManager.saveAuthToken(it)
                    onResult(true, null)
                } ?: onResult(false, "Failed to retrieve token")
            } else {
                onResult(false, "Login failed")
            }
        }
    }
}
