package np.com.harishankarsah.tuntun.presentation

sealed class Screen(val route: String) {
    object ClientListScreen : Screen("client_list_screen")
    object AddEditClientScreen : Screen("add_edit_client_screen")
    object ClientDetailScreen : Screen("client_detail_screen")
    object PinScreen : Screen("pin_screen")
    object SettingsScreen : Screen("settings_screen")
    object DashboardScreen : Screen("dashboard_screen")
}