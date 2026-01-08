package np.com.harishankarsah.tuntun.domain.usecase

import kotlinx.coroutines.flow.Flow
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import javax.inject.Inject

class GetClientsUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    operator fun invoke(query: String = ""): Flow<List<Client>> {
        return if (query.isBlank()) {
            repository.getClients()
        } else {
            repository.searchClients(query)
        }
    }
}