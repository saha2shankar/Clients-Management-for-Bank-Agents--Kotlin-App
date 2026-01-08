package np.com.harishankarsah.tuntun.presentation.add_edit_client

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.usecase.AddClientUseCase
import np.com.harishankarsah.tuntun.domain.usecase.GetClientUseCase
import np.com.harishankarsah.tuntun.domain.usecase.UpdateClientUseCase
import javax.inject.Inject

@HiltViewModel
class AddEditClientViewModel @Inject constructor(
    private val addClientUseCase: AddClientUseCase,
    private val getClientUseCase: GetClientUseCase,
    private val updateClientUseCase: UpdateClientUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var clientName = mutableStateOf("")
        private set
    var accountNumber = mutableStateOf("")
        private set
    var serialNumber = mutableStateOf("")
        private set
    var planPrice = mutableStateOf("")
        private set
    var mobile = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var address = mutableStateOf("")
        private set
    var notes = mutableStateOf("")
        private set
    var openingDate = mutableStateOf(System.currentTimeMillis())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentClientId: String? = null

    init {
        savedStateHandle.get<String>("clientId")?.let { clientId ->
            if (clientId != "-1") {
                viewModelScope.launch {
                    getClientUseCase(clientId)?.also { client ->
                        currentClientId = client.id
                        clientName.value = client.clientName
                        accountNumber.value = client.accountNumber
                        serialNumber.value = client.serialNumber
                        planPrice.value = client.planPrice.toString()
                        mobile.value = client.mobile
                        email.value = client.email
                        address.value = client.address
                        notes.value = client.notes
                        openingDate.value = client.openingDate
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditClientEvent) {
        when(event) {
            is AddEditClientEvent.EnteredClientName -> clientName.value = event.value
            is AddEditClientEvent.EnteredAccountNumber -> accountNumber.value = event.value
            is AddEditClientEvent.EnteredSerialNumber -> serialNumber.value = event.value
            is AddEditClientEvent.EnteredPlanPrice -> planPrice.value = event.value
            is AddEditClientEvent.EnteredMobile -> mobile.value = event.value
            is AddEditClientEvent.EnteredEmail -> email.value = event.value
            is AddEditClientEvent.EnteredAddress -> address.value = event.value
            is AddEditClientEvent.EnteredNotes -> notes.value = event.value
            is AddEditClientEvent.EnteredOpeningDate -> openingDate.value = event.value
            is AddEditClientEvent.SaveClient -> {
                viewModelScope.launch {
                    try {
                        val client = Client(
                            id = currentClientId ?: "",
                            clientName = clientName.value,
                            accountNumber = accountNumber.value,
                            serialNumber = serialNumber.value,
                            planPrice = planPrice.value.toDoubleOrNull() ?: 0.0,
                            mobile = mobile.value,
                            email = email.value,
                            address = address.value,
                            notes = notes.value,
                            openingDate = openingDate.value
                        )
                        if (currentClientId == null) {
                            addClientUseCase(client)
                        } else {
                            updateClientUseCase(client)
                        }
                        _eventFlow.emit(UiEvent.SaveClient)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Couldn't save client"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        object SaveClient : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}