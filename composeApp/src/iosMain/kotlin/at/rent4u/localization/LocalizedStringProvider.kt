package at.rent4u.localization

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.string

actual class LocalizedStringProvider : StringResource {

    actual constructor()

    private val bundle = NSBundle.mainBundle

    override fun getString(id: StringResourceId): String {
        val key = when (id) {
            StringResourceId.HELLO -> "hello"
            StringResourceId.WELCOME -> "welcome"
            StringResourceId.LOGIN -> "login"
            StringResourceId.REGISTER -> "register"
            StringResourceId.EMAIL -> "email"
            StringResourceId.PASSWORD -> "password"
            StringResourceId.BOOK_NOW -> "book_now"
            StringResourceId.BOOK_TOOL -> "book_tool"
            StringResourceId.BOOKING_SUCCESS -> "booking_success"
            StringResourceId.BOOKING_ERROR -> "booking_error"
            StringResourceId.START_DATE -> "start_date"
            StringResourceId.END_DATE -> "end_date"
            StringResourceId.PROFILE -> "profile"
            StringResourceId.SETTINGS -> "settings"
            StringResourceId.MY_BOOKINGS -> "my_bookings"
            StringResourceId.LOGOUT -> "logout"
            StringResourceId.UPDATE_PROFILE -> "update_profile"
            StringResourceId.SAVE -> "save"
            StringResourceId.CANCEL -> "cancel"
            StringResourceId.CONFIRM -> "confirm"
        }
        return bundle.localizedStringForKey(key, key, null)
    }
}
