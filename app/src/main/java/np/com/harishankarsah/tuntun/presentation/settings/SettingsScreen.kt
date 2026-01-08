package np.com.harishankarsah.tuntun.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import np.com.harishankarsah.tuntun.presentation.Screen
import np.com.harishankarsah.tuntun.presentation.pin.PinMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Reload settings when entering screen to ensure sync
    LaunchedEffect(true) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ListItem(
                headlineContent = { Text("App Lock") },
                supportingContent = { Text(if (state.isPinSet) "PIN is set" else "Secure your app with a PIN") },
                leadingContent = { Icon(Icons.Default.Lock, contentDescription = "App Lock") },
                trailingContent = {
                    Switch(
                        checked = state.isPinSet,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                navController.navigate(Screen.PinScreen.route + "?mode=${PinMode.Create.name}")
                            } else {
                                navController.navigate(Screen.PinScreen.route + "?mode=${PinMode.Remove.name}")
                            }
                        }
                    )
                }
            )
            
            if (state.isPinSet) {
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Change PIN") },
                    leadingContent = { Spacer(Modifier.size(24.dp)) }, 
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.PinScreen.route + "?mode=${PinMode.ChangeOld.name}")
                    }
                )
            }
        }
    }
}
