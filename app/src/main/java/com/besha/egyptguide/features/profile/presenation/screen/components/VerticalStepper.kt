package com.besha.egyptguide.features.profile.presenation.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.besha.egyptguide.R

@Composable
fun VerticalStepper(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = colorResource(R.color.blue))
        }
        Text(
            text = value.toString().padStart(2, '0'),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Color.Black
        )
        IconButton(
            onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colorResource(R.color.blue))
        }
    }
}