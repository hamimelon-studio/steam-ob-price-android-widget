package com.mikeapp.steamob.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikeapp.steamob.R
import com.mikeapp.steamob.ui.about.AboutScreen
import com.mikeapp.steamob.ui.about.AuthorGithubWebScreen
import com.mikeapp.steamob.ui.commonui.AddWidgetIntroScreen
import com.mikeapp.steamob.ui.commonui.SteamSearchWebScreen
import com.mikeapp.steamob.ui.theme.SteamObTheme

@Composable
fun MainNav(forceDark: (Boolean) -> Unit) {
    SteamObTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { _ ->
                    forceDark.invoke(false)
                    HomeScreen(navController, innerPadding)
                }
                composable("steam/{appId}") { backStackEntry ->
                    forceDark.invoke(true)
                    val url = backStackEntry.arguments?.getString("appId") ?: ""
                    SteamWebScreen(url, innerPadding)
                }
                composable("add") { _ ->
                    forceDark.invoke(false)
                    AddWidgetIntroScreen(false) { keyword ->
                        navController.navigate("steamSearch/$keyword")
                    }
                }
                composable("steamSearch/{keyword}") { backStackEntry ->
                    forceDark.invoke(true)
                    val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
                    SteamSearchWebScreen(keyword, false, innerPadding) {
                        navController.navigate("home")
                    }
                }
                composable("about") {
                    forceDark.invoke(false)
                    AboutScreen(navController, innerPadding)
                }
                composable("github/{url}/{scrollY}") { backStackEntry ->
                    forceDark.invoke(false)
                    val url = backStackEntry.arguments?.getString("url") ?: ""
                    val scrollY = backStackEntry.arguments?.getInt("scrollY") ?: 0
                    AuthorGithubWebScreen(url, scrollY, innerPadding)
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntry?.destination?.route

    // Simulate shadow using a top Box
    Box {
        // Thin top divider or shadow line
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // subtle shadow
        )

        NavigationBar(
            tonalElevation = 5.dp // adds slight material shadow
        ) {
            NavigationBarItem(
                selected = currentDestination == "home",
                onClick = { navController.navigate("home") },
                label = { Text(LocalContext.current.getString(R.string.nav_tab_home)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (currentDestination == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            NavigationBarItem(
                selected = currentDestination == "about",
                onClick = { navController.navigate("about") },
                label = { Text(LocalContext.current.getString(R.string.nav_tab_about)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (currentDestination == "about") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}
