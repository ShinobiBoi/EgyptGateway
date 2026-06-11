package com.besha.egyptguide.features.main.presentaion.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.navigation.ScreenResources


data class BottomNavItem(
    val route: ScreenResources,
    val label: String,
    val icon: Int,
)

val navItems = listOf(
    BottomNavItem(route = ScreenResources.HomeRoute, label = "Explore", icon = R.drawable.baseline_explore_24),
    BottomNavItem(route = ScreenResources.MapsRoute(), label = "Map", icon = R.drawable.maps_svg),
    BottomNavItem(route = ScreenResources.CameraRoute, label = "Camera", icon = R.drawable.camera_ic),
    BottomNavItem(route = ScreenResources.ProfileRoute, label = "Profile", icon = R.drawable.outline_person_24),
)

// A simplified version for previewing
@Composable
fun CustomBottomNavigationBar(
    selectedRoute: ScreenResources,
    onItemSelected: (ScreenResources) -> Unit
) {
    val containerColor = colorResource(R.color.white)
    val selectedIndicatorColor = colorResource(R.color.blue)
    val selectedIconColorOnCircle = colorResource(R.color.white)

    // Wrap the NavigationBar with Surface to round its corners
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp, // optional shadow
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)

    ) {
        NavigationBar(
            containerColor = Color.Transparent, // important! transparent to use Surface color
            tonalElevation = 0.dp
        ) {
            navItems.forEach { item ->
                val isSelected = selectedRoute == item.route



                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(item.route) },
                    icon = {
                        val iconContent = @Composable {
                            Icon(
                                painter = painterResource(id = item.icon),
                                modifier = Modifier.size(28.dp),
                                contentDescription = item.label,
                                tint = if (isSelected) selectedIconColorOnCircle else colorResource(R.color.gray)
                            )
                        }

                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = selectedIndicatorColor,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    iconContent()
                                }
                            }
                        } else {
                            iconContent()
                        }
                    },
                    label = {
                        Text(
                            text = if (isSelected) "" else item.label,
                            color = colorResource(R.color.gray)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = colorResource(R.color.gray),
                        selectedTextColor = colorResource(R.color.gray),
                        unselectedTextColor =colorResource(R.color.gray)
                    )
                )
            }
        }
    }
}

