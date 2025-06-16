import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)


    fun saveAuthToken(token: String) {
        val expiryTime = System.currentTimeMillis() + 3 * 60 * 60 * 1000 // 3 uur in milliseconden
        prefs.edit().putString("auth_token", token)
            .putLong("token_expiry_time", expiryTime)
            .apply()
    }


    fun isTokenValid(): Boolean {
        val tokenExpiryTime = prefs.getLong("token_expiry_time", 0)
        return System.currentTimeMillis() < tokenExpiryTime
    }


    fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun clearAuthToken() {
        prefs.edit().remove("auth_token").apply()
    }

    fun logout() {
        clearAuthToken()
        prefs.edit().remove("last_logged_in_username").apply()

    }

    fun saveLastLoggedInUsername(username: String) {
        prefs.edit().putString("last_logged_in_username", username).apply()
    }

    fun getLastLoggedInUsername(): String? {
        return prefs.getString("last_logged_in_username", null)
    }

    fun getUserAccountType(): String {
        return prefs.getString("account_type", "Free") ?: "Free"
    }
    fun saveAccountType(accountType: String) {
        prefs.edit().putString("account_type", accountType).apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

}


