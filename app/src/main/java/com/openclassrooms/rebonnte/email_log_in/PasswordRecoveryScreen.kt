package com.openclassrooms.rebonnte.email_log_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    navController: NavController
) {
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val emailError = rememberSaveable { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { PasswordRecoveryTopBar(navController) }
    ) { padding ->
        PasswordRecoveryContent(
            email = email,
            emailError = emailError,
            showDialog = showDialog,
            onSendResetEmail = { emailValue ->
                handleSendResetEmail(auth, emailValue, emailError) { success ->
                    showDialog = success
                }
            },
            onDismissDialog = {
                showDialog = false
                navController.navigateUp()
            },
            padding = padding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.password_recovery), color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.contentDescription_go_back),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun PasswordRecoveryContent(
    email: MutableState<TextFieldValue>,
    emailError: MutableState<String?>,
    showDialog: Boolean,
    onSendResetEmail: (String) -> Unit,
    onDismissDialog: () -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.password_recovery_label),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        EmailInputField(email, emailError)

        Spacer(modifier = Modifier.height(16.dp))

        SendResetEmailButton(email.value.text, emailError, onSendResetEmail)

        if (showDialog) {
            PasswordResetDialog(onDismissDialog, email.value.text)
        }
    }
}

@Composable
fun EmailInputField(email: MutableState<TextFieldValue>, emailError: MutableState<String?>) {
    TextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = { Text(text = stringResource(id = R.string.email), color = Color.White) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            errorTextColor = Color.Red,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedContainerColor = colorResource(id = R.color.teal_700),
            unfocusedContainerColor = colorResource(id = R.color.teal_700),
            errorContainerColor = Color.White
        ),
        isError = emailError.value != null,
        modifier = Modifier.fillMaxWidth()
    )

    emailError.value?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
}

@Composable
fun SendResetEmailButton(
    email: String,
    emailError: MutableState<String?>,
    onSendResetEmail: (String) -> Unit
) {
    Button(
        onClick = {
            emailError.value = if (email.isBlank()) "Email cannot be empty" else null
            if (emailError.value == null) {
                onSendResetEmail(email)
            }
        },
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RectangleShape
    ) {
        Text("Send Reset Email", color = Color.White)
    }
}

@Composable
fun PasswordResetDialog(onDismissDialog: () -> Unit, email: String) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = { Text("Password Reset", color = Color.White) },
        text = {
            Text(
                text = stringResource(id = R.string.password_recovery_message, email),
                color = Color.White
            )
        },
        confirmButton = {
            TextButton(onClick = onDismissDialog) {
                Text("OK", color = Color.White)
            }
        }
    )
}

private fun handleSendResetEmail(
    auth: FirebaseAuth,
    email: String,
    emailError: MutableState<String?>,
    onResult: (Boolean) -> Unit
) {
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                task.exception?.let {
                    emailError.value = if (it.message?.contains("no user record", ignoreCase = true) == true) {
                        "No account found with this email address."
                    } else {
                        "Failed to send reset email. Error: ${it.message}"
                    }
                }
                onResult(false)
            }
        }
}