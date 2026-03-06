package com.nomride.karoo

import io.hammerhead.karooext.models.ViewConfig

const val NO_DATA = "-"

/**
 * Data field size classification based on ViewConfig.
 *
 * Karoo 3 screen: 480x800px
 * - 4 fields: 470x195px | 5 fields: 470x156px | 6 fields: 470x130px
 * - 2x2: 235x390px | 2x4: 235x195px
 */
enum class LayoutSize {
    /** Half-width, height < 200px — minimal content */
    SMALL,
    /** Full-width, height < 160px — horizontal compact */
    SMALL_WIDE,
    /** Full-width, height 160-249px — medium horizontal */
    MEDIUM_WIDE,
    /** Half-width, height 200-599px — medium vertical */
    MEDIUM,
    /** Full-width, height >= 250px — detailed layout */
    LARGE,
    /** Half-width, height >= 600px — tall narrow */
    NARROW,
}

fun getLayoutSize(config: ViewConfig): LayoutSize {
    val isFullWidth = config.gridSize.first >= 50
    val height = config.viewSize.second

    return if (isFullWidth) {
        when {
            height >= 250 -> LayoutSize.LARGE
            height >= 160 -> LayoutSize.MEDIUM_WIDE
            else -> LayoutSize.SMALL_WIDE
        }
    } else {
        when {
            height >= 600 -> LayoutSize.NARROW
            height >= 200 -> LayoutSize.MEDIUM
            else -> LayoutSize.SMALL
        }
    }
}
