package at.rent4u

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import at.rent4u.navigation.Rent4uNavGraph

@Composable
fun App() {
    MaterialTheme {
        Rent4uNavGraph()
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
