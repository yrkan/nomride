package com.nomride.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun DataFieldContainer(
    content: @Composable () -> Unit,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(8.dp)
            .background(GlanceColors.Background)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun ColoredFieldContainer(
    bgColor: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(8.dp)
            .background(bgColor)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun LabelText(
    text: String,
    fontSize: TextUnit = 12.sp,
    color: Color = GlanceColors.Label,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        style = TextStyle(
            color = ColorProvider(color),
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
        ),
        maxLines = maxLines,
    )
}

@Composable
fun ValueText(
    text: String,
    color: Color = GlanceColors.White,
    fontSize: TextUnit = 18.sp,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        style = TextStyle(
            color = ColorProvider(color),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
        maxLines = maxLines,
    )
}

@Composable
fun MetricValueRow(
    label: String,
    value: String,
    valueColor: Color = GlanceColors.White,
    fontSize: TextUnit = 18.sp,
    labelColor: Color = GlanceColors.Label,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = ColorProvider(labelColor),
                fontSize = fontSize,
            ),
            maxLines = 1,
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        Text(
            text = value,
            style = TextStyle(
                color = ColorProvider(valueColor),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
        )
    }
}

@Composable
fun DualMetric(
    label1: String,
    value1: String,
    value1Color: Color = GlanceColors.White,
    label2: String,
    value2: String,
    value2Color: Color = GlanceColors.White,
    labelFontSize: TextUnit = 10.sp,
    valueFontSize: TextUnit = 18.sp,
    labelColor: Color = GlanceColors.Label,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabelText(text = label1, fontSize = labelFontSize, color = labelColor)
            ValueText(text = value1, color = value1Color, fontSize = valueFontSize)
        }
        GlanceVerticalDivider()
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabelText(text = label2, fontSize = labelFontSize, color = labelColor)
            ValueText(text = value2, color = value2Color, fontSize = valueFontSize)
        }
    }
}

@Composable
fun TripleMetric(
    label1: String,
    value1: String,
    value1Color: Color = GlanceColors.White,
    label2: String,
    value2: String,
    value2Color: Color = GlanceColors.White,
    label3: String,
    value3: String,
    value3Color: Color = GlanceColors.White,
    labelFontSize: TextUnit = 10.sp,
    valueFontSize: TextUnit = 16.sp,
    labelColor: Color = GlanceColors.Label,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabelText(text = label1, fontSize = labelFontSize, color = labelColor)
            ValueText(text = value1, color = value1Color, fontSize = valueFontSize)
        }
        GlanceVerticalDivider()
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabelText(text = label2, fontSize = labelFontSize, color = labelColor)
            ValueText(text = value2, color = value2Color, fontSize = valueFontSize)
        }
        GlanceVerticalDivider()
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabelText(text = label3, fontSize = labelFontSize, color = labelColor)
            ValueText(text = value3, color = value3Color, fontSize = valueFontSize)
        }
    }
}

@Composable
fun GlanceDivider(
    color: Color = GlanceColors.Divider,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(thickness)
            .background(color),
    ) {}
}

@Composable
fun GlanceVerticalDivider(
    color: Color = GlanceColors.Divider,
    thickness: Dp = 1.dp,
    height: Dp = 28.dp,
) {
    Box(
        modifier = GlanceModifier
            .width(thickness)
            .height(height)
            .background(color),
    ) {}
}

fun formatMinutesAgo(timestampMs: Long): String {
    if (timestampMs <= 0) return "-"
    val minutes = ((System.currentTimeMillis() - timestampMs) / 60_000).toInt()
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}min"
        else -> {
            val h = minutes / 60
            val m = minutes % 60
            if (m == 0) "${h}h" else "${h}h${m}m"
        }
    }
}
