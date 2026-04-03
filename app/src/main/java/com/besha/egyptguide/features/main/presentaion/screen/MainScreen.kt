package com.besha.egyptguide.features.main.presentaion.screen

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.calendar.CalendarScreen
import com.besha.egyptguide.features.home.presenation.screen.HomeScreen
import com.besha.egyptguide.features.main.presentaion.components.CustomBottomNavigationBar
import com.besha.egyptguide.features.main.presentaion.viewmodel.BottomNavViewModel
import com.besha.egyptguide.features.maps.presentaion.screen.MapsScreen
import com.besha.egyptguide.features.profile.presenation.screen.ProfileScreen

@Composable
fun MainScreen(rootController: NavController) {



    val navController = rememberNavController()
    val bottomNavViewModel = hiltViewModel<BottomNavViewModel>()
    val currentRoute by bottomNavViewModel.currentRoute.collectAsState()


    Scaffold(
        bottomBar = {
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

                )
            }
            composable<ScreenResources.MapsRoute> {

                MapsScreen()

            }
            composable<ScreenResources.CalendarRoute> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CalendarScreen()
                }
            }
            composable<ScreenResources.ProfileRoute> {

                ProfileScreen(rootController,navController)

            }

        }
    }
}