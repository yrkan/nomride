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
fun HydrationView(
    totalWaterMl: Double,
    viewConfig: ViewConfig,
) {
    val layoutSize = getLayoutSize(viewConfig)
    val primarySp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.PRIMARY)
    val labelSp = TextSizeHelper.calculateSp(viewConfig, TextSizeHelper.Role.LABEL)
    val padDp = TextSizeHelper.paddingDp(viewConfig.viewSize.second)

    Box(
        modifier = GlanceModifier.fillMaxSize().background(Color(0xFF1565C0)).padding(padDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (layoutSize) {
            LayoutSize.SMALL, LayoutSize.SMALL_WIDE -> {
                // Minimal: just the value with "ml" suffix
                Text(
                    text = "${totalWaterMl.toInt()}ml",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = primarySp.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            LayoutSize.MEDIUM, LayoutSize.LARGE -> {
                // Value + separate "ml" label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalWaterMl.toInt()}",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = primarySp.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "ml",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFBBDEFB)),
                            fontSize = labelSp.sp,
                        ),
                    )
                }
            }
        }
    }
}
