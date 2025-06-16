package be.odisee.veilingplatform.userInterface.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import be.odisee.veilingplatform.R
import kotlinx.coroutines.delay


//In de NoConnectionScreen composable toon ik een afbeelding en tekst die aangeven dat er geen internetverbinding is,
// samen met een knop waarmee gebruikers opnieuw kunnen proberen verbinding te maken
@Composable
fun NoConnectionScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.no_connection)
        )
        Text(text = stringResource(R.string.no_connection), modifier = Modifier.padding(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry_button))
        }
    }
}


//Mijn checkForInternet functie controleert de werkelijke netwerkstatus door de netwerkcapaciteiten van het apparaat te raadplegen,
// zoals WIFI, mobiele data of ethernet,
// om te bepalen of er een actieve verbinding is.
fun checkForInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return true
        }
    }
    return false
}