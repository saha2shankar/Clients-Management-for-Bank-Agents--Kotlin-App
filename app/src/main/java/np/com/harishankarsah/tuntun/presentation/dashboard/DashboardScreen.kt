package np.com.harishankarsah.tuntun.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import np.com.harishankarsah.tuntun.presentation.Screen
import np.com.harishankarsah.tuntun.presentation.pin.PinMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(true) {
        viewModel.refreshSecurityStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    DashboardCard(
                        title = "Total Clients",
                        value = state.totalClients.toString(),
                        icon = Icons.Default.Person,
                        color = MaterialTheme.colorScheme.errorContainer,         // red-ish professional
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = { navController.navigate(Screen.ClientListScreen.route) },
                        modifier = Modifier.weight(1f)
                    )

                    DashboardCard(
                        title = "Total Payments",
                        value = "Rs. ${state.totalPayments}",
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF4CAF50).copy(alpha = 0.4f),  // Green with 70% opacity
                        contentColor = Color(0xFF000000),
                        modifier = Modifier.weight(1f)
                    )
                }


            }

            // Client Growth Chart
            if (state.clientGrowthData.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Client Growth",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ClientGrowthChart(
                            data = state.clientGrowthData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

//            // Payment History Chart
//            if (state.paymentHistoryData.isNotEmpty()) {
//                Card(
//                    modifier = Modifier.fillMaxWidth().height(300.dp),
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            text = "Payment Trends",
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        PaymentHistoryChart(
//                            data = state.paymentHistoryData,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
//            }

            // Security Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isAppSecure) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (state.isAppSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Security Status",
                            tint = if (state.isAppSecure) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isAppSecure) "App Secured" else "Security Risk",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isAppSecure) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.isAppSecure) "App Lock is enabled. Your data is protected." else "App Lock is disabled. Enable PIN to secure your data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.isAppSecure) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (state.isAppSecure) {
                                navController.navigate(Screen.SettingsScreen.route)
                            } else {
                                navController.navigate(Screen.PinScreen.route + "?mode=${PinMode.Create.name}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAppSecure) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(if (state.isAppSecure) "Manage Security" else "Enable App Lock")
                    }
                }
            }
            
            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 OutlinedButton(
                    onClick = { navController.navigate(Screen.AddEditClientScreen.route + "?clientId=-1") },
                    modifier = Modifier.weight(1f)
                 ) {
                     Icon(Icons.Default.Add, contentDescription = null)
                     Spacer(Modifier.width(8.dp))
                     Text("Add Client")
                 }
                 OutlinedButton(
                    onClick = { navController.navigate(Screen.ClientListScreen.route) },
                    modifier = Modifier.weight(1f)
                 ) {
                     Icon(Icons.Default.List, contentDescription = null)
                     Spacer(Modifier.width(8.dp))
                     Text("View Clients")
                 }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Developer Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Developed by",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Harishankar Sah",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        ),
        modifier = modifier
            .height(120.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

