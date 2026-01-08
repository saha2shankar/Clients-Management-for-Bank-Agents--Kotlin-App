package np.com.harishankarsah.tuntun.domain.model

data class Payment(
    val id: String = "",
    val clientId: String = "",
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val title: String = "",
    val notes: String = ""
)
