package np.com.harishankarsah.tuntun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import np.com.harishankarsah.tuntun.presentation.Screen
import np.com.harishankarsah.tuntun.presentation.add_edit_client.AddEditClientScreen
import np.com.harishankarsah.tuntun.presentation.client_detail.ClientDetailScreen
import np.com.harishankarsah.tuntun.presentation.client_list.ClientListScreen
import np.com.harishankarsah.tuntun.presentation.theme.TuntunTheme

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import javax.inject.Inject
import np.com.harishankarsah.tuntun.domain.repository.SecurityRepository
import np.com.harishankarsah.tuntun.presentation.pin.PinScreen
import np.com.harishankarsah.tuntun.presentation.dashboard.DashboardScreen
import np.com.harishankarsah.tuntun.presentation.pin.PinMode
import np.com.harishankarsah.tuntun.presentation.settings.SettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var securityRepository: SecurityRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuntunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val lifecycleOwner = LocalLifecycleOwner.current

                    // Handle App Lock on Resume
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) { // ON_START is better for visible
                                if (securityRepository.isPinSet()) {
                                    val currentRoute = navController.currentDestination?.route
                                    if (currentRoute?.startsWith(Screen.PinScreen.route) != true) {
                                        navController.navigate(Screen.PinScreen.route + "?mode=${PinMode.Unlock.name}")
                                    }
                                }
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = if (securityRepository.isPinSet()) 
                            Screen.PinScreen.route + "?mode=${PinMode.Unlock.name}" 
                        else 
                            Screen.DashboardScreen.route
                    ) {
                        composable(route = Screen.DashboardScreen.route) {
                            DashboardScreen(navController = navController)
                        }
                        composable(route = Screen.ClientListScreen.route) {
                            ClientListScreen(navController = navController)
                        }
                        composable(
                            route = Screen.AddEditClientScreen.route + "?clientId={clientId}",
                            arguments = listOf(
                                navArgument(name = "clientId") {
                                    type = NavType.StringType
                                    defaultValue = "-1"
                                }
                            )
                        ) {
                            AddEditClientScreen(navController = navController)
                        }
                        composable(
                            route = Screen.ClientDetailScreen.route + "/{clientId}",
                            arguments = listOf(
                                navArgument(name = "clientId") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            ClientDetailScreen(navController = navController)
                        }
                        composable(
                            route = Screen.PinScreen.route + "?mode={mode}",
                            arguments = listOf(
                                navArgument("mode") {
                                    type = NavType.StringType
                                    defaultValue = PinMode.Unlock.name
                                }
                            )
                        ) { backStackEntry ->
                            val modeStr = backStackEntry.arguments?.getString("mode") ?: PinMode.Unlock.name
                            val mode = PinMode.valueOf(modeStr)
                            
                            PinScreen(
                                onUnlockSuccess = {
                                    if (navController.previousBackStackEntry != null) {
                                        navController.popBackStack()
                                    } else {
                                        navController.navigate(Screen.DashboardScreen.route) {
                                            popUpTo(Screen.PinScreen.route + "?mode={mode}") { inclusive = true }
                                        }
                                    }
                                },
                                onPinSetSuccess = { navController.popBackStack() },
                                onPinRemoved = { navController.popBackStack() },
                                onPinChanged = { navController.popBackStack() },
                                initialMode = mode
                            )
                        }
                        composable(Screen.SettingsScreen.route) {
                            SettingsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}