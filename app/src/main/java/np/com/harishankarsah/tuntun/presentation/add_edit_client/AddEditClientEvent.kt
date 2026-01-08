package np.com.harishankarsah.tuntun.presentation.add_edit_client

sealed class AddEditClientEvent {
    data class EnteredClientName(val value: String) : AddEditClientEvent()
    data class EnteredAccountNumber(val value: String) : AddEditClientEvent()
    data class EnteredSerialNumber(val value: String) : AddEditClientEvent()
    data class EnteredPlanPrice(val value: String) : AddEditClientEvent()
    data class EnteredMobile(val value: String) : AddEditClientEvent()
    data class EnteredEmail(val value: String) : AddEditClientEvent()
    data class EnteredAddress(val value: String) : AddEditClientEvent()
    data class EnteredNotes(val value: String) : AddEditClientEvent()
    data class EnteredOpeningDate(val value: Long) : AddEditClientEvent()
    object SaveClient : AddEditClientEvent()
}