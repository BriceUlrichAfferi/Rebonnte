package com.openclassrooms.rebonnte.email_sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun NameSurnameInputScreen(
    name: String,
    surname: String,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    // State for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_name_surname),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Name Input
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(
                text = stringResource(id = R.string.name),
                color = Color.White
            ) },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                errorIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = colorResource(id = R.color.teal_700),
                unfocusedContainerColor = colorResource(id = R.color.teal_700),
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color.White
            )
        )
        nameError?.let {
            Text(
                text = it,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Surname Input
        OutlinedTextField(
            value = surname,
            onValueChange = onSurnameChange,
            label = {
                Text("Surname",
                    color = Color.White)
            },
            isError = surnameError != null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                errorIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = colorResource(id = R.color.teal_700),
                unfocusedContainerColor = colorResource(id = R.color.teal_700),
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color.White
            )
        )
        surnameError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Next Button to proceed to password screen
        Button(
            onClick = {

                nameError = when {
                    name.isBlank() -> "Name cannot be empty"
                    else -> null
                }

                surnameError = when {
                    surname.isBlank() -> "Surname cannot be empty"
                    else -> null
                }

                if (nameError == null && surnameError == null) {
                    onNext()
                }
            },
            modifier = Modifier
                .padding(16.dp, bottom = 16.dp)
                .width(150.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RectangleShape,
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
}