package com.besha.egyptguide.features.quiz.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.features.quiz.data.model.QuizItem
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

    val primaryColor = colorResource(R.color.blue)
    val secondaryColor = primaryColor.copy(alpha = 0.1f)

    LaunchedEffect(monument.id) {
        viewModel.executeAction(QuizActions.GetQuiz(monument.id!!))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${monument.name} Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (state.quizItems.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryColor
                )
            } else if (state.quizItems.errorThrowable != null) {
                Text(
                    text = state.quizItems.errorThrowable?.message ?: "An error occurred",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            } else {
                state.quizItems.data?.let { quizItems ->
                    if (quizItems.isNotEmpty()) {
                        val currentQuestion = quizItems[currentQuestionIndex]

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Progress Text
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${quizItems.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = { (currentQuestionIndex + 1).toFloat() / quizItems.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = primaryColor,
                                trackColor = secondaryColor
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Question Card
                            QuizQuestionContent(
                                questionNumber = currentQuestionIndex + 1,
                                quizItem = currentQuestion,
                                selectedAnswer = selectedAnswers[currentQuestionIndex],
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                onAnswerSelected = { answer ->
                                    selectedAnswers[currentQuestionIndex] = answer
                                }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Navigation Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Previous Button
                                OutlinedButton(
                                    onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                                    modifier = Modifier.weight(1f),
                                    enabled = currentQuestionIndex > 0,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if(currentQuestionIndex > 0) primaryColor else Color.LightGray)
                                ) {
                                    Text("Previous", fontWeight = FontWeight.Bold)
                                }

                                // Next / Submit Button
                                val isLastQuestion = currentQuestionIndex == quizItems.size - 1
                                Button(
                                    onClick = {
                                        if (isLastQuestion) {
                                            // TODO: Handle Submit logic (e.g., viewModel action)
                                        } else {
                                            currentQuestionIndex++
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                                ) {
                                    Text(
                                        text = if (isLastQuestion) "Submit" else "Next",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No questions available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizQuestionContent(
    questionNumber: Int,
    quizItem: QuizItem,
    selectedAnswer: String?,
    primaryColor: Color,
    secondaryColor: Color,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = quizItem.question ?: "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        quizItem.options?.forEach { option ->
            option?.let {
                val isSelected = selectedAnswer == it
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onAnswerSelected(it) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) secondaryColor else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        color = if (isSelected) primaryColor else Color.Transparent
                    ),
                    shadowElevation = if (isSelected) 0.dp else 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null, // Handled by surface click
                            colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) primaryColor else Color.DarkGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
