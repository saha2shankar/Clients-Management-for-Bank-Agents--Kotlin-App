package np.com.harishankarsah.tuntun.presentation.client_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.usecase.DeleteClientUseCase
import np.com.harishankarsah.tuntun.domain.usecase.GetClientsUseCase
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val getClientsUseCase: GetClientsUseCase,
    private val deleteClientUseCase: DeleteClientUseCase
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        getClients()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        getClients(query)
    }

    private fun getClients(query: String = "") {
        viewModelScope.launch {
            getClientsUseCase(query)
                .catch { e ->
                    _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error occurred"))
                }
                .collectLatest {
                    _clients.value = it
                }
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            try {
                deleteClientUseCase(client)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Could not delete client"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}