package com.openclassrooms.rebonnte.sign_in

data class SignInResult(
    val data: Userdata?,
    val errorMessage: String?
)

data class Userdata(
    val userId: String,
    val userName: String,
    val email: String,
    val profilePictureUrl: String?,
    val photoUrl: String?
)
