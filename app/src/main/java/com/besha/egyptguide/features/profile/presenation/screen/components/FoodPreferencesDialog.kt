package com.besha.egyptguide.features.profile.presenation.screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.besha.egyptguide.R
import com.besha.egyptguide.features.profile.domain.model.FoodCategory
import java.time.LocalTime


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FoodPreferencesDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, LocalTime, LocalTime) -> Unit
) {
    val categories = listOf(
        FoodCategory("Burger", Icons.Default.LunchDining, Color(0xFFFF9800)),
        FoodCategory("Pizza", Icons.Default.LocalPizza, Color(0xFFE91E63)),
        FoodCategory("Sushi", Icons.Default.SetMeal, Color(0xFF2196F3)),
        FoodCategory("Grills", Icons.Default.OutdoorGrill, Color(0xFF4CAF50)),
    )

    var selectedLunchCategory by remember { mutableStateOf(categories[0].name) }
    var selectedDinnerCategory by remember { mutableStateOf(categories[0].name) }

    var lunchHour by remember { mutableIntStateOf(14) }
    var lunchMinute by remember { mutableIntStateOf(0) }

    var dinnerHour by remember { mutableIntStateOf(20) }
    var dinnerMinute by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(colorResource(R.color.blue).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = colorResource(R.color.blue),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Daily Food Plan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                // 🍔🍽️ Categories Section
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                    // 🍔 Lunch Category
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Lunch Preference",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                val isSelected = selectedLunchCategory == category.name

                                Surface(
                                    onClick = { selectedLunchCategory = category.name },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected)
                                        category.color.copy(alpha = 0.15f)
                                    else Color.Transparent,
                                    border = if (isSelected)
                                        BorderStroke(2.dp, category.color)
                                    else
                                        BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            category.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected) category.color else Color.Gray
                                        )
                                        Text(
                                            text = category.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.Black else Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 🍽️ Dinner Category
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Dinner Preference",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                val isSelected = selectedDinnerCategory == category.name

                                Surface(
                                    onClick = { selectedDinnerCategory = category.name },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected)
                                        category.color.copy(alpha = 0.15f)
                                    else Color.Transparent,
                                    border = if (isSelected)
                                        BorderStroke(2.dp, category.color)
                                    else
                                        BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            category.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected) category.color else Color.Gray
                                        )
                                        Text(
                                            text = category.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.Black else Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ⏰ Time Pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimePickerCard(
                        modifier = Modifier.weight(1f),
                        title = "Lunch",
                        hour = lunchHour,
                        minute = lunchMinute,
                        color = Color(0xFFFFC107),
                        onTimeChange = { h, m ->
                            lunchHour = h
                            lunchMinute = m
                        }
                    )

                    TimePickerCard(
                        modifier = Modifier.weight(1f),
                        title = "Dinner",
                        hour = dinnerHour,
                        minute = dinnerMinute,
                        color = Color(0xFF3F51B5),
                        onTimeChange = { h, m ->
                            dinnerHour = h
                            dinnerMinute = m
                        }
                    )
                }

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Later", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onSave(
                                selectedLunchCategory,
                                selectedDinnerCategory,
                                LocalTime.of(lunchHour, lunchMinute),
                                LocalTime.of(dinnerHour, dinnerMinute)
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.blue)
                        )
                    ) {
                        Text("Save & Set Alarms", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}