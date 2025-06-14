package com.mike.steamob.release.ui.addwidget.mainflow

import android.content.Intent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mike.steamob.release.ui.theme.SteamObTheme

@Composable
fun AddWidgetNav(
    appWidgetId: Int,
    isNewEntry: Boolean,
    forceDark: (Boolean) -> Unit,
    onFinish: (Int, Intent) -> Unit,
    onDismiss: () -> Unit
) {
    SteamObTheme {
        val navController = rememberNavController()
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isNewEntry) "add" else "dialog"
            ) {
                composable("add") { _ ->
                    forceDark.invoke(false)
                    AddWidgetIntroScreen(
                        onProceed = { keyword ->
                            navController.navigate("steamSearch/$keyword")
                        },
                        onSkip = {
                            navController.navigate("dialog")
                        }
                    )
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
                @Composable
                fun DialogScreen(steamAppIdInternal: String?) {
                    AddWidgetDialogScreen(
                        appWidgetId = appWidgetId,
                        steamAppId = steamAppIdInternal,
                        onBack = {
                            val isAtStart =
                                navBackStackEntry?.destination?.route == navController.graph.startDestinationRoute
                            if (isAtStart) {
                                onDismiss.invoke()
                            } else {
                                navController.popBackStack()
                            }
                        }
                    ) { result, resultIntent ->
                        onFinish.invoke(result, resultIntent)
                    }
                }
                composable("dialog/{steamAppId}") { backStackEntry ->
                    forceDark.invoke(false)
                    val steamAppId = backStackEntry.arguments?.getString("steamAppId")
                    DialogScreen(steamAppId)
                }
                composable("dialog") {
                    forceDark.invoke(false)
                    DialogScreen(null)
                }
            }
        }
    }
}