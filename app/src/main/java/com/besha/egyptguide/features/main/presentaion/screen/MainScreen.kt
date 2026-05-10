package com.besha.egyptguide.features.main.presentaion.screen

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.camera.presenation.screen.CameraScreen
import com.besha.egyptguide.features.home.presenation.screen.HomeScreen
import com.besha.egyptguide.features.leaderboard.presenation.screen.LeaderboardScreen
import com.besha.egyptguide.features.main.presentaion.components.CustomBottomNavigationBar
import com.besha.egyptguide.features.main.presentaion.viewmodel.BottomNavViewModel
import com.besha.egyptguide.features.maps.presentaion.screen.MapsScreen
import com.besha.egyptguide.features.placedetails.presentation.screen.PlaceDetailsScreen
import com.besha.egyptguide.features.profile.presenation.screen.ProfileScreen
import com.besha.egyptguide.features.quiz.presentation.screen.QuizScreen
import com.besha.egyptguide.features.tickets.presentation.screen.TicketsScreen

@Composable
fun MainScreen(rootController: NavController,category: String?) {


    val navController = rememberNavController()
    val bottomNavViewModel = hiltViewModel<BottomNavViewModel>()
    val currentRoute by bottomNavViewModel.currentRoute.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route


    LaunchedEffect(category) {

        if (category != null) {
            navController.navigate(ScreenResources.MapsRoute)
        }

    }

    LaunchedEffect(route) {
        Log.d("MainScreen", "route: $route")
            route?.let {
                ScreenResources.fromRoute(it)?.let { screen ->
                    bottomNavViewModel.onRouteSelected(screen)
                }
            }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute !is ScreenResources.QuizRoute &&
                currentRoute !is ScreenResources.PlaceDetailsRoute &&
                currentRoute !is ScreenResources.TicketsRoute &&
                currentRoute !is ScreenResources.LeaderboardRoute
                )
            CustomBottomNavigationBar(currentRoute) { selectedRoute ->
                if (selectedRoute != currentRoute) {
                    bottomNavViewModel.onRouteSelected(selectedRoute)
                    navController.navigate(selectedRoute) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true

                    }
                }
            }
        }

    ) { innerPadding ->

        NavHost(
            navController,
            startDestination = ScreenResources.HomeRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<ScreenResources.HomeRoute> {
                HomeScreen(
                    onPlaceClick = { place ->
                        navController.navigate(
                            ScreenResources.PlaceDetailsRoute(
                                id = place.id,
                                displayName = place.displayName,
                                formattedAddress = place.formattedAddress,
                                imageUri = place.imageUri?.toString(),
                                lat = place.location?.latitude ?: 0.0,
                                lng = place.location?.longitude ?: 0.0
                            )
                        )
                    },
                    onNavigateToQuiz = { monumentId, monumentName ->
                        navController.navigate(
                            ScreenResources.QuizRoute(
                                id = monumentId,
                                name = monumentName
                            )
                        )
                    }
                )
            }
            composable<ScreenResources.MapsRoute> {

                MapsScreen(category) { screenRoute ->
                    navController.navigate(
                        screenRoute
                    )
                }

            }
            composable<ScreenResources.CalendarRoute> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //CalendarScreen()
                }
            }
            composable<ScreenResources.ProfileRoute> {

                ProfileScreen(rootController, navController)

            }
            composable<ScreenResources.CameraRoute> {
                CameraScreen(navController)
            }

            composable<ScreenResources.PlaceDetailsRoute> { backStackEntry ->
                val placeDetails: ScreenResources.PlaceDetailsRoute = backStackEntry.toRoute()
                PlaceDetailsScreen(
                    place = placeDetails,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<ScreenResources.QuizRoute> { backStackEntry ->
                val quiz: ScreenResources.QuizRoute = backStackEntry.toRoute()
                QuizScreen(
                    monument = quiz,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<ScreenResources.LeaderboardRoute> {backStackEntry ->
                val leaderboard: ScreenResources.LeaderboardRoute = backStackEntry.toRoute()
                LeaderboardScreen(
                    userId = leaderboard.userId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<ScreenResources.TicketsRoute> {
                TicketsScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}