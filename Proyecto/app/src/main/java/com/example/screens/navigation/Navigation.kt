package com.example.screens.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.screens.ui.*
import com.example.screens.viewmodel.AuthViewModel

// Definicion de rutas
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Loading : Screen("loading")
    object Map : Screen("map")

    object Community : Screen("community")
    object NewReport : Screen("new_report")
    object LostPetReport : Screen("lost_pet_report/{reportId}") {
        fun createRoute(reportId: String) = "lost_pet_report/$reportId"
    }

    object Groups : Screen("groups")
    object CreateGroup : Screen("create_group")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }

    object PetProfile : Screen("pet_profile")
    object ConnectTracker : Screen("connect_tracker")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object LocationHistory : Screen("location_history")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val authViewModel: AuthViewModel = viewModel()
    val userProfile by authViewModel.currentUserProfile.collectAsState()

    // Ajusta estos nombres si tu UserProfile usa otros campos
    val currentUserId = userProfile?.userId ?: ""
    val currentUserName = userProfile?.name ?: ""
    val currentUserEmail = userProfile?.email ?: ""

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
        // Login
        composable(Screen.Login.route) {
            LoginScreenWithNavigation(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(Screen.Signup.route) }
            )
        }

        // Signup
        composable(Screen.Signup.route) {
            SignupScreenWithNavigation(
                viewModel = authViewModel,
                onSignupSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.popBackStack() }
            )
        }

        // Loading
        composable(Screen.Loading.route) {
            LoadingPageWithNavigation(
                onLoadingComplete = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                navController = navController
            )
        }

        // Map
        composable(Screen.Map.route) {
            MapPageWithNavigation(
                userProfile = userProfile,
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onConnectClick = { navController.navigate(Screen.ConnectTracker.route) },
                navController = navController
            )
        }

        // Community (lista)
        composable(Screen.Community.route) {
            CommunityPageWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onReportClick = { navController.navigate(Screen.NewReport.route) },
                onCardClick = { reportId ->
                    navController.navigate(Screen.LostPetReport.createRoute(reportId))
                }
            )
        }

        // New Report (crear)
        composable(Screen.NewReport.route) {
            NewReportScreenWithNavigation(
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                currentUserEmail = currentUserEmail,
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { reportId ->
                    navController.navigate(Screen.LostPetReport.createRoute(reportId)) {
                        popUpTo(Screen.Community.route) { inclusive = false }
                    }
                }
            )
        }

        // Lost Pet Report (detalle por reportId)
        composable(
            route = Screen.LostPetReport.route,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            LostPetReportPageWithNavigation(
                navController = navController,
                reportId = reportId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Groups (lista)
        composable(Screen.Groups.route) {
            GroupScreenWithNavigation(
                navController = navController,
                currentUserId = currentUserId,
                onBackClick = { navController.popBackStack() },
                onCreateGroupClick = { navController.navigate(Screen.CreateGroup.route) },
                onGroupClick = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId))
                }
            )
        }

        // Create Group (crear)
        composable(Screen.CreateGroup.route) {
            CreateGroupScreenWithNavigation(
                currentUserId = currentUserId,
                onBackClick = { navController.popBackStack() },
                onCreated = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId)) {
                        popUpTo(Screen.Groups.route) { inclusive = false }
                    }
                }
            )
        }

        // Group Detail (detalle por groupId)
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            // Si tu GroupDetailScreen necesita groupName, puedes cargarlo dentro con Firestore.
            GroupDetailScreenWithNavigation(
                navController = navController,
                groupId = groupId,
                groupName = "Group",
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pet Profile
        composable(Screen.PetProfile.route) {
            PetProfilePageWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Connect Tracker
        composable(Screen.ConnectTracker.route) {
            ConnectTrackerPageWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onConnectClick = { navController.popBackStack() }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsPageWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Notifications
        composable(Screen.Notifications.route) {
            NotificationsScreenWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Location History
        composable(Screen.LocationHistory.route) {
            LocationHistoryScreenWithNavigation(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
