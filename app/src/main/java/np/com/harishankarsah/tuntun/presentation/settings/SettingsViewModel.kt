package np.com.harishankarsah.tuntun.presentation.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        _state.value = _state.value.copy(
            isPinSet = securityRepository.isPinSet()
        )
    }
}

data class SettingsState(
    val isPinSet: Boolean = false
)
