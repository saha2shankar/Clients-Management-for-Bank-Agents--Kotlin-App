package np.com.harishankarsah.tuntun.domain.usecase

import np.com.harishankarsah.tuntun.domain.model.Payment
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import javax.inject.Inject

class DeletePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment) {
        repository.deletePayment(payment)
    }
}
