package com.openclassrooms.rebonnte.email_sign_up

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun PasswordInputScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    val passwordError = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText()
        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            passwordError = passwordError.value
        )

        if (passwordError.value != null) {
            ErrorText(errorMessage = passwordError.value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            password = password,
            passwordError = passwordError,
            context = context,
            onLogin = onLogin
        )
    }
}

@Composable
fun HeaderText() {
    Text(
        text = stringResource(id = R.string.enter_password),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    passwordError: String?
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(text = stringResource(id = R.string.password), color = Color.Black) },
        isError = passwordError != null,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            PasswordVisibilityIcon(
                passwordVisible = passwordVisible,
                onVisibilityToggle = onVisibilityToggle
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Gray,
            errorTextColor = Color.Red,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            errorIndicatorColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedContainerColor = colorResource(id = R.color.teal_700),
            unfocusedContainerColor = colorResource(id = R.color.teal_700),
            disabledContainerColor = Color.LightGray,
            errorContainerColor = Color.Red
        )
    )
}

@Composable
fun PasswordVisibilityIcon(
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit
) {
    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
    val description = if (passwordVisible) "Hide password" else "Show password"
    IconButton(onClick = onVisibilityToggle) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = Color.White
        )
    }
}

@Composable
fun ErrorText(errorMessage: String?) {
    Text(
        text = errorMessage ?: "",
        color = Color.White,
        style = MaterialTheme.typography.bodySmall,

        )
}

@Composable
fun LoginButton(
    password: String,
    passwordError: MutableState<String?>,
    context: Context,
    onLogin: () -> Unit
) {
    Button(
        onClick = {
            validatePassword(password, passwordError)

            if (passwordError.value == null) {
                Toast.makeText(context, "Proceeding with login", Toast.LENGTH_SHORT).show()
                onLogin()
            } else {
                Toast.makeText(context, "Password validation failed", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RectangleShape
    ) {
        Text(text = stringResource(id = R.string.save))
    }
}

private fun validatePassword(password: String, passwordError: MutableState<String?>) {
    passwordError.value = when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}
