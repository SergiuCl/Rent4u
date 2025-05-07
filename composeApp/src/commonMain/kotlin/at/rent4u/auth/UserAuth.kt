package at.rent4u.auth

expect class UserAuth() {
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean
}