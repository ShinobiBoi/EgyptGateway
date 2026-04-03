package com.besha.egyptguide.auth.screens.login.data.google


import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.besha.egyptguide.R
import com.besha.egyptguide.auth.screens.login.data.model.GoogleSignInResult
import com.besha.egyptguide.auth.screens.login.data.model.LoginRequest
import com.besha.egyptguide.auth.screens.login.data.model.LoginResponse
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpResponse
import com.besha.egyptguide.auth.screens.login.data.model.UserData
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException



class GoogleAuthClient  @Inject constructor(
    @ApplicationContext private val context: Context,
) {


    private val auth = Firebase.auth

    private val credentialManager= CredentialManager.create(context)




    suspend fun googleSignIn(activity: Activity): GoogleSignInResult {
        return try {


            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.google_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activity,
                request = request
            )

            handleCredential(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            GoogleSignInResult(null, e.message)
        }
    }

    private suspend fun handleCredential(
        result: GetCredentialResponse
    ): GoogleSignInResult {

        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            val googleIdToken = googleIdTokenCredential.idToken


            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)

            val user = auth.signInWithCredential(firebaseCredential)
                .await()
                .user ?: throw IllegalStateException("Sign in failed")

            return GoogleSignInResult(
                user = UserData(
                    userId = user.uid,
                    fullName = user.displayName,
                    email = user.email,
                    phoneNumber = user.phoneNumber,
                    profilePhoto = user.photoUrl?.toString()
                ),
                errorMessage = null
            )
        }

        return GoogleSignInResult(null, "Invalid credential")
    }


    suspend fun signUp(signUpRequest: SignUpRequest): SignUpResponse {
        return try {
            auth.createUserWithEmailAndPassword(signUpRequest.email, signUpRequest.password).await()

            val user = auth.currentUser ?: throw IllegalStateException("User creation failed")
            user.sendEmailVerification().await()


            SignUpResponse(
                isSuccessful = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            SignUpResponse(
                isSuccessful = false,
                errorMessage = e.message
            )
        }
    }



    suspend fun logIn(loginRequest: LoginRequest): LoginResponse {
        return try {
            // 1. Sign in with email & password
            val authResult = auth.signInWithEmailAndPassword(
                loginRequest.email,
                loginRequest.password
            ).await()

            val user = authResult.user ?: throw IllegalStateException("Login failed")


            // 2. Check if email is verified
            if (!user.isEmailVerified) {
                return LoginResponse(
                    isSuccessful = false,
                    errorMessage = "Please verify your email before logging in."
                )
            }

            // 3. Get Firebase ID token (JWT)
            val idTokenResult = user.getIdToken( true).await()
            val idToken = idTokenResult.token
            Log.d("TAG", "Firebase ID Token: $idToken")  // <-- This logs the JWT

            // 4. Return successful login
            LoginResponse(
                isSuccessful = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            LoginResponse(
                isSuccessful = false,
                errorMessage = e.message ?: "Something went wrong"
            )
        }
    }

    suspend fun signOut(){
        try {
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )
            auth.signOut()
        }catch (e:Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser():UserData?= auth.currentUser?.run {
        UserData(
            userId = uid,
            fullName = displayName,
            email = email,
            phoneNumber = phoneNumber,
            profilePhoto = photoUrl?.toString()
        )
    }

}