package com.openclassrooms.rebonnte.route

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openclassrooms.rebonnte.email_log_in.LoginScreen
import com.openclassrooms.rebonnte.email_log_in.PasswordRecoveryScreen
import com.openclassrooms.rebonnte.email_sign_up.SignUpScreen
import com.openclassrooms.rebonnte.sign_in.EmailAuthClient
import com.openclassrooms.rebonnte.sign_in.EmailSignInScreen
import com.openclassrooms.rebonnte.sign_in.GoogleAuthUiClient
import com.openclassrooms.rebonnte.sign_in.SignInScreen
import com.openclassrooms.rebonnte.sign_in.SignInViewModel
import com.openclassrooms.rebonnte.sign_in.Userdata
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel

import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.appNavigation(
    navController: NavController,
    googleAuthUiClient: GoogleAuthUiClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope
) {
    composable("sign_in") {
        val viewModel: SignInViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        SignInScreen(
            state = state,
            onGoogleSignInClick = { navController.navigate("google_sign_in") },
            onEmailSignInClick = { navController.navigate("email_sign_in") }
        )
    }

    composable("google_sign_in") {
        val viewModel: SignInViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(result.data ?: return@launch)
                        viewModel.onSignInResult(signInResult)
                        if (signInResult.data != null) {
                            navController.navigate("aisle") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            googleAuthUiClient.signIn()?.let { intentSender ->
                googleSignInLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
    }

    composable("email_sign_in") {
        EmailSignInScreen(
            onLogInClick = { navController.navigate("log_in") },
            onSignUpClick = { navController.navigate("sign_up") },
            navController = navController
        )
    }

    composable("aisle") {
        val aisleViewModel: AisleViewModel = koinViewModel()
    }

    composable("medicine") {
        val medicineViewModel: MedicineViewModel = koinViewModel()
        MedicineScreen(navController, medicineViewModel)
    }

    composable("sign_up") {
        SignUpScreen(
            onLoginSuccess = {
                navController.navigate("aisle") {
                    popUpTo("sign_up") { inclusive = true }
                }
            },
            navController = navController
        )
    }

    composable("log_in") {
        LoginScreen(navController = navController)
    }

    composable("password_recovery") {
        PasswordRecoveryScreen(navController = navController)
    }

    composable(
        "medicine_detail/{medicineName}",
        arguments = listOf(navArgument("medicineName") { type = NavType.StringType })
    ) { backStackEntry ->
        val medicineName = backStackEntry.arguments?.getString("medicineName") ?: "Unknown"
        val medicineViewModel: MedicineViewModel = koinViewModel()
        MedicineDetailScreen(
            name = medicineName,
            viewModel = medicineViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}