package com.openclassrooms.rebonnte.ui.medicine

import com.openclassrooms.rebonnte.ui.history.History
import java.util.UUID

data class Medicine(
    var name: String = "",
    var stock: Int = 0,
    var nameAisle: String = "",
    var histories: List<History> = emptyList(),
    var id: String = UUID.randomUUID().toString(),
    val addedByEmail: String = ""
) {
    constructor() : this("", 0, "", emptyList(), UUID.randomUUID().toString(), "")
}
