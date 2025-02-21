package com.openclassrooms.rebonnte.ui.aisle

import java.util.UUID

data class Aisle(
    var name: String = "",
    var id: String = UUID.randomUUID().toString(),
    var timestamp: Long = System.currentTimeMillis()  // Add timestamp field
) {
    // Explicit no-arg constructor for Firestore deserialization
    constructor() : this("", UUID.randomUUID().toString(), System.currentTimeMillis())
}
