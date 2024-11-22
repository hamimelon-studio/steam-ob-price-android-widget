package com.mike.steamob.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mike.steamob.R
import com.mike.steamob.ui.about.AboutScreen
import com.mike.steamob.ui.about.AuthorGithubWebScreen
import com.mike.steamob.ui.home.HomeScreen
import com.mike.steamob.ui.home.SteamWebScreen
import com.mike.steamob.ui.theme.SteamObTheme

@Composable
fun SteamAppNav() {
    SteamObTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen(navController, innerPadding) }
                composable("steam/{appId}") { backStackEntry ->
                    val url = backStackEntry.arguments?.getString("appId") ?: ""
                    SteamWebScreen(url, innerPadding)
                }
                composable("about") { AboutScreen(navController, innerPadding) }
                composable("github/{url}/{scrollY}") { backStackEntry ->
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
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("Home")
            },
            label = { Text(LocalContext.current.getString(R.string.nav_tab_home)) },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("About")
            },
            label = { Text(LocalContext.current.getString(R.string.nav_tab_about)) },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )
    }
}