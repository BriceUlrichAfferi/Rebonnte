package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random

class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {
    val medicines: StateFlow<List<Medicine>> = repository.medicines

    fun addRandomMedicine(aisles: List<Aisle>) {
        if (aisles.isEmpty()) return
        val addingUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"
        val newMedicine = Medicine(
            name = "Medicine ${Random().nextInt(1000)}", // Use random to avoid duplicates
            stock = Random().nextInt(100),
            nameAisle = aisles[Random().nextInt(aisles.size)].name,
            histories = emptyList(),
            addedByEmail = addingUserEmail
        )
        repository.addMedicine(newMedicine)
    }

    fun addMedicine(medicine: Medicine) {
        val addingUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"
        val updatedMedicine = medicine.copy(addedByEmail = addingUserEmail)
        repository.addMedicine(updatedMedicine)
    }

    fun updateMedicine(medicine: Medicine) {
        repository.updateMedicine(medicine)
    }

    fun deleteMedicine(medicineId: String) {
        repository.deleteMedicine(medicineId)
    }

    fun filterByName(name: String) {
        repository.filterByName(name)
    }

    fun sortByName() {
        repository.sortByName()
    }

    fun sortByStock() {
        repository.sortByStock()
    }

    fun sortByNone() {
        repository.sortByNone()
    }
}