package np.com.harishankarsah.tuntun.domain.model

data class Client(
    val id: String = "",
    val serialNumber: String = "",
    val clientName: String = "",
    val accountNumber: String = "",
    val openingDate: Long = 0,
    val closingDate: Long? = null,
    val planPrice: Double = 0.0,
    val mobile: String = "",
    val email: String = "",
    val address: String = "",
    val notes: String = ""
)