package com.example.myapitest.ui.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapitest.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state
    var verificationId: String = ""
    var isLoggedIn: Boolean = false

    suspend fun requestOtp(phoneNumber: String, activity: Activity): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    viewModelScope.launch {
                        signInWithCredential(credential)
                        continuation.resume(true)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _state.value = _state.value.copy(errorMessage = e.message)
                    Log.e("LoginViewModel", "OTP request failed: ${e.message}")
                    continuation.resume(false)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginViewModel.verificationId = verificationId
                    continuation.resume(true)
                    Log.d("LoginViewModel", "OTP sent successfully.")
                }
            }

            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()
            )
        }

    }

    fun checkUserAuthentication(): Boolean {
        return authRepository.isUserAuthenticated()
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.firebaseAuthWithGoogle(account, onResult)
            _state.value = _state.value.copy(isGoogleLoginSuccessful = true)
        }
    }

    suspend fun verifyOtp(otp: String): Boolean {
        if (otp.isEmpty()) {
            _state.value = _state.value.copy(errorMessage = "Código OTP é obrigatório")
            return false
        }

        if (verificationId.isEmpty()) {
            _state.value = _state.value.copy(errorMessage = "Código de verificação não encontrado")
            return false
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        return signInWithCredential(credential)
    }

    private suspend fun signInWithCredential(credential: PhoneAuthCredential): Boolean {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isLoggedIn = true
                    _state.value = _state.value.copy(errorMessage = null)
                    Log.d("LoginViewModel", "OTP verification successful")
                    continuation.resume(true)
                } else {
                    val exception = task.exception
                    _state.value = _state.value.copy(errorMessage = exception?.message)
                    Log.e("LoginViewModel", "OTP verification failed: ${exception?.message}")
                    continuation.resume(false)
                }
            }
        }
    }
}

data class AuthState(
    val phoneNumber: String = "",
    val otp: String = "",
    val verificationId: String = "",
    val errorMessage: String? = null,
    val isGoogleLoginSuccessful: Boolean? = null
)
