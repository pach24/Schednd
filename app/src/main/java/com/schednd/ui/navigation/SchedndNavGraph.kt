package com.schednd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.schednd.ui.create.CreateEventScreen
import com.schednd.ui.detail.EventDetailScreen
import com.schednd.ui.home.HomeScreen
import com.schednd.ui.join.JoinEventScreen

@Composable
fun SchedndNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCreateEvent = { navController.navigate("create") },
                onJoinEvent = { navController.navigate("join") },
                onOpenEvent = { code -> navController.navigate("event/$code") }
            )
        }
        composable("create") {
            CreateEventScreen(
                onEventCreated = { code ->
                    navController.navigate("event/$code") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("join") {
            JoinEventScreen(
                onJoined = { code ->
                    navController.navigate("event/$code") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "event/{code}",
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) {
            EventDetailScreen(
                onBack = { navController.popBackStack("home", inclusive = false) }
            )
        }
    }
}
