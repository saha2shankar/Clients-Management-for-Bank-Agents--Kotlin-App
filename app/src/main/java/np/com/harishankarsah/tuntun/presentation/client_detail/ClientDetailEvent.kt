package np.com.harishankarsah.tuntun.presentation.client_detail

import np.com.harishankarsah.tuntun.domain.model.Payment

sealed class ClientDetailEvent {
    data class AddPayment(
        val amount: Double,
        val date: Long,
        val title: String,
        val notes: String
    ) : ClientDetailEvent()
    data class UpdatePayment(val payment: Payment) : ClientDetailEvent()
    data class DeletePayment(val payment: Payment) : ClientDetailEvent()
}
