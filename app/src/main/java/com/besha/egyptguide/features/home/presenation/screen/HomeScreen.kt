package com.besha.egyptguide.features.home.presenation.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeActions
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import androidx.core.net.toUri
import com.besha.egyptguide.features.home.presenation.components.HomeBanner
import com.besha.egyptguide.features.home.presenation.components.HomeGenreList
import com.besha.egyptguide.features.home.presenation.components.PlacesHorizontalList
import com.google.android.libraries.places.api.model.PlaceTypes
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@Composable
fun HomeScreen() {

    val pagerState = rememberPagerState(pageCount = { 10 })
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp


    var showDialog by remember() {
        mutableStateOf(
            false
        )
    }


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraUri
        }
    }

    val egyptPlaces = listOf(
        MyPlace(
            id = "ChIJzbs54n1PWBQRizZuWjV0dMo",
            displayName = "Giza Necropolis",
            formattedAddress = "Al Haram, Giza",
            location = LatLng(29.977296199999998, 31.132495499999997),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHTs79nKF_Ci53fecc1SDIU_pKslB1dowWA-6-LrcEQ84HEpx7urXmpLdyGEZJwlK3yYfQuBktC1XBTR_L9j6G2ThMn95X9wi3Ks0SHPUkqEVnsZklItA_blnuBORtuz7wWTvOPSCsFqEBkXw=s4800-w4800-h4408".toUri()
        ),
        MyPlace(
            id = "ChIJeamuo2JPWBQRqb1mQKbw08k",
            displayName = "Great Sphinx of Giza",
            formattedAddress = "Al Haram, Giza",
            location = LatLng(29.9752687, 31.1375674),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNEeW1LMSp4BhK7CxcITKqQzy82zSD02gKNvaI18TaswMFsrDiw_Ht5snGPa1sLtU25eANaUEm9ilashVAuGCHHQAGW-FfpJLhq5vfed3UGR2P9Vio4tSgNEfSra1lHBX5A9cvqFayfZ02TL=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJzcD-KJIVSRQR2FnCCMDoGsc",
            displayName = "Karnak",
            formattedAddress = "Karnak, Luxor",
            location = LatLng(25.718834599999997, 32.6572703),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNGpUEBYi6BrzXCEHSVUxFDvmfqsasbQini4nSVirXlyej8YA929CwnO700XuhMTcB9HL4M6pHkEJWX5y81MkWVNTrSlxgYjC4gpLaVMPcj-hqx-qIFlEv4UEb2rzlpdm0f7pNUWA-hVMThI9w=s4800-w4032-h3024".toUri()
        ),
        MyPlace(
            id = "ChIJ1_7etYo9SRQR2qnjovbMj3E",
            displayName = "Valley of the Kings",
            formattedAddress = "Luxor, Luxor",
            location = LatLng(25.7401643, 32.601411),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHfcPPIErOnbvCM0uIuuY_Fcr9cMgr-9tk6mryibZc5cmTpsnYix8Kwsiv-uGxYI1NzXD2NfdTwnVvNCYmb78OtgaXzjVvKCUvrEoSa6773M-nmC2WusqDSz9FlkTNDt3SSxYmREgxGSida5Mc=s4800-w4000-h3000".toUri()
        ),
        MyPlace(
            id = "ChIJ3ae_xxgWSRQR07VjFXrhHwM",
            displayName = "Mortuary Temple of Hatshepsut",
            formattedAddress = "Al Qarna, Luxor",
            location = LatLng(25.738277300000004, 32.6064906),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHFK1z45jyqMi5nfkeXML3B3uknXorhCq6u7aA12CUHdh8NwyYY7S66SNXmCWsSBkvS_fJzXN7Q39KyI3uoRhaPhgKz1ao3jHz8MZbrVipg9Xj3P7RRphNKjj3uuOrEnglf-Nku5NG-ta-b=s4800-w2048-h1431".toUri()
        ),
        MyPlace(
            id = "ChIJn1b3gKpAWBQRbsg1WfPe1NA",
            displayName = "Mosque of Muhammad Ali",
            formattedAddress = "Saladin, Cairo",
            location = LatLng(30.0290456, 31.259796599999998),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHH6_RKeEJW5fB6PYoAbN-g_yZpRFgESGrAo_-wqJ13sZOOrbVM85B95hjXOKEzwovfO2bd_iNdm8v2f94pq-tjYdZzbAakcH3T7sFFaJL4ekvhjICBVHZL26nDQPLkOYO-FOgOq3S2BWwtcQ=s4800-w2689-h2477".toUri()
        ),
        MyPlace(
            id = "ChIJn3WoRhBHWBQRuhsCgung4tQ",
            displayName = "Hanging Church",
            formattedAddress = "Old Cairo, Cairo",
            location = LatLng(30.0052389, 31.2301689),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNH_J_S4OQOnVnwiuzw0baAuhc289z6lFaqw5zwNXBtCvk9j3k3r3gVBPvg6t46bXRFlV1Pdcc2HuX-i-0mub6ZNzPBLQMe8oPdUNd5kNGQXCQZnhvobc9N3A87uQvVx6ngCq46IiRfRspB_sf4=s4800-w529-h398".toUri()
        ),
        MyPlace(
            id = "ChIJuxEpKFLD9RQRHz2y-AiDn-U",
            displayName = "Qaitbay Citadel",
            formattedAddress = "Al Gomrok, Alexandria ",
            location = LatLng(31.213698700000002, 29.8853921),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNEdOFZSryJLlEVUp4crTjTnwaXpmgFbHrdkhmvEl0Lcv7NYFZlVDEPL05t_EaLbGGHZLte0yGjmkHuGRhvLv93gPy4U2vJxM-Od4yy7HlfrYDWbTPpGVQ2duaKPBLDoHWNgshsyCgUVRR5cIA=s4800-w4800-h3600".toUri()
        ),
        MyPlace(
            id = "ChIJuXeJLNtiNhQReOUrg0ms8pk",
            displayName = "Philae Temple",
            formattedAddress = "Aswan 1, Aswan",
            location = LatLng(24.025583599999997, 32.8841021),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNFsI17xNawXiX8J8maxEy7QHda11H3K7bK0Z0YGWYnP73-SF5EGUbSiSxh59dl0XKu4vvwXKhnhjBRZjinDCUEeIe4axcgvzsmipx9bvSyBX9JEUuqYBNQUPBlY-6jSX5NacPdVexVYLS-YyA=s4800-w4032-h3024".toUri()
        ),
        MyPlace(
            id = "ChIJWwUmsYipOhQR0pj4GGbM06c",
            displayName = "Abu Simbel Temples",
            formattedAddress = "Abu Simbel, Aswan",
            location = LatLng(22.3372319, 31.625798999999997),
            imageUri = "https://lh3.googleusercontent.com/place-photos/AL8-SNHqdmKD6AvN9mShclZ7s6HE_en5qWOCW9q3H8D78bMQnD5z9LYqHopMcWRbjqoeg1phqRTs8zOuSg5LLytxLE5yRfgm3DxC6zJXLrod80bm-VT21LF5SXSiXwXnyWn0rerEtQ8jnpD8Z-6MpA=s4800-w4800-h3614".toUri()
        )
    )

    val viewModel = hiltViewModel<HomeViewModel>()

    val state by viewModel.viewStates.collectAsState()

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

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let {
            viewModel.executeAction(HomeActions.IdentifyPhoto(uriToMultipart(context, it)))
        }
    }
    LaunchedEffect(state.location) {
        state.location.data?.let {
            if (state.places.data == null){
               viewModel.executeAction(HomeActions.GetHotelPlaces(it))

            }
        }
    }




    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {},
            dismissButton = {},
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showDialog = false
                            cameraUri = createImageUri(context)
                            cameraUri?.let {
                                cameraLauncher.launch(it)

                            }
                        }
                    ) {
                        Text("Open Camera")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Text("Choose From Gallery")
                    }
                }
            }
        )
    }



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
            }
        }
    ) {
        val x = it


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

            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = screenHeight * 0.5f - 100.dp, start = 8.dp, end = 8.dp)
            ) {

                HomeGenreList(
                    modifier = Modifier.padding(top = 16.dp)
                ) { genre ->

                    val location = state.location.data ?: return@HomeGenreList

                    when (genre.placeTypes) {
                        "hotels" -> {
                            viewModel.executeAction(
                                HomeActions.GetHotelPlaces(location)
                            )
                        }

                        PlaceTypes.RESTAURANT -> {
                            viewModel.executeAction(
                                HomeActions.GetRestaurantsPlaces(location)
                            )
                        }

                        PlaceTypes.CAFE -> {
                            viewModel.executeAction(
                                HomeActions.GetCafePlaces(location)
                            )
                        }

                        PlaceTypes.SHOPPING_MALL -> {
                            viewModel.executeAction(
                                HomeActions.GetMallPlaces(location)
                            )
                        }
                    }
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


                PlacesHorizontalList(state.places,state.location.data)


            }
        }
    }

}


fun createImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "camera_image_",
        ".jpg",
        context.cacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {

    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)

    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = file
        .asRequestBody("image/*".toMediaType())

    return MultipartBody.Part.createFormData(
        "file",
        file.name,
        requestFile
    )
}

