package at.rent4u

import android.app.Application
import at.rent4u.localization.LocalizedStringProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Rent4uApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the localization system with application context
        LocalizedStringProvider.initialize(applicationContext)

    }
}
