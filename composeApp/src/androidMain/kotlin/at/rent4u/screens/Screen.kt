package at.rent4u.screens

const val ARG_TOOL_ID = "toolId"

sealed class Screen(val route: String) {
    object ToolList : Screen("tool_list")
    object ToolDetails : Screen("tool_details/{$ARG_TOOL_ID}") {
        fun createRoute(toolId: String) = "tool_details/$toolId"
    }
    object Booking : Screen("booking/{$ARG_TOOL_ID}") {
        fun createRoute(toolId: String) = "booking/$toolId"
    }
    object AdminToolCreate : Screen("admin_tool_create")
    object AdminToolUpdate : Screen("admin_tool_update/{$ARG_TOOL_ID}") {
        fun createRoute(toolId: String): String {
            return "admin_tool_update/$toolId"
        }
    }
    object Login : Screen("login")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Register : Screen("register")
    object MyBookings : Screen("my_bookings")
    object ContactUs : Screen("contact_us")
}
