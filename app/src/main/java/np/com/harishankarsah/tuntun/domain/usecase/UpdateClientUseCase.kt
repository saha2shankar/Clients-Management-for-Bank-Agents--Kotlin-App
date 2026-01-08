package np.com.harishankarsah.tuntun.domain.usecase

import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import javax.inject.Inject

class UpdateClientUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(client: Client) {
        if (client.clientName.isBlank()) {
            throw IllegalArgumentException("Client name cannot be empty")
        }
        if (client.accountNumber.isBlank()) {
            throw IllegalArgumentException("Account number cannot be empty")
        }
        repository.updateClient(client)
    }
}