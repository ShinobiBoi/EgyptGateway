package com.besha.egyptguide.features.objectives.presentaion.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponseItem
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponseItem
import com.besha.egyptguide.features.objectives.presentaion.viewmodel.ObjectivesActions
import com.besha.egyptguide.features.objectives.presentaion.viewmodel.ObjectivesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectivesScreen(
    onBackClick: () -> Unit
) {
    val viewModel: ObjectivesViewModel = hiltViewModel()
    val state by viewModel.viewStates.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Monuments", "Tickets")

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0) {
            viewModel.executeAction(ObjectivesActions.GetMonumentObjectives)
        } else {
            viewModel.executeAction(ObjectivesActions.GetTicketObjectives)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Your Objectives",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, colorResource(R.color.blue).copy(alpha = 0.05f))
                    )
                )
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = colorResource(R.color.blue),
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = colorResource(R.color.blue)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTabIndex == 0) {
                    MonumentObjectivesContent(state.monumentObjectives)
                } else {
                    TicketObjectivesContent(state.ticketObjectives)
                }
            }
        }
    }
}

@Composable
fun MonumentObjectivesContent(state: CommonViewState<MonumentObjectivesResponse>) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(R.color.blue))
            }
        }
        state.errorThrowable != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load monument objectives", color = Color.Gray)
            }
        }
        state.data != null -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.data!!) { objective ->
                    ObjectiveCard(
                        title = objective.title ?: "Ticket Collector",
                        description = objective.description ?: "",
                        progress = objective.current_progress ?: "",
                        progressValue = objective.visited_count?: 0,
                        total = objective.total_monuments?: 1,
                        points = objective.points_reward ?: 0,
                        isCompleted = objective.completed ?: false,
                        icon = Icons.Default.ConfirmationNumber
                    )
                }
            }
        }
    }
}

@Composable
fun TicketObjectivesContent(state: CommonViewState<TicketObjectivesResponse>) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(R.color.blue))
            }
        }
        state.errorThrowable != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load ticket objectives", color = Color.Gray)
            }
        }
        state.data != null -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.data!!) { objective ->
                    ObjectiveCard(
                        title = objective.title ?: "Ticket Collector",
                        description = objective.description ?: "",
                        progress = objective.current_progress ?: "",
                        progressValue = objective.approved_count?: 0,
                        total = objective.required_tickets?: 1,
                        points = objective.points_reward ?: 0,
                        isCompleted = objective.completed ?: false,
                        icon = Icons.Default.ConfirmationNumber
                    )
                }
            }
        }
    }
}

@Composable
fun ObjectiveCard(
    title: String,
    description: String,
    progress: String,
    progressValue: Int,
    total: Int,
    points: Int,
    isCompleted: Boolean,
    icon: ImageVector
) {
    val barProgress = progressValue.toFloat() / total.coerceAtLeast(1).toFloat()


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = colorResource(R.color.blue).copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colorResource(R.color.blue),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }
                }

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Completed",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = progress,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.blue)
                    )
                }

                LinearProgressIndicator(
                    progress = { barProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = colorResource(R.color.blue),
                    trackColor = colorResource(R.color.blue).copy(alpha = 0.1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "+$points PTS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.blue)
                )
            }
        }
    }
}
