package com.besha.egyptguide.features.maps.presentaion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.besha.egyptguide.R
import com.besha.egyptguide.features.maps.domain.model.MapsPlace
import java.util.Locale

@Composable
fun NearbyPlacesSheet(
    places: List<MapsPlace>,
    onPlaceClick: (MapsPlace) -> Unit,
    onCloseClick: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.55f)
            .background(colorResource(R.color.surface_white))
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

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.nearby_places),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.text_primary),
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "${places.size} results found",
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_secondary),
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.stroke_gray))
                    .clickable { onCloseClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colorResource(R.color.text_secondary),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.divider_color),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(places) { place ->
                NearbyPlaceItem(place = place, onClick = { onPlaceClick(place) })
            }
        }
    }
}

@Composable
fun NearbyPlaceItem(place: MapsPlace, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.surface_white),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, colorResource(R.color.divider_color)
        ),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box with gradient
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                colorResource(R.color.blue).copy(alpha = 0.12f),
                                colorResource(R.color.blue).copy(alpha = 0.06f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colorResource(R.color.blue),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.displayName ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colorResource(R.color.text_primary),
                    maxLines = 1
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = place.formattedAddress ?: "",
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_secondary),
                    maxLines = 1
                )

                if (place.distanceMeters != null || place.duration != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (place.duration != null) {
                            Text(
                                text = formatDuration(place.duration),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.blue)
                            )
                        }
                        if (place.duration != null && place.distanceMeters != null) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                        if (place.distanceMeters != null) {
                            Text(
                                text = formatDistance(place.distanceMeters),
                                fontSize = 11.sp,
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDistance(meters: Int?): String {
    if (meters == null) return ""
    return if (meters < 1000) "${meters}m" else String.format(Locale.getDefault(), "%.1fkm", meters / 1000.0)
}

private fun formatDuration(durationStr: String?): String {
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
