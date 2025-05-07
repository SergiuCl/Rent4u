package at.rent4u

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import at.rent4u.navigation.Rent4uNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Rent4uNavGraph()
    }
}