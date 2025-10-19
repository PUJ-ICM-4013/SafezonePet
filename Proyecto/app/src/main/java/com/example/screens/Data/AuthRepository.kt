package com.example.screens.Data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()){

    suspend fun signIn(email: String, password: String): FirebaseUser? =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(auth.currentUser)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("Authentication failed"))
                    }
                }
        }


    suspend fun signUp(email: String, password: String): FirebaseUser? =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(auth.currentUser)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("Registration failed"))
                    }
                }
        }

    fun signOut() {
        auth.signOut()
    }
}
