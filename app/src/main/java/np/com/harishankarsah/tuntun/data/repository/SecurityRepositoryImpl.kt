package np.com.harishankarsah.tuntun.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import javax.inject.Inject
import java.security.MessageDigest

class SecurityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecurityRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun isPinSet(): Boolean {
        return sharedPreferences.contains(KEY_PIN_HASH)
    }

    override fun verifyPin(pin: String): Boolean {
        val savedHash = sharedPreferences.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(pin) == savedHash
    }

    override fun setPin(pin: String) {
        sharedPreferences.edit()
            .putString(KEY_PIN_HASH, hashPin(pin))
            .apply()
    }

    override fun removePin() {
        sharedPreferences.edit()
            .remove(KEY_PIN_HASH)
            .apply()
    }

    private fun hashPin(pin: String): String {
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
    }
}
