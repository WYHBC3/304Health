package com.fit5046.health304

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.fit5046.health304.ui.screens.DataVisualizationScreen
import com.fit5046.health304.ui.screens.ExerciseLibraryScreen
import com.fit5046.health304.ui.screens.HomeDashboardScreen
import com.fit5046.health304.ui.screens.LoginScreen
import com.fit5046.health304.ui.screens.MedicationReminderRoute
import com.fit5046.health304.ui.screens.MoodDiaryScreen
import com.fit5046.health304.ui.screens.ProfilePreferencesRoute
import com.fit5046.health304.ui.screens.RegisterScreen
import com.fit5046.health304.ui.screens.SearchScreen
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.viewmodel.AuthViewModel


class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            _304HealthTheme {

                AppNavHost(
                    authViewModel = authViewModel
                )
            }
        }
    }
}


@Composable
fun AppNavHost(
    authViewModel: AuthViewModel
) {

    val navController =
        rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry
            ?.destination
            ?.route

    /*
     * Search and visualisation are secondary pages,
     * so the main four-item bottom bar is not changed.
     */
    val bottomBarRoutes =
        listOf(
            "home",
            "mood",
            "exercise",
            "medication"
        )


    Scaffold(

        modifier =
            Modifier.fillMaxSize(),

        bottomBar = {

            if (
                currentRoute in bottomBarRoutes
            ) {

                BottomNavigationBar(

                    currentRoute =
                        currentRoute
                            ?: "home",

                    onNavigate = { route ->

                        if (
                            route == "home"
                        ) {

                            navController
                                .popBackStack(
                                    route = "home",
                                    inclusive = false
                                )

                        } else {

                            navController
                                .navigate(route) {

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

            navController =
                navController,

            startDestination =
                if (
                    authViewModel.isLoggedIn
                ) {
                    "home"
                } else {
                    "login"
                },

            modifier =
                Modifier.padding(
                    innerPadding
                )
        ) {


            /*
             * Login
             */
            composable("login") {

                LoginScreen(

                    authViewModel =
                        authViewModel,

                    onLoginSuccess = {

                        navController
                            .navigate("home") {

                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                    },

                    onNavigateToRegister = {

                        navController
                            .navigate("register")
                    }
                )
            }


            /*
             * Register
             */
            composable("register") {

                RegisterScreen(

                    authViewModel =
                        authViewModel,

                    onRegisterSuccess = {

                        navController
                            .navigate("home") {

                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                    },

                    onNavigateToLogin = {

                        navController
                            .popBackStack()
                    }
                )
            }


            /*
             * Home
             */
            composable("home") {

                HomeDashboardScreen(

                    onMoodClick = {

                        navController
                            .navigate("mood")
                    },

                    onViewAllMoodClick = {

                        navController
                            .navigate("mood")
                    },

                    onProfileClick = {

                        navController
                            .navigate("profile")
                    },

                    onSearchClick = {

                        navController
                            .navigate("search")
                    },

                    onHealthTrendsClick = {

                        navController
                            .navigate(
                                "visualisation"
                            )
                    }
                )
            }


            /*
             * Mood Diary
             */
            composable("mood") {

                MoodDiaryScreen(

                    onHealthTrendsClick = {

                        navController
                            .navigate(
                                "visualisation"
                            )
                    }
                )
            }


            /*
             * Exercise Library
             */
            composable("exercise") {

                ExerciseLibraryScreen()
            }


            /*
             * Medication
             */
            composable("medication") {

                MedicationReminderRoute()
            }


            /*
             * Profile
             */
            composable("profile") {

                ProfilePreferencesRoute(

                    onSaved = {

                        navController
                            .popBackStack()
                    },

                    onCancel = {

                        navController
                            .popBackStack()
                    },

                    onLogout = {

                        authViewModel.logout()

                        navController
                            .navigate("login") {

                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                    }
                )
            }


            /*
             * Health Trends
             */
            composable("visualisation") {

                DataVisualizationScreen(

                    onBackClick = {

                        navController
                            .popBackStack()
                    }
                )
            }


            /*
             * Search
             */
            composable("search") {

                SearchScreen(

                    onBackClick = {

                        navController
                            .popBackStack()
                    },

                    onMoodClick = {

                        navController
                            .navigate("mood")
                    },

                    onMedicationClick = {

                        navController
                            .navigate(
                                "medication"
                            )
                    },

                    onExerciseClick = {

                        navController
                            .navigate(
                                "exercise"
                            )
                    },

                    onHealthDataClick = {

                        navController
                            .navigate(
                                "visualisation"
                            )
                    }
                )
            }
        }
    }
}