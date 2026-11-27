package com.example.proyectogym.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectogym.GymApplication
import com.example.proyectogym.R
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.admin.AdminBillingScreen
import com.example.proyectogym.ui.admin.AdminDashboardScreen
import com.example.proyectogym.ui.admin.AdminManagementScreen
import com.example.proyectogym.ui.admin.AdminStoreScreen
import com.example.proyectogym.ui.admin.AdminViewModel
import com.example.proyectogym.ui.auth.LoginScreen
import com.example.proyectogym.ui.client.ClientHomeScreen
import com.example.proyectogym.ui.client.ClientProgressScreen
import com.example.proyectogym.ui.client.ClientViewModel
import com.example.proyectogym.ui.client.RoutineScreen
import com.example.proyectogym.ui.coach.CoachClientsScreen
import com.example.proyectogym.ui.coach.CoachRoutinesScreen
import com.example.proyectogym.ui.coach.CoachViewModel
import com.example.proyectogym.ui.coach.ExerciseCatalogScreen
import com.example.proyectogym.ui.coach.RoutineEditorScreen
import com.example.proyectogym.ui.components.GymOutlinedTitle
import com.example.proyectogym.ui.components.GymTopBar
import com.example.proyectogym.ui.components.LanguagePreferences
import com.example.proyectogym.ui.profile.ProfileScreen
import com.example.proyectogym.ui.theme.GymGreen
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.GymGreenLight
import com.example.proyectogym.ui.theme.NavBackgroundWhite
import com.example.proyectogym.ui.theme.NavSelectedGreenLight
import com.example.proyectogym.ui.theme.NavSelectedLightGray
import com.example.proyectogym.ui.theme.NavUnselectedGray
import com.example.proyectogym.ui.utils.GymNavigationType
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.toNavigationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun GymApp(
    windowSize: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    val rootNavController = rememberNavController()

    CompositionLocalProvider(LocalWindowWidthSizeClass provides windowSize) {
        NavHost(
            navController = rootNavController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    windowSize = windowSize,
                    onLoginSuccess = { usuario ->
                        rootNavController.navigate("main/${usuario.id}/${usuario.rol}") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = "main/{userId}/{rol}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("rol") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 1
                val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"

                RoleMainScreen(
                    userId = userId,
                    rol = rol,
                    windowSize = windowSize,
                    onLogout = {
                        rootNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RoleMainScreen(
    userId: Int,
    rol: String,
    onLogout: () -> Unit,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current
) {
    val tabNavController = rememberNavController()
    val itemsMenu = BottomNavItem.menuPorRol(rol)
    val startDestination = itemsMenu.first().route
    val navigationType = windowSize.toNavigationType()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val currentLang = LanguagePreferences.getCurrentLanguageCode(context)

    val rolLabel = when (rol) {
        "ADMIN" -> stringResource(R.string.role_admin)
        "ENTRENADOR" -> stringResource(R.string.role_coach)
        else -> stringResource(R.string.role_client)
    }

    val clientViewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val coachViewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val adminViewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Sincronización automática periódica cada 25 s en segundo plano
    LaunchedEffect(userId, rol) {
        val app = context.applicationContext as GymApplication
        while (true) {
            withContext(Dispatchers.IO) {
                runCatching { app.container.gymRepository.syncFromRemote() }
            }
            delay(25_000L)
        }
    }

    // Sincronización silenciosa al cambiar de pestaña/pantalla
    LaunchedEffect(currentDestination?.route) {
        val app = context.applicationContext as GymApplication
        withContext(Dispatchers.IO) {
            runCatching { app.container.gymRepository.syncFromRemote() }
        }
    }

    when (navigationType) {
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
                            Surface(
                                color = GymGreenLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = rolLabel,
                                    color = GymGreen,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = NavSelectedLightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        itemsMenu.forEach { item ->
                            val title = stringResource(item.titleRes)
                            val selected = isNavItemSelected(currentDestination, item)
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = title
                                    )
                                },
                                label = { Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                                selected = selected,
                                onClick = { navigateToTab(tabNavController, item.route) },
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
                    VerticalDivider(color = NavSelectedLightGray, thickness = 1.dp)
                    Scaffold(
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            GymTopBar(
                                rolLabel = rolLabel,
                                currentLang = currentLang,
                                showBrand = false,
                                onToggleLanguage = {
                                    LanguagePreferences.toggleLanguage(context)
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            RoleNavHost(
                                tabNavController = tabNavController,
                                startDestination = startDestination,
                                userId = userId,
                                windowSize = windowSize,
                                clientViewModel = clientViewModel,
                                coachViewModel = coachViewModel,
                                adminViewModel = adminViewModel,
                                onLogout = onLogout
                            )
                        }
                    }
                }
            }
        }

        GymNavigationType.NAVIGATION_RAIL -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    GymTopBar(
                        rolLabel = rolLabel,
                        currentLang = currentLang,
                        showBrand = true,
                        onToggleLanguage = {
                            LanguagePreferences.toggleLanguage(context)
                        }
                    )
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavigationRail(
                        containerColor = NavBackgroundWhite
                    ) {
                        itemsMenu.forEach { item ->
                            val title = stringResource(item.titleRes)
                            val selected = isNavItemSelected(currentDestination, item)
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.15f else 1f,
                                label = "railIconScale"
                            )
                            val iconTint by animateColorAsState(
                                targetValue = if (selected) GymGreenDark else NavUnselectedGray,
                                label = "railIconTint"
                            )
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = title,
                                        tint = iconTint,
                                        modifier = Modifier.scale(iconScale)
                                    )
                                },
                                label = { Text(title) },
                                selected = selected,
                                onClick = { navigateToTab(tabNavController, item.route) },
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
                    VerticalDivider(color = NavSelectedLightGray, thickness = 1.dp)
                    Box(modifier = Modifier.weight(1f)) {
                        RoleNavHost(
                            tabNavController = tabNavController,
                            startDestination = startDestination,
                            userId = userId,
                            windowSize = windowSize,
                            clientViewModel = clientViewModel,
                            coachViewModel = coachViewModel,
                            adminViewModel = adminViewModel,
                            onLogout = onLogout
                        )
                    }
                }
            }
        }

        GymNavigationType.BOTTOM_NAVIGATION -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    GymTopBar(
                        rolLabel = rolLabel,
                        currentLang = currentLang,
                        showBrand = true,
                        onToggleLanguage = {
                            LanguagePreferences.toggleLanguage(context)
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = NavBackgroundWhite,
                        tonalElevation = 4.dp
                    ) {
                        itemsMenu.forEach { item ->
                            val title = stringResource(item.titleRes)
                            val selected = isNavItemSelected(currentDestination, item)
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.15f else 1f,
                                label = "navIconScale"
                            )
                            val iconTint by animateColorAsState(
                                targetValue = if (selected) GymGreenDark else NavUnselectedGray,
                                label = "navIconTint"
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = title,
                                        tint = iconTint,
                                        modifier = Modifier.scale(iconScale)
                                    )
                                },
                                label = { Text(title) },
                                selected = selected,
                                onClick = { navigateToTab(tabNavController, item.route) },
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    RoleNavHost(
                        tabNavController = tabNavController,
                        startDestination = startDestination,
                        userId = userId,
                        windowSize = windowSize,
                        clientViewModel = clientViewModel,
                        coachViewModel = coachViewModel,
                        adminViewModel = adminViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

private fun isNavItemSelected(
    currentDestination: androidx.navigation.NavDestination?,
    item: BottomNavItem
): Boolean {
    if (currentDestination?.hierarchy?.any { it.route == item.route } == true) return true
    if (item == BottomNavItem.CoachRutinas) {
        val route = currentDestination?.route
        return route == BottomNavItem.ROUTE_EDITOR_RUTINA ||
            route == BottomNavItem.ROUTE_CATALOGO_EJERCICIOS
    }
    return false
}

private fun navigateToTab(tabNavController: NavHostController, route: String) {
    tabNavController.navigate(route) {
        popUpTo(tabNavController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RoleNavHost(
    tabNavController: NavHostController,
    startDestination: String,
    userId: Int,
    windowSize: WindowWidthSizeClass,
    clientViewModel: ClientViewModel,
    coachViewModel: CoachViewModel,
    adminViewModel: AdminViewModel,
    onLogout: () -> Unit
) {
    NavHost(
        navController = tabNavController,
        startDestination = startDestination
    ) {
        composable(BottomNavItem.ClienteInicio.route) {
            ClientHomeScreen(
                userId = userId,
                windowSize = windowSize,
                viewModel = clientViewModel,
                onIrARutina = {
                    navigateToTab(tabNavController, BottomNavItem.ClienteRutina.route)
                }
            )
        }
        composable(BottomNavItem.ClienteRutina.route) {
            RoutineScreen(
                userId = userId,
                windowSize = windowSize,
                viewModel = clientViewModel
            )
        }
        composable(BottomNavItem.ClienteProgreso.route) {
            ClientProgressScreen(
                userId = userId,
                windowSize = windowSize,
                viewModel = clientViewModel
            )
        }

        composable(BottomNavItem.CoachClientes.route) {
            CoachClientsScreen(
                coachId = userId,
                windowSize = windowSize,
                viewModel = coachViewModel
            )
        }
        composable(BottomNavItem.CoachRutinas.route) {
            CoachRoutinesScreen(
                coachId = userId,
                windowSize = windowSize,
                viewModel = coachViewModel,
                onCrearRutina = {
                    tabNavController.navigate(BottomNavItem.ROUTE_EDITOR_RUTINA)
                },
                onEditarRutina = {
                    tabNavController.navigate(BottomNavItem.ROUTE_EDITOR_RUTINA)
                }
            )
        }
        composable(BottomNavItem.ROUTE_EDITOR_RUTINA) {
            RoutineEditorScreen(
                windowSize = windowSize,
                viewModel = coachViewModel,
                onAbrirCatalogoEjercicios = {
                    tabNavController.navigate(BottomNavItem.ROUTE_CATALOGO_EJERCICIOS)
                },
                onBack = { tabNavController.popBackStack() }
            )
        }
        composable(BottomNavItem.ROUTE_CATALOGO_EJERCICIOS) {
            ExerciseCatalogScreen(
                windowSize = windowSize,
                viewModel = coachViewModel,
                onBack = { tabNavController.popBackStack() }
            )
        }

        composable(BottomNavItem.AdminTablero.route) {
            AdminDashboardScreen(
                windowSize = windowSize,
                viewModel = adminViewModel
            )
        }
        composable(BottomNavItem.AdminCobrar.route) {
            AdminBillingScreen(
                windowSize = windowSize,
                viewModel = adminViewModel
            )
        }
        composable(BottomNavItem.AdminTienda.route) {
            AdminStoreScreen(
                adminId = userId,
                windowSize = windowSize,
                viewModel = adminViewModel
            )
        }
        composable(BottomNavItem.AdminGestion.route) {
            AdminManagementScreen(
                windowSize = windowSize,
                viewModel = adminViewModel
            )
        }

        composable(BottomNavItem.Perfil.route) {
            ProfileScreen(
                userId = userId,
                windowSize = windowSize,
                onLogout = onLogout
            )
        }
    }
}
