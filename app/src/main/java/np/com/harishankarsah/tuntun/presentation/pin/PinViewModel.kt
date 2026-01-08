package np.com.harishankarsah.tuntun.presentation.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PinState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<PinUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var tempPin: String? = null

    init {
        checkPinStatus()
    }

    private fun checkPinStatus() {
        val isPinSet = securityRepository.isPinSet()
        _state.value = _state.value.copy(
            isPinSet = isPinSet,
            mode = if (isPinSet) PinMode.Unlock else PinMode.Create
        )
    }

    fun onPinDigitEntered(digit: Char) {
        if (_state.value.enteredPin.length < 4) {
            val newPin = _state.value.enteredPin + digit
            _state.value = _state.value.copy(enteredPin = newPin)

            if (newPin.length == 4) {
                processPin(newPin)
            }
        }
    }

    fun onBackspace() {
        if (_state.value.enteredPin.isNotEmpty()) {
            _state.value = _state.value.copy(enteredPin = _state.value.enteredPin.dropLast(1))
        }
    }

    private fun processPin(pin: String) {
        when (_state.value.mode) {
            PinMode.Unlock -> {
                if (securityRepository.verifyPin(pin)) {
                    viewModelScope.launch {
                        _eventFlow.emit(PinUiEvent.UnlockSuccess)
                    }
                } else {
                    onPinError()
                }
            }
            PinMode.Create -> {
                tempPin = pin
                _state.value = _state.value.copy(enteredPin = "", mode = PinMode.Confirm, title = "Confirm PIN")
            }
            PinMode.Confirm -> {
                if (pin == tempPin) {
                    securityRepository.setPin(pin)
                    tempPin = null
                    _state.value = _state.value.copy(enteredPin = "", isPinSet = true)
                    viewModelScope.launch {
                        _eventFlow.emit(PinUiEvent.PinSetSuccess)
                    }
                } else {
                    onPinError("PINs do not match")
                    _state.value = _state.value.copy(enteredPin = "", mode = PinMode.Create, title = "Create PIN")
                }
            }
            PinMode.Remove -> {
                if (securityRepository.verifyPin(pin)) {
                    securityRepository.removePin()
                    _state.value = _state.value.copy(enteredPin = "", isPinSet = false, mode = PinMode.Create, title = "Create PIN")
                    viewModelScope.launch {
                        _eventFlow.emit(PinUiEvent.PinRemoved)
                    }
                } else {
                    onPinError()
                }
            }
            PinMode.ChangeOld -> {
                 if (securityRepository.verifyPin(pin)) {
                     _state.value = _state.value.copy(enteredPin = "", mode = PinMode.ChangeNew, title = "Enter New PIN")
                 } else {
                     onPinError()
                 }
            }
            PinMode.ChangeNew -> {
                tempPin = pin
                _state.value = _state.value.copy(enteredPin = "", mode = PinMode.ChangeConfirm, title = "Confirm New PIN")
            }
            PinMode.ChangeConfirm -> {
                if (pin == tempPin) {
                    securityRepository.setPin(pin)
                    tempPin = null
                    _state.value = _state.value.copy(enteredPin = "", mode = PinMode.Unlock)
                    viewModelScope.launch {
                        _eventFlow.emit(PinUiEvent.PinChanged)
                    }
                } else {
                    onPinError("PINs do not match")
                    _state.value = _state.value.copy(enteredPin = "", mode = PinMode.ChangeNew, title = "Enter New PIN")
                }
            }
        }
    }

    private fun onPinError(message: String = "Incorrect PIN") {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = message, enteredPin = "")
            _eventFlow.emit(PinUiEvent.Error(message))
        }
    }

    fun setMode(mode: PinMode) {
        _state.value = _state.value.copy(mode = mode, enteredPin = "", error = null, title = getTitleForMode(mode))
    }
    
    private fun getTitleForMode(mode: PinMode): String {
        return when(mode) {
            PinMode.Unlock -> "Enter PIN"
            PinMode.Create -> "Create PIN"
            PinMode.Confirm -> "Confirm PIN"
            PinMode.Remove -> "Enter PIN to Disable"
            PinMode.ChangeOld -> "Enter Current PIN"
            PinMode.ChangeNew -> "Enter New PIN"
            PinMode.ChangeConfirm -> "Confirm New PIN"
        }
    }
}

data class PinState(
    val enteredPin: String = "",
    val isPinSet: Boolean = false,
    val mode: PinMode = PinMode.Unlock,
    val error: String? = null,
    val title: String = "Enter PIN"
)

enum class PinMode {
    Unlock, Create, Confirm, Remove, ChangeOld, ChangeNew, ChangeConfirm
}

sealed class PinUiEvent {
    object UnlockSuccess : PinUiEvent()
    object PinSetSuccess : PinUiEvent()
    object PinRemoved : PinUiEvent()
    object PinChanged : PinUiEvent()
    data class Error(val message: String) : PinUiEvent()
}
