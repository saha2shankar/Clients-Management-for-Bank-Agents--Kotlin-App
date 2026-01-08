package np.com.harishankarsah.tuntun.domain.usecase

import np.com.harishankarsah.tuntun.domain.model.Payment
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import javax.inject.Inject

class UpdatePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment) {
        if (payment.amount <= 0) {
            throw IllegalArgumentException("Amount must be greater than 0")
        }
        if (payment.title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }
        repository.updatePayment(payment)
    }
}
