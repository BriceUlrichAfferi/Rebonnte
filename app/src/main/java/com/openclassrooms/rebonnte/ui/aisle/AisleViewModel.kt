package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.StateFlow

class AisleViewModel(val repository: AisleRepository) : ViewModel() {
    val aisles: StateFlow<List<Aisle>> = repository.aisles


    fun addRandomAisle() {
        val currentAisles = aisles.value
        val newAisle = Aisle("Aisle ${currentAisles.size + 1}")
        repository.addAisle(newAisle)
    }

    fun refreshAisles() {
        repository.loadAisles()
    }

    fun deleteAisle(aisle: Aisle) {
        repository.deleteAisle(aisle)
    }
}
