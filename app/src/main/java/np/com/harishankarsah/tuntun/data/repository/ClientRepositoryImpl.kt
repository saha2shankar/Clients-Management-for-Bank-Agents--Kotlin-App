package np.com.harishankarsah.tuntun.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import np.com.harishankarsah.tuntun.domain.model.Client
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ClientRepository {

    private val clientsCollection = firestore.collection("clients")

    override fun getClients(): Flow<List<Client>> = callbackFlow {
        val snapshotListener = clientsCollection
            .orderBy("clientName", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val clients = snapshot?.toObjects(Client::class.java) ?: emptyList()
                trySend(clients)
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getClient(id: String): Client? {
        return try {
            clientsCollection.document(id).get().await().toObject(Client::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addClient(client: Client) {
        val id = clientsCollection.document().id
        clientsCollection.document(id).set(client.copy(id = id)).await()
    }

    override suspend fun updateClient(client: Client) {
        clientsCollection.document(client.id).set(client).await()
    }

    override suspend fun deleteClient(client: Client) {
        clientsCollection.document(client.id).delete().await()
    }

    override fun searchClients(query: String): Flow<List<Client>> = callbackFlow {
        val snapshotListener = clientsCollection
            .orderBy("clientName")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val clients = snapshot?.toObjects(Client::class.java) ?: emptyList()
                val filtered = clients.filter { 
                    it.clientName.contains(query, ignoreCase = true) || 
                    it.accountNumber.contains(query, ignoreCase = true)
                }
                trySend(filtered)
            }
        awaitClose { snapshotListener.remove() }
    }
}