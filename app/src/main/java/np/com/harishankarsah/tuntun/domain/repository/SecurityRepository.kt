package np.com.harishankarsah.tuntun.domain.repository

interface SecurityRepository {
    fun isPinSet(): Boolean
    fun verifyPin(pin: String): Boolean
    fun setPin(pin: String)
    fun removePin()
}
