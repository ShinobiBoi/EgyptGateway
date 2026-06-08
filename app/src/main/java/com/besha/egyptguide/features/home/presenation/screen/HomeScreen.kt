package com.besha.egyptguide.features.home.presenation.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.home.presenation.components.HomeBanner
import com.besha.egyptguide.features.home.presenation.components.HomeGenreList
import com.besha.egyptguide.features.home.presenation.components.PlacesHorizontalList
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeActions
import com.besha.egyptguide.features.home.presenation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onPlaceClick: (MyPlace) -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToMonuments: () -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state by viewModel.viewStates.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 10 })
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.executeAction(HomeActions.GetCurrentLocation)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        viewModel.executeAction(HomeActions.GetMonuments)
    }

    LaunchedEffect(state.location) {
        state.location.data?.let {
            if (state.places.data == null) {
                viewModel.executeAction(HomeActions.SelectGenre(state.selectedGenre, it))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dim))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Hero / Banner block ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.52f)
            ) {
                // Banner
                state.monuments.data?.let {
                    HomeBanner(
                        modifier = Modifier.fillMaxSize(),
                        monuments = it,
                        pagerState = pagerState,
                        screenHeight = screenHeight,
                        onClick = { onNavigateToMonuments() }
                    )
                }

                // Top bar — greeting + scan button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Greeting block
                    Column {
                        Text(
                            text = "Discover Egypt",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Explore monuments & places",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Scan & Earn pill
                    Surface(
                        onClick = onNavigateToCamera,
                        shape = RoundedCornerShape(14.dp),
                        color = colorResource(R.color.blue).copy(alpha = 0.92f),
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Scan",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(7.dp))
                            Column {
                                Text(
                                    "SCAN",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    lineHeight = 13.sp
                                )
                                Text(
                                    "& EARN",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }

                // Bottom fade on banner for seamless blend into sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, colorResource(R.color.blue_dim))
                            )
                        )
                )
            }

            // ── Content sheet ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(colorResource(R.color.blue_dim))
            ) {

                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp, bottom = 4.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.text_secondary).copy(alpha = 0.2f))
                )

                Spacer(Modifier.height(8.dp))

                // ── Category genre chips ──────────────────────────────────
                HomeSectionLabel(
                    label = "Categories",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(12.dp))

                HomeGenreList(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    selectedGenre = state.selectedGenre
                ) { genre ->
                    val location = state.location.data ?: return@HomeGenreList
                    viewModel.executeAction(HomeActions.SelectGenre(genre, location))
                }

                Spacer(Modifier.height(26.dp))

                // ── Nearby section ────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeSectionLabel(label = stringResource(R.string.near_by_locations))

                    // Live dot badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorResource(R.color.blue_surface))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colorResource(R.color.blue))
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.blue)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                PlacesHorizontalList(
                    state.places,
                    state.location.data,
                    onPlaceClick
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colorResource(R.color.text_primary),
        letterSpacing = (-0.3).sp,
        modifier = modifier
    )
}