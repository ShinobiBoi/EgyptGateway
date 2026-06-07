package com.besha.egyptguide.features.maps.presentaion.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.data.model.MyPlace

@Composable
fun SelectedPlacesSheet(
    place: MyPlace,
    onBackClick: () -> Unit,
    onDetailsClick: (MyPlace) -> Unit // kept but not used anymore if you want
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.surface_white))
            .padding(bottom = 28.dp)
    ) {

        // Drag handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 16.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.text_secondary).copy(alpha = 0.2f))
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.blue_surface))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(R.color.blue),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = stringResource(R.string.selected_place),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(R.color.text_primary)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = place.imageUri,
                    contentDescription = place.displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.location_ic)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = place.displayName ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = colorResource(R.color.text_primary)
            )

            Spacer(Modifier.height(8.dp))

            // Address (REMOVED pink → replaced with blue accent)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.blue_surface)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colorResource(R.color.blue),
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = place.formattedAddress ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.text_secondary),
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(22.dp))

            // ✅ GET DIRECTIONS BUTTON (Google Maps Intent)
            Button(
                onClick = {
                    val lat = place.location?.latitude
                    val lng = place.location?.longitude

                    if (lat != null && lng != null) {
                        val uri = Uri.parse("google.navigation:q=$lat,$lng")
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.blue),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Get Directions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}