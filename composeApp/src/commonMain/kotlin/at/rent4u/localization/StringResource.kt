package at.rent4u.localization

interface StringResource {
    fun getString(id: StringResourceId): String
}

enum class StringResourceId {
    HELLO,
    WELCOME,
    LOGIN,
    REGISTER,
    EMAIL,
    PASSWORD,
    BOOK_NOW,
    BOOK_TOOL,
    BOOKING_SUCCESS,
    BOOKING_ERROR,
    START_DATE,
    END_DATE,
    PROFILE,
    SETTINGS,
    MY_BOOKINGS,
    LOGOUT,
    UPDATE_PROFILE,
    SAVE,
    CANCEL,
    CONFIRM,
    PROFILE_INFORMATION,
    SAVE_PROFILE,

    // Tool list screen
    SHOW_FILTERS,
    HIDE_FILTERS,
    RESET_FILTERS,
    LOADING_TOOLS,
    NO_TOOLS_FOUND,
    ADD_TOOL,
    MIN_PRICE,
    MAX_PRICE,
    TOOL_IMAGE,

    // Tool details screen
    TOOL_NOT_FOUND,
    EDIT_TOOL,
    DELETE_TOOL,
    BRAND,
    MODEL,
    DESCRIPTION,
    AVAILABILITY,
    POWER_SOURCE,
    TYPE,
    VOLTAGE,
    FUEL_TYPE,
    WEIGHT,
    DIMENSIONS,
    RENTAL_RATE,

    // My bookings screen
    NO_ACTIVE_BOOKINGS,
    BROWSE_TOOLS,
    CANCEL_BOOKING,
    CONFIRM_CANCELLATION,
    CANCELLATION_CONFIRMATION,
    YES,
    NO,

    // Profile screen
    LOGGED_IN_AS,
    VIEW_PROFILE,

    // Contact us screen
    CONTACT_US,
    CONTACT_INFO,
    YOUR_MESSAGE,
    SEND,

    // Login/register screens
    WELCOME_BACK,
    STAY_LOGGED_IN,
    DONT_HAVE_ACCOUNT,
    ALREADY_HAVE_ACCOUNT,
    CREATE_ACCOUNT,
    USERNAME,
    FIRST_NAME,
    LAST_NAME,
    PHONE_NUMBER,
    CONFIRM_PASSWORD,

    // Edit profile screen
    SECURITY_SETTINGS,
    CHANGE_EMAIL,
    CHANGE_PASSWORD,
    DANGER_ZONE,
    DELETE_ACCOUNT,

    // Dialog strings
    ERROR,
    OK,
    VERIFICATION_EMAIL_SENT,
    VERIFICATION_EMAIL_MESSAGE
}

expect class LocalizedStringProvider() : StringResource {
    override fun getString(id: StringResourceId): String
}

