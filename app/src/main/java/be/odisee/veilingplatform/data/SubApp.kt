package be.odisee.veilingplatform.data

import AuthManager
import android.app.Application

class SubApp : Application() {
    lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()
        authManager = AuthManager(this)
    }
}




// In mijn MyApplication klasse, die een subclass is van Application,
// initialiseer ik een globale instantie van AuthManager zodra de applicatie start.
// Dit gebruik ik om de authenticatiestatus en het JWT-token voor de hele app bij te houden en gemakkelijk toegankelijk te maken.
// Door deze aanpak zorg ik ervoor dat de authenticatiegegevens consistent en centraal beheerd worden binnen mijn applicatie
// En zorg ik ook dat ik geen fouten heb in mijn logcat en dat mijn app niet altijd crash als ik wil inloggen