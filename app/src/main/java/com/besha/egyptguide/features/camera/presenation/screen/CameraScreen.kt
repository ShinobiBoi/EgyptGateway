package com.besha.egyptguide.features.camera.presenation.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.camera.data.model.CameraScreenResponse
import com.besha.egyptguide.features.camera.presenation.viewmodel.CameraActions
import com.besha.egyptguide.features.camera.presenation.viewmodel.CameraViewModel
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import com.besha.egyptguide.features.camera.data.model.VisitRequest
import com.besha.egyptguide.features.camera.data.model.VisitResponse
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
    var showVisitScoreDialog by remember { mutableStateOf(false) }
    var showRateScoreDialog by remember { mutableStateOf(false) }




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

    LaunchedEffect(key1 = state.identificationResult.data) {
        state.identificationResult.data?.monument_id?.let {
            if (state.visitResponse.data == null){
                viewModel.executeAction(CameraActions.Visit(VisitRequest(it)))
                showVisitScoreDialog = true
            }
        }
    }


    if (showRatingDialog) {
        ModernRatingDialog(
            monumentId = state.identificationResult.data?.monument_id ?: "",
            onDismiss = { showRatingDialog = false },
            onSubmit = { ratingRequest ->
                viewModel.executeAction(CameraActions.RateMonument(ratingRequest))
                showRateScoreDialog = true
                showRatingDialog = false
            }
        )
    }

    if ( showVisitScoreDialog && state.visitResponse.data != null) {
        state.visitResponse.data?.let {
            ScoreDialog(
                response = CameraScreenResponse(
                    it.earned_points,
                    it.message,
                    it.total_points
                ),
                onDismiss = {
                    showVisitScoreDialog = false
                }
            )
        }
    }

    if (  showRateScoreDialog && state.rateResponse.data != null) {
        state.rateResponse.data?.let {
            ScoreDialog(
                response = CameraScreenResponse(
                    it.earned_points,
                    it.message,
                    it.total_points
                ),
                onDismiss = {
                    showRateScoreDialog = false
                }
            )
        }
    }

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
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White, colorResource(R.color.blue).copy(alpha = 0.08f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val hasResult = state.identificationResult.data != null
                val isLoading = state.identificationResult.isLoading

                // Only show Capture section if NO result is being displayed or loaded
                AnimatedVisibility(
                    visible = !hasResult && !isLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .size(320.dp)
                                .padding(16.dp)
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

                        Spacer(modifier = Modifier.height(32.dp))

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
                    }
                }

                // Analyzing state
                if (isLoading) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White)
                            .shadow(12.dp)
                    ) {
                        AsyncImage(
                            model = state.selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.02f)
                                .offset(y = 280.dp * scanProgress)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, colorResource(R.color.blue), Color.Transparent)
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(color = colorResource(R.color.blue))
                    Text("Analyzing Monument...", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                }

                // Result State
                if (hasResult) {
                    val result = state.identificationResult.data!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = state.selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .shadow(8.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(colorResource(R.color.blue).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = colorResource(R.color.blue),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = result.name ?: stringResource(R.string.unknown_monument),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colorResource(R.color.black),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${result.city ?: ""}, Egypt",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.blue)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(modifier = Modifier.width(60.dp), thickness = 2.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = result.description ?: "",
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        navController.navigate(ScreenResources.QuizRoute(
                                            id = result.monument_id,
                                            name = result.name
                                        ))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.black))
                                ) {
                                    Icon(Icons.Default.Extension, contentDescription = null)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(stringResource(R.string.take_a_challenge), fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { showRatingDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, colorResource(R.color.blue))
                                ) {
                                    Icon(Icons.Default.RateReview, contentDescription = null, tint = colorResource(R.color.blue))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(stringResource(R.string.submit_feedback), fontWeight = FontWeight.Bold, color = colorResource(R.color.blue))
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                TextButton(
                                    onClick = { viewModel.executeAction(CameraActions.ResetState) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retake Photo", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

}

@Composable
fun ScoreDialog(response: CameraScreenResponse, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(colorResource(R.color.blue).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "Score update!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = response.message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue).copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Earned", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("+${response.earned_points}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colorResource(R.color.blue))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("${response.total_points}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue))
                ) {
                    Text("Awesome!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernRatingDialog(
    monumentId: String,
    onDismiss: () -> Unit,
    onSubmit: (RatingRequest) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
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
