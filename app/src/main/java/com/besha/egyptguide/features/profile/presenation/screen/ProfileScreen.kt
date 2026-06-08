package com.besha.egyptguide.features.profile.presenation.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.profile.data.dto.UpdateProfileRequest
import com.besha.egyptguide.features.profile.domain.model.FoodCategory
import com.besha.egyptguide.features.profile.presenation.screen.components.FoodPreferencesDialog
import com.besha.egyptguide.features.profile.presenation.screen.components.NotificationPermissionToggle
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
        profileViewModel.executeAction(ProfileActions.GetNotificationStatus(context))
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
                                            model = "http://127.0.0.1:8000$profilePic".toUri(),
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

                    // objectives Section
                    Text(
                        text = "Objectives",
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
                            ProfileNavigateItemRow("Objectives") {
                                childController.navigate(ScreenResources.ObjectivesRoute)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}




private fun profileUriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
    file.outputStream().use { output -> inputStream.copyTo(output) }
    val requestFile = file.asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData("photo", file.name, requestFile)
}


