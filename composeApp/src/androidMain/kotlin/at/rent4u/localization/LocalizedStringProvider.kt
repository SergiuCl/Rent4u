package at.rent4u.localization

import android.content.Context
import at.rent4u.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlin.random.Random

actual class LocalizedStringProvider constructor(private val context: Context) :
    StringResource {

    // Static reference to an application context for the default constructor
    companion object {
        private var applicationContext: Context? = null

        fun initialize(appContext: Context) {
            applicationContext = appContext.applicationContext
        }

        @Composable
        fun create(): LocalizedStringProvider {
            val context = LocalContext.current
            return LocalizedStringProvider(context)
        }
    }


    // No-arg constructor required by expect declaration
    actual constructor() : this(
        applicationContext ?: throw IllegalStateException(
            "LocalizedStringProvider requires initialization with a Context via initialize() before use."
        )
    )

    actual override fun getString(id: StringResourceId): String {
        val resourceId = when (id) {
            StringResourceId.HELLO -> R.string.hello
            StringResourceId.WELCOME -> R.string.welcome
            StringResourceId.LOGIN -> R.string.login
            StringResourceId.REGISTER -> R.string.register
            StringResourceId.EMAIL -> R.string.email
            StringResourceId.PASSWORD -> R.string.password
            StringResourceId.BOOK_NOW -> R.string.book_now
            StringResourceId.BOOK_TOOL -> R.string.book_tool
            StringResourceId.BOOKING_SUCCESS -> R.string.booking_success
            StringResourceId.BOOKING_ERROR -> R.string.booking_error
            StringResourceId.START_DATE -> R.string.start_date
            StringResourceId.END_DATE -> R.string.end_date
            StringResourceId.PROFILE -> R.string.profile
            StringResourceId.SETTINGS -> R.string.settings
            StringResourceId.MY_BOOKINGS -> R.string.my_bookings
            StringResourceId.LOGOUT -> R.string.logout
            StringResourceId.UPDATE_PROFILE -> R.string.update_profile
            StringResourceId.SAVE -> R.string.save
            StringResourceId.CANCEL -> R.string.cancel
            StringResourceId.CONFIRM -> R.string.confirm
            StringResourceId.PROFILE_INFORMATION -> R.string.profile_information
            StringResourceId.SAVE_PROFILE -> R.string.save_profile

            // Tool list screen
            StringResourceId.SHOW_FILTERS -> R.string.show_filters
            StringResourceId.HIDE_FILTERS -> R.string.hide_filters
            StringResourceId.RESET_FILTERS -> R.string.reset_filters
            StringResourceId.LOADING_TOOLS -> R.string.loading_tools
            StringResourceId.NO_TOOLS_FOUND -> R.string.no_tools_found
            StringResourceId.ADD_TOOL -> R.string.add_tool
            StringResourceId.MIN_PRICE -> R.string.min_price
            StringResourceId.MAX_PRICE -> R.string.max_price
            StringResourceId.TOOL_IMAGE -> R.string.tool_image

            // Tool details screen
            StringResourceId.TOOL_NOT_FOUND -> R.string.tool_not_found
            StringResourceId.EDIT_TOOL -> R.string.edit_tool
            StringResourceId.DELETE_TOOL -> R.string.delete_tool
            StringResourceId.BRAND -> R.string.brand
            StringResourceId.MODEL -> R.string.model
            StringResourceId.DESCRIPTION -> R.string.description
            StringResourceId.AVAILABILITY -> R.string.availability
            StringResourceId.POWER_SOURCE -> R.string.power_source
            StringResourceId.TYPE -> R.string.type
            StringResourceId.VOLTAGE -> R.string.voltage
            StringResourceId.FUEL_TYPE -> R.string.fuel_type
            StringResourceId.WEIGHT -> R.string.weight
            StringResourceId.DIMENSIONS -> R.string.dimensions
            StringResourceId.RENTAL_RATE -> R.string.rental_rate

            // My bookings screen
            StringResourceId.NO_ACTIVE_BOOKINGS -> R.string.no_active_bookings
            StringResourceId.BROWSE_TOOLS -> R.string.browse_tools
            StringResourceId.CANCEL_BOOKING -> R.string.cancel_booking
            StringResourceId.CONFIRM_CANCELLATION -> R.string.confirm_cancellation
            StringResourceId.CANCELLATION_CONFIRMATION -> R.string.cancellation_confirmation
            StringResourceId.YES -> R.string.yes
            StringResourceId.NO -> R.string.no
            StringResourceId.BOOKING_DATE_RANGE -> R.string.booking_date_range
            StringResourceId.SELECT_BOOKING_DATES -> R.string.select_booking_date
            StringResourceId.CONFIRM_BOOKING -> R.string.confirm_booking
            StringResourceId.BOOK_THIS_TOOL -> R.string.book_this_tool

            // Profile screen
            StringResourceId.LOGGED_IN_AS -> R.string.logged_in_as
            StringResourceId.VIEW_PROFILE -> R.string.view_profile

            // Contact us screen
            StringResourceId.CONTACT_US -> R.string.contact_us
            StringResourceId.CONTACT_INFO -> R.string.contact_info
            StringResourceId.YOUR_MESSAGE -> R.string.your_message
            StringResourceId.SEND -> R.string.send

            // Login/register screens
            StringResourceId.WELCOME_BACK -> R.string.welcome_back
            StringResourceId.STAY_LOGGED_IN -> R.string.stay_logged_in
            StringResourceId.DONT_HAVE_ACCOUNT -> R.string.dont_have_account
            StringResourceId.ALREADY_HAVE_ACCOUNT -> R.string.already_have_account
            StringResourceId.CREATE_ACCOUNT -> R.string.create_account
            StringResourceId.USERNAME -> R.string.username
            StringResourceId.FIRST_NAME -> R.string.first_name
            StringResourceId.LAST_NAME -> R.string.last_name
            StringResourceId.PHONE_NUMBER -> R.string.phone_number
            StringResourceId.CONFIRM_PASSWORD -> R.string.confirm_password

            // Edit profile screen
            StringResourceId.SECURITY_SETTINGS -> R.string.security_settings
            StringResourceId.CHANGE_EMAIL -> R.string.change_email
            StringResourceId.CHANGE_PASSWORD -> R.string.change_password
            StringResourceId.DANGER_ZONE -> R.string.danger_zone
            StringResourceId.DELETE_ACCOUNT -> R.string.delete_account

            // Dialog strings
            StringResourceId.ERROR -> R.string.error
            StringResourceId.OK -> R.string.ok
            StringResourceId.VERIFICATION_EMAIL_SENT -> R.string.verification_email_sent
            StringResourceId.VERIFICATION_EMAIL_MESSAGE -> R.string.verification_email_message
            StringResourceId.NO_PAST_BOOKINGS -> R.string.no_past_bookings
            StringResourceId.CURRENT_PASSWORD -> R.string.current_password
            StringResourceId.NEW_PASSWORD -> R.string.new_password
            StringResourceId.CONFIRM_NEW_PASSWORD -> R.string.confirm_new_password
            StringResourceId.PASSWORD_LENGTH_ERROR -> R.string.password_length_error
            StringResourceId.PASSWORDS_DONT_MATCH -> R.string.passwords_dont_match
            StringResourceId.CURRENT_PASSWORD_REQUIRED -> R.string.current_password_required
            StringResourceId.PASSWORD_SECURITY_HINT -> R.string.password_security_hint
            StringResourceId.LOADING_PROFILE -> R.string.loading_profile
            StringResourceId.SHOW_PAST_BOOKINGS -> R.string.show_past_bookings
            StringResourceId.HOME -> R.string.home
            StringResourceId.DELETE_USER_CONFIRMATION -> R.string.delete_user_confirmation
            StringResourceId.DELETE_USER_WARNING -> R.string.delete_user_warning
            StringResourceId.DELETE_CONFIRMATION_PROMPT -> R.string.delete_confirmation_prompt
            StringResourceId.DELETE -> R.string.delete
            StringResourceId.EMAIL_NOT_VERIFIED -> R.string.email_not_verified
            StringResourceId.EMAIL_VERIFIED -> R.string.email_verified
            StringResourceId.VERIFY -> R.string.verify
            StringResourceId.EMAIL_VERIFICATION_NEEDED -> R.string.email_verification_needed
            StringResourceId.EMAIL_VERIFIED_MESSAGE -> R.string.email_verified_message
            StringResourceId.PROFILE_UPDATE_SUCCESS -> R.string.profile_update_success
            StringResourceId.PROFILE_UPDATE_ERROR -> R.string.profile_update_error
            StringResourceId.EMAIL_UPDATE_SUCCESS -> R.string.email_update_success
            StringResourceId.EMAIL_UPDATE_ERROR -> R.string.email_update_error
            StringResourceId.EMAIL_VERIFICATION_REQUIRED -> R.string.email_verification_required
            StringResourceId.PASSWORD_UPDATE_SUCCESS -> R.string.password_update_success
            StringResourceId.PASSWORD_UPDATE_ERROR -> R.string.password_update_error
            StringResourceId.USER -> R.string.user

        }
        return context.getString(resourceId)
    }
}
