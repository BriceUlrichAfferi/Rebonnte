package com.openclassrooms.rebonnte.ui.aisle

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AisleRepository(private val firestore: FirebaseFirestore) {
    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> = _aisles.asStateFlow()
    private val AISLES_COLLECTION = "aisles"

    init {
        loadAisles()
    }

    private fun loadAisles() {
        firestore.collection(AISLES_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val aisleList = snapshot?.documents?.mapNotNull { it.toObject(Aisle::class.java) } ?: emptyList()
                _aisles.value = aisleList
                if (aisleList.isEmpty()) {
                    addAisle(Aisle("I am Here Aisle"))
                }
            }
    }

    fun addAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION).document(aisle.id).set(aisle)
    }

    fun deleteAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION).document(aisle.id).delete()
    }
}