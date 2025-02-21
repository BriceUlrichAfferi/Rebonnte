package com.openclassrooms.rebonnte.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { currentState ->
            currentState.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                userData = result.data
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun getUserData(): Userdata? = _state.value.userData

    // Add this to update the state after successful sign-in
    fun onSignInSuccess(userData: Userdata) {
        _state.update { it.copy(userData = userData, isSignInSuccessful = true) }
    }
}