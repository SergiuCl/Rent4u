package at.rent4u.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun addScreenLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = getCurrentRoute(navController)

    BottomAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 8.dp,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            // Left-aligned Home
            BottomNavIcon(
                icon = Icons.Default.Home,
                contentDescription = "Home",
                isSelected = currentRoute == Screen.ToolList.route,
                onClick = {
                    navController.navigate(Screen.ToolList.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )

            // Right-aligned Profile
            BottomNavIcon(
                icon = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                isSelected = currentRoute == Screen.Profile.route,
                onClick = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
            )
        }
    }
}

@Composable
fun BottomNavIcon(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isSelected) Color.White else Color.LightGray
    val topBorderColor = if (isSelected) Color.White else Color.Transparent

    Column(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .background(topBorderColor, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor
        )
    }
}

@Composable
fun getCurrentRoute(navController: NavController): String? {
    return navController.currentBackStackEntryAsState().value?.destination?.route
}

fun validateEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return email.matches(emailRegex)
}
