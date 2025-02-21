package com.openclassrooms.rebonnte.sign_in

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.notification.NotificationViewModel
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    userdata: Userdata?,

    viewModel: NotificationViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val userDataState = remember { mutableStateOf(userdata) }

    LaunchedEffect(userdata?.userId) {
        userdata?.userId?.let { uid ->
            val firestore = FirebaseFirestore.getInstance()
            val userRef = firestore.collection("users").document(uid)

            try {
                val document = userRef.get().await()
                if (document.exists()) {
                    val firstName = document.getString("firstName") ?: "No Name"
                    val lastName = document.getString("lastName") ?: "No Surname"
                    val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""

                    val email = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"

                    userDataState.value = Userdata(
                        userId = uid,
                        userName =  "$firstName $lastName",
                        email = email,
                        profilePictureUrl =  profilePictureUrl,
                        photoUrl =  photoUrl
                    )
                } else {
                    userDataState.value = userdata // Fallback to passed userdata
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_LONG).show()
                userDataState.value = userdata // If fetching fails, use the passed userdata
            }
        }
    }

    val userData = userDataState.value
    if (userData == null) {
        Text("Loading user data...")
        return
    }

    val notificationsEnabled = remember { mutableStateOf(viewModel.areNotificationsEnabled()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "User Profile",
                        color = Color.White
                    )
                },
                actions = {
                    if (!userdata?.profilePictureUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = userdata?.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default profile icon",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Name Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "Name", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = userData.userName, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "E-mail", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = userData.email, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Toggle
            NotificationParameter(
                notificationsEnabled = notificationsEnabled.value,
                onNotificationDisabledClicked = {
                    viewModel.disableNotifications()
                    notificationsEnabled.value = viewModel.areNotificationsEnabled()
                },
                onNotificationEnabledClicked = {
                    viewModel.enableNotifications()
                    notificationsEnabled.value = viewModel.areNotificationsEnabled()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .padding(16.dp)
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RectangleShape,
            ) {
                Text(text = "Sign out", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationParameter(
    modifier: Modifier = Modifier,
    notificationsEnabled: Boolean,
    onNotificationEnabledClicked: () -> Unit,
    onNotificationDisabledClicked: () -> Unit
) {
    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationsPermissionState?.launchPermissionRequest()
                    }
                    onNotificationEnabledClicked()
                } else {
                    onNotificationDisabledClicked()
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Red,
                uncheckedThumbColor = Color.Black,
                uncheckedTrackColor = Color.LightGray
            )
        )

        Text(
            modifier = Modifier
                .padding(8.dp),
            text = stringResource(id = R.string.notifications),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}