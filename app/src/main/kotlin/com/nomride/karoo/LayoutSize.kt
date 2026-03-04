package com.nomride.karoo

import io.hammerhead.karooext.models.ViewConfig

/**
 * Data field size classification based on ViewConfig.
 *
 * Karoo 3 screen: 480x800px
 * - 4 fields: 470x195px | 5 fields: 470x156px | 6 fields: 470x130px
 * - 2x2: 235x390px | 2x4: 235x195px
 */
enum class LayoutSize {
    /** Narrow half-width, short height (2x4 grid cells) - minimal content */
    SMALL,
    /** Full width, very short (~130-160px) - horizontal compact */
    SMALL_WIDE,
    /** Full width medium or half-width tall (~160-250px) */
    MEDIUM,
    /** Large height, full width (250px+) - detailed layout */
    LARGE,
}

fun getLayoutSize(config: ViewConfig): LayoutSize {
    val isFullWidth = config.gridSize.first >= 50
    val height = config.viewSize.second

    return if (isFullWidth) {
        when {
            height >= 250 -> LayoutSize.LARGE
            height >= 160 -> LayoutSize.MEDIUM
            else -> LayoutSize.SMALL_WIDE
        }
    } else {
        when {
            height >= 250 -> LayoutSize.LARGE
            height >= 160 -> LayoutSize.MEDIUM
            else -> LayoutSize.SMALL
        }
    }
}
