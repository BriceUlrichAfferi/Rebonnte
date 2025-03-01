package com.openclassrooms.rebonnte.ui.history

data class History(
    val medicineName: String = "",
    val userId: String = "",
    val date: String = "",
    val details: String = ""
) {
    constructor() : this("", "", "", "")
}