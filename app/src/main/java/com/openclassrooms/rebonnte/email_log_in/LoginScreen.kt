package com.openclassrooms.rebonnte.email_log_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }
    val currentStep = remember { mutableStateOf(1) }
    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            LoginTopAppBar(currentStep, navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginScreenLogo()

            when (currentStep.value) {
                1 -> EmailStep(
                    email = email,
                    emailError = emailError,
                    onNext = {
                        emailError.value = validateEmail(email.value)
                        if (emailError.value == null) currentStep.value = 2
                    }
                )
                2 -> PasswordStep(
                    email = email,
                    password = password,
                    passwordError = passwordError,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                    onSignIn = {
                        passwordError.value = validatePassword(password.value)
                        if (emailError.value == null && passwordError.value == null) {
                            signInWithEmail(auth, email.value, password.value, navController)
                        }
                    },
                    onForgotPassword = { navController.navigate("password_recovery") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopAppBar(currentStep: MutableState<Int>, navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.sign_in), color = Color.White) },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (currentStep.value > 1) {
                        currentStep.value -= 1
                    } else {
                        navController.navigate("email_sign_in")
                    }
                }
            ) {
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
fun LoginScreenLogo() {
    Image(
        painter = painterResource(id = R.drawable.eventorias777ff),
        contentDescription = "App Icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(27f / 25f)
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun EmailStep(
    email: MutableState<String>,
    emailError: MutableState<String?>,
    onNext: () -> Unit
) {
    TextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = { Text("Email Address") },
        isError = emailError.value != null,
        modifier = Modifier.fillMaxWidth()
    )
    emailError.value?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RectangleShape,
    ) {
        Text("Next", color = Color.White)
    }
}

@Composable
fun PasswordStep(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordError: MutableState<String?>,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    TextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Password") },
        isError = passwordError.value != null,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(imageVector = image, contentDescription = description, tint = Color.Black)
            }
        }
    )
    passwordError.value?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSignIn,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RectangleShape,
    ) {
        Text(text = stringResource(id = R.string.sign_in), color = Color.White)
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onForgotPassword) {
        Text("Trouble signing in?", color = Color.White)
    }
}

fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !isValidEmail(email) -> "Email not valid"
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return if (password.isBlank()) "Password cannot be empty" else null
}

fun signInWithEmail(auth: FirebaseAuth, email: String, password: String, navController: NavController) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("aisle") {
                    popUpTo(0) { inclusive = true }
                }
                Toast.makeText(navController.context, "Sign in successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(navController.context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}