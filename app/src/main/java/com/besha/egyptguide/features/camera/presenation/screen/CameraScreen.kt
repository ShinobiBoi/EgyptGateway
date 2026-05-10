package com.besha.egyptguide.features.camera.presenation.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.camera.presenation.viewmodel.CameraActions
import com.besha.egyptguide.features.camera.presenation.viewmodel.CameraViewModel
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CameraViewModel = hiltViewModel()
    val state by viewModel.viewStates.collectAsState()

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                viewModel.executeAction(CameraActions.SetSelectedImageUri(it))
                viewModel.executeAction(CameraActions.IdentifyPhoto(uriToMultipart(context, it)))
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.executeAction(CameraActions.SetSelectedImageUri(it))
            viewModel.executeAction(CameraActions.IdentifyPhoto(uriToMultipart(context, it)))
        }
    }

    // Animation for scanning effect
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ai_identifier),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(16.dp))

                // Modern Image Preview
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(24.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (state.selectedImageUri != null) {
                                AsyncImage(
                                    model = state.selectedImageUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Scanning Overlay
                                if (state.identificationResult.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.02f)
                                            .align(Alignment.TopCenter)
                                            .offset(y = 280.dp * scanProgress)
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        colorResource(R.color.blue),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = colorResource(R.color.blue).copy(alpha = 0.2f)
                                    )
                                    Text(
                                        stringResource(R.string.capture_a_monument_to_identify),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorResource(R.color.gray)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            cameraUri = createImageUri(context)
                            cameraUri?.let { cameraLauncher.launch(it) }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.camera), fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.blue).copy(alpha = 0.2f))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = colorResource(R.color.blue))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.gallery), fontWeight = FontWeight.Bold, color = colorResource(R.color.blue))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Result Section
                AnimatedVisibility(
                    visible = state.identificationResult.data != null || state.identificationResult.isLoading,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    if (state.identificationResult.isLoading) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorResource(R.color.blue), strokeWidth = 5.dp)
                        }
                    } else if (state.identificationResult.data != null) {
                        val result = state.identificationResult.data!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(colorResource(R.color.blue).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = colorResource(R.color.blue),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = result.name ?: stringResource(R.string.unknown_monument),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colorResource(R.color.black),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${result.city ?: ""}, Egypt",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.blue)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(modifier = Modifier.width(100.dp), thickness = 2.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = result.description ?: "",
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                Button(
                                    onClick = {
                                        navController.navigate(ScreenResources.QuizRoute(
                                            id = result.monument_id,
                                            name = result.name
                                        ))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.black))
                                ) {
                                    Icon(Icons.Default.Extension, contentDescription = null)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(stringResource(R.string.take_a_challenge), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { showRatingDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, colorResource(R.color.blue))
                                ) {
                                    Icon(Icons.Default.RateReview, contentDescription = null, tint = colorResource(R.color.blue))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(stringResource(R.string.submit_feedback), fontWeight = FontWeight.Bold, color = colorResource(R.color.blue))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showRatingDialog) {
        ModernRatingDialog(
            monumentId = state.identificationResult.data?.monument_id ?: "",
            onDismiss = { showRatingDialog = false },
            onSubmit = { ratingRequest ->
                viewModel.executeAction(CameraActions.RateMonument(ratingRequest))
                showRatingDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernRatingDialog(
    monumentId: String,
    onDismiss: () -> Unit,
    onSubmit: (RatingRequest) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var crowdLevel by remember { mutableStateOf("Low") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(8.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    stringResource(R.string.how_was_your_visit),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                // Animated Stars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { index ->
                        IconButton(
                            onClick = { rating = index },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (index <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index <= rating) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // Crowd Level Selection
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.crowd_level), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Low", "Medium", "High").forEach { level ->
                            val isSelected = crowdLevel == level
                            Surface(
                                onClick = { crowdLevel = level },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) colorResource(R.color.blue) else colorResource(R.color.blue).copy(alpha = 0.05f),
                                contentColor = if (isSelected) Color.White else colorResource(R.color.blue)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(level, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text(stringResource(R.string.share_your_experience)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                )

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
                        Text(stringResource(R.string.later), color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onSubmit(RatingRequest(comment, crowdLevel.lowercase(), monumentId, rating))
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue))
                    ) {
                        Text(stringResource(R.string.submit), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val file = File.createTempFile("camera_image_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
    file.outputStream().use { output -> inputStream.copyTo(output) }
    val requestFile = file.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData("file", file.name, requestFile)
}
