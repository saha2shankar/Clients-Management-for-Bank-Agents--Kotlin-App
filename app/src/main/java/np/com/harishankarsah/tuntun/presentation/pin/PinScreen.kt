package np.com.harishankarsah.tuntun.presentation.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PinScreen(
    viewModel: PinViewModel = hiltViewModel(),
    onUnlockSuccess: () -> Unit,
    onPinSetSuccess: () -> Unit = {},
    onPinRemoved: () -> Unit = {},
    onPinChanged: () -> Unit = {},
    initialMode: PinMode = PinMode.Unlock
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(initialMode) {
        viewModel.setMode(initialMode)
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is PinUiEvent.UnlockSuccess -> onUnlockSuccess()
                is PinUiEvent.PinSetSuccess -> onPinSetSuccess()
                is PinUiEvent.PinRemoved -> onPinRemoved()
                is PinUiEvent.PinChanged -> onPinChanged()
                is PinUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            PinDots(pinLength = state.enteredPin.length)

            Spacer(modifier = Modifier.height(48.dp))

            PinKeypad(
                onDigitClick = viewModel::onPinDigitEntered,
                onBackspaceClick = viewModel::onBackspace
            )
        }
    }
}

@Composable
fun PinDots(pinLength: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) { index ->
            val filled = index < pinLength
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        1.dp,
                        if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun PinKeypad(
    onDigitClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val rows = listOf(
            listOf('1', '2', '3'),
            listOf('4', '5', '6'),
            listOf('7', '8', '9'),
            listOf(null, '0', 'B')
        )

        for (row in rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                for (key in row) {
                    if (key == null) {
                        Spacer(modifier = Modifier.size(72.dp))
                    } else if (key == 'B') {
                         Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable { onBackspaceClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        PinKey(digit = key, onClick = { onDigitClick(key) })
                    }
                }
            }
        }
    }
}

@Composable
fun PinKey(digit: Char, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
