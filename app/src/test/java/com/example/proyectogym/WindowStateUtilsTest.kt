package com.example.proyectogym

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.example.proyectogym.ui.utils.GymContentType
import com.example.proyectogym.ui.utils.GymNavigationType
import com.example.proyectogym.ui.utils.gridColumns
import com.example.proyectogym.ui.utils.isCompact
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium
import com.example.proyectogym.ui.utils.toContentType
import com.example.proyectogym.ui.utils.toNavigationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowStateUtilsTest {

    @Test
    fun compactWidth_mapsToBottomNavigationAndSinglePane() {
        val size = WindowWidthSizeClass.Compact
        assertTrue(size.isCompact)
        assertFalse(size.isMedium)
        assertFalse(size.isExpanded)
        assertEquals(GymNavigationType.BOTTOM_NAVIGATION, size.toNavigationType())
        assertEquals(GymContentType.LIST_ONLY, size.toContentType())
        assertEquals(1, size.gridColumns())
    }

    @Test
    fun mediumWidth_mapsToNavigationRailAndTwoColumns() {
        val size = WindowWidthSizeClass.Medium
        assertFalse(size.isCompact)
        assertTrue(size.isMedium)
        assertFalse(size.isExpanded)
        assertEquals(GymNavigationType.NAVIGATION_RAIL, size.toNavigationType())
        assertEquals(GymContentType.LIST_ONLY, size.toContentType())
        assertEquals(2, size.gridColumns())
    }

    @Test
    fun expandedWidth_mapsToPermanentDrawerAndListAndDetail() {
        val size = WindowWidthSizeClass.Expanded
        assertFalse(size.isCompact)
        assertFalse(size.isMedium)
        assertTrue(size.isExpanded)
        assertEquals(GymNavigationType.PERMANENT_NAVIGATION_DRAWER, size.toNavigationType())
        assertEquals(GymContentType.LIST_AND_DETAIL, size.toContentType())
        assertEquals(3, size.gridColumns())
    }
}
