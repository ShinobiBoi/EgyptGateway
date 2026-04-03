package com.besha.egyptguide.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
     rootController: NavHostController
){

    val viewModel = hiltViewModel<SplashViewModel>()
    val isLoggedIn= viewModel.isLoggedIn()

    LaunchedEffect(Unit) {

        delay(2000)
            // This code runs after the animation ends
            if (isLoggedIn) {
                rootController.navigate(ScreenResources.MainRoute) {
                    popUpTo(ScreenResources.SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                rootController.navigate(ScreenResources.AuthRoute) {
                    popUpTo(ScreenResources.SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_white))
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.egypt_gateway_logo,
                contentDescription = "icon"
            )
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        )
    }



}