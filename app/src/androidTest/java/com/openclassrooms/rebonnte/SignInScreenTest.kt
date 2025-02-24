package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.rebonnte.sign_in.SignInScreen
import com.openclassrooms.rebonnte.sign_in.SignInState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun emailSignInButton_isClickable() {
        var emailSignInClicked = false

        composeTestRule.setContent {
            SignInScreen(
                state = SignInState(signInError = null),
                onGoogleSignInClick = {},
                onEmailSignInClick = { emailSignInClicked = true }
            )
        }

        composeTestRule
            .onNodeWithText("Sign in with Email")
            .performClick()

        assert(emailSignInClicked)
    }

    @Test
    fun showsToast_whenSignInErrorOccurs() {
        val state = SignInState(signInError = "Sign-In Failed")

        composeTestRule.setContent {
            SignInScreen(
                state = state,
                onGoogleSignInClick = {},
                onEmailSignInClick = {}
            )
        }

        assert(state.signInError == "Sign-In Failed")
    }
}
