package com.besha.egyptguide.auth.screens.signup.presentation.screen


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.components.AuthOutlinedTextField
import android.util.Patterns
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.besha.egyptguide.appcore.components.AuthButton
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.presentation.viewmodel.SignUpActions
import com.besha.egyptguide.auth.screens.signup.presentation.viewmodel.SignUpViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(
    navController: NavController,
) {

    val viewModel = hiltViewModel<SignUpViewModel>()
    val state by viewModel.viewStates.collectAsState()


    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }


    val fieldsTouched by remember {
        derivedStateOf {
            fullNameTouched && emailTouched &&  passwordTouched && confirmPasswordTouched
        }
    }

    var fullNameErr by remember { mutableStateOf(false) }
    var emailErr by remember { mutableStateOf(false) }
    var passwordErr by remember { mutableStateOf(false) }
    var confirmPasswordErr by remember { mutableStateOf(false) }

    val signUpErr by remember {
        derivedStateOf {
            fullNameErr || emailErr  || passwordErr || confirmPasswordErr || !fieldsTouched
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val showDialog by remember(state.signUpState) {
        mutableStateOf(
            state.signUpState.isLoading ||
                    state.signUpState.isSuccess ||
                    state.signUpState.errorThrowable != null
        )
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                if (state.signUpState.isSuccess) {
                    Button(onClick = {
                        navController.popBackStack()
                    }) {
                        Text("OK")
                    }
                } else if (state.signUpState.errorThrowable != null) {

                    Button(onClick = {
                        viewModel.executeAction(SignUpActions.ResetState)

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
                        state.signUpState.isLoading -> {
                            CircularProgressIndicator()
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = "Creating your account..."
                            )
                        }

                        state.signUpState.isSuccess -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = "Verify your email to continue",
                                textAlign = TextAlign.Center
                            )
                        }

                        state.signUpState.errorThrowable != null -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = state.signUpState.errorThrowable?.message ?: "",
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
                        contentDescription = "Visibility",
                        tint = colorResource(R.color.gray)
                    )
                }
            }
        }
    ) { x ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp)
                .padding(x)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(end = 24.dp),
                text = stringResource(R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32 * 1.3.sp
            )

            Text(
                modifier = Modifier.padding(top = 12.dp),
                color = colorResource(R.color.gray),
                text = stringResource(R.string.create_an_account),
                fontSize = 12.sp,
            )


            AuthOutlinedTextField(
                title = stringResource(R.string.full_name),
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameTouched = true
                    fullNameErr = fullName.isBlank()
                },
                placeholder = stringResource(R.string.enter_your_full_name),
                modifier = Modifier.padding(top = 32.dp),
                isError = fullNameErr,
                supportingText = "Full name is required"
            )

            AuthOutlinedTextField(
                title = stringResource(R.string.email),
                value = email,
                onValueChange = {
                    email = it
                    emailTouched = true
                    emailErr = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                },
                placeholder = stringResource(R.string.email_address),
                modifier = Modifier.padding(top = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailErr,
                supportingText = "Invalid email"
            )

            /*
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            color = colorResource(R.color.gray),
                            text = stringResource(R.string.phone_number),
                            fontSize = 12.sp,
                        )

                     CountryCodePickerTextField(
                            number = phoneNumber,
                            onValueChange = { country, number, isValid ->
                                selectedCountry = Country.findCountry(country)
                                phoneNumber = number
                                phoneNumberErr = !isValid
                                phoneNumberTouched = true
                            },
                            selectedCountry = selectedCountry,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            trailingIcon = {
                                if (phoneNumber.isNotEmpty()) {
                                    IconButton(onClick = { phoneNumber = "" }) {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                }
                            },
                            placeholder = {
                                Text(
                                    stringResource(R.string.enter_your_phone_number),
                                    color = colorResource(R.color.gray)
                                )
                            },
                            showError = true,
                            showSheet = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colorResource(R.color.white),
                                unfocusedContainerColor = colorResource(R.color.white),
                                focusedBorderColor = colorResource(R.color.stroke_gray),
                                unfocusedBorderColor = colorResource(R.color.stroke_gray),
                                errorContainerColor = colorResource(R.color.white),

                                ),
                        )*/

            AuthOutlinedTextField(
                title = stringResource(R.string.password),
                value = password,
                onValueChange = {
                    password = it
                    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z]).{8,}$")
                    passwordErr = !regex.matches(it)
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
                supportingText = "Must be at least 8 characters with upper & lower case",
                passwordVisible = passwordVisible
            )

            AuthOutlinedTextField(
                title = stringResource(R.string.confirm_password),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordErr = confirmPassword != password
                    confirmPasswordTouched = true
                },
                placeholder = stringResource(R.string.repeat_password),
                modifier = Modifier.padding(top = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPasswordErr,
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
                supportingText = "Passwords don't match",
                passwordVisible = passwordVisible
            )
            AuthButton(
                text = stringResource(R.string.sign_up),
                modifier = Modifier
                    .padding(top = 38.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                hasError = signUpErr,
            ) {
                viewModel.executeAction(
                    SignUpActions.SignUp(
                        SignUpRequest(
                            fullName = fullName,
                            email = email,
                            password = password
                        )
                    )
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
                    text = stringResource(R.string.already_have_an_account),
                    color = colorResource(R.color.gray),
                    fontSize = 12.sp,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    text = stringResource(R.string.log_in),
                    color = colorResource(R.color.blue),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )

            }


        }
    }
}