package be.odisee.veilingplatform.userInterface.ui.screens

import AuthManager
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import be.odisee.veilingplatform.model.Item
import androidx.navigation.NavController
import be.odisee.veilingplatform.R
import be.odisee.veilingplatform.userInterface.ui.viewmodel.ItemsViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsListScreen(navController: NavController, viewModel: ItemsViewModel, authManager: AuthManager) {
    var maxPrice by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val categories by viewModel.categories.collectAsState()
    val enableButton = maxPrice.isNotBlank() && selectedCategory != null
    val context = LocalContext.current
    val itemsState = viewModel.items.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLoginPrompt by remember { mutableStateOf(false) }
    val isLoggedIn = remember { mutableStateOf(authManager.isTokenValid()) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.loginlogo),
                            contentDescription = "Login Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    Log.d("ItemsListScreen", "Navigating to login")
                                    navController.navigate("login"){

                                    }
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.app_name))
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.veiling_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(90.dp)
                    )
                },

            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = PaddingValues(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryDropdownMenu(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = category
                        },
                        categories = categories
                    )
                }

                item {
                    MaxPriceInput(
                        maxPrice = maxPrice,
                        onMaxPriceChanged = { maxPrice = it }
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (enableButton) {
                                viewModel.getFilteredItems(maxPrice, selectedCategory)
                            } else {
                                Toast.makeText(context, "Selecteer beide filters om te zoeken.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = enableButton,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Text(stringResource(id = R.string.apply_filter), color = if (enableButton) Color.White else Color.Gray)
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.resetFilters()
                            maxPrice = ""
                            selectedCategory = null
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Text(stringResource(id = R.string.reset_filters), color = Color.White)
                    }
                }

                item {
                    // Gebruik de functies van AuthManager om de ingelogde status te controleren
                    if (authManager.isLoggedIn() && authManager.isTokenValid()) {
                        Button(
                            onClick = { navController.navigate("newItem") },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Text(stringResource(id = R.string.add_new_item), color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { showLoginPrompt = true },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Text(stringResource(id = R.string.add_new_item_disabled), color = Color.Gray)
                        }
                    }
                }

                itemsState.value.let { result ->
                    when {
                        result.isSuccess -> {
                            val items = result.getOrNull() ?: emptyList()
                            items(items) { item ->
                                ItemRow(item) { selectedItem ->
                                    navController.navigate("itemDetail/${selectedItem.id}")
                                }
                            }
                        }
                        result.isFailure -> {
                            item {
                                Text(stringResource(id = R.string.error_fetching_items))
                            }
                        }
                        else -> {
                            item {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            LaunchedEffect(showLoginPrompt) {
                if (showLoginPrompt) {
                    Toast.makeText(context, "Je moet eerst inloggen om een item toe te voegen.", Toast.LENGTH_LONG).show()
                    showLoginPrompt = false
                }
            }
        }
        ,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.refreshItemsAndResetFilters()
                maxPrice = ""
                selectedCategory = null
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
fun ItemRow(item: Item, onItemClick: (Item) -> Unit) {
    Card(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(item) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Startprijs: ${item.startingPrice}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Categorie: ${item.category}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CategoryDropdownMenu(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    categories: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Text(
            text = selectedCategory ?: stringResource(id = R.string.select_category),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .clickable { expanded = !expanded }
                .padding(16.dp),
            color = Color.Black
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                ) {
                    Text(text = category)
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MaxPriceInput(
    maxPrice: String,
    onMaxPriceChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = maxPrice,
        onValueChange = onMaxPriceChanged,
        label = { Text(stringResource(id = R.string.max_price_label)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            // zie documentatie android kkotlin (https://developer.android.com/reference/kotlin/androidx/compose/ui/text/input/ImeAction)
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        })
    )
}

// PREVIEW, maar render probleem
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemListTopBarPreview() {
    TopAppBar(
        title = { Text("Veilingplatform") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            Spacer(modifier = Modifier.weight(1f, true))
            Image(
                painter = painterResource(id = R.drawable.veiling_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
            )
        }
    )
}
