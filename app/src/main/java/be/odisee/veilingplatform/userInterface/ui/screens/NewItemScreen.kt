package be.odisee.veilingplatform.userInterface.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.veilingplatform.R
import be.odisee.veilingplatform.userInterface.ui.viewmodel.ItemsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewItemScreen(viewModel: ItemsViewModel, navController: NavController, userAccountType: String) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var startingPrice by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current


    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val startDateTime = LocalDateTime.now()
    val defaultEndDateTime = startDateTime.plusDays(3).format(formatter)
    var endDateTime by rememberSaveable { mutableStateOf(defaultEndDateTime) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current


    fun isEndDateTimeValid(): Boolean {
        val parsedEndDateTime = LocalDateTime.parse(endDateTime, formatter)
        return parsedEndDateTime.isAfter(startDateTime.plusHours(12))
    }

    if (userAccountType != "Free" && showDatePicker) {
        ShowDatePickerDialog(
            initialDate = endDateTime,
            onDateSelected = { newDate ->
                endDateTime = newDate
                showDatePicker = false
            },
            onCancel = { showDatePicker = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(stringResource(R.string.add_new_item_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.item_name_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.item_description_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = startingPrice,
            onValueChange = { startingPrice = it },
            label = { Text(stringResource(R.string.item_starting_price_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Spacer(modifier = Modifier.height(8.dp))
        CategoryDropdown(
            viewModel = viewModel,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it })

        if (userAccountType != "Free") {
            OutlinedTextField(
                value = endDateTime,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.item_end_date_label)) },
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, "Kalender", Modifier.clickable { showDatePicker = true })
                }
            )
        } else {
            Text(stringResource(R.string.end_date_automatic_set, defaultEndDateTime), style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val price = startingPrice.toDoubleOrNull() ?: 0.0
                val endDateTimeParsed = if (userAccountType == "Free") {
                    LocalDateTime.parse(defaultEndDateTime, formatter)
                } else {
                    LocalDateTime.parse(endDateTime, formatter)
                }
                if (userAccountType != "Free" && !isEndDateTimeValid()) {
                    return@Button
                }
                viewModel.addItem(
                    name = name,
                    description = description,
                    startingPrice = price,
                    startDateTime = startDateTime.format(formatter),
                    endDateTime = endDateTimeParsed.format(formatter),
                    category = selectedCategory
                )
                navController.navigate("itemsList")
            },
            enabled = name.isNotBlank() && description.isNotBlank() && startingPrice.isNotBlank() && selectedCategory.isNotBlank() && (userAccountType == "Free" || isEndDateTimeValid())
        ) {
            Text(stringResource(R.string.add_item_button))
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("itemsList") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(stringResource(R.string.add_item_button))
        }
    }


    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (message.contains("succesvol toegevoegd")) {
                navController.navigate("itemsList")
            }
            viewModel.toastMessage = null
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowDatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val localDateTime = LocalDateTime.parse(initialDate, formatter)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth,
                localDateTime.hour, localDateTime.minute)
            onDateSelected(selectedDate.format(formatter))
        },
        localDateTime.year, localDateTime.monthValue - 1, localDateTime.dayOfMonth
    )

    datePickerDialog.setOnCancelListener { onCancel() }
    DisposableEffect(Unit) {
        datePickerDialog.show()
        onDispose { datePickerDialog.dismiss() }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(viewModel: ItemsViewModel, selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories by viewModel.categories.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kies een categorie") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, "Drop-Down", Modifier.clickable { expanded = true })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    onCategorySelected(category)
                    expanded = false
                }) {
                    Text(category)
                }
            }
        }
    }
}
