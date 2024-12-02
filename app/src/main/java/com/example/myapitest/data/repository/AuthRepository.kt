package com.example.myapitest.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {


    fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onResult: (Boolean) -> Unit) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
}
