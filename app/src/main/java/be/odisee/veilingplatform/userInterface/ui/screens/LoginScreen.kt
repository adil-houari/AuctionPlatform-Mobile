package be.odisee.veilingplatform.userInterface.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.veilingplatform.R
import be.odisee.veilingplatform.data.SubApp
import be.odisee.veilingplatform.userInterface.ui.viewmodel.UserAuthenticationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userAuthViewModel: UserAuthenticationViewModel
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val authManager = (context.applicationContext as SubApp).authManager
    val coroutineScope = rememberCoroutineScope()
    val welcomeMessage by userAuthViewModel.welcomeMessage.collectAsState()


    fun handleLogin() {
        isLoading = true
        userAuthViewModel.login(username, password) { success, error ->
            isLoading = false
            if (success) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(userAuthViewModel.welcomeMessage.value)
                }
                coroutineScope.launch {
                    delay(1500)  // 1500 milliseconden vertraging
                    navController.navigate("itemsList")
                }
            } else {
                val errorMessage = error ?: "Onbekende fout opgetreden"
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = welcomeMessage,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(id = R.string.username_hint)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_hint)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { handleLogin() }) {
                Text(stringResource(id = R.string.login_button))
            }
            Button(onClick = {
                userAuthViewModel.logout()
                navController.navigate("login")
            }) {
                Text(stringResource(id = R.string.logout_button))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("itemsList")
            }) {
                Text(stringResource(id = R.string.to_auctions_button))
            }
        }
        }


    SnackbarHost(hostState = snackbarHostState)
}

//Preview, maar render probleem