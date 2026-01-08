package np.com.harishankarsah.tuntun.domain.usecase

import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import javax.inject.Inject

class DeleteClientUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(client: Client) {
        repository.deleteClient(client)
    }
}