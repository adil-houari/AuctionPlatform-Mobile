package be.odisee.veilingplatform.userInterface.ui.viewmodel

import AuthManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import be.odisee.veilingplatform.network.AuctionApiService
import android.util.Log
import be.odisee.veilingplatform.network.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserAuthenticationViewModel(
    private val authManager: AuthManager,
    private val apiService: AuctionApiService
) : ViewModel() {

    private val _loginStatus = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val loginStatus: StateFlow<Pair<Boolean, String?>> = _loginStatus.asStateFlow()
    private val _welcomeMessage = MutableStateFlow("Welkom, log in a.u.b.")
    val welcomeMessage: StateFlow<String> = _welcomeMessage.asStateFlow()

    fun login(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("LoginDebug", "Attempting login for user: $username")
                val loginRequest = LoginRequest(username, password)
                val response = apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    authManager.saveAuthToken(loginResponse.token)
                    authManager.saveAccountType(loginResponse.accountType)
                    authManager.saveLastLoggedInUsername(username)
                    _welcomeMessage.value = "Welkom terug, $username!"
                    onResult(true, null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when {
                        response.code() == 401 -> "Ongeldige gebruikersnaam of wachtwoord."
                        response.code() == 404 -> "Gebruikersaccount niet gevonden."
                        else -> "Er is een fout opgetreden bij het inloggen. Probeer het opnieuw."
                    }
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                Log.e("LoginDebug", "Login error: ${e.message}")
                onResult(false, e.message ?: "Onbekende fout opgetreden")
            }
        }
    }

    init {
        if (authManager.isLoggedIn() && authManager.isTokenValid()) {
            val username = authManager.getLastLoggedInUsername() ?: "Gebruiker"
            _welcomeMessage.value = "Welkom terug, $username!"
        }
    }

    fun logout() {
        authManager.logout()
        _welcomeMessage.value = "Welkom, log in a.u.b."
    }
}




