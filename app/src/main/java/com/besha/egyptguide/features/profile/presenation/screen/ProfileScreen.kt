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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.leaderboard.presenation.viewmodel.LeaderboardActions
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.presenation.screen.components.ProfileInfoItemRow
import com.besha.egyptguide.features.profile.presenation.screen.components.ProfileNavigateItemRow
import com.besha.egyptguide.features.profile.presenation.viewmodel.ProfileActions
import com.besha.egyptguide.features.profile.presenation.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


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
                            text = lang,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    profileViewModel.executeAction(
                                        ProfileActions.UpdateProfile(UpdateProfileRequest(language = lang.lowercase()))
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
                    Text(text = "Couldn't load leaderboard", color = Color.Gray)
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


                    // Account Section//////////////////////////////////////////////////
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


                    // profile Section//////////////////////////////////////////////////
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
                            ProfileInfoItemRow("Language", state.profile.data?.language) {
                                showLanguageDialog = true
                            }
                            HorizontalDivider(color = colorResource(R.color.gray))
                            ProfileInfoItemRow("Change password") {
                                // childController.navigate(ScreenResources.FavouritesScreenRoute)
                            }
                        }
                    }

                    // Leaderboard Section//////////////////////////////////////////////////
                    Text(
                        text = "Leaderboard",
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
                            HorizontalDivider(color = colorResource(R.color.gray))
                            ProfileNavigateItemRow("Objectives") {
                                // childController.navigate(ScreenResources.FavouritesScreenRoute)
                            }
                        }
                    }


                    // Tickets Section//////////////////////////////////////////////////


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

private fun profileUriToMultipart(context: Context, uri: Uri): MultipartBody.Part {

    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)

    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = file
        .asRequestBody("image/jpeg".toMediaType())

    return MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestFile
    )
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
            if (description.isNotEmpty()) Text(
                text = description,
                color = colorResource(R.color.gray),
                fontSize = 7.sp
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
    val activity = LocalContext.current as? Activity

    // Launcher to request notification permission (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permission granted, update toggle state
            onToggle(true)
        } else {
            // Permission denied
            onToggle(false)
        }
    }

    SettingRowSwitch(
        title = "Notification",
        description = "Enable notifications to receive your daily trending reminder.",
        checked = isEnabled,
        onToggle = { checked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted && checked) {
                    // Check if we should show a rationale
                    val showRationale = activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } ?: false

                    if (showRationale) {
                        // User denied before but can ask again
                        Toast.makeText(
                            context,
                            "Please allow notifications to get daily trending updates",
                            Toast.LENGTH_LONG
                        ).show()
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Permission denied permanently or first time
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    // Already granted or user turned off toggle
                    onToggle(checked)
                }
            } else {
                // Android < 13: no runtime permission
                val notificationsEnabled =
                    NotificationManagerCompat.from(context).areNotificationsEnabled()

                if (!notificationsEnabled) {
                    // Notifications disabled manually, redirect to settings
                    Toast.makeText(
                        context,
                        "Please enable notifications in app settings",
                        Toast.LENGTH_LONG
                    ).show()
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