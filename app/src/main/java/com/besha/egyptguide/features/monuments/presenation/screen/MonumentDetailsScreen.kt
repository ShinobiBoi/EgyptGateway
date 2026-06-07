package com.besha.egyptguide.features.monuments.presenation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.presenation.viewmodel.MonumentActions
import com.besha.egyptguide.features.monuments.presenation.viewmodel.MonumentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonumentDetailsScreen(
    monumentId: String,
    onBackClick: () -> Unit
) {
    val viewModel: MonumentViewModel = hiltViewModel()
    val state by viewModel.viewStates.collectAsState()
    var ratingsLimit by remember { mutableIntStateOf(5) }

    LaunchedEffect(monumentId) {
        viewModel.executeAction(MonumentActions.GetMonumentDetails(monumentId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dim))
    ) {
        if (state.selectedMonument.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(R.color.blue),
                strokeWidth = 3.dp
            )
        } else if (state.selectedMonument.data != null) {
            val monument = state.selectedMonument.data!!
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // ── Hero ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                ) {

                    AsyncImage(
                        model = monument.images?.firstOrNull() ?: "",
                        contentDescription = monument.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Scrim: bottom-heavy gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color.Black.copy(alpha = 0.15f),
                                    0.5f to Color.Transparent,
                                    0.75f to Color.Black.copy(alpha = 0.55f),
                                    1.0f to Color.Black.copy(alpha = 0.88f)
                                )
                            )
                    )

                    // Back button — glass pill
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(20.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Surface(
                            onClick = onBackClick,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, Color.White.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Hero text block
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 28.dp, vertical = 32.dp)
                    ) {
                        // Location chip
                        if (!monument.city.isNullOrEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(colorResource(R.color.blue).copy(alpha = 0.75f))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = monument.city ?: "",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        Text(
                            text = monument.name ?: "",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 36.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 8f
                                )
                            )
                        )

                        // Quick rating pill
                        if (state.ratingSummary.data != null) {
                            Log.d("RatingSummary", state.ratingSummary.data.toString())
                            Spacer(Modifier.height(14.dp))
                            val avg = state.ratingSummary.data!!.average_rating?.toFloat() ?: 0f
                            val count = state.ratingSummary.data!!.ratings_count ?: 0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.3f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = colorResource(R.color.gold_star),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = String.format("%.1f", avg),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "  ·  $count visitors",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // ── Content sheet ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(colorResource(R.color.blue_dim))
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp, bottom = 20.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.text_secondary).copy(alpha = 0.25f))
                    )

                    Column(modifier = Modifier.padding(horizontal = 22.dp)) {

                        // ── About ─────────────────────────────────────────
                        SectionLabel("About")
                        Spacer(Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color))
                        ) {
                            Text(
                                text = monument.description ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorResource(R.color.text_secondary),
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        // ── Ratings & Reviews ─────────────────────────────
                        if (state.ratingSummary.data != null) {
                            Spacer(Modifier.height(28.dp))
                            SectionLabel("Ratings & Reviews")
                            Spacer(Modifier.height(10.dp))
                            ModernRatingSummaryCard(state.ratingSummary.data!!)
                        }

                        // ── Review list ───────────────────────────────────
                        if (!state.ratings.data.isNullOrEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            state.ratings.data!!.forEach { rating ->
                                ModernReviewCard(rating)
                                Spacer(Modifier.height(10.dp))
                            }
                        } else if (state.ratings.data?.isEmpty() == true) {
                            Spacer(Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colorResource(R.color.surface_white))
                                    .border(1.dp, colorResource(R.color.divider_color), RoundedCornerShape(20.dp))
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No visitor reviews yet", color = colorResource(R.color.text_secondary), fontSize = 14.sp)
                            }
                        }

                        // ── Show More ─────────────────────────────────────
                        if (state.ratings.data != null && state.ratings.data!!.size >= ratingsLimit) {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    ratingsLimit += 5
                                    viewModel.executeAction(
                                        MonumentActions.GetMoreRatings(monumentId, ratingsLimit)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, colorResource(R.color.blue)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.blue))
                            ) {
                                if (state.ratings.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = colorResource(R.color.blue),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Load More Reviews", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colorResource(R.color.text_primary),
        letterSpacing = (-0.3).sp
    )
}

@Composable
fun ModernRatingSummaryCard(summary: RatingSummaryDto) {
    val breakdown = summary.rating_breakdown

    val five = breakdown?.five_stars ?: 0
    val four = breakdown?.four_stars ?: 0
    val three = breakdown?.three_stars ?: 0
    val two = breakdown?.two_stars ?: 0
    val one = breakdown?.one_star ?: 0
    val zero = breakdown?.zero_stars ?: 0

    val total = (five + four + three + two + one + zero).coerceAtLeast(1)

    val avg = summary.average_rating?.toFloat() ?: 0f
    val count = summary.ratings_count ?: total

    val fractionFor: (Int) -> Float = remember(total) {
        { starCount -> (starCount.toFloat() / total.toFloat()).coerceIn(0f, 1f) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Summary
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.1f", avg),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = colorResource(R.color.text_primary),
                    lineHeight = 52.sp
                )

                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(5) { i ->
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = if (i < avg.toInt()) colorResource(R.color.gold_star) else Color(0xFFE0E8F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "$count ratings",
                    fontSize = 11.sp,
                    color = colorResource(R.color.text_secondary),
                    fontWeight = FontWeight.Medium
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .width(1.dp)
                    .height(80.dp)
                    .background(colorResource(R.color.divider_color))
            )

            // RIGHT: Breakdown bars (REAL DATA)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val data = listOf(
                    5 to five,
                    4 to four,
                    3 to three,
                    2 to two,
                    1 to one
                )

                data.forEach { (star, countForStar) ->
                    val fraction = fractionFor(countForStar)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$star",
                            fontSize = 10.sp,
                            color = colorResource(R.color.text_secondary),
                            modifier = Modifier.width(10.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(colorResource(R.color.blue_light), colorResource(R.color.blue))
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernReviewCard(rating: RatingDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar circle with initials-style icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(colorResource(R.color.blue_light), colorResource(R.color.blue))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "E",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Explorer", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorResource(R.color.text_primary))
                        Text(rating.date ?: "", fontSize = 11.sp, color = colorResource(R.color.text_secondary))
                    }
                }

                // Star badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorResource(R.color.blue_surface))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = colorResource(R.color.gold_star), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${rating.rating}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = colorResource(R.color.blue)
                    )
                }
            }

            if (!rating.comment.isNullOrEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = colorResource(R.color.divider_color), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = rating.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.text_secondary),
                    lineHeight = 21.sp
                )
            }

            if (!rating.crowd_level.isNullOrEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.blue_surface))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👥",
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Crowd: ${rating.crowd_level}",
                        fontSize = 11.sp,
                        color = colorResource(R.color.blue),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}