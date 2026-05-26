package com.besha.egyptguide.features.tickets.presentation.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.features.tickets.domain.models.TicketDetails
import com.besha.egyptguide.features.tickets.domain.models.TicketStatus
import com.besha.egyptguide.features.tickets.presentation.viewmodel.TicketsActions
import com.besha.egyptguide.features.tickets.presentation.viewmodel.TicketsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(
    ticketId: String,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel<TicketsViewModel>()
    val state by viewModel.viewStates.collectAsState()

    LaunchedEffect(ticketId) {
        viewModel.executeAction(TicketsActions.GetTicketDetails(ticketId))
    }

    Scaffold(
        containerColor = colorResource(R.color.white),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Digital Ticket",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            colorResource(R.color.blue).copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            when {
                state.ticketDetails.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(R.color.blue)
                    )
                }

                state.ticketDetails.errorThrowable != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Unable to load ticket", color = Color.Gray)
                        Button(
                            onClick = {
                                viewModel.executeAction(
                                    TicketsActions.GetTicketDetails(
                                        ticketId
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                state.ticketDetails.data != null -> {
                    TicketDetailsContent(state.ticketDetails.data!!)
                }
            }
        }
    }
}

@Composable
fun TicketDetailsContent(ticket: TicketDetails) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Digital Pass
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                // Header Image
                Box(modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()) {
                    AsyncImage(
                        model = if (ticket.imageUrl?.startsWith("/") == true) "http://10.0.2.2:8000${ticket.imageUrl}" else ticket.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Status Overlay
                    StatusBadge(
                        status = ticket.status,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                // Perforation Divider
                TicketPerforation()

                // Ticket Details
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = ticket.name ?: "Monument Visit",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailInfo(
                            label = "DATE",
                            value = ticket.bookingDate ?: "N/A",
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(3f)
                        )
                        DetailInfo(
                            label = "VERIFIED",
                            value = ticket.verifiedAt ?: "N/A",
                            icon = Icons.Default.Verified,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "TICKET NUMBER",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "#${ticket.id?.take(10)?.uppercase() ?: "--------"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.blue)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = colorResource(R.color.blue).copy(alpha = 0.2f)
                        )
                    }
                }
                Log.d("TicketDetailsContent", "TicketDetailsContent: ${ticket.bookingDate}")
                Text(
                    text = "Visit Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                val location = LatLng(
                    ticket.latitude ?: 0.0,
                    ticket.longitude ?: 0.0
                )

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        scrollGesturesEnabled = true
                    )
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = ticket.name
                    )
                }
            }
        }
    }
}


@Composable
fun StatusBadge(status: TicketStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        TicketStatus.APPROVED -> Color(0xFF4CAF50)
        TicketStatus.DECLINED -> Color(0xFFF44336)
        TicketStatus.PENDING -> Color(0xFFFFC107)
        else -> Color.Transparent
    }
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            color = if (status == TicketStatus.PENDING) Color.Black else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun TicketPerforation() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color.White)
    ) {
        // Left Cutout
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-15).dp)
                .clip(CircleShape)
                .background(colorResource(R.color.off_white))
        )

        Canvas(modifier = Modifier
            .weight(1f)
            .height(1.dp)) {
            drawLine(
                color = Color.LightGray.copy(alpha = 0.6f),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
                strokeWidth = 2f
            )
        }

        // Right Cutout
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = 15.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.off_white))
        )
    }
}

@Composable
fun DetailInfo(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = colorResource(R.color.blue), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
