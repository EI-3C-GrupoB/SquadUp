package com.example.squadup.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextSecondary

data class AppNavBarItem(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val appNavBarItems = listOf(
    AppNavBarItem(
        route = "home",
        labelRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    AppNavBarItem(
        route = "events",
        labelRes = R.string.nav_events,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    AppNavBarItem(
        route = "teams",
        labelRes = R.string.nav_teams,
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups
    ),
    AppNavBarItem(
        route = "profile",
        labelRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun AppNavBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = SquadGrayLight
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .selectableGroup(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                appNavBarItems.forEach { item ->
                    val selected = selectedRoute == item.route

                    AppNavBarButton(
                        item = item,
                        selected = selected,
                        onClick = {
                            onItemClick(item.route)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavBarButton(
    item: AppNavBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Duração consistente para as animações de tamanho e forma
    val animationDuration = 160

    val backgroundColor = when {
        selected -> SquadOrangeLight
        pressed -> SquadOrangeLight.copy(alpha = 0.45f)
        else -> Color.Transparent
    }

    val cornerRadius by animateDpAsState(
        targetValue = if (selected || pressed) 20.dp else 28.dp,
        animationSpec = tween(durationMillis = animationDuration),
        label = "NavItemCornerRadius"
    )

    val itemWidth by animateDpAsState(
        targetValue = if (selected || pressed) 76.dp else 64.dp,
        animationSpec = tween(durationMillis = animationDuration),
        label = "NavItemWidth"
    )

    val itemHeight by animateDpAsState(
        targetValue = if (selected || pressed) 56.dp else 50.dp,
        animationSpec = tween(durationMillis = animationDuration),
        label = "NavItemHeight"
    )

    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(itemWidth)
                .height(itemHeight)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(cornerRadius)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val label = stringResource(item.labelRes)

            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (selected) SquadOrange else SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label.uppercase(),
                color = if (selected) SquadOrange else SquadTextSecondary,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
