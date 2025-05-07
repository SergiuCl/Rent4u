package at.rent4u

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform