package com.besha.egyptguide.features.objectives.presentaion.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto
import com.besha.egyptguide.features.objectives.presentaion.viewmodel.ObjectivesActions
import com.besha.egyptguide.features.objectives.presentaion.viewmodel.ObjectivesViewModel

@Composable
fun ObjectivesScreen(onBackClick: () -> Unit) {
    val viewModel: ObjectivesViewModel = hiltViewModel()
    val state by viewModel.viewStates.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0) viewModel.executeAction(ObjectivesActions.GetMonumentObjectives)
        else viewModel.executeAction(ObjectivesActions.GetTicketObjectives)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dim))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Gradient header ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(colorResource(R.color.blue), colorResource(R.color.blue_light))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Your Objectives",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = (-0.4).sp
                            )
                            Text(
                                "Track your exploration progress",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Custom pill tab switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Monuments", "Tickets").forEachIndexed { index, label ->
                            val isSelected = selectedTabIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color.White else Color.Transparent
                                    )
                                    .clickable { selectedTabIndex = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) colorResource(R.color.blue) else Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // ── Content ───────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTabIndex,
                    transitionSpec = {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(150))
                    },
                    label = "tab_anim"
                ) { tab ->
                    if (tab == 0) MonumentObjectivesContent(state.monumentObjectives)
                    else TicketObjectivesContent(state.ticketObjectives)
                }
            }
        }
    }
}

// ── Monument tab ──────────────────────────────────────────────────────────────

@Composable
fun MonumentObjectivesContent(state: CommonViewState<List<MonumentObjectivesDto>>) {
    when {
        state.isLoading -> LoadingBox()
        state.errorThrowable != null -> ErrorBox("Failed to load monument objectives")
        state.data != null -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items( state.data) { objective ->
                    MonumentObjectiveCard(objective)
                }
            }
        }
        else -> EmptyBox("No objectives yet")
    }
}

// ── Ticket tab ────────────────────────────────────────────────────────────────

@Composable
fun TicketObjectivesContent(state: CommonViewState<List<TicketObjectivesDto>>) {
    when {
        state.isLoading -> LoadingBox()
        state.errorThrowable != null -> ErrorBox("Failed to load ticket objectives")
        state.data != null -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.data!!) { objective ->
                    TicketObjectiveCard(objective)
                }
            }
        }
        else -> EmptyBox("No objectives yet")
    }
}

// ── Monument objective card ───────────────────────────────────────────────────

@Composable
fun MonumentObjectiveCard(objective: MonumentObjectivesDto) {
    var expanded by remember { mutableStateOf(false) }
    val barProgress = (objective.visited_count ?: 0).toFloat() /
            (objective.total_monuments ?: 1).coerceAtLeast(1).toFloat()
    val hasSubtasks = !objective.monumentObjectivesItems.isNullOrEmpty()
    val chevronAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250),
        label = "chevron"
    )

    Log.d("Composable", "${objective.monumentObjectivesItems}")

    ObjectiveCardShell(
        title = objective.title ?: "Monument Objective",
        description = objective.description ?: "",
        progress = objective.current_progress ?: "${objective.visited_count ?: 0}/${objective.total_monuments ?: 0}",
        barProgress = barProgress,
        points = objective.points_reward ?: 0,
        isCompleted = objective.completed ?: false,
        icon = Icons.Default.LocationOn,
        hasSubtasks = hasSubtasks,
        chevronAngle = chevronAngle,
        onHeaderClick = { if (hasSubtasks) expanded = !expanded }
    ) {
        // Subtasks section
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(280)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(220)) + fadeOut(tween(150))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                HorizontalDivider(
                    color = colorResource(R.color.divider_color),
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    "Monuments",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.text_secondary),
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                objective.monumentObjectivesItems?.forEach { item ->
                    item?.let { SubtaskRow(name = it.monument_name ?: "", done = it.visited ?: false) }
                }
            }
        }
    }
}

// ── Ticket objective card ─────────────────────────────────────────────────────

