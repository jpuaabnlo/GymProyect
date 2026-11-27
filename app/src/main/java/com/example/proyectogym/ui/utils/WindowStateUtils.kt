package com.example.proyectogym.ui.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf

enum class GymNavigationType {
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    PERMANENT_NAVIGATION_DRAWER
}

enum class GymContentType {
    LIST_ONLY,
    LIST_AND_DETAIL
}

val LocalWindowWidthSizeClass = compositionLocalOf { WindowWidthSizeClass.Compact }

val WindowWidthSizeClass.isCompact: Boolean
    get() = this == WindowWidthSizeClass.Compact

val WindowWidthSizeClass.isMedium: Boolean
    get() = this == WindowWidthSizeClass.Medium

val WindowWidthSizeClass.isExpanded: Boolean
    get() = this == WindowWidthSizeClass.Expanded

fun WindowWidthSizeClass.toNavigationType(): GymNavigationType = when (this) {
    WindowWidthSizeClass.Compact -> GymNavigationType.BOTTOM_NAVIGATION
    WindowWidthSizeClass.Medium -> GymNavigationType.NAVIGATION_RAIL
    WindowWidthSizeClass.Expanded -> GymNavigationType.PERMANENT_NAVIGATION_DRAWER
    else -> GymNavigationType.BOTTOM_NAVIGATION
}

fun WindowWidthSizeClass.toContentType(): GymContentType = when (this) {
    WindowWidthSizeClass.Expanded -> GymContentType.LIST_AND_DETAIL
    else -> GymContentType.LIST_ONLY
}

fun WindowWidthSizeClass.gridColumns(
    compact: Int = 1,
    medium: Int = 2,
    expanded: Int = 3
): Int = when (this) {
    WindowWidthSizeClass.Compact -> compact
    WindowWidthSizeClass.Medium -> medium
    WindowWidthSizeClass.Expanded -> expanded
    else -> compact
}
