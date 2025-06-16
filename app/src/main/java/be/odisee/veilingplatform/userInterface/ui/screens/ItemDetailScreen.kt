package be.odisee.veilingplatform.userInterface.ui.screens

import AuthManager
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.veilingplatform.R
import be.odisee.veilingplatform.model.Item
import be.odisee.veilingplatform.userInterface.ui.viewmodel.ItemsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemDetailScreen(navController: NavController, item: Item?, viewModel: ItemsViewModel,
                     authManager: AuthManager) {
    var bodBedrag by remember { mutableStateOf("") }

    val ingelogdeGebruikersnaam = authManager.getLastLoggedInUsername()
    val isLoggedIn = authManager.isLoggedIn()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.toastMessage = null
        }
    }

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            // Reset het bericht na tonen !!
            viewModel.toastMessage = null
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.item_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Terug")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            item?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Item Detail", style = MaterialTheme.typography.titleLarge)
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text(text = "Naam: ${it.name}", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Beschrijving: ${it.description}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Startprijs: ${it.startingPrice}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Startdatum: ${it.startDateTime}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Einddatum: ${it.endDateTime}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Categorie: ${it.category}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Hoogste bod: ${it.highestBid}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Verkoper: ${it.seller}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (it.seller.lowercase() == ingelogdeGebruikersnaam) {
                            Log.d("ItemDetailScreen", "Huidige gebruiker is de verkoper, toon de verwijderknop.")
                            Button(
                                onClick = {
                                    viewModel.cancelItem(item.id.toString())
                                    navController.navigate("itemsList")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.verwijderen), color = Color.White)
                            }

                        } else if (isLoggedIn) {
                            Log.d("ItemDetailScreen", "Huidige gebruiker is niet de verkoper, toon de bod invoer.")
                            OutlinedTextField(
                                value = bodBedrag,
                                onValueChange = { bodBedrag = it },
                                label = { Text("Bodbedrag") },
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Button(
                                onClick = {
                                    val bedrag = bodBedrag.toDoubleOrNull()
                                    bedrag?.let {
                                        viewModel.placeBid(item.id.toString(), it)
                                    } ?: run {
                                        viewModel.toastMessage = "Voer een geldig bod in"
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(stringResource(id = R.string.plaats_bid))

                            }
                        }

                        Button(
                            onClick = { navController.navigate("itemsList") },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(stringResource(id = R.string.terug_naar_items))

                        }
                    }
                }
            }
        }
    }
}



// render probleem
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemDetailTopBarPreview() {
    TopAppBar(
        title = { Text("Item Details") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Terug")
            }
        }
    )
}

