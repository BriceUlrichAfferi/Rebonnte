package com.openclassrooms.rebonnte.ui.history

data class History(
    var medicineName: String = "",
    var userId: String = "",
    var date: String = "",
    var details: String = ""
) {
    constructor() : this("", "", "", "")
}
