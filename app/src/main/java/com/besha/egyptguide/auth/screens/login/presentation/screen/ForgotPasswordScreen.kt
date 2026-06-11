package com.besha.egyptguide.auth.screens.login.presentation.screen

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.components.AuthButton
import com.besha.egyptguide.appcore.components.AuthOutlinedTextField
import com.besha.egyptguide.auth.screens.login.presentation.viewmodel.LogInActions
import com.besha.egyptguide.auth.screens.login.presentation.viewmodel.LogInViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
) {
    val viewModel = hiltViewModel<LogInViewModel>()
    val state by viewModel.viewStates.collectAsState()

    var email by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }
    var emailErr by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            emailTouched && !emailErr && email.isNotBlank()
        }
    }

    val showDialog by remember(state.logInState) {
        mutableStateOf(
            state.logInState.isLoading ||
                    state.logInState.isSuccess ||
                    state.logInState.errorThrowable != null
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                if (state.logInState.isSuccess) {
                    Button(onClick = {
                        viewModel.executeAction(LogInActions.ResetState)
                        navController.popBackStack()
                    }) {
                        Text("OK")
                    }
                } else if (state.logInState.errorThrowable != null) {
                    Button(onClick = {
                        viewModel.executeAction(LogInActions.ResetState)
                    }) {
                        Text("Retry")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when {
                        state.logInState.isLoading -> {
                            CircularProgressIndicator()
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = "Sending reset link..."
                            )
                        }
                        state.logInState.isSuccess -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = "Reset link sent to your email",
                                textAlign = TextAlign.Center
                            )
                        }
                        state.logInState.errorThrowable != null -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = state.logInState.errorThrowable?.message ?: "",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .padding(top = 40.dp, start = 24.dp, end = 24.dp)
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = colorResource(R.color.gray)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.forgot_password),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32 * 1.3.sp
            )

            Text(
                modifier = Modifier.padding(top = 12.dp),
                color = colorResource(R.color.gray),
                text = "Enter your email to receive a password reset link",
                fontSize = 12.sp,
            )

            AuthOutlinedTextField(
                title = stringResource(R.string.email),
                value = email,
                onValueChange = {
                    email = it
                    emailTouched = true
                    emailErr = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                },
                placeholder = stringResource(R.string.enter_your_email),
                modifier = Modifier.padding(top = 32.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailErr,
                supportingText = if (email.isBlank()) stringResource(R.string.email_is_required)
                else stringResource(R.string.invalid_email)
            )

            AuthButton(
                text = "Send Reset Link",
                modifier = Modifier
                    .padding(top = 38.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                hasError = !isFormValid,
            ) {
                viewModel.executeAction(LogInActions.ForgotPassword(email))
            }
        }
    }
}
