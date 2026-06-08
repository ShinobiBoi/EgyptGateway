package com.besha.egyptguide.features.quiz.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.quiz.data.model.QuizItem
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.presentation.viewmodel.QuizActions
import com.besha.egyptguide.features.quiz.presentation.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    monument: ScreenResources.QuizRoute,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<QuizViewModel>()
    val state by viewModel.viewStates.collectAsState()

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }
    var showResultDialog by remember { mutableStateOf(false) }
    var showReviewScreen by remember { mutableStateOf(false) }

    LaunchedEffect(monument.id) {
        viewModel.executeAction(QuizActions.GetQuiz(monument.id!!))
    }
    LaunchedEffect(state.submitQuizResponse.data) {
        if (state.submitQuizResponse.data != null) showResultDialog = true
    }

    // ── Review screen overlay ─────────────────────────────────────────────
    if (showReviewScreen && state.quizItems.data != null && state.submitQuizResponse.data != null) {
        QuizReviewScreen(
            quizItems = state.quizItems.data!!,
            selectedAnswers = selectedAnswers,
            onClose = {
                showReviewScreen = false
                onBackClick()
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dim))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Hero header ───────────────────────────────────────────────
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
                                text = monument.name ?: "Quiz",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "Test your knowledge",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Progress bar
                    state.quizItems.data?.let { items ->
                        if (items.isNotEmpty()) {
                            Spacer(Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Question ${currentQuestionIndex + 1} of ${items.size}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${((currentQuestionIndex + 1f) / items.size * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (currentQuestionIndex + 1f) / items.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            // ── Body ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
            ) {
                when {
                    state.quizItems.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = colorResource(R.color.blue),
                            strokeWidth = 3.dp
                        )
                    }
                    state.quizItems.errorThrowable != null -> {
                        Text(
                            text = state.quizItems.errorThrowable?.message ?: "An error occurred",
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            color = colorResource(R.color.text_secondary),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        state.quizItems.data?.let { quizItems ->
                            if (quizItems.isNotEmpty()) {
                                val currentQuestion = quizItems[currentQuestionIndex]
                                val isLastQuestion = currentQuestionIndex == quizItems.size - 1

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp)
                                ) {
                                    // Step indicators
                                    LazyQuizStepRow(
                                        total = quizItems.size,
                                        current = currentQuestionIndex,
                                        answered = selectedAnswers.keys
                                    )

                                    Spacer(Modifier.height(20.dp))

                                    // Question + options
                                    AnimatedContent(
                                        targetState = currentQuestionIndex,
                                        transitionSpec = {
                                            if (targetState > initialState)
                                                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                                            else
                                                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                                        },
                                        label = "question_anim"
                                    ) { index ->
                                        QuizQuestionContent(
                                            quizItem = quizItems[index],
                                            selectedAnswer = selectedAnswers[index],
                                            onAnswerSelected = { answer ->
                                                selectedAnswers[index] = answer
                                            }
                                        )
                                    }

                                    Spacer(Modifier.weight(1f))

                                    // Navigation
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        AnimatedVisibility(visible = currentQuestionIndex > 0) {
                                            OutlinedButton(
                                                onClick = { currentQuestionIndex-- },
                                                modifier = Modifier.height(52.dp),
                                                shape = RoundedCornerShape(14.dp),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.5.dp, colorResource(R.color.blue)
                                                ),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = colorResource(R.color.blue)
                                                )
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.ArrowBack,
                                                    null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (isLastQuestion) {
                                                    var grade = 0
                                                    selectedAnswers.forEach {
                                                        if (it.value == quizItems[it.key].correct_answer) grade++
                                                    }
                                                    viewModel.executeAction(
                                                        QuizActions.SubmitQuiz(SubmitQuizRequest(monument.id!!, grade))
                                                    )
                                                } else {
                                                    currentQuestionIndex++
                                                }
                                            },
                                            modifier = Modifier.weight(1f).height(52.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(R.color.blue)
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(0.dp)
                                        ) {
                                            if (state.submitQuizResponse.isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text(
                                                    text = if (isLastQuestion) "Submit Quiz" else "Next",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Icon(
                                                    if (isLastQuestion) Icons.Default.Check
                                                    else Icons.AutoMirrored.Filled.ArrowForward,
                                                    null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    "No questions available",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = colorResource(R.color.text_secondary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Result dialog ─────────────────────────────────────────────────────
    if (showResultDialog && state.submitQuizResponse.data != null) {
        QuizScoreDialog(
            response = state.submitQuizResponse.data!!,
            totalQuestions = state.quizItems.data?.size ?: 0,
            onQuit = {
                showResultDialog = false
                onBackClick()
            },
            onReview = {
                showResultDialog = false
                showReviewScreen = true
            }
        )
    }
}

// ── Step dots row ─────────────────────────────────────────────────────────────

@Composable
private fun LazyQuizStepRow(total: Int, current: Int, answered: Set<Int>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val isActive = i == current
            val isDone = answered.contains(i) && i != current
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> colorResource(R.color.blue)
                            isDone   -> colorResource(R.color.blue_light).copy(alpha = 0.6f)
                            else     -> colorResource(R.color.divider_color)
                        }
                    )
            )
        }
    }
}

// ── Question content ──────────────────────────────────────────────────────────

@Composable
fun QuizQuestionContent(
    quizItem: QuizItem,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
            border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Text(
                text = quizItem.question ?: "",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary),
                lineHeight = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Options
        quizItem.options?.forEachIndexed { idx, option ->
            option?.let {
                val isSelected = selectedAnswer == it
                val letter = listOf("A", "B", "C", "D").getOrNull(idx) ?: ""

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onAnswerSelected(it) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) colorResource(R.color.blue_surface) else colorResource(R.color.surface_white),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) colorResource(R.color.blue) else colorResource(R.color.divider_color)
                    ),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) colorResource(R.color.blue)
                                    else colorResource(R.color.stroke_gray)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else colorResource(R.color.text_secondary)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = it,
                            fontSize = 15.sp,
                            color = if (isSelected) colorResource(R.color.blue) else colorResource(R.color.text_primary),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = colorResource(R.color.blue),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Score dialog ──────────────────────────────────────────────────────────────

@Composable
fun QuizScoreDialog(
    response: SubmitQuizResponse,
    totalQuestions: Int,
    onQuit: () -> Unit,
    onReview: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(R.color.surface_white),
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trophy icon
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    colorResource(R.color.blue_surface),
                                    colorResource(R.color.blue_dim)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(50.dp)
                    )
                }

                Text(
                    text = "Quiz Completed!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.text_primary),
                    letterSpacing = (-0.4).sp
                )

                Text(
                    text = response.message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.text_secondary),
                    lineHeight = 20.sp
                )

                // Points card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue_dim)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Earned",
                                fontSize = 11.sp,
                                color = colorResource(R.color.text_secondary),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "+${response.earned_points}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = colorResource(R.color.blue)
                            )
                            Text(
                                "pts",
                                fontSize = 11.sp,
                                color = colorResource(R.color.blue).copy(alpha = 0.7f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(colorResource(R.color.divider_color))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Total",
                                fontSize = 11.sp,
                                color = colorResource(R.color.text_secondary),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${response.total_points}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = colorResource(R.color.text_primary)
                            )
                            Text(
                                "pts",
                                fontSize = 11.sp,
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Review button
                Button(
                    onClick = onReview,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue)),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Review My Answers", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Quit button
                OutlinedButton(
                    onClick = onQuit,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, colorResource(R.color.blue)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.blue))
                ) {
                    Text("Continue Exploring", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// ── Review screen ─────────────────────────────────────────────────────────────

@Composable
fun QuizReviewScreen(
    quizItems: List<QuizItem>,
    selectedAnswers: Map<Int, String>,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dim))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close, "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Answer Review",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                        // Score summary
                        val correct = quizItems.indices.count { i ->
                            selectedAnswers[i] == quizItems[i].correct_answer
                        }
                        Text(
                            "$correct / ${quizItems.size} correct",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                quizItems.forEachIndexed { index, quizItem ->
                    val userAnswer = selectedAnswers[index]
                    val correctAnswer = quizItem.correct_answer
                    val isCorrect = userAnswer == correctAnswer

                    ReviewQuestionCard(
                        index = index + 1,
                        quizItem = quizItem,
                        userAnswer = userAnswer,
                        isCorrect = isCorrect
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue)),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun ReviewQuestionCard(
    index: Int,
    quizItem: QuizItem,
    userAnswer: String?,
    isCorrect: Boolean
) {
    val correctColor = Color(0xFF2E7D32)
    val wrongColor = Color(0xFFC62828)
    val statusColor = if (isCorrect) correctColor else wrongColor
    val statusBg = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.surface_white)),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.divider_color)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Question header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Q$index",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.text_secondary)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isCorrect) "Correct" else "Incorrect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = quizItem.question ?: "",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_primary),
                lineHeight = 21.sp
            )

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = colorResource(R.color.divider_color), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))

            // Options
            quizItem.options?.forEachIndexed { idx, option ->
                option?.let { opt ->
                    val letter = listOf("A", "B", "C", "D").getOrNull(idx) ?: ""
                    val isUserPick = opt == userAnswer
                    val isCorrectOpt = opt == quizItem.correct_answer

                    val bgColor = when {
                        isCorrectOpt -> Color(0xFFE8F5E9)
                        isUserPick && !isCorrectOpt -> Color(0xFFFFEBEE)
                        else -> colorResource(R.color.stroke_gray)
                    }
                    val borderColor = when {
                        isCorrectOpt -> correctColor
                        isUserPick && !isCorrectOpt -> wrongColor
                        else -> Color.Transparent
                    }
                    val textColor = when {
                        isCorrectOpt -> correctColor
                        isUserPick && !isCorrectOpt -> wrongColor
                        else -> colorResource(R.color.text_secondary)
                    }
                    val letterBg = when {
                        isCorrectOpt -> correctColor
                        isUserPick && !isCorrectOpt -> wrongColor
                        else -> colorResource(R.color.divider_color)
                    }
                    val letterTint = when {
                        isCorrectOpt || (isUserPick && !isCorrectOpt) -> Color.White
                        else -> colorResource(R.color.text_secondary)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .then(
                                if (isCorrectOpt || (isUserPick && !isCorrectOpt))
                                    Modifier.padding(1.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgColor)
                                else Modifier
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(letterBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(letter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = letterTint)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = opt,
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = if (isCorrectOpt || isUserPick) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrectOpt) {
                            Icon(Icons.Default.Check, null, tint = correctColor, modifier = Modifier.size(16.dp))
                        } else if (isUserPick) {
                            Icon(Icons.Default.Close, null, tint = wrongColor, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}