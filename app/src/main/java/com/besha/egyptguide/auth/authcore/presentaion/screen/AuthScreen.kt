package com.besha.egyptguide.auth.authcore.presentaion.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.auth.authcore.presentaion.viewmodel.AuthActions
import com.besha.egyptguide.auth.authcore.presentaion.viewmodel.AuthViewModel
import com.besha.egyptguide.auth.screens.login.presentation.screen.ForgotPasswordScreen
import com.besha.egyptguide.auth.screens.login.presentation.screen.LogInScreen
import com.besha.egyptguide.auth.screens.signup.presentation.screen.SignUpScreen

@Composable
fun AuthScreen(rootController: NavController) {


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        val viewModel = hiltViewModel<AuthViewModel>()
        val state by viewModel.viewStates.collectAsState()

        LaunchedEffect(Unit){
            viewModel.executeAction(AuthActions.IsLoggedIn)
        }

        LaunchedEffect(state.isLoggedIn){
            if (state.isLoggedIn){
                rootController.navigate(ScreenResources.MainRoute) {
                    popUpTo(ScreenResources.AuthRoute) { inclusive = true }
                    launchSingleTop = true
                }

            }
        }

        val navController = rememberNavController()



        NavHost(navController, startDestination = ScreenResources.LoginRoute) {
            composable<ScreenResources.LoginRoute> {
                LogInScreen(
                   rootController= rootController,
                    navController = navController
                )
            }

            composable<ScreenResources.SignUpRoute> {
                SignUpScreen(navController)
            }

            composable<ScreenResources.ForgotPassword> {
                ForgotPasswordScreen(navController)
            }
        }

    }


}