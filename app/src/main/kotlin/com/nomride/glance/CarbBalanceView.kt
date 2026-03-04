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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
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
fun CarbBalanceView(
    balance: Double,
    burned: Double,
    eaten: Double,
    lastIntakeTimestampMs: Long,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val primarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.PRIMARY)
    val secondarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.SECONDARY)
    val tertiarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.TERTIARY)
    val labelSp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.LABEL)
    val padDp = TextSizeHelper.paddingDp(viewConfig.viewSize.second)

    val bgColor = when {
        balance > 0 -> Color(0xFF4CAF50)
        balance > -50 -> Color(0xFF8BC34A)
        balance > -100 -> Color(0xFFFFEB3B)
        balance > -150 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    val textColor = if (balance > -100) Color.Black else Color.White

    Box(
        modifier = GlanceModifier.fillMaxSize().background(bgColor).padding(padDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (layoutSize) {
            LayoutSize.SMALL, LayoutSize.SMALL_WIDE -> {
                // Minimal: just the balance value
                Text(
                    text = "${balance.toInt()}g",
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        fontSize = primarySp.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            LayoutSize.MEDIUM -> {
                // Balance + burned/eaten summary row
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${balance.toInt()}g",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = primarySp.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Row(modifier = GlanceModifier.fillMaxWidth()) {
                        Text(
                            text = "B:${burned.toInt()}g",
                            style = TextStyle(
                                color = ColorProvider(textColor),
                                fontSize = secondarySp.sp,
                            ),
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = "E:${eaten.toInt()}g",
                            style = TextStyle(
                                color = ColorProvider(textColor),
                                fontSize = secondarySp.sp,
                            ),
                        )
                    }
                }
            }

            LayoutSize.LARGE -> {
                // Full detail: balance + burned + eaten + last eat time
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${balance.toInt()}g",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = primarySp.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Burned: ${burned.toInt()}g",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = tertiarySp.sp,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "Eaten: ${eaten.toInt()}g",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = tertiarySp.sp,
                        ),
                    )
                    if (lastIntakeTimestampMs > 0) {
                        val minutesAgo =
                            ((System.currentTimeMillis() - lastIntakeTimestampMs) / 60_000).toInt()
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = "Last eat: ${minutesAgo}min ago",
                            style = TextStyle(
                                color = ColorProvider(textColor.copy(alpha = 0.8f)),
                                fontSize = labelSp.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}
