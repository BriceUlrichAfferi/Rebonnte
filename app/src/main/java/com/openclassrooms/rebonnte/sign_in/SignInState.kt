package com.openclassrooms.rebonnte.sign_in

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userData: Userdata? = null
)
