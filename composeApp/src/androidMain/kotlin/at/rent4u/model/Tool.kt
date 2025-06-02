package at.rent4u.model

data class Tool(
    val createdAt: Long = 0,
    val type: String = "",
    val brand: String = "",
    val modelNumber: String = "",
    val description: String = "",
    val powerSource: String = "",
    val weight: String = "",
    val dimensions: String = "",
    val fuelType: String = "",
    val voltage: String = "",
    val availabilityStatus: String = "",
    var rentalRate: Double = 0.0,
    val image: String = "",
    val id: String = "",
    )
