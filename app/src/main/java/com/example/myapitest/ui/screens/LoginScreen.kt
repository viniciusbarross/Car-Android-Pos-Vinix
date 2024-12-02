package com.example.myapitest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapitest.R
import com.example.myapitest.ui.auth.GoogleAuthHelper
import com.example.myapitest.ui.components.AuthButton
import com.example.myapitest.ui.viewModel.LoginViewModel
import com.example.myapitest.ui.viewModel.AuthState
import com.example.myapitest.utils.PhoneNumber
import kotlinx.coroutines.launch

data class LoginScreenState(
    var phoneNumber: String = "",
    var otpCode: String = "",
    var isOtpRequested: Boolean = false,
    var isOtpSended: Boolean = false,
    var isLoading: Boolean = false,
)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LoginScreen(
    navController: NavController, viewModel: LoginViewModel = LoginViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember { mutableStateOf(LoginScreenState()) }
    val stateViewModel by viewModel.state.collectAsState(AuthState())

    LaunchedEffect(stateViewModel.errorMessage) {
        if (viewModel.checkUserAuthentication()) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        GoogleAuthHelper.handleGoogleSignInResult(result, viewModel, context, navController)
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    fontSize = 28.sp,
                    color = Color(0xFF6200EE),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (!state.value.isOtpRequested) {
                    InputField(
                        value = state.value.phoneNumber,
                        label = stringResource(R.string.phone_number_input),
                        keyboardType = KeyboardType.Phone,
                        onValueChange = { state.value = state.value.copy(phoneNumber = it) },
                        isError = stateViewModel.errorMessage != null
                    )
                    if (state.value.isOtpSended) VerificationMessage(!state.value.isOtpSended)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = PhoneNumber.formatPhoneNumber(state.value.phoneNumber)
                            .matches(Regex("^\\+[1-9]\\d{1,14}\$")),
                        onClick = {
                            state.value = state.value.copy(isLoading = true)
                            scope.launch {
                                val isValidPhone = viewModel.requestOtp(
                                    PhoneNumber.formatPhoneNumber(state.value.phoneNumber),
                                    context as Activity
                                )

                                if (isValidPhone) {
                                    state.value = state.value.copy(isOtpRequested = true, isLoading = false)
                                } else {
                                    state.value = state.value.copy(isLoading = false)
                                    Toast.makeText(
                                        context,
                                        "Telefone inválido. Tente novamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text(text = stringResource(R.string.send_otp_button), color = Color.White)
                    }
                } else {
                    InputField(
                        value = state.value.otpCode,
                        label = stringResource(R.string.otp_code_input),
                        keyboardType = KeyboardType.Number,
                        onValueChange = { state.value = state.value.copy(otpCode = it) },
                        isError = state.value.otpCode.length != 6 && state.value.isOtpSended
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = state.value.otpCode.isNotBlank(),
                        onClick = {
                            scope.launch {
                                val result = viewModel.verifyOtp(state.value.otpCode)
                                state.value = state.value.copy(isOtpSended = true)
                                if (result) {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    state.value = state.value.copy(isOtpRequested = false)
                                    state.value = state.value.copy(otpCode = "")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text(text = stringResource(R.string.verify_code_button), color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                AuthButtonSection(context, googleSignInLauncher)
            }
        }
    )
}

@Composable
fun InputField(
    value: String,
    label: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF6200EE)) },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),

    )
}

@Composable
fun VerificationMessage(isOtpValid: Boolean?) {
    when (isOtpValid) {
        true -> Text(
            text = stringResource(R.string.otp_verified_successfully),
            color = Color.Green,
            modifier = Modifier.padding(top = 8.dp)
        )

        false -> Text(
            text = stringResource(R.string.invalid_otp_message),
            color = Color.Red,
            modifier = Modifier.padding(top = 8.dp)
        )

        else -> {}
    }
}

@Composable
fun AuthButtonSection(
    context: Context,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
) {
    Spacer(modifier = Modifier.height(12.dp))
    AuthButton(
        onClick = { GoogleAuthHelper.initiateGoogleLogin(context, googleSignInLauncher) },
        text = stringResource(R.string.login_with_google),
        iconResId = R.drawable.ic_google,
        textColor = Color.Black,
        backgroundColor = Color.White
    )
    Spacer(modifier = Modifier.height(24.dp))
}
