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
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZOpRCFuaYhkZRT_bzRysxBNP0WOCY-TsK7BPhyPb2l83dkuOx706iwK9YUu0THXDHTa3_H-Klruc-P8I5kRuGf9aSaYB2pXQfXnkENrSb79eNYj7EmoZBzFHWyNReC_JkHbb4VKxyQtsVOUJg=s4800-w4800-h4408".toUri()
        ),
        MyPlace(
            id = "ChIJeamuo2JPWBQRqb1mQKbw08k",
            displayName = "Great Sphinx of Giza",
            formattedAddress = "Al Haram, Giza",
            location = LatLng(29.9752687, 31.1375674),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZNnAh61hBAKEtAuCHHSMtcQbX2wMbEH1kGRDtJ56sVpiGFuwnPMKaWqAGvcUalQhpL5fkDc0M_p2-FoR9lMprDhUzb0TTGNVKAtox5NtQbZUrwv4pMEeO-OTmq5MWeQTqWufGDbSBK_o2Fn=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJzcD-KJIVSRQR2FnCCMDoGsc",
            displayName = "Karnak",
            formattedAddress = "Karnak, Luxor",
            location = LatLng(25.718834599999997, 32.6572703),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZO53rqdBY-1wSfiuiz-kDATEURIxKistdz35t6ckSOE_yrMQs4WlcTf8Kemlhx4t_4c6WbyWCYON5UUYbKKqvlxEjXh69osbuhDM0BTWEHBLaf-krxjV7lUC3Gw8t9L1v0wvyLtPGLKZkMlug=s4800-w4800-h2703".toUri()
        ),
        MyPlace(
            id = "ChIJ1_7etYo9SRQR2qnjovbMj3E",
            displayName = "Valley of the Kings",
            formattedAddress = "Luxor, Luxor",
            location = LatLng(25.7401643, 32.601411),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZNHBNxHZQNOhO89XjR6ZCQgM4xnMlDq-KgVXo2EJnw9D2cDXmI0JbscXNlAc5ybjxq8a1tguW8SU3oS1t1KAdasWedXzjbPtd7YSvsDM94Kykgh9rDDT1489W3ydI1bdeHa8lkOqYBfjMep6Tk=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJ3ae_xxgWSRQR07VjFXrhHwM",
            displayName = "Mortuary Temple of Hatshepsut",
            formattedAddress = "Al Qarna, Luxor",
            location = LatLng(25.738277300000004, 32.6064906),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZO7qdYvvBk0QhJZRrdsEmOiFTT3L2dp9R6H84FqP-R6BT4gJyfXm3OW5R88hgUuVPyGfJaLAFclXR2MAMjHoW5OqEkP9-xZLm1fmI06rBfpK-F0c1HbduMtGa5HieDCzVJnWpzgsMloiUyw=s4800-w2048-h1431".toUri()
        ),
        MyPlace(
            id = "ChIJn1b3gKpAWBQRbsg1WfPe1NA",
            displayName = "Mosque of Muhammad Ali",
            formattedAddress = "Saladin, Cairo",
            location = LatLng(30.0290456, 31.259796599999998),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZNb30IR73XBO5D7nEYVjoRlhGGqJRC9XVeTXMO-MnAfj5iYKVA8hTsXlerzCqdRC5mlhv-_LuGbPjVEzFXADslmm6eTTUCqopQsfox1GykinOStBYQkHxcM85Gg6erZJu4Osl2t-CZEykksbQ=s4800-w2689-h2477".toUri()
        ),
        MyPlace(
            id = "ChIJn3WoRhBHWBQRuhsCgung4tQ",
            displayName = "Hanging Church",
            formattedAddress = "Old Cairo, Cairo",
            location = LatLng(30.0052389, 31.2301689),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZOOAyTbQ3mPTcI-3wjM67Iwn9oC5afdXO_ATSGvQ2-fbxx7C6GiBGOoDZxOO2C0R8hkLDucOyph0gnERfEtjDKMPUlDs3brOCGqQvNW5sEhaMK7YI9AYJLwcodCWibT9ZGNtIIn2dfi98XROwU=s4800-w529-h398".toUri()
        ),
        MyPlace(
            id = "ChIJuxEpKFLD9RQRHz2y-AiDn-U",
            displayName = "Qaitbay Citadel",
            formattedAddress = "Al Gomrok, Alexandria ",
            location = LatLng(31.213698700000002, 29.8853921),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZMIRL9Zgi8mmb0gDORXRHs6z_JpyttwYkJ4mdjclpgGPsb4Qn6ZcClGKAbE1zh1fjljBWRC1a1OnJO6-raMX49EazToqJKvZYX0yxtz7iRGB_UIDrIgzxnqhjUrxwxcytx2mTpwC-54YFzxAg=s4800-w4800-h3600".toUri()
        ),
        MyPlace(
            id = "ChIJuXeJLNtiNhQReOUrg0ms8pk",
            displayName = "Philae Temple",
            formattedAddress = "Aswan 1, Aswan",
            location = LatLng(24.025583599999997, 32.8841021),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZMu7c_fwcJoRTmEkjuqaiH8xSUcoET5QiYT_OnOGi3gnxTV7xZxPF9UoJu8IwfdoXkylgCDgsZzArPOmzgl5AM2qHWEmocLGlDeA-PpLNFjlm-qF-BIH9ATpnkESo1k53DsLQm1qrantfmLHQ=s4800-w4032-h3024".toUri()
        ),
        MyPlace(
            id = "ChIJWwUmsYipOhQR0pj4GGbM06c",
            displayName = "Abu Simbel Temples",
            formattedAddress = "Abu Simbel, Aswan",
            location = LatLng(22.3372319, 31.625798999999997),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AJRVUZMu7c_fwcJoRTmEkjuqaiH8xSUcoET5QiYT_OnOGi3gnxTV7xZxPF9UoJu8IwfdoXkylgCDgsZzArPOmzgl5AM2qHWEmocLGlDeA-PpLNFjlm-qF-BIH9ATpnkESo1k53DsLQm1qrantfmLHQ=s4800-w4032-h3024".toUri()
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

