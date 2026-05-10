package com.besha.egyptguide.features.profile.presenation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.presenation.screen.components.ProfileInfoItemRow
import com.besha.egyptguide.features.profile.presenation.screen.components.ProfileNavigateItemRow
import com.besha.egyptguide.features.profile.presenation.viewmodel.ProfileActions
import com.besha.egyptguide.features.profile.presenation.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(rootController: NavController, childController: NavController) {

    val context = LocalContext.current
    val profileViewModel = hiltViewModel<ProfileViewModel>()

    val state by profileViewModel.viewStates.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.executeAction(ProfileActions.GetProfile)
    }

    var showNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFoodPreferencesDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileViewModel.executeAction(
                ProfileActions.UpdateProfile(
                    UpdateProfileRequest(photoUri = profileUriToMultipart(context, it))
                )
            )
        }
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text(text = "Update Name") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editedName.isNotBlank()) {
                        profileViewModel.executeAction(
                            ProfileActions.UpdateProfile(UpdateProfileRequest(name = editedName))
                        )
                        showNameDialog = false
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(text = "Select Language") },
            text = {
                Column {
                    listOf("en", "ar").forEach { lang ->
                        Text(
                            text = if (lang == "en") "English" else "Arabic",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    profileViewModel.executeAction(
                                        ProfileActions.UpdateProfile(UpdateProfileRequest(language = lang))
                                    )
                                    showLanguageDialog = false
                                }
                                .padding(16.dp),
                            fontSize = 16.sp,
                            color = colorResource(R.color.black)
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showFoodPreferencesDialog) {
        FoodPreferencesDialog(
            onDismiss = { showFoodPreferencesDialog = false },
            onSave = { lunchCategory, dinnerCategory , lunchTime, dinnerTime ->
                profileViewModel.executeAction(
                    ProfileActions.ScheduleFoodAlarms(lunchCategory, lunchTime)
                )
                profileViewModel.executeAction(ProfileActions.ScheduleFoodAlarms(dinnerCategory,dinnerTime))
                showFoodPreferencesDialog = false
                Toast.makeText(context, "Food reminders set for lunch and dinner!", Toast.LENGTH_SHORT).show()
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        when {
            state.profile.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.blue)
                )
            }

            state.profile.errorThrowable != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Couldn't load profile", color = Color.Gray)
                    Button(
                        onClick = { profileViewModel.executeAction(ProfileActions.GetProfile) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
            state.profile.data != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {


                    // Account Section
                    Text(
                        text = "Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                val profilePic = state.profile.data?.photo_url


                                if (!profilePic.isNullOrEmpty())

                                    if (profilePic.startsWith("http"))
                                        AsyncImage(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(54.dp),
                                            model = profilePic.toUri(),
                                            contentDescription = "profile pic",
                                            contentScale = ContentScale.Crop,
                                        )
                                    else
                                        AsyncImage(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(54.dp),
                                            model = "http://10.0.2.2:8000$profilePic".toUri(),
                                            contentDescription = "profile pic",
                                            contentScale = ContentScale.Crop,
                                        )
                                else
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colorResource(R.color.blue)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val initials =
                                            state.profile.data?.name?.take(1)?.uppercase() ?: "?"
                                        Text(
                                            text = initials,
                                            color = colorResource(R.color.white),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }


                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Welcome,",
                                        color = colorResource(R.color.black),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = state.profile.data?.name ?: "Guest",
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(R.color.black),
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = colorResource(R.color.black),
                                modifier = Modifier.clickable(
                                    onClick = {
                                        profileViewModel.executeAction(ProfileActions.LogOut)
                                        rootController.navigate(ScreenResources.AuthRoute) {
                                            popUpTo(ScreenResources.MainRoute) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            )
                        }
                    }


                    // Profile info Section
                    Text(
                        text = "Profile info",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            ProfileInfoItemRow("Edit profile photo") {
                                photoPickerLauncher.launch("image/*")
                            }
                            HorizontalDivider(color = colorResource(R.color.gray))
                            ProfileInfoItemRow("Name", state.profile.data?.name) {
                                editedName = state.profile.data?.name ?: ""
                                showNameDialog = true
                            }
                            HorizontalDivider(color = colorResource(R.color.gray))
                            ProfileInfoItemRow("Language", if(state.profile.data?.language == "en") "English" else "Arabic") {
                                showLanguageDialog = true
                            }
                        }
                    }

                    // Preferences & Gamification
                    Text(
                        text = "Preferences",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            NotificationPermissionToggle(
                                state.notification,
                            ) {
                                profileViewModel.executeAction(ProfileActions.ToggleNotification(it))
                            }
                            HorizontalDivider(color = colorResource(R.color.gray))

                            ProfileNavigateItemRow(
                                title = "Food preferences",
                                enabled = state.notification
                            ) {
                                showFoodPreferencesDialog = true
                            }
                        }
                    }

                    Text(
                        text = "Stats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            ProfileNavigateItemRow("Leaderboard") {
                                childController.navigate(
                                    ScreenResources.LeaderboardRoute(
                                        userId = state.profile.data?.id ?: ""
                                    )
                                )
                            }
                        }
                    }

                    // Tickets Section
                    Text(
                        text = "Tickets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            ProfileNavigateItemRow("Tickets") {
                                childController.navigate(ScreenResources.TicketsRoute)
                            }
                        }
                    }
                }
            }
        }
    }
}
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
@Composable
fun TimePickerCard(
    modifier: Modifier = Modifier,
    title: String,
    hour: Int,
    minute: Int,
    color: Color,
    onTimeChange: (Int, Int) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 12.sp, color = color)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                VerticalStepper(value = hour, range = 0..23) { onTimeChange(it, minute) }
                Text(":", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                VerticalStepper(value = minute, range = 0..59) { onTimeChange(hour, it) }
            }
        }
    }
}

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

data class FoodCategory(val name: String, val icon: ImageVector, val color: Color)

private fun profileUriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
    file.outputStream().use { output -> inputStream.copyTo(output) }
    val requestFile = file.asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData("photo", file.name, requestFile)
}

@Composable
fun SettingRowSwitch(
    title: String,
    description: String = "",
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = title, color = colorResource(R.color.black), fontSize = 15.sp)
            if (description.isNotEmpty())
                Text(
                text = description,
                color = colorResource(R.color.gray),
                fontSize = 11.sp
            )
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
fun NotificationPermissionToggle(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onToggle(granted)
    }

    SettingRowSwitch(
        title = "Notifications",
        description = "Enable to set food reminders and receive updates.",
        checked = isEnabled,
        onToggle = { checked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted && checked) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onToggle(checked)
                }
            } else {
                val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
                if (!notificationsEnabled && checked) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra("app_package", context.packageName)
                        putExtra("app_uid", context.applicationInfo.uid)
                    }
                    context.startActivity(intent)
                    onToggle(false)
                } else {
                    onToggle(checked)
                }
            }
        }
    )
}
