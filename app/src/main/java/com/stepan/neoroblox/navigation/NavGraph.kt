package com.stepan.neoroblox.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stepan.neoroblox.NeoApp
import com.stepan.neoroblox.ui.screens.home.HomeScreen
import com.stepan.neoroblox.ui.screens.login.AuthViewModel
import com.stepan.neoroblox.ui.screens.login.LoginScreen
import com.stepan.neoroblox.ui.screens.profile.ProfileScreen
import com.stepan.neoroblox.ui.theme.NeoColors

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PROFILE = "profile"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Home", Icons.Filled.Home),
    Tab(Routes.PROFILE, "Profile", Icons.Filled.Person),
)

private fun NavHostController.navigateTab(route: String) = navigate(route) {
    popUpTo(Routes.HOME) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun NeoNavHost(authVm: AuthViewModel) {
    val nav = rememberNavController()
    val auth by authVm.state.collectAsStateWithLifecycle()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val start = remember { if (NeoApp.graph.auth.hasSession()) Routes.HOME else Routes.LOGIN }

    // After a successful sign-in, leave the login screen.
    LaunchedEffect(auth.user) {
        if (auth.user != null && nav.currentDestination?.route == Routes.LOGIN) {
            nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
        }
    }

    Box(Modifier.fillMaxSize().background(NeoColors.backgroundBrush)) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                if (route == Routes.HOME || route == Routes.PROFILE) {
                    NavigationBar(containerColor = Color(0xCC0B1020)) {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = route == tab.route,
                                onClick = { nav.navigateTab(tab.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    indicatorColor = NeoColors.Violet.copy(alpha = 0.35f),
                                    unselectedIconColor = NeoColors.TextSecondary,
                                    unselectedTextColor = NeoColors.TextSecondary,
                                ),
                            )
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(nav, startDestination = start, modifier = Modifier.padding(padding)) {
                composable(Routes.LOGIN) {
                    LoginScreen(authVm, onSkip = {
                        nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    })
                }
                composable(Routes.HOME) {
                    HomeScreen(
                        avatarUrl = auth.avatarUrl,
                        onOpenProfile = { nav.navigateTab(Routes.PROFILE) },
                    )
                }
                composable(Routes.PROFILE) { ProfileScreen(authVm) }
            }
        }
    }
}
