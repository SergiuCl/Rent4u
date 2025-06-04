package at.rent4u

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import at.rent4u.localization.LocalizedStringProvider

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import at.rent4u.navigation.Rent4uNavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) { LocalizedStringProvider(context) }

    MaterialTheme {
        Rent4uNavGraph()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AppPreview() {
    App()
}