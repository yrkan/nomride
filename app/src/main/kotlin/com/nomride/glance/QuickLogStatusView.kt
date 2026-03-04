package com.nomride.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.nomride.karoo.LayoutSize
import com.nomride.karoo.TextSizeHelper
import com.nomride.karoo.getLayoutSize
import io.hammerhead.karooext.models.ViewConfig

@Composable
fun QuickLogStatusView(
    lastIntakeName: String?,
    lastIntakeTimestampMs: Long,
    balance: Double,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val primarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.PRIMARY)
    val secondarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.SECONDARY)
    val tertiarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.TERTIARY)
    val labelSp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.LABEL)
    val padDp = TextSizeHelper.paddingDp(viewConfig.viewSize.second)

    Box(
        modifier = GlanceModifier.fillMaxSize().background(Color(0xFF000000)).padding(padDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (layoutSize) {
            LayoutSize.SMALL, LayoutSize.SMALL_WIDE -> {
                // Minimal: just the food name or "No food"
                Text(
                    text = lastIntakeName ?: "No food",
                    style = TextStyle(
                        color = ColorProvider(
                            if (lastIntakeName != null) Color.White else Color(0xFF757575),
                        ),
                        fontSize = secondarySp.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            LayoutSize.MEDIUM, LayoutSize.LARGE -> {
                // Full: name + time ago + balance
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (lastIntakeName != null && lastIntakeTimestampMs > 0) {
                        Text(
                            text = lastIntakeName,
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = primarySp.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                        val minutesAgo =
                            ((System.currentTimeMillis() - lastIntakeTimestampMs) / 60_000).toInt()
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = "${minutesAgo}min ago",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFBDBDBD)),
                                fontSize = tertiarySp.sp,
                            ),
                        )
                    } else {
                        Text(
                            text = "No food logged",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF757575)),
                                fontSize = secondarySp.sp,
                            ),
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Bal: ${balance.toInt()}g",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF9E9E9E)),
                            fontSize = labelSp.sp,
                        ),
                    )
                }
            }
        }
    }
}
