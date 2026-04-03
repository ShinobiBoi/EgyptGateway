package com.besha.egyptguide.features.maps.presentaion.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
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
    viewModel: MapsViewModel = hiltViewModel()
) {

    val state by viewModel.viewStates.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val focusManager = LocalFocusManager.current

    var isSearchFocused by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState()

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
        if (state.selectedPlace.data == null && state.nearByPlaces.data.isNullOrEmpty() && scaffoldState.bottomSheetState.isVisible) {

            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (state.nearByPlaces.data.isNullOrEmpty()) 0.dp else 32.dp,
        sheetContent = {
            if (state.selectedPlace.data != null) {
                SelectedPlacesSheet(
                    place = state.selectedPlace.data!!
                ) {
                    viewModel.executeAction(MapsActions.EmptySelectedPlace)

                }
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
                Spacer(modifier = Modifier.height(1.dp))
            }
        },

        ) {

        Box(modifier = Modifier.fillMaxSize()) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { focusManager.clearFocus() },
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                )
            ) {

                // Selected place marker
                state.selectedPlace.data?.location?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = state.selectedPlace.data?.displayName ?: "",
                        snippet = state.selectedPlace.data?.formattedAddress ?: ""
                    )
                }

                // Nearby markers
                state.nearByPlaces.data?.forEach { place ->
                    place.location?.let { latLng ->
                        Marker(
                            state = MarkerState(position = latLng),
                            title = place.displayName ?: "",
                            snippet = place.formattedAddress ?: ""
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .onFocusChanged {
                            isSearchFocused = it.isFocused
                        },
                    placeholder = { Text(stringResource(R.string.search_location)) },
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
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.predictions.data?.let { predictionsList ->
                    if (state.query.isNotEmpty()) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .heightIn(max = 300.dp),
                            shape = RoundedCornerShape(12.dp)
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
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(text = state.query)
                                            Text(
                                                text = stringResource(R.string.search_nearby_places),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }

                                items(predictionsList) { prediction ->
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

                                                focusManager.clearFocus()
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = prediction.getPrimaryText(null).toString()
                                            )
                                            Text(
                                                text = prediction.getSecondaryText(null).toString(),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        MapsGenreList{

                            state.currentLocation.data?.let {location ->
                                viewModel.executeAction(MapsActions.NearBySearch(location, listOf(it)))

                            }
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun MapsGenreList(onCardClick:(String)->Unit) {


    val genreList = listOf(
        GenreIconListItem( "Hotels", R.drawable.hotels_ic, "hotel"),
        GenreIconListItem( "Restaurants", R.drawable.restaurant_ic,PlaceTypes.RESTAURANT),
        GenreIconListItem( "Cafes", R.drawable.cafe_ic, PlaceTypes.CAFE),
        GenreIconListItem( "Malls", R.drawable.mall_ic, PlaceTypes.SHOPPING_MALL),
        GenreIconListItem( "Gas stations", R.drawable.gas_ic, PlaceTypes.GAS_STATION),
        GenreIconListItem( "Hospitals", R.drawable.hospital_ic, PlaceTypes.HOSPITAL),
        GenreIconListItem( "Churches", R.drawable.church_ic, PlaceTypes.CHURCH),
        GenreIconListItem( "Mosques", R.drawable.mosque_ic, PlaceTypes.MOSQUE),
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth().padding(top = 8.dp,start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        items(genreList) { genre ->


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

data class GenreIconListItem(
    val title: String,
    val icon: Int,
    val placeTypes: String
)
