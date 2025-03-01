package com.openclassrooms.rebonnte.ui.aisle

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AisleRepository(private val firestore: FirebaseFirestore) {
    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> = _aisles.asStateFlow()
    private val AISLES_COLLECTION = "aisles"
    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadAisles()
    }

    fun loadAisles() {
        listenerRegistration?.remove()
        println("Attaching new snapshot listener")
        listenerRegistration = firestore.collection(AISLES_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Snapshot listener error: $e")
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    println("Snapshot is null")
                    return@addSnapshotListener
                }
                val aisleList = snapshot.documents.mapNotNull { it.toObject(Aisle::class.java) }
                println("Aisles updated: $aisleList")
                _aisles.value = aisleList
                if (aisleList.isEmpty()) {
                    addAisle(Aisle("Aisle 1"))
                }
            }
    }

    fun addAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION).document(aisle.id).set(aisle)
            .addOnSuccessListener {
                println("Aisle ${aisle.name} added successfully, ID: ${aisle.id}")
                loadAisles() // Force refresh
            }
            .addOnFailureListener { e -> println("Failed to add aisle: $e") }
    }



    fun deleteAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION)
            .document(aisle.id)
            .delete()
            .addOnSuccessListener { println("Medicine deleted: $aisles") }
            .addOnFailureListener { e -> println("Failed to delete medicine: ${e.message}") }
    }

}