package np.com.harishankarsah.tuntun.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.model.Payment
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val paymentRepository: PaymentRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Get Client Count & Chart Data
            clientRepository.getClients()
                .catch { e -> e.printStackTrace() }
                .collectLatest { clients ->
                    val chartData = processClientGrowth(clients)
                    _state.value = _state.value.copy(
                        totalClients = clients.size,
                        clientGrowthData = chartData
                    )
                }
        }
        
        viewModelScope.launch {
            // Get Total Payments & Chart Data
            paymentRepository.getAllPayments()
                .catch { e ->
                    e.printStackTrace()
                }
                .collectLatest { payments ->
                    val total = payments.sumOf { it.amount }
                    val chartData = processPaymentHistory(payments)
                    _state.value = _state.value.copy(
                        totalPayments = total,
                        paymentHistoryData = chartData
                    )
                }
        }
        
        refreshSecurityStatus()
    }
    
    fun refreshSecurityStatus() {
        _state.value = _state.value.copy(isAppSecure = securityRepository.isPinSet())
    }

    private fun processClientGrowth(clients: List<Client>): List<FloatEntry> {
        if (clients.isEmpty()) return emptyList()
        val grouped = clients.groupBy { TimeUnit.MILLISECONDS.toDays(it.openingDate) }
            .mapValues { it.value.size.toFloat() }
            .toSortedMap()
        
        return grouped.map { (day, count) ->
            FloatEntry(day.toFloat(), count)
        }.toList()
    }

    private fun processPaymentHistory(payments: List<Payment>): List<FloatEntry> {
        if (payments.isEmpty()) return emptyList()
        val grouped = payments.groupBy { TimeUnit.MILLISECONDS.toDays(it.date) }
            .mapValues { it.value.sumOf { p -> p.amount }.toFloat() }
            .toSortedMap()
            
        return grouped.map { (day, amount) ->
             FloatEntry(day.toFloat(), amount)
        }.toList()
    }
}

data class DashboardState(
    val totalClients: Int = 0,
    val totalPayments: Double = 0.0,
    val isAppSecure: Boolean = false,
    val clientGrowthData: List<FloatEntry> = emptyList(),
    val paymentHistoryData: List<FloatEntry> = emptyList()
)
