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

    private val resourceMap: Map<StringResourceId, Int> = mapOf(
        StringResourceId.HELLO to R.string.hello,
        StringResourceId.WELCOME to R.string.welcome,
        StringResourceId.LOGIN to R.string.login,
        StringResourceId.REGISTER to R.string.register,
        StringResourceId.EMAIL to R.string.email,
        StringResourceId.PASSWORD to R.string.password,
        StringResourceId.BOOK_NOW to R.string.book_now,
        StringResourceId.BOOK_TOOL to R.string.book_tool,
        StringResourceId.BOOKING_SUCCESS to R.string.booking_success,
        StringResourceId.BOOKING_ERROR to R.string.booking_error,
        StringResourceId.START_DATE to R.string.start_date,
        StringResourceId.END_DATE to R.string.end_date,
        StringResourceId.PROFILE to R.string.profile,
        StringResourceId.SETTINGS to R.string.settings,
        StringResourceId.MY_BOOKINGS to R.string.my_bookings,
        StringResourceId.LOGOUT to R.string.logout,
        StringResourceId.UPDATE_PROFILE to R.string.update_profile,
        StringResourceId.SAVE to R.string.save,
        StringResourceId.CANCEL to R.string.cancel,
        StringResourceId.CONFIRM to R.string.confirm,
        StringResourceId.PROFILE_INFORMATION to R.string.profile_information,
        StringResourceId.SAVE_PROFILE to R.string.save_profile,
        // Tool list screen
        StringResourceId.SHOW_FILTERS to R.string.show_filters,
        StringResourceId.HIDE_FILTERS to R.string.hide_filters,
        StringResourceId.RESET_FILTERS to R.string.reset_filters,
        StringResourceId.LOADING_TOOLS to R.string.loading_tools,
        StringResourceId.NO_TOOLS_FOUND to R.string.no_tools_found,
        StringResourceId.ADD_TOOL to R.string.add_tool,
        StringResourceId.MIN_PRICE to R.string.min_price,
        StringResourceId.MAX_PRICE to R.string.max_price,
        StringResourceId.TOOL_IMAGE to R.string.tool_image,
        // Tool details screen
        StringResourceId.TOOL_NOT_FOUND to R.string.tool_not_found,
        StringResourceId.EDIT_TOOL to R.string.edit_tool,
        StringResourceId.DELETE_TOOL to R.string.delete_tool,
        StringResourceId.BRAND to R.string.brand,
        StringResourceId.MODEL to R.string.model,
        StringResourceId.DESCRIPTION to R.string.description,
        StringResourceId.AVAILABILITY to R.string.availability,
        StringResourceId.POWER_SOURCE to R.string.power_source,
        StringResourceId.TYPE to R.string.type,
        StringResourceId.VOLTAGE to R.string.voltage,
        StringResourceId.FUEL_TYPE to R.string.fuel_type,
        StringResourceId.WEIGHT to R.string.weight,
        StringResourceId.DIMENSIONS to R.string.dimensions,
        StringResourceId.RENTAL_RATE to R.string.rental_rate,
        // My bookings screen
        StringResourceId.NO_ACTIVE_BOOKINGS to R.string.no_active_bookings,
        StringResourceId.BROWSE_TOOLS to R.string.browse_tools,
        StringResourceId.CANCEL_BOOKING to R.string.cancel_booking,
        StringResourceId.CONFIRM_CANCELLATION to R.string.confirm_cancellation,
        StringResourceId.CANCELLATION_CONFIRMATION to R.string.cancellation_confirmation,
        StringResourceId.YES to R.string.yes,
        StringResourceId.NO to R.string.no,
        StringResourceId.BOOKING_DATE_RANGE to R.string.booking_date_range,
        StringResourceId.SELECT_BOOKING_DATES to R.string.select_booking_date,
        StringResourceId.CONFIRM_BOOKING to R.string.confirm_booking,
        StringResourceId.BOOK_THIS_TOOL to R.string.book_this_tool,
        // Profile screen
        StringResourceId.LOGGED_IN_AS to R.string.logged_in_as,
        StringResourceId.VIEW_PROFILE to R.string.view_profile,
        // Contact us screen
        StringResourceId.CONTACT_US to R.string.contact_us,
        StringResourceId.CONTACT_INFO to R.string.contact_info,
        StringResourceId.YOUR_MESSAGE to R.string.your_message,
        StringResourceId.SEND to R.string.send,
        // Login/register screens
        StringResourceId.WELCOME_BACK to R.string.welcome_back,
        StringResourceId.STAY_LOGGED_IN to R.string.stay_logged_in,
        StringResourceId.DONT_HAVE_ACCOUNT to R.string.dont_have_account,
        StringResourceId.ALREADY_HAVE_ACCOUNT to R.string.already_have_account,
        StringResourceId.CREATE_ACCOUNT to R.string.create_account,
        StringResourceId.USERNAME to R.string.username,
        StringResourceId.FIRST_NAME to R.string.first_name,
        StringResourceId.LAST_NAME to R.string.last_name,
        StringResourceId.PHONE_NUMBER to R.string.phone_number,
        StringResourceId.CONFIRM_PASSWORD to R.string.confirm_password,
        // Edit profile screen
        StringResourceId.SECURITY_SETTINGS to R.string.security_settings,
        StringResourceId.CHANGE_EMAIL to R.string.change_email,
        StringResourceId.CHANGE_PASSWORD to R.string.change_password,
        StringResourceId.DANGER_ZONE to R.string.danger_zone,
        StringResourceId.DELETE_ACCOUNT to R.string.delete_account,
        // Dialog strings
        StringResourceId.ERROR to R.string.error,
        StringResourceId.OK to R.string.ok,
        StringResourceId.VERIFICATION_EMAIL_SENT to R.string.verification_email_sent,
        StringResourceId.VERIFICATION_EMAIL_MESSAGE to R.string.verification_email_message,
        StringResourceId.NO_PAST_BOOKINGS to R.string.no_past_bookings,
        StringResourceId.CURRENT_PASSWORD to R.string.current_password,
        StringResourceId.NEW_PASSWORD to R.string.new_password,
        StringResourceId.CONFIRM_NEW_PASSWORD to R.string.confirm_new_password,
        StringResourceId.PASSWORD_LENGTH_ERROR to R.string.password_length_error,
        StringResourceId.PASSWORDS_DONT_MATCH to R.string.passwords_dont_match,
        StringResourceId.CURRENT_PASSWORD_REQUIRED to R.string.current_password_required,
        StringResourceId.PASSWORD_SECURITY_HINT to R.string.password_security_hint,
        StringResourceId.LOADING_PROFILE to R.string.loading_profile,
        StringResourceId.SHOW_PAST_BOOKINGS to R.string.show_past_bookings,
        StringResourceId.HOME to R.string.home,
        StringResourceId.DELETE_USER_CONFIRMATION to R.string.delete_user_confirmation,
        StringResourceId.DELETE_USER_WARNING to R.string.delete_user_warning,
        StringResourceId.DELETE_CONFIRMATION_PROMPT to R.string.delete_confirmation_prompt,
        StringResourceId.DELETE to R.string.delete,
        StringResourceId.EMAIL_NOT_VERIFIED to R.string.email_not_verified,
        StringResourceId.EMAIL_VERIFIED to R.string.email_verified,
        StringResourceId.VERIFY to R.string.verify,
        StringResourceId.EMAIL_VERIFICATION_NEEDED to R.string.email_verification_needed,
        StringResourceId.EMAIL_VERIFIED_MESSAGE to R.string.email_verified_message,
        StringResourceId.PROFILE_UPDATE_SUCCESS to R.string.profile_update_success,
        StringResourceId.PROFILE_UPDATE_ERROR to R.string.profile_update_error,
        StringResourceId.EMAIL_UPDATE_SUCCESS to R.string.email_update_success,
        StringResourceId.EMAIL_UPDATE_ERROR to R.string.email_update_error,
        StringResourceId.EMAIL_VERIFICATION_REQUIRED to R.string.email_verification_required,
        StringResourceId.PASSWORD_UPDATE_SUCCESS to R.string.password_update_success,
        StringResourceId.PASSWORD_UPDATE_ERROR to R.string.password_update_error,
        StringResourceId.USER to R.string.user,
        StringResourceId.AVAILABILITY_STATUS to R.string.availability_status,
        StringResourceId.RENTAL_RATE_EURO to R.string.rental_rate_euro,
        StringResourceId.IMAGE_URL to R.string.image_url
    )

    actual override fun getString(id: StringResourceId): String {
        val resId = resourceMap[id]
            ?: throw IllegalArgumentException("No string resource found for id: $id")
        return context.getString(resId)
    }
}
