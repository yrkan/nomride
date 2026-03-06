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
import com.nomride.karoo.getLayoutSize
import io.hammerhead.karooext.models.ViewConfig

@Composable
fun HydrationView(
    totalWaterMl: Double,
    mlPerHour: Double,
    sipCount: Int,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val waterInt = totalWaterMl.toInt()

    DataFieldContainer {
        when (layoutSize) {
            LayoutSize.SMALL -> {
                ValueText(text = "$waterInt", color = GlanceColors.Water, fontSize = 24.sp)
            }

            LayoutSize.SMALL_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "HYDRATION", fontSize = 10.sp)
                    ValueText(text = "${waterInt}ml", color = GlanceColors.Water, fontSize = 24.sp)
                }
            }

            LayoutSize.MEDIUM_WIDE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "HYDRATION", fontSize = 10.sp)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    DualMetric(
                        label1 = "TOTAL",
                        value1 = "${waterInt}ml",
                        value1Color = GlanceColors.Water,
                        label2 = "RATE",
                        value2 = "${mlPerHour.toInt()}ml/h",
                        value2Color = GlanceColors.Water,
                        labelFontSize = 9.sp,
                        valueFontSize = 18.sp,
                    )
                }
            }

            LayoutSize.MEDIUM -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "HYDRATION", fontSize = 11.sp)
                    ValueText(text = "$waterInt", color = GlanceColors.Water, fontSize = 24.sp)
                    LabelText(text = "ml", fontSize = 12.sp, color = GlanceColors.WaterLight)
                }
            }

            LayoutSize.LARGE -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "HYDRATION", fontSize = 11.sp)
                    ValueText(text = "${waterInt}ml", color = GlanceColors.Water, fontSize = 36.sp)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    GlanceDivider()
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    MetricValueRow(
                        label = "RATE",
                        value = "${mlPerHour.toInt()}ml/h",
                        valueColor = GlanceColors.Water,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    MetricValueRow(
                        label = "SIPS",
                        value = "$sipCount",
                        valueColor = GlanceColors.Water,
                        fontSize = 16.sp,
                    )
                }
            }

            LayoutSize.NARROW -> {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LabelText(text = "HYDRATION", fontSize = 11.sp)
                    ValueText(text = "${waterInt}ml", color = GlanceColors.Water, fontSize = 24.sp)
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    LabelText(text = "${mlPerHour.toInt()}ml/h", fontSize = 13.sp, color = GlanceColors.Water)
                }
            }
        }
    }
}
