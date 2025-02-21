package com.openclassrooms.rebonnte.email_sign_up

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onLoginSuccess: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val email = rememberSaveable { mutableStateOf("") }
    val name = rememberSaveable { mutableStateOf("") }
    val surname = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf<String?>(null) }
    val currentStep = rememberSaveable { mutableStateOf(1) }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordError = rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            if (currentStep.value > 0) SignUpTopBar(currentStep, navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayAppIcon()

            Spacer(modifier = Modifier.height(21.dp))

            when (currentStep.value) {
                1 -> EmailInputScreen(
                    email = email.value,
                    emailError = emailError.value,
                    onEmailChange = { email.value = it },
                    onValidateEmail = { handleEmailValidation(email, emailError, currentStep) }
                )
                2 -> NameSurnameInputScreen(
                    name = name.value,
                    surname = surname.value,
                    onNameChange = { name.value = it },
                    onSurnameChange = { surname.value = it },
                    onNext = { if (name.value.isNotBlank() && surname.value.isNotBlank()) currentStep.value = 3 }
                )
                3 -> PasswordInputScreen(
                    password = password.value,
                    onPasswordChange = { password.value = it },
                    onLogin = { handlePasswordValidation(email, password, name, surname, passwordError, context, onLoginSuccess) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTopBar(currentStep: MutableState<Int>, navController: NavController) {
    val title = when (currentStep.value) {
        1 -> R.string.sign_up
        2 -> R.string.sign_up2
        else -> R.string.sign_up3
    }
    TopAppBar(
        title = { Text(text = stringResource(id = title), color = Color.White) },
        navigationIcon = {
            IconButton(onClick = {
                if (currentStep.value > 1) {
                    currentStep.value -= 1
                } else {
                    navController.navigate("sign_in")
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.contentDescription_go_back),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun DisplayAppIcon() {
    Image(
        painter = painterResource(id = R.drawable.eventorias777ff),
        contentDescription = "App Icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(27f / 25f)
    )
}

fun handleEmailValidation(
    email: MutableState<String>,
    emailError: MutableState<String?>,
    currentStep: MutableState<Int>
) {
    emailError.value = when {
        email.value.isBlank() -> "Email cannot be empty"
        !Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> "Please enter a valid email address"
        else -> null
    }

    if (emailError.value == null) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isExistingUser = task.result?.signInMethods?.isNotEmpty() == true
                    currentStep.value = if (isExistingUser) 3 else 2
                } else {
                    emailError.value = "Error: ${task.exception?.message}"
                }
            }
    }
}

fun handlePasswordValidation(
    email: MutableState<String>,
    password: MutableState<String>,
    name: MutableState<String>,
    surname: MutableState<String>,
    passwordError: MutableState<String?>,
    context: android.content.Context,
    onLoginSuccess: () -> Unit
) {
    passwordError.value = when {
        password.value.isBlank() -> "Password cannot be empty"
        password.value.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

    if (passwordError.value == null) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore(name, surname, context, onLoginSuccess)
                } else {
                    signInWithExistingCredentials(email, password, context, onLoginSuccess, passwordError)
                }
            }
    }
}

fun saveUserToFirestore(
    name: MutableState<String>,
    surname: MutableState<String>,
    context: android.content.Context,
    onLoginSuccess: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.let {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(it.uid)
        userRef.set(mapOf(
            "firstName" to name.value,
            "lastName" to surname.value
        )).addOnSuccessListener {
            onLoginSuccess()
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to save user details: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

fun signInWithExistingCredentials(
    email: MutableState<String>,
    password: MutableState<String>,
    context: android.content.Context,
    onLoginSuccess: () -> Unit,
    passwordError: MutableState<String?>
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
        .addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                onLoginSuccess()
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                passwordError.value = "Error: ${signInTask.exception?.message}"
            }
        }
}
