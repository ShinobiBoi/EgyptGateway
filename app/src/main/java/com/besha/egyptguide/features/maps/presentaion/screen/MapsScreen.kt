package com.besha.egyptguide.features.maps.presentaion.screen

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.besha.egyptguide.features.maps.data.dto.request.MyLatLng
import com.besha.egyptguide.features.maps.presentaion.components.NearbyPlacesSheet
import com.besha.egyptguide.features.maps.presentaion.components.SelectedPlacesSheet
import com.besha.egyptguide.features.maps.presentaion.utils.PolylineDecoder
import com.besha.egyptguide.features.maps.presentaion.viewmodel.MapsActions
import com.besha.egyptguide.features.maps.presentaion.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) viewModel.executeAction(MapsActions.GetCurrentLocation)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(state.currentLocation.data) {
        state.currentLocation.data?.let { latLng ->
            if (!state.isCurrentlocationLoaded) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f), 800)
                viewModel.executeAction(MapsActions.CurrentLocationLoaded)
                if (category != null) {
                    viewModel.executeAction(MapsActions.SearchByText(latLng, category))
                }
            }
        }
    }

    LaunchedEffect(state.nearByPlaces.data) {
        if (!state.nearByPlaces.data.isNullOrEmpty()) {
            scaffoldState.bottomSheetState.expand()
            state.currentLocation.data?.let { latLng ->
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(latLng.latitude - 0.02, latLng.longitude), 12f), 800
                )
            }
        }
        isSearchFocused = false
        if (state.selectedPlace.data == null && state.nearByPlaces.data.isNullOrEmpty() && scaffoldState.bottomSheetState.isVisible) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    LaunchedEffect(state.selectedPlace.data) {
        state.selectedPlace.data?.location?.let { latLng ->
            val origin = state.currentLocation.data
            if (origin != null) {
                viewModel.executeAction(MapsActions.GetMapsRoutes(destination = MyLatLng(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                ), origin = MyLatLng(latitude = origin.latitude, longitude =  origin.longitude)))
            }
            
            scaffoldState.bottomSheetState.expand()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(latLng.latitude-0.001, latLng.longitude), 16f), 800)
        }
        isSearchFocused = false
        if (state.selectedPlace.data == null && state.nearByPlaces.data.isNullOrEmpty() && scaffoldState.bottomSheetState.isVisible) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (state.nearByPlaces.data.isNullOrEmpty() && state.selectedPlace.data == null) 0.dp else 110.dp,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContainerColor = colorResource(R.color.surface_white),
        sheetShadowElevation = 20.dp,
        sheetDragHandle = null,
        sheetContent = {
            when {
                state.selectedPlace.data != null -> SelectedPlacesSheet(
                    place = state.selectedPlace.data!!,
                    onBackClick = { viewModel.executeAction(MapsActions.EmptySelectedPlace) },
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
                !state.nearByPlaces.data.isNullOrEmpty() -> NearbyPlacesSheet(
                    places = state.nearByPlaces.data!!,
                    onPlaceClick = { place ->
                        place.location?.let {
                            viewModel.executeAction(MapsActions.SelectPlace(place.id!!, state.sessionToken))
                        }
                    },
                    onCloseClick = { viewModel.executeAction(MapsActions.EmptyNearBySearch) }
                )
                else -> Box(Modifier.fillMaxWidth().height(1.dp).background(Color.Transparent))
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // ── Map ───────────────────────────────────────────────────────
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { focusManager.clearFocus() },
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
            ) {

                if (state.selectedPlace.data==null)
                // Nearby markers — pink accent
                state.nearByPlaces.data?.forEach { place ->
                    place.location?.let { latLng ->
                        MarkerComposable(
                            state = MarkerState(position = latLng),
                            title = place.displayName ?: "",
                            snippet = place.formattedAddress ?: "",
                            onClick = {
                                viewModel.executeAction(MapsActions.SelectPlace(place.id!!, state.sessionToken))
                                true
                            }
                        ) {
                            ModernMarker(
                                color = colorResource(R.color.blue),
                                isSelected = false
                            )
                        }
                    }
                }

                // Selected place marker
                state.selectedPlace.data?.location?.let { latLng ->
                    val firstRoute = state.routes.data?.routes?.firstOrNull()
                    MarkerComposable(
                        state = MarkerState(position = latLng),
                        title = state.selectedPlace.data?.displayName ?: "",
                        snippet = state.selectedPlace.data?.formattedAddress ?: "",
                        
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (firstRoute != null) {
                                RouteInfoLabel(
                                    duration = formatDuration(firstRoute.duration),
                                    distance = formatDistance(firstRoute.distanceMeters)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            ModernMarker(
                                color = colorResource(R.color.blue),
                                isSelected = true
                            )
                        }
                    }
                }

                // Show Route Polyline
                if (state.selectedPlace.data != null)
                state.routes.data?.routes?.forEach { route ->
                    route.polyline?.encodedPolyline?.let { encoded ->
                        val points = PolylineDecoder.decode(encoded)
                        Polyline(
                            points = points,
                            color = colorResource(R.color.blue),
                            width = 12f
                        )
                    }
                }
            }

            // ── Search overlay ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                // Search bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(
                            elevation = if (isSearchFocused) 12.dp else 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = colorResource(R.color.blue).copy(alpha = 0.15f),
                            spotColor = colorResource(R.color.blue).copy(alpha = 0.2f)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(colorResource(R.color.surface_white))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isSearchFocused) colorResource(R.color.blue) else colorResource(R.color.text_secondary),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = state.query,
                            onValueChange = {
                                viewModel.executeAction(MapsActions.OnQueryChange(it, state.sessionToken))
                                isSearchFocused = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { isSearchFocused = it.isFocused },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = colorResource(R.color.text_primary),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            decorationBox = { inner ->
                                if (state.query.isEmpty()) {
                                    Text(
                                        stringResource(R.string.search_location),
                                        color = colorResource(R.color.text_secondary),
                                        fontSize = 15.sp
                                    )
                                }
                                inner()
                            }
                        )
                        AnimatedVisibility(
                            visible = state.query.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colorResource(R.color.blue_surface))
                                    .clickable {
                                        viewModel.executeAction(
                                            MapsActions.ResetState(
                                                state.sessionToken,
                                                state.currentLocation.data!!,
                                                state.isCurrentlocationLoaded
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.clear),
                                    tint = colorResource(R.color.blue),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Autocomplete dropdown
                AnimatedVisibility(
                    visible = state.query.isNotEmpty() && isSearchFocused && !state.predictions.data.isNullOrEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .heightIn(max = 300.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        LazyColumn {
                            // "Search nearby" fast-action row
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            state.currentLocation.data?.let {
                                                viewModel.executeAction(MapsActions.SearchByText(it, state.query))
                                            }
                                            focusManager.clearFocus()
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colorResource(R.color.blue_surface)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null,
                                            tint = colorResource(R.color.blue),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = state.query,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.text_primary)
                                        )
                                        Text(
                                            text = stringResource(R.string.search_nearby_places),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorResource(R.color.text_secondary)
                                        )
                                    }
                                }
                            }

                            items(state.predictions.data ?: emptyList()) { prediction ->
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    thickness = 0.5.dp,
                                    color = colorResource(R.color.divider_color)
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
                                                MapsActions.SelectPlace(prediction.placeId, state.sessionToken)
                                            )
                                            viewModel.executeAction(MapsActions.EmptyNearBySearch)
                                            focusManager.clearFocus()
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colorResource(R.color.stroke_gray)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = colorResource(R.color.text_secondary),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = prediction.getPrimaryText(null).toString(),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.text_primary),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = prediction.getSecondaryText(null).toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorResource(R.color.text_secondary),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Genre chips (shown when not searching)
                AnimatedVisibility(
                    visible = !(state.query.isNotEmpty() && isSearchFocused),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    MapsGenreList {
                        state.currentLocation.data?.let { location ->
                            viewModel.executeAction(MapsActions.NearBySearch(location, listOf(it)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteInfoLabel(duration: String, distance: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.5.dp, colorResource(R.color.blue))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = duration,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(R.color.blue)
            )
            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.Gray))
            Text(
                text = distance,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.text_secondary)
            )
        }
    }
}

