package com.openclassrooms.rebonnte.bottom_navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.sign_in.GoogleAuthUiClient
import com.openclassrooms.rebonnte.sign_in.EmailAuthClient
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    googleAuthUiClient: GoogleAuthUiClient,
    emailAuthClient: EmailAuthClient,
    onSignOut: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Aisle") },
            label = { Text("Aisle") },
            selected = currentRoute == "aisle",
            onClick = {
                navController.navigate("aisle") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Medicine") },
            label = { Text("Medicine") },
            selected = currentRoute == "medicine",
            onClick = {
                navController.navigate("medicine") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out") },
            label = { Text("Sign Out") },
            selected = false,
            onClick = {
                showSignOutDialog = true // Show dialog instead of signing out directly
            }
        )
    }

    // Sign-out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        // Sign out from Google (includes Firebase sign-out)
                        googleAuthUiClient.signOut()

                        // Clear email auth session (includes Firebase sign-out)
                        emailAuthClient.clearSession()

                        // Navigate to sign-in screen and clear back stack
                        navController.navigate("sign_in") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }

                        // Call additional sign-out callback
                        onSignOut()
                    }
                    showSignOutDialog = false // Close dialog after confirming
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}