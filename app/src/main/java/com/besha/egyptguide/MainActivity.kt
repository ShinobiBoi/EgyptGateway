package com.besha.egyptguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.auth.authcore.presentaion.screen.AuthScreen
import com.besha.egyptguide.features.main.presentaion.screen.MainScreen
import com.besha.egyptguide.features.splash.SplashScreen
import com.besha.egyptguide.ui.theme.EgyptguideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var backEndServices: BackEndServices


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        setContent {
            EgyptguideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    val x= innerPadding

                    val category = intent.getStringExtra("category")



                    NavHost(
                        navController,
                        startDestination = ScreenResources.SplashRoute,
                        //modifier = Modifier.padding(innerPadding)
                    ) {


                        composable<ScreenResources.AuthRoute> {

                            AuthScreen(navController,)


                        }

                        composable <ScreenResources.MainRoute>{
                            MainScreen(navController,category)
                        }
                        composable <ScreenResources.SplashRoute>{
                            SplashScreen(navController)
                        }


                    }
                }
            }
        }
    }


}