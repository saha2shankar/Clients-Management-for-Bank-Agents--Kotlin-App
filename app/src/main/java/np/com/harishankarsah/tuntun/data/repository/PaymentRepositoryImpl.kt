package np.com.harishankarsah.tuntun.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import np.com.harishankarsah.tuntun.domain.model.Payment
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PaymentRepository {

    override fun getPayments(clientId: String): Flow<List<Payment>> = callbackFlow {
        val snapshotListener = firestore.collection("clients").document(clientId).collection("payments")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val payments = snapshot?.toObjects(Payment::class.java) ?: emptyList()
                trySend(payments)
            }
        awaitClose { snapshotListener.remove() }
    }

    override fun getAllPayments(): Flow<List<Payment>> = callbackFlow {
        val listener = firestore.collectionGroup("payments")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val payments = snapshot?.toObjects(Payment::class.java) ?: emptyList()
                trySend(payments)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addPayment(payment: Payment) {
        val collection = firestore.collection("clients").document(payment.clientId).collection("payments")
        val id = collection.document().id
        collection.document(id).set(payment.copy(id = id)).await()
    }

    override suspend fun updatePayment(payment: Payment) {
        firestore.collection("clients").document(payment.clientId)
            .collection("payments").document(payment.id)
            .set(payment).await()
    }

    override suspend fun deletePayment(payment: Payment) {
        firestore.collection("clients").document(payment.clientId)
            .collection("payments").document(payment.id)
            .delete().await()
    }

    override fun getTotalPaymentsAmount(): Flow<Double> = callbackFlow {
        val listener = firestore.collectionGroup("payments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val total = snapshot?.documents?.sumOf { doc ->
                    doc.getDouble("amount") ?: 0.0
                } ?: 0.0
                trySend(total)
            }
        awaitClose { listener.remove() }
    }
}
