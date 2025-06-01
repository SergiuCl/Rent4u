package at.rent4u.screens

const val ARG_TOOL_ID = "toolId"

sealed class Screen(val route: String) {
    object ToolList : Screen("tool_list")
    object ToolDetails : Screen("tool_details/{toolId}") {
        fun createRoute(toolId: String) = "tool_details/$toolId"
    }
    object Booking : Screen("booking/{$ARG_TOOL_ID}") {
        fun createRoute(toolId: String) = "booking/$toolId"
    }
    object AdminToolCreate : Screen("admin_tool_create")
    object AdminToolEditor : Screen("admin_tool_editor/{toolId}") {
        fun createRoute(toolId: String) = "admin_tool_editor/$toolId"
    }
    object Login : Screen("login")
    object Profile : Screen("profile")
    object Register : Screen("register")
    object MyBookings : Screen("my_bookings")
    object ContactUs : Screen("contact_us")
}
