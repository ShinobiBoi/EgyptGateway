package com.besha.egyptguide.features.home.presenation.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeActions
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import androidx.core.net.toUri
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.besha.egyptguide.features.home.presenation.components.HomeBanner
import com.besha.egyptguide.features.home.presenation.components.HomeGenreList
import com.besha.egyptguide.features.home.presenation.components.PlacesHorizontalList
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@Composable
fun HomeScreen(
    onPlaceClick: (MyPlace) -> Unit,
    onNavigateToQuiz: (String, String) -> Unit
) {

    val viewModel = hiltViewModel<HomeViewModel>()
    val state by viewModel.viewStates.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 10 })
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp


    val egyptPlaces = listOf(
        MyPlace(
            id = "ChIJzbs54n1PWBQRizZuWjV0dMo",
            displayName = "Giza Necropolis",
            formattedAddress = "Al Haram, Giza",
            location = LatLng(29.977296199999998, 31.132495499999997),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNEatBUyzhDFadPP7cvGQOARkSFMGDTsBwlLZP6-mKyv3gBSi0C-68a1KpzB24XGWEQkklMzwJr0pCphL6OH9XDtCWVe7ELzdOjtCJmSqJl7IScS4rR6B1fQV8YyQmIq1W_zq_ta4-bN8z1mYg=s4800-w4800-h4408".toUri()
        ),
        MyPlace(
            id = "ChIJeamuo2JPWBQRqb1mQKbw08k",
            displayName = "Great Sphinx of Giza",
            formattedAddress = "Al Haram, Giza",
            location = LatLng(29.9752687, 31.1375674),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNFcj1GIVtAE8JOKHqyr18BoLAtlYYliwHdys5dzXBx6xbObpl8Yuo8wgeUN454nS-9QUoraFl0H5OTDwwLXSzxXJU4PcvtO53Yy2wrZad6BthTB6goi51_wWAXTV2hIuPaznud5B6Xr1fWU=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJzcD-KJIVSRQR2FnCCMDoGsc",
            displayName = "Karnak",
            formattedAddress = "Karnak, Luxor",
            location = LatLng(25.718834599999997, 32.6572703),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNF2M270t3nxP591aJcMzhjeuoaYEs73MTcv9zJA-8RYuD4gIv-IHDLEsoXXk-7d_oqHy-qg2_OO7lXxGENj36UXiOKidV8Db4261wuY0CyReWqborFGbp96Qu6N9BvQFkVf-CEwDKKN19stNw=s4800-w4800-h2703".toUri()
        ),
        MyPlace(
            id = "ChIJ1_7etYo9SRQR2qnjovbMj3E",
            displayName = "Valley of the Kings",
            formattedAddress = "Luxor, Luxor",
            location = LatLng(25.7401643, 32.601411),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNG6hMc4UtNtnW8FR9cAAh4B5Jjj8PsbaSBvjwOFBpZxP1cRxMmsqDuVOELraXd4Zbth0b69acUk7gsH7epIf3W3QDOcEzqUA_6EgF0tv5mu_7mXTwB0iT83j5WQ25m_3blmXwh1M887R7DObak=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJ3ae_xxgWSRQR07VjFXrhHwM",
            displayName = "Mortuary Temple of Hatshepsut",
            formattedAddress = "Al Qarna, Luxor",
            location = LatLng(25.738277300000004, 32.6064906),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNEXKpFDPRl-jQokI1gAQtmCo-IwPVTn1nOzKB1U5DMMh_-aKhvHv7n5tPkczYztGKQc41gdiTA37-5lWZrH1JlTsviluz9C1lunsdZV3lY1FQKy5SBz4jtne3JyOkO5BkVQNZMdoCAIdUO-=s4800-w2048-h1431".toUri()
        ),
        MyPlace(
            id = "ChIJn1b3gKpAWBQRbsg1WfPe1NA",
            displayName = "Mosque of Muhammad Ali",
            formattedAddress = "Saladin, Cairo",
            location = LatLng(30.0290456, 31.259796599999998),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNGrKfZ6u7GSjz8wynKGA2PP--QJrPafX5BAZ2FXug_4t_BzmhffBczaKsC5gNg9JAV4bkL_nDjs4KApE6TlcLf_6WhM_qFJreAAPZ2CLmoUehc0Tl-FJbcdVZhtmQwRlmKXrpm1TApX7C7aTQ=s4800-w2689-h2477".toUri()
        ),
        MyPlace(
            id = "ChIJn3WoRhBHWBQRuhsCgung4tQ",
            displayName = "Hanging Church",
            formattedAddress = "Old Cairo, Cairo",
            location = LatLng(30.0052389, 31.2301689),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHlonKRFOHn9n9NXIVh5cORHE7uaOWz99FsDiD7GZZVHn4oCGpRyCGLiPv4L6tbk3hIyOzCrlE3q6L0zSMzxqWapzkSFAqB4HD4xnJO3TQlKxKo8mjwOxUPN3evpV5-TiZKPRbk8OG7QOp72XM=s4800-w529-h398".toUri()
        ),
        MyPlace(
            id = "ChIJuxEpKFLD9RQRHz2y-AiDn-U",
            displayName = "Qaitbay Citadel",
            formattedAddress = "Al Gomrok, Alexandria ",
            location = LatLng(31.213698700000002, 29.8853921),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHmtW_yPXAw9S30ReqCzifsU_ezWNHJJIg008EM1JLjCi6MbVUjlIsAkPpol5U6W1-XgjKjjQQx8AP13lD4RryzeeNsNCva3fDhK23whJqOkMVhhfIvHxaRiO23dHg1Pl-EatSrbYsY0TJdzQ=s4800-w4800-h3600".toUri()
        ),
        MyPlace(
            id = "ChIJuXeJLNtiNhQReOUrg0ms8pk",
            displayName = "Philae Temple",
            formattedAddress = "Aswan 1, Aswan",
            location = LatLng(24.025583599999997, 32.8841021),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNEeqOoUcCMhDEu2pGxrpEQ12sTRs72dGrXctov5LIkQMqlILwATH2q_VocAyg23x-vv3YAQ000Z93xqLk-Tn5OmV8VEV3FDRpBRtxCFGR8FwbD8eHxWK4m4s7TF_vvZuNC-umJmAzwjySvKnQ=s4800-w4032-h3024".toUri()
        ),
        MyPlace(
            id = "ChIJWwUmsYipOhQR0pj4GGbM06c",
            displayName = "Abu Simbel Temples",
            formattedAddress = "Abu Simbel, Aswan",
            location = LatLng(22.3372319, 31.625798999999997),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNFYQhsJpbd-n4O1yUZw6hX16NJj2pda_BVDZiN3yvgZXchBWxY4_e0ll8215F8OvfixgIVVbSYUcD7oSZ0jo7MWGu1yoN9PAm78I-tNYgQGK21t94XbQ_yo3bJwX9YaJGKJoTgdKlu5qZbB7w=s4800-w4800-h3614".toUri()
        )
    )


    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.executeAction(HomeActions.GetCurrentLocation)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(state.location) {
        state.location.data?.let {
            if (state.places.data == null) {
                // viewModel.executeAction(HomeActions.SelectGenre(GenreType.HOTELS, it))

            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.white)),
    ) {


        HomeBanner(
            modifier = Modifier
                .fillMaxWidth(),
            places = egyptPlaces,
            screenHeight = screenHeight,
            pagerState = pagerState
        ) {
            // onPlaceClick(it)
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = screenHeight * 0.5f - 100.dp, start = 8.dp, end = 8.dp)
        ) {

            HomeGenreList(
                modifier = Modifier.padding(top = 16.dp),
                selectedGenre = state.selectedGenre
            ) { genre ->

                val location = state.location.data ?: return@HomeGenreList
                viewModel.executeAction(HomeActions.SelectGenre(genre, location))
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.near_by_locations),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                color = Color.Black
            )

            Spacer(Modifier.height(12.dp))


            PlacesHorizontalList(state.places, state.location.data, onPlaceClick)


        }
    }
}

