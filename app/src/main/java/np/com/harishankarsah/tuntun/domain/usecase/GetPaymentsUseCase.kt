package np.com.harishankarsah.tuntun.domain.usecase

import kotlinx.coroutines.flow.Flow
import np.com.harishankarsah.tuntun.domain.model.Payment
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(clientId: String): Flow<List<Payment>> {
        return repository.getPayments(clientId)
    }
}
