package com.example.proyectogym.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectogym.R
import com.example.proyectogym.navigation.BottomNavItem
import com.example.proyectogym.ui.components.ExerciseVisualCard
import com.example.proyectogym.ui.components.GymOutlinedTitle
import com.example.proyectogym.ui.components.GymTopBar
import com.example.proyectogym.ui.theme.GymGreen
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.GymGreenLight
import com.example.proyectogym.ui.theme.NavBackgroundWhite
import com.example.proyectogym.ui.theme.NavSelectedGreenLight
import com.example.proyectogym.ui.theme.NavSelectedLightGray
import com.example.proyectogym.ui.theme.NavUnselectedGray
import com.example.proyectogym.ui.theme.ProyectoGymTheme
import com.example.proyectogym.ui.utils.GymNavigationType
import com.example.proyectogym.ui.utils.toNavigationType

@Composable
private fun AdaptiveShellPreviewSample(
    windowSize: WindowWidthSizeClass
) {
    val items = BottomNavItem.menuPorRol("CLIENTE")
    val selectedRoute = items.first().route
    val rolLabel = stringResource(R.string.role_client)
    val navType = windowSize.toNavigationType()

    when (navType) {
        GymNavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(220.dp),
                        drawerContainerColor = NavBackgroundWhite
                    ) {
                        Column(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GymOutlinedTitle(
                                text = stringResource(R.string.app_name_display),
                                fontSize = 26.sp
                            )
                            Surface(color = GymGreenLight, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    text = rolLabel,
                                    color = GymGreen,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = NavSelectedLightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        items.forEach { item ->
                            val title = stringResource(item.titleRes)
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = item.route == selectedRoute,
                                onClick = {},
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = NavSelectedGreenLight,
                                    selectedIconColor = GymGreenDark,
                                    selectedTextColor = GymGreenDark,
                                    unselectedIconColor = NavUnselectedGray,
                                    unselectedTextColor = NavUnselectedGray
                                ),
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    VerticalDivider(color = NavSelectedLightGray)
                    Scaffold(
                        modifier = Modifier.weight(1f),
                        topBar = {
                            GymTopBar(
                                rolLabel = rolLabel,
                                currentLang = "es",
                                showBrand = false,
                                onToggleLanguage = {}
                            )
                        }
                    ) { innerPadding ->
                        AdaptivePreviewContent(
                            windowSize = windowSize,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        GymNavigationType.NAVIGATION_RAIL -> {
            Scaffold(
                topBar = {
                    GymTopBar(
                        rolLabel = rolLabel,
                        currentLang = "es",
                        showBrand = true,
                        onToggleLanguage = {}
                    )
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavigationRail(containerColor = NavBackgroundWhite) {
                        items.forEach { item ->
                            val title = stringResource(item.titleRes)
                            NavigationRailItem(
                                icon = { Icon(item.icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = item.route == selectedRoute,
                                onClick = {},
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = GymGreenDark,
                                    selectedTextColor = GymGreenDark,
                                    indicatorColor = NavSelectedGreenLight,
                                    unselectedIconColor = NavUnselectedGray,
                                    unselectedTextColor = NavUnselectedGray
                                )
                            )
                        }
                    }
                    VerticalDivider(color = NavSelectedLightGray)
                    AdaptivePreviewContent(
                        windowSize = windowSize,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )
                }
            }
        }

        GymNavigationType.BOTTOM_NAVIGATION -> {
            Scaffold(
                topBar = {
                    GymTopBar(
                        rolLabel = rolLabel,
                        currentLang = "es",
                        showBrand = true,
                        onToggleLanguage = {}
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = NavBackgroundWhite) {
                        items.forEach { item ->
                            val title = stringResource(item.titleRes)
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = item.route == selectedRoute,
                                onClick = {},
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GymGreenDark,
                                    selectedTextColor = GymGreenDark,
                                    indicatorColor = NavSelectedGreenLight,
                                    unselectedIconColor = NavUnselectedGray,
                                    unselectedTextColor = NavUnselectedGray
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                AdaptivePreviewContent(
                    windowSize = windowSize,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AdaptivePreviewContent(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "GymApp · $windowSize",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (windowSize == WindowWidthSizeClass.Compact) {
            PreviewSampleSummaryCard()
            ExerciseVisualCard(
                nombre = "Press de banca",
                grupoMuscular = "Pecho",
                series = 4,
                repeticiones = 10,
                pesoSugeridoKg = 60.0
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    PreviewSampleSummaryCard()
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExerciseVisualCard(
                        nombre = "Press de banca",
                        grupoMuscular = "Pecho",
                        series = 4,
                        repeticiones = 10,
                        pesoSugeridoKg = 60.0
                    )
                    ExerciseVisualCard(
                        nombre = "Sentadilla libre",
                        grupoMuscular = "Pierna",
                        series = 4,
                        repeticiones = 8,
                        pesoSugeridoKg = 80.0
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewSampleSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.client_membership_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Plan Mensual · Activa (22 días restantes)")
        }
    }
}

@Preview(name = "Compact (411x891)", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun CompactAdaptivePreview() {
    ProyectoGymTheme {
        AdaptiveShellPreviewSample(windowSize = WindowWidthSizeClass.Compact)
    }
}

@Preview(name = "Medium (700x900)", showBackground = true, widthDp = 700, heightDp = 900)
@Composable
private fun MediumAdaptivePreview() {
    ProyectoGymTheme {
        AdaptiveShellPreviewSample(windowSize = WindowWidthSizeClass.Medium)
    }
}

@Preview(name = "Expanded (1100x800)", showBackground = true, widthDp = 1100, heightDp = 800)
@Composable
private fun ExpandedAdaptivePreview() {
    ProyectoGymTheme {
        AdaptiveShellPreviewSample(windowSize = WindowWidthSizeClass.Expanded)
    }
}
