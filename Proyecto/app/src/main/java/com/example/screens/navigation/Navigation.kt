package com.example.screens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.screens.ui.*

// Definicion de rutas
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Loading : Screen("loading")
    object Map : Screen("map")
    object Community : Screen("community")
    object Groups : Screen("groups")
    object PetProfile : Screen("pet_profile")
    object ConnectTracker : Screen("connect_tracker")
    object LostPetReport : Screen("lost_pet_report")
    object NewReport : Screen("new_report")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object LocationHistory : Screen("location_history")
    object CreateGroup : Screen("create_group")
    object GroupDetail : Screen("group_detail/{groupName}") {
        fun createRoute(groupName: String) = "group_detail/$groupName"
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreenWithNavigation(
                onLoginSuccess = {
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        // Signup Screen
        composable(Screen.Signup.route) {
            SignupScreenWithNavigation(
                onSignupSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        // Loading Screen
        composable(Screen.Loading.route) {
            LoadingPageWithNavigation(
                onLoadingComplete = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                navController = navController
            )
        }

        // Map Screen
        composable(Screen.Map.route) {
            MapPageWithNavigation(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onConnectClick = {
                    navController.navigate(Screen.ConnectTracker.route)
                },
                navController = navController
            )
        }

        // Community Screen
        composable(Screen.Community.route) {
            CommunityPageWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                onReportClick = {
                    navController.navigate(Screen.NewReport.route)
                },
                onCardClick = {
                    navController.navigate(Screen.LostPetReport.route)
                },
                navController = navController
            )
        }

        // Groups Screen
        composable(Screen.Groups.route) {
            GroupScreenWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateGroupClick = {
                    navController.navigate(Screen.CreateGroup.route)
                },
                onGroupClick = { groupName ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupName))
                },
                navController = navController
            )
        }

        // Pet Profile Screen
        composable(Screen.PetProfile.route) {
            PetProfilePageWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // Connect Tracker Screen
        composable(Screen.ConnectTracker.route) {
            ConnectTrackerPageWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onConnectClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // Lost Pet Report Screen
        composable(Screen.LostPetReport.route) {
            LostPetReportPageWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // New Report Screen
        composable(Screen.NewReport.route) {
            NewReportScreenWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitClick = {
                    navController.popBackStack()
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsPageWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // Notifications Screen
        composable(Screen.Notifications.route) {
            NotificationsScreenWithNavigation(
                navController = navController,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Location History Screen
        composable(Screen.LocationHistory.route) {
            LocationHistoryScreenWithNavigation(
                navController = navController,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Create Group Screen
        composable(Screen.CreateGroup.route) {
            CreateGroupScreenWithNavigation(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateClick = { name, description ->
                    // Guardar grupo y regresar
                    navController.popBackStack()
                }
            )
        }

        // Group Detail Screen
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupName") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupName = backStackEntry.arguments?.getString("groupName") ?: "Group"
            GroupDetailScreenWithNavigation(
                navController = navController,
                groupName = groupName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}