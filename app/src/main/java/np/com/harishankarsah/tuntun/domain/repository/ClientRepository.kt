package np.com.harishankarsah.tuntun.domain.repository

import kotlinx.coroutines.flow.Flow
import np.com.harishankarsah.tuntun.domain.model.Client

interface ClientRepository {
    fun getClients(): Flow<List<Client>>
    suspend fun getClient(id: String): Client?
    suspend fun addClient(client: Client)
    suspend fun updateClient(client: Client)
    suspend fun deleteClient(client: Client)
    fun searchClients(query: String): Flow<List<Client>>
}