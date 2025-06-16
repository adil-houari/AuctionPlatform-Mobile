package be.odisee.veilingplatform.userInterface.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import be.odisee.veilingplatform.userInterface.ui.NavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRootScreen() {
    val context = LocalContext.current
    val isConnected = remember { mutableStateOf(checkForInternet(context)) }
    val retryKey = remember { mutableStateOf(0) }

    LaunchedEffect(retryKey.value) {
        isConnected.value = checkForInternet(context)
    }

    if (isConnected.value) {
        NavGraph() // Als er verbinding is, toon dan de app
    } else {
        NoConnectionScreen(onRetry = { retryKey.value = retryKey.value + 1 })
    }
}

//Wanneer de app start, controleert het de netwerkverbinding. Als er geen internetverbinding is,
// toont het een scherm (NoConnectionScreen) met een bericht dat er geen verbinding is en een knop om opnieuw te proberen verbinding te maken.
//Als er wel verbinding is, gaat de app door naar het hoofdscherm van de app (NavGraph)

// Het was gemakkelijker voor mij om zo te doen