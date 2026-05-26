package com.besha.egyptguide.features.maps.presentaion.screen

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.besha.egyptguide.features.maps.presentaion.components.NearbyPlacesSheet
import com.besha.egyptguide.features.maps.presentaion.components.SelectedPlacesSheet
import com.besha.egyptguide.features.maps.presentaion.viewmodel.MapsActions
import com.besha.egyptguide.features.maps.presentaion.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    category: String?,
    onNavigateToDetails: (ScreenResources.PlaceDetailsRoute) -> Unit,
) {

    val viewModel = hiltViewModel<MapsViewModel>()
    val state by viewModel.viewStates.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val focusManager = LocalFocusManager.current

    var isSearchFocused by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            viewModel.executeAction(MapsActions.GetCurrentLocation)
        }
    }



    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Animate to current location once
    LaunchedEffect(state.currentLocation.data) {
        state.currentLocation.data?.let { latLng ->
            if (!state.isCurrentlocationLoaded) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                    durationMs = 800
                )
                viewModel.executeAction(MapsActions.CurrentLocationLoaded)
                if (category != null) {
                    Log.d("TAG", "MapsScreen: $category")
                    Log.d("TAG", "MapsScreen: $latLng")
                    viewModel.executeAction(
                        MapsActions.SearchByText(
                            latLng,
                            category
                        )
                    )
                }

            }
        }
    }


    // Expand sheet when nearby places arrive
    LaunchedEffect(state.nearByPlaces.data) {
        if (!state.nearByPlaces.data.isNullOrEmpty()) {
            scaffoldState.bottomSheetState.expand()
            state.currentLocation.data?.let { latLng ->

                val offsetLatLng = LatLng(
                    latLng.latitude - 0.01,   // move camera target slightly north
                    latLng.longitude
                )

                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(offsetLatLng, 12f),
                    durationMs = 800
                )
            }
        }

        isSearchFocused = false

        if (state.selectedPlace.data == null && state.nearByPlaces.data.isNullOrEmpty() && scaffoldState.bottomSheetState.isVisible) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    // Animate when a place is selected
    LaunchedEffect(state.selectedPlace.data) {
        state.selectedPlace.data?.location?.let { latLng ->
            scaffoldState.bottomSheetState.expand()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                durationMs = 800
            )
        }

        isSearchFocused = false

        if (state.selectedPlace.data == null && state.nearByPlaces.data.isNullOrEmpty() && scaffoldState.bottomSheetState.isVisible) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (state.nearByPlaces.data.isNullOrEmpty() && state.selectedPlace.data == null) 0.dp else 100.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 16.dp,
        sheetDragHandle = null, // Custom handle in components
        sheetContent = {
            if (state.selectedPlace.data != null) {
                SelectedPlacesSheet(
                    place = state.selectedPlace.data!!,
                    onBackClick = {
                        viewModel.executeAction(MapsActions.EmptySelectedPlace)
                    },
                    onDetailsClick = { place ->
                        onNavigateToDetails(
                            ScreenResources.PlaceDetailsRoute(
                                id = place.id,
                                displayName = place.displayName,
                                formattedAddress = place.formattedAddress,
                                imageUri = place.imageUri?.toString(),
                                lat = place.location?.latitude ?: 0.0,
                                lng = place.location?.longitude ?: 0.0
                            )
                        )
                    }
                )
            } else if (!state.nearByPlaces.data.isNullOrEmpty()) {
                NearbyPlacesSheet(
                    places = state.nearByPlaces.data!!,
                    onPlaceClick = { place ->
                        place.location?.let {
                            viewModel.executeAction(
                                MapsActions.SelectPlace(
                                    place.id!!,
                                    state.sessionToken
                                )
                            )
                        }
                    },
                    onCloseClick = {
                        viewModel.executeAction(MapsActions.EmptyNearBySearch)
                    }
                )
            } else {
                Box(Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Transparent))
            }
        },
    ) { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { focusManager.clearFocus() },
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,

                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false
                )
            ) {

                // Selected place marker
                state.selectedPlace.data?.location?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = state.selectedPlace.data?.displayName ?: "",
                        snippet = state.selectedPlace.data?.formattedAddress ?: "",
                    )
                }

                // Nearby markers
                state.nearByPlaces.data?.forEach { place ->
                    place.location?.let { latLng ->
                        Marker(
                            state = MarkerState(position = latLng),
                            title = place.displayName ?: "",
                            snippet = place.formattedAddress ?: "",
                            onClick = { _->
                                viewModel.executeAction(
                                    MapsActions.SelectPlace(
                                        place.id!!,
                                        state.sessionToken
                                    )
                                )
                                return@Marker true
                            }
                        )
                    }
                }
            }

            // Search UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
            ) {

                TextField(
                    value = state.query,
                    onValueChange = {
                        viewModel.executeAction(
                            MapsActions.OnQueryChange(
                                it,
                                state.sessionToken
                            )
                        )
                        isSearchFocused = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .onFocusChanged {
                            isSearchFocused = it.isFocused
                        },
                    placeholder = {
                        Text(
                            stringResource(R.string.search_location),
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.executeAction(
                                    MapsActions.ResetState(
                                        state.sessionToken,
                                        state.currentLocation.data!!,
                                        state.isCurrentlocationLoaded
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.clear),
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colorResource(R.color.pink)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = colorResource(R.color.pink)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.predictions.data?.let { predictionsList ->
                    if (state.query.isNotEmpty() && isSearchFocused) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .heightIn(max = 300.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {

                            LazyColumn {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                state.currentLocation.data?.let {
                                                    viewModel.executeAction(
                                                        MapsActions.SearchByText(
                                                            it,
                                                            state.query
                                                        )
                                                    )
                                                }
                                                focusManager.clearFocus()
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(36.dp),
                                            shape = CircleShape,
                                            color = Color(0xFFFCE4EC)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                modifier = Modifier.padding(8.dp),
                                                tint = colorResource(R.color.pink)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                            Text(
                                                text = state.query,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = stringResource(R.string.search_nearby_places),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                items(predictionsList) { prediction ->
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = Color.LightGray
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.executeAction(
                                                    MapsActions.OnQueryChange(
                                                        prediction.getPrimaryText(null).toString(),
                                                        state.sessionToken
                                                    )
                                                )
                                                viewModel.executeAction(
                                                    MapsActions.SelectPlace(
                                                        prediction.placeId,
                                                        state.sessionToken
                                                    )
                                                )
                                                viewModel.executeAction(
                                                    MapsActions.EmptyNearBySearch
                                                )
                                                focusManager.clearFocus()
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.location_ic),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                            Text(
                                                text = prediction.getPrimaryText(null).toString(),
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = prediction.getSecondaryText(null).toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        MapsGenreList {
                            state.currentLocation.data?.let { location ->
                                viewModel.executeAction(
                                    MapsActions.NearBySearch(
                                        location,
                                        listOf(it)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MapsGenreList(onCardClick: (String) -> Unit) {


    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        items(GenreType.entries) { genre ->


            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable(onClick = {
                        onCardClick(genre.placeTypes)
                    }
                    ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.off_white))
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),


                    ) {

                    Icon(
                        painter = painterResource(id = genre.icon),
                        contentDescription = genre.title,
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color.Black
                    )

                    Text(
                        text = genre.title,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

