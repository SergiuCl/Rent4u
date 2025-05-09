package at.rent4u.auth

expect class UserAuth() {
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Pair<Boolean, String?>

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean

    fun logoutUser()
}