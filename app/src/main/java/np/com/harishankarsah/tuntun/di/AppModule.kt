package np.com.harishankarsah.tuntun.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import np.com.harishankarsah.tuntun.data.repository.ClientRepositoryImpl
import np.com.harishankarsah.tuntun.data.repository.PaymentRepositoryImpl
import np.com.harishankarsah.tuntun.data.repository.SecurityRepositoryImpl
import np.com.harishankarsah.tuntun.domain.repository.ClientRepository
import np.com.harishankarsah.tuntun.domain.repository.PaymentRepository
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideClientRepository(firestore: FirebaseFirestore): ClientRepository {
        return ClientRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(firestore: FirebaseFirestore): PaymentRepository {
        return PaymentRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSecurityRepository(@ApplicationContext context: Context): SecurityRepository {
        return SecurityRepositoryImpl(context)
    }
}