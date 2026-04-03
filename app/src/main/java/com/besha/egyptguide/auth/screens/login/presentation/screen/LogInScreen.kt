package com.besha.egyptguide.auth.screens.login.presentation.screen

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import com.besha.egyptguide.appcore.navigation.ScreenResources
import com.besha.egyptguide.appcore.components.AuthOutlinedTextField
import com.besha.egyptguide.auth.screens.login.data.model.LoginRequest
import com.besha.egyptguide.auth.screens.login.presentation.viewmodel.LogInActions
import com.besha.egyptguide.auth.screens.login.presentation.viewmodel.LogInViewModel


@Composable
fun LogInScreen(
    rootController: NavController,
    navController: NavController

) {

    val viewModel = hiltViewModel<LogInViewModel>()
    val state by viewModel.viewStates.collectAsState()
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity



    LaunchedEffect(
        state.logInState
    ) {
        if (state.logInState.isSuccess) {
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
            rootController.navigate(ScreenResources.MainRoute) {
                popUpTo(ScreenResources.AuthRoute) { inclusive = true }
                launchSingleTop = true
            }
        }
        if (state.logInState.errorThrowable != null) {
            Toast.makeText(context, "${state.logInState.errorThrowable!!.message}", Toast.LENGTH_SHORT).show()
        }

    }




    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    var emailErr by remember { mutableStateOf(false) }
    var passwordErr by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    val fieldsTouched by remember {
        derivedStateOf {
            emailTouched && passwordTouched
        }
    }


    val loginErr by remember {
        derivedStateOf {
            emailErr || passwordErr || !fieldsTouched
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
            onDismissRequest = {  },
            confirmButton = {
                if (state.logInState.errorThrowable != null){
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
                                text = "Logging in..."
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            modifier = Modifier.padding(end = 24.dp),
            text = stringResource(R.string.sign_in_to_your_account),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32 * 1.3.sp
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            color = colorResource(R.color.gray),
            text = stringResource(R.string.enter_email_and_password_to_log_in),
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

        AuthOutlinedTextField(
            title = stringResource(R.string.password),
            value = password,
            onValueChange = {
                password = it
                passwordErr = password.isBlank()
                passwordTouched = true
            },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Visibility",
                        tint = colorResource(R.color.gray)
                    )

                }

            },
            placeholder = stringResource(R.string.enter_your_password),
            modifier = Modifier.padding(top = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordErr,
            supportingText = stringResource(R.string.password_is_required),
            passwordVisible = passwordVisible
        )


        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = colorResource(R.color.blue),
            text = stringResource(R.string.forgot_password),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )


        AuthButton(
            text = stringResource(R.string.log_in),
            modifier = Modifier
                .padding(top = 38.dp)
                .fillMaxWidth()
                .height(48.dp),
            hasError = loginErr,
        ) {
            viewModel.executeAction(LogInActions.LogIn(
                LoginRequest(
                    email = email,
                    password = password
                )
            ))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            // Left line
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color.Gray
            )

            // Text in the middle
            Text(
                text = stringResource(R.string.or),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                color = colorResource(R.color.gray)
            )

            // Right line
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color.Gray
            )
        }




        Button(
            modifier = Modifier
                .padding(top = 38.dp)
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, colorResource(R.color.stroke_gray)),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.white)),
            onClick = {

                viewModel.executeAction(LogInActions.GoogleSignIn(activity!!))

            }
        ) {
            Icon(
                painter = painterResource(R.drawable.google),
                contentDescription = "google",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.continue_with_google),
                color = colorResource(R.color.black),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(R.string.don_t_have_an_account),
                color = colorResource(R.color.gray),
                fontSize = 12.sp,
            )



            Text(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clickable {
                        navController.navigate(ScreenResources.SignUpRoute)
                    },
                text = stringResource(R.string.sign_up),
                color = colorResource(R.color.blue),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )

        }


    }
}