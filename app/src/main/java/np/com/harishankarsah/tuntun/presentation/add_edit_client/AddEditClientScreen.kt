package np.com.harishankarsah.tuntun.presentation.add_edit_client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClientScreen(
    navController: NavController,
    viewModel: AddEditClientViewModel = hiltViewModel()
) {
    val clientName = viewModel.clientName.value
    val accountNumber = viewModel.accountNumber.value
    val serialNumber = viewModel.serialNumber.value
    val planPrice = viewModel.planPrice.value
    val mobile = viewModel.mobile.value
    val email = viewModel.email.value
    val address = viewModel.address.value
    val notes = viewModel.notes.value
    val openingDate = viewModel.openingDate.value

    val screenTitle = if (clientName.isNotBlank()) "Edit Client" else "Add Client"

    val snackbarHostState = remember { SnackbarHostState() }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = openingDate)
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onEvent(AddEditClientEvent.EnteredOpeningDate(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is AddEditClientViewModel.UiEvent.SaveClient -> {
                    navController.navigateUp()
                }
                is AddEditClientViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                onClick = { viewModel.onEvent(AddEditClientEvent.SaveClient) },
//                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
//                text = { Text("Save Client") }
//            )
//        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Personal Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = clientName,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredClientName(it)) },
                label = { Text("Client Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = serialNumber,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredSerialNumber(it)) },
                label = { Text("Serial Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(openingDate)),
                onValueChange = { },
                label = { Text("Opening Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            )

            HorizontalDivider()

            Text("Account Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredAccountNumber(it)) },
                label = { Text("Account Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = planPrice,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredPlanPrice(it)) },
                label = { Text("Plan Price") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                prefix = { Text("Rs. ") }
            )

            HorizontalDivider()

            Text("Contact Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = mobile,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredMobile(it)) },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredEmail(it)) },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredAddress(it)) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            HorizontalDivider()

            Text("Additional Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.onEvent(AddEditClientEvent.EnteredNotes(it)) },
                label = { Text("Mood / Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Button(
                onClick = { viewModel.onEvent(AddEditClientEvent.SaveClient) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Client",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB

        }
    }
}