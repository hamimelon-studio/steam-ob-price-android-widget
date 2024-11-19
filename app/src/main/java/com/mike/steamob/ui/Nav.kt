package com.mike.steamob.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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
                composable("about") { AboutScreen(navController, innerPadding) }
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
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("About")
            },
            label = { Text("About") },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )
    }
}