package np.com.harishankarsah.tuntun.domain.repository

import kotlinx.coroutines.flow.Flow
import np.com.harishankarsah.tuntun.domain.model.Payment

interface PaymentRepository {
    fun getPayments(clientId: String): Flow<List<Payment>>
    fun getAllPayments(): Flow<List<Payment>>
    suspend fun addPayment(payment: Payment)
    suspend fun updatePayment(payment: Payment)
    suspend fun deletePayment(payment: Payment)
    fun getTotalPaymentsAmount(): Flow<Double>
}
