package com.openclassrooms.rebonnte.di

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.sign_in.EmailAuthClient
import com.openclassrooms.rebonnte.sign_in.GoogleAuthUiClient
import com.openclassrooms.rebonnte.sign_in.SignInViewModel
import com.openclassrooms.rebonnte.ui.aisle.AisleRepository
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineRepository
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Instances
    single { FirebaseFirestore.getInstance() }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    single { GoogleAuthUiClient(context = androidContext(), oneTapClient = Identity.getSignInClient(androidContext())) }
    single { EmailAuthClient() }
    // ViewModels

    viewModel { AisleViewModel(get()) }
    viewModel { MedicineViewModel(get()) }
    viewModel { SignInViewModel(get(), get()) }
    // Repositories
    single { AisleRepository(get()) }
    single { MedicineRepository(get()) }


}
