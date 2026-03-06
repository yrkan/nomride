package com.nomride.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import com.nomride.karoo.LayoutSize
import com.nomride.karoo.NO_DATA
import com.nomride.karoo.getLayoutSize
import io.hammerhead.karooext.models.ViewConfig

@Composable
fun QuickLogStatusView(
    lastIntakeName: String?,
    lastIntakeTimestampMs: Long,
    balance: Double,
    burnRateGph: Double,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val name = lastIntakeName ?: NO_DATA
    val timeAgo = formatMinutesAgo(lastIntakeTimestampMs)
    val minutesAgo = if (lastIntakeTimestampMs > 0) {
        ((System.currentTimeMillis() - lastIntakeTimestampMs) / 60_000).toInt()
    } else -1

    DataFieldContainer {
        when (layoutSize) {
            LayoutSize.SMALL -> {
                ValueText(
                    text = name,
                    color = if (lastIntakeName != null) GlanceColors.White else GlanceColors.Label,
                    fontSize = 14.sp,
                )
            }

            LayoutSize.SMALL_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "LAST FOOD", fontSize = 10.sp)
                    ValueText(
                        text = name,
                        color = if (lastIntakeName != null) GlanceColors.Food else GlanceColors.Label,
                        fontSize = 18.sp,
                    )
                }
            }

            LayoutSize.MEDIUM_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "LAST FOOD", fontSize = 10.sp)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    TripleMetric(
                        label1 = "NAME",
                        value1 = name,
                        value1Color = if (lastIntakeName != null) GlanceColors.Food else GlanceColors.Label,
                        label2 = "AGO",
                        value2 = timeAgo,
                        value2Color = if (minutesAgo >= 0) GlanceColors.timeSinceColor(minutesAgo) else GlanceColors.Label,
                        label3 = "BAL",
                        value3 = "${balance.toInt()}g",
                        value3Color = GlanceColors.White,
                        labelFontSize = 9.sp,
                        valueFontSize = 14.sp,
                    )
                }
            }

            LayoutSize.MEDIUM -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "LAST FOOD", fontSize = 11.sp)
                    ValueText(
                        text = name,
                        color = if (lastIntakeName != null) GlanceColors.Food else GlanceColors.Label,
                        fontSize = 18.sp,
                    )
                    if (lastIntakeTimestampMs > 0) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        LabelText(
                            text = timeAgo,
                            fontSize = 12.sp,
                            color = GlanceColors.timeSinceColor(minutesAgo),
                        )
                    }
                }
            }

            LayoutSize.LARGE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "LAST FOOD", fontSize = 11.sp)
                    ValueText(
                        text = name,
                        color = if (lastIntakeName != null) GlanceColors.Food else GlanceColors.Label,
                        fontSize = 28.sp,
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    GlanceDivider()
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    MetricValueRow(
                        label = "TIME",
                        value = timeAgo,
                        valueColor = if (minutesAgo >= 0) GlanceColors.timeSinceColor(minutesAgo) else GlanceColors.Label,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    MetricValueRow(
                        label = "BALANCE",
                        value = "${balance.toInt()}g",
                        valueColor = GlanceColors.White,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    MetricValueRow(
                        label = "BURN RATE",
                        value = "${burnRateGph.toInt()}g/h",
                        valueColor = GlanceColors.burnRateColor(burnRateGph),
                        fontSize = 16.sp,
                    )
                }
            }

            LayoutSize.NARROW -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "LAST FOOD", fontSize = 11.sp)
                    ValueText(
                        text = name,
                        color = if (lastIntakeName != null) GlanceColors.Food else GlanceColors.Label,
                        fontSize = 16.sp,
                    )
                    if (lastIntakeTimestampMs > 0) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        LabelText(text = timeAgo, fontSize = 13.sp, color = GlanceColors.timeSinceColor(minutesAgo))
                    }
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    LabelText(text = "Bal: ${balance.toInt()}g", fontSize = 13.sp)
                }
            }
        }
    }
}