fun formatDistance(meters: Int?): String {
    if (meters == null) return ""
    return if (meters < 1000) "${meters}m" else String.format(Locale.getDefault(), "%.1fkm", meters / 1000.0)
}

fun formatDuration(durationStr: String?): String {
    if (durationStr == null) return ""
    val seconds = durationStr.removeSuffix("s").toDoubleOrNull()?.toInt() ?: return durationStr
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}

// ── Custom map marker ─────────────────────────────────────────────────────────

@Composable
fun ModernMarker(color: Color, isSelected: Boolean) {
    Box(contentAlignment = Alignment.BottomCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(if (isSelected) 46.dp else 36.dp)
                    .shadow(
                        elevation = if (isSelected) 8.dp else 4.dp,
                        shape = RoundedCornerShape(
                            topStart = 50f, topEnd = 50f,
                            bottomStart = 50f, bottomEnd = 12f
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 50f, topEnd = 50f,
                            bottomStart = 50f, bottomEnd = 12f
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(color.copy(alpha = 0.85f), color)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(if (isSelected) 24.dp else 18.dp)
                )
            }
            // Pin tail
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(6.dp)
                    .background(color)
            )
        }

        // Pulse ring for selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f))
                    .align(Alignment.Center)
            )
        }
    }
}

// ── Genre chips ───────────────────────────────────────────────────────────────

@Composable
fun MapsGenreList(onCardClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(GenreType.entries) { genre ->
            Surface(
                onClick = { onCardClick(genre.placeTypes) },
                shape = RoundedCornerShape(14.dp),
                color = colorResource(R.color.surface_white),
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, colorResource(R.color.divider_color)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.blue_surface)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = genre.icon),
                            contentDescription = genre.title,
                            modifier = Modifier.size(14.dp),
                            tint = colorResource(R.color.blue)
                        )
                    }
                    Text(
                        text = genre.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.text_primary)
                    )
                }
            }
        }
    }
}