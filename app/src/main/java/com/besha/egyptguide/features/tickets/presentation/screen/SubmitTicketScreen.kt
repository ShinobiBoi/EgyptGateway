package com.besha.egyptguide.features.tickets.presentation.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.presentation.viewmodel.TicketsActions
import com.besha.egyptguide.features.tickets.presentation.viewmodel.TicketsViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitTicketScreen(
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<TicketsViewModel>()
    val state by viewModel.viewStates.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSearchFocused by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(state.submitResult) {
        if (state.submitResult.data != null && state.submitResult.data?.success == true) {
            Toast.makeText(context, "Ticket submitted successfully!", Toast.LENGTH_SHORT).show()
            viewModel.executeAction(TicketsActions.ResetSubmitState)
            selectedImageUri=null
        } else if (state.submitResult.errorThrowable != null) {
            Toast.makeText(context, "Error: ${state.submitResult.errorThrowable?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit a Ticket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Image Picker
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { galleryLauncher.launch("image/*") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F5)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(36.dp),
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.5f),
                                onClick = { selectedImageUri = null }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.padding(8.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = colorResource(R.color.blue).copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Upload Ticket Photo", color = Color.Gray, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Place Search
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Monument / Place", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    TextField(
                        value = state.query,
                        onValueChange = {
                            viewModel.executeAction(TicketsActions.OnQueryChange(it, state.sessionToken))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isSearchFocused = it.isFocused },
                        placeholder = { Text("Search for place...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.executeAction(TicketsActions.OnQueryChange("", state.sessionToken)) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F3F5),
                            unfocusedContainerColor = Color(0xFFF1F3F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    if (isSearchFocused && !state.predictions.data.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            LazyColumn {
                                items(state.predictions.data!!) { prediction ->
                                    ListItem(
                                        headlineContent = { Text(prediction.getPrimaryText(null).toString()) },
                                        supportingContent = { Text(prediction.getSecondaryText(null).toString(), maxLines = 1) },
                                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) },
                                        modifier = Modifier.clickable {
                                            viewModel.executeAction(TicketsActions.SelectPlace(prediction.placeId, state.sessionToken))
                                            viewModel.executeAction(TicketsActions.OnQueryChange(prediction.getPrimaryText(null).toString(), state.sessionToken))
                                            focusManager.clearFocus()
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // Selected Place Info
                if (state.selectedPlace.data != null) {
                    val place = state.selectedPlace.data!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue).copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colorResource(R.color.blue))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(place.displayName ?: "", fontWeight = FontWeight.Bold)
                                Text(place.formattedAddress ?: "", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val place = state.selectedPlace.data
                        if (place != null && selectedImageUri != null) {
                            val request = SubmitTicketRequest(
                                name = place.displayName ?: "",
                                google_place_id = place.id ?: "",
                                latitude = place.location!!.latitude,
                                longitude = place.location.longitude,
                                photo = uriToMultipart(context, selectedImageUri!!)
                            )
                            viewModel.executeAction(TicketsActions.SubmitTicket(request))
                        } else {
                            Toast.makeText(context, "Please select a place and upload a photo", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue)),
                    enabled = !state.submitResult.isLoading
                ) {
                    if (state.submitResult.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("ticket_upload", ".jpg", context.cacheDir)
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    val requestFile = file.asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData("photo", file.name, requestFile)
}
