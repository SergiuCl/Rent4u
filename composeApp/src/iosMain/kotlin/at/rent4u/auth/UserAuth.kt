package at.rent4u.auth

actual class UserAuth actual constructor() {
    actual suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        // Stubbed out — you can implement this later with Ktor or native libs
        println("iOS not implemented yet.")
        return false
    }

    actual suspend fun loginUser(email: String, password: String): Boolean {
        // Stubbed out — you can implement this later with Ktor or native libs
        println("iOS not implemented yet.")
        return false
    }
}