@Composable
fun TicketObjectiveCard(objective: TicketObjectivesDto) {
    val barProgress = (objective.approved_count ?: 0).toFloat() /
            (objective.required_tickets ?: 1).coerceAtLeast(1).toFloat()

    ObjectiveCardShell(
        title = objective.title ?: "Ticket Objective",
        description = objective.description ?: "",
        progress = objective.current_progress ?: "${objective.approved_count ?: 0}/${objective.required_tickets ?: 0}",
        barProgress = barProgress,
        points = objective.points_reward ?: 0,
        isCompleted = objective.completed ?: false,
        icon = Icons.Default.ConfirmationNumber,
        hasSubtasks = false,
        chevronAngle = 0f,
        onHeaderClick = {}
    ) {}
}

// ── Shared card shell ─────────────────────────────────────────────────────────

@Composable
private fun ObjectiveCardShell(
    title: String,
    description: String,
    progress: String,
    barProgress: Float,
    points: Int,
    isCompleted: Boolean,
    icon: ImageVector,
    hasSubtasks: Boolean,
    chevronAngle: Float,
    onHeaderClick: () -> Unit,
    subtasksContent: @Composable () -> Unit
) {
    Log.d("Composable", "$hasSubtasks")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                colorResource(R.color.blue_surface)
            else colorResource(R.color.surface_white)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCompleted) 1.5.dp else 1.dp,
            color = if (isCompleted) colorResource(R.color.blue).copy(alpha = 0.4f)
            else colorResource(R.color.divider_color)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // ── Header row ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (hasSubtasks) Modifier.clickable { onHeaderClick() } else Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon box
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (isCompleted)
                                colorResource(R.color.blue).copy(alpha = 0.15f)
                            else colorResource(R.color.blue_surface)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorResource(R.color.blue),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.text_primary),
                        letterSpacing = (-0.2).sp
                    )
                    if (description.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = description,
                            fontSize = 12.sp,
                            color = colorResource(R.color.text_secondary),
                            maxLines = 2,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Right side: completed star OR expand chevron
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB800).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else if (hasSubtasks) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.blue_surface)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            null,
                            tint = colorResource(R.color.blue),
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(chevronAngle)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Progress bar ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Progress",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.text_secondary),
                    letterSpacing = 0.5.sp
                )
                Text(
                    progress,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.blue)
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { barProgress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape),
                color = colorResource(R.color.blue),
                trackColor = colorResource(R.color.divider_color)
            )

            Spacer(Modifier.height(12.dp))

            // ── Points footer ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCompleted) Color(0xFFFFB800).copy(alpha = 0.12f)
                            else colorResource(R.color.stroke_gray)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isCompleted) "✓ Completed" else "In Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) Color(0xFFB8860B) else colorResource(R.color.text_secondary)
                    )
                }

                // Points badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.blue_surface))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "+$points pts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorResource(R.color.blue)
                    )
                }
            }

            // ── Expandable subtasks ───────────────────────────────────────
            subtasksContent()
        }
    }
}

// ── Subtask row ───────────────────────────────────────────────────────────────

@Composable
private fun SubtaskRow(name: String, done: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Check circle
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (done) colorResource(R.color.blue) else colorResource(R.color.stroke_gray)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.text_secondary).copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = name,
            fontSize = 13.sp,
            color = if (done) colorResource(R.color.text_primary) else colorResource(R.color.text_secondary),
            fontWeight = if (done) FontWeight.SemiBold else FontWeight.Normal,
            textDecoration = if (done) TextDecoration.None else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        if (done) {
            Text(
                "Visited",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue).copy(alpha = 0.7f)
            )
        }
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = colorResource(R.color.blue),
            strokeWidth = 3.dp
        )
    }
}

@Composable
private fun ErrorBox(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ErrorOutline,
                null,
                tint = colorResource(R.color.text_secondary),
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(message, color = colorResource(R.color.text_secondary), fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyBox(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = colorResource(R.color.text_secondary), fontSize = 14.sp)
    }
}