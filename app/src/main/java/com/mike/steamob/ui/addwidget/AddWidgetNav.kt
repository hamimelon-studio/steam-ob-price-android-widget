package com.mike.steamob.ui.addwidget

import android.content.Intent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mike.steamob.ui.commonui.AddWidgetIntroScreen
import com.mike.steamob.ui.commonui.SteamSearchWebScreen
import com.mike.steamob.ui.theme.SteamObTheme

@Composable
fun AddWidgetNav(appWidgetId: Int, forceDark: (Boolean) -> Unit, onFinish: (Int, Intent) -> Unit) {
    SteamObTheme {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "add"
            ) {
                composable("add") { _ ->
                    forceDark.invoke(false)
                    AddWidgetIntroScreen(true) { keyword ->
                        navController.navigate("steamSearch/$keyword")
                    }
                }
                composable("steamSearch/{keyword}") { backStackEntry ->
                    forceDark.invoke(true)
                    val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
                    SteamSearchWebScreen(keyword, true, innerPadding) { appId ->
                        appId?.let {
                            navController.navigate("dialog/$appId")
                        } ?: navController.navigate("dialog")
                    }
                }
                composable("dialog/{steamAppId}") { backStackEntry ->
                    forceDark.invoke(false)
                    val steamAppId = backStackEntry.arguments?.getString("steamAppId")
                    AddWidgetDialogScreen(
                        appWidgetId = appWidgetId,
                        steamAppId = steamAppId,
                        onBack = { navController.popBackStack() }
                    ) { result, resultIntent ->
                        onFinish.invoke(result, resultIntent)
                    }
                }
            }
        }
    }
}