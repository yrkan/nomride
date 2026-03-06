package com.nomride.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import com.nomride.karoo.LayoutSize
import com.nomride.karoo.getLayoutSize
import io.hammerhead.karooext.models.ViewConfig

@Composable
fun CarbBalanceView(
    balance: Double,
    burned: Double,
    eaten: Double,
    burnRateGph: Double,
    lastIntakeTimestampMs: Long,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val bgColor = GlanceColors.balanceBgColor(balance)
    val textColor = GlanceColors.balanceTextColor(balance)
    val dimColor = GlanceColors.balanceDimColor(balance)
    val balanceText = "${balance.toInt()}g"

    ColoredFieldContainer(bgColor = bgColor) {
        when (layoutSize) {
            LayoutSize.SMALL -> {
                ValueText(text = balanceText, color = textColor, fontSize = 24.sp)
            }

            LayoutSize.SMALL_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "BALANCE", fontSize = 10.sp, color = dimColor)
                    ValueText(text = balanceText, color = textColor, fontSize = 26.sp)
                }
            }

            LayoutSize.MEDIUM_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "CARB BALANCE", fontSize = 10.sp, color = dimColor)
                    ValueText(text = balanceText, color = textColor, fontSize = 28.sp)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Row(modifier = GlanceModifier.fillMaxWidth()) {
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LabelText(text = "BURNED", fontSize = 9.sp, color = dimColor)
                            ValueText(text = "${burned.toInt()}g", color = textColor, fontSize = 14.sp)
                        }
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LabelText(text = "EATEN", fontSize = 9.sp, color = dimColor)
                            ValueText(text = "${eaten.toInt()}g", color = textColor, fontSize = 14.sp)
                        }
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LabelText(text = "RATE", fontSize = 9.sp, color = dimColor)
                            ValueText(text = "${burnRateGph.toInt()}g/h", color = textColor, fontSize = 14.sp)
                        }
                    }
                }
            }

            LayoutSize.MEDIUM -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "BALANCE", fontSize = 11.sp, color = dimColor)
                    ValueText(text = balanceText, color = textColor, fontSize = 24.sp)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    LabelText(
                        text = "B:${burned.toInt()}g  E:${eaten.toInt()}g",
                        fontSize = 12.sp,
                        color = dimColor,
                    )
                }
            }

            LayoutSize.LARGE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "CARB BALANCE", fontSize = 11.sp, color = dimColor)
                    ValueText(text = balanceText, color = textColor, fontSize = 36.sp)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    GlanceDivider(color = dimColor)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    MetricValueRow(
                        label = "BURNED",
                        value = "${burned.toInt()}g",
                        valueColor = textColor,
                        fontSize = 16.sp,
                        labelColor = dimColor,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    MetricValueRow(
                        label = "EATEN",
                        value = "${eaten.toInt()}g",
                        valueColor = textColor,
                        fontSize = 16.sp,
                        labelColor = dimColor,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    MetricValueRow(
                        label = "BURN RATE",
                        value = "${burnRateGph.toInt()}g/h",
                        valueColor = textColor,
                        fontSize = 16.sp,
                        labelColor = dimColor,
                    )
                    if (lastIntakeTimestampMs > 0) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        MetricValueRow(
                            label = "LAST EAT",
                            value = formatMinutesAgo(lastIntakeTimestampMs),
                            valueColor = textColor,
                            fontSize = 16.sp,
                            labelColor = dimColor,
                        )
                    }
                }
            }

            LayoutSize.NARROW -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "BALANCE", fontSize = 11.sp, color = dimColor)
                    ValueText(text = balanceText, color = textColor, fontSize = 28.sp)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    LabelText(
                        text = "E: ${eaten.toInt()}g",
                        fontSize = 13.sp,
                        color = dimColor,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    LabelText(
                        text = "B: ${burned.toInt()}g",
                        fontSize = 13.sp,
                        color = dimColor,
                    )
                }
            }
        }
    }
}
