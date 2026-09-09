package com.fit5046.health304

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit5046.health304.ui.components.BottomNavigationBar
import com.fit5046.health304.ui.screens.ExerciseLibraryScreen
import com.fit5046.health304.ui.screens.HomeDashboardScreen
import com.fit5046.health304.ui.screens.LoginScreen
import com.fit5046.health304.ui.screens.MedicationReminderScreen
import com.fit5046.health304.ui.screens.MoodDiaryScreen
import com.fit5046.health304.ui.screens.ProfilePreferencesScreen
import com.fit5046.health304.ui.screens.RegisterScreen
import com.fit5046.health304.ui.theme._304HealthTheme

import com.fit5046.health304.ui.screens.DataVisualizationScreen
import com.fit5046.health304.ui.screens.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _304HealthTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        "home",
        "mood",
        "exercise",
        "medication"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute ?: "home",
                    onNavigate = { route ->
                        if (route == "home") {
                            navController.popBackStack(
                                route = "home",
                                inclusive = false
                            )
                        } else {
                            navController.navigate(route) {
                                popUpTo("home") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("login") {
                LoginScreen(
                    onLoginClick = { _, _ ->
                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onForgotPassword = {
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterClick = { _, _, _ ->
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable("home") {
                HomeDashboardScreen(
                    onMoodClick = {
                        navController.navigate("mood")
                    },
                    onViewAllMoodClick = {
                        navController.navigate("mood")
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    }
                )
            }

            composable("mood") {
                MoodDiaryScreen()
            }

            composable("exercise") {
                ExerciseLibraryScreen()
            }

            composable("medication") {
                MedicationReminderScreen()
            }

            composable("profile") {
                ProfilePreferencesScreen(
                    onSave = { _, _, _ ->
                        navController.popBackStack()
                    }
                )
            }
        }
        composable("visualisation") {
            DataVisualizationScreen()
        }
        composable("search") {
            SearchScreen()
        }
    }
}