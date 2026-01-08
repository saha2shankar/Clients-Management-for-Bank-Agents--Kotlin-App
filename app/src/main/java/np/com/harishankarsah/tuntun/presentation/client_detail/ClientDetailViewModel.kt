package np.com.harishankarsah.tuntun.presentation.client_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.model.Payment
import np.com.harishankarsah.tuntun.domain.usecase.AddPaymentUseCase
import np.com.harishankarsah.tuntun.domain.usecase.DeletePaymentUseCase
import np.com.harishankarsah.tuntun.domain.usecase.GetClientUseCase
import np.com.harishankarsah.tuntun.domain.usecase.GetPaymentsUseCase
import np.com.harishankarsah.tuntun.domain.usecase.UpdatePaymentUseCase
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val getClientUseCase: GetClientUseCase,
    private val getPaymentsUseCase: GetPaymentsUseCase,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val updatePaymentUseCase: UpdatePaymentUseCase,
    private val deletePaymentUseCase: DeletePaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client.asStateFlow()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _totalPaid = MutableStateFlow(0.0)
    val totalPaid: StateFlow<Double> = _totalPaid.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentClientId: String? = null

    init {
        savedStateHandle.get<String>("clientId")?.let { clientId ->
            currentClientId = clientId
            getClient(clientId)
            getPayments(clientId)
        }
    }

    private fun getClient(clientId: String) {
        viewModelScope.launch {
            _client.value = getClientUseCase(clientId)
        }
    }

    private fun getPayments(clientId: String) {
        viewModelScope.launch {
            getPaymentsUseCase(clientId).collectLatest { payments ->
                _payments.value = payments
                calculateTotal(payments)
            }
        }
    }

    private fun calculateTotal(payments: List<Payment>) {
        val total = payments.sumOf { it.amount }
        _totalPaid.value = total
    }

    fun onEvent(event: ClientDetailEvent) {
        when (event) {
            is ClientDetailEvent.AddPayment -> {
                viewModelScope.launch {
                    try {
                        addPaymentUseCase(
                            Payment(
                                clientId = currentClientId ?: return@launch,
                                amount = event.amount,
                                date = event.date,
                                title = event.title,
                                notes = event.notes
                            )
                        )
                        _eventFlow.emit(UiEvent.PaymentAdded)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Could not add payment"))
                    }
                }
            }
            is ClientDetailEvent.UpdatePayment -> {
                viewModelScope.launch {
                    try {
                        updatePaymentUseCase(event.payment)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Payment Updated"))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Could not update payment"))
                    }
                }
            }
            is ClientDetailEvent.DeletePayment -> {
                viewModelScope.launch {
                    try {
                        deletePaymentUseCase(event.payment)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Payment Deleted"))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Could not delete payment"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        object PaymentAdded : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
