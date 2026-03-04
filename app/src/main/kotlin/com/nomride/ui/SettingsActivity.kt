package com.nomride.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomride.model.FoodTemplate
import com.nomride.ui.theme.Background
import com.nomride.ui.theme.NomRideTheme
import com.nomride.ui.theme.OnSurface
import com.nomride.ui.theme.OnSurfaceVariant
import com.nomride.ui.theme.Primary
import com.nomride.ui.theme.PrimaryDark
import com.nomride.ui.theme.Success
import com.nomride.ui.theme.SurfaceElevated
import com.nomride.ui.theme.SurfaceVariant
import com.nomride.ui.theme.Error
import com.nomride.ui.theme.Accent
import com.nomride.ui.theme.Surface
import com.nomride.ui.theme.garminBorder
import com.nomride.util.Preferences

class SettingsActivity : ComponentActivity() {
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(applicationContext)

        setContent {
            NomRideTheme {
                SettingsScreen(
                    preferences = preferences,
                    onNavigateBack = { finish() },
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    preferences: Preferences,
    onNavigateBack: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isCompact = screenHeight < 420.dp
    val borderColor = SurfaceVariant.copy(alpha = 0.5f)

    var eatInterval by remember { mutableIntStateOf(preferences.eatIntervalMinutes) }
    var drinkInterval by remember { mutableIntStateOf(preferences.drinkIntervalMinutes) }
    var reminderStart by remember { mutableIntStateOf(preferences.reminderStartMinutes) }
    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var fitExport by remember { mutableStateOf(preferences.fitExportEnabled) }
    var ftpOverride by remember { mutableIntStateOf(preferences.ftpOverride) }
    var weightOverride by remember { mutableStateOf(preferences.weightOverride) }

    val templates = remember { mutableStateListOf(*preferences.loadFoodTemplates().toTypedArray()) }
    var showAddTemplate by remember { mutableStateOf(false) }
    var newTemplateName by remember { mutableStateOf("") }
    var newTemplateCarbs by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 2.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = "NOMRIDE SETTINGS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                letterSpacing = 1.sp,
            )
        }

        // Settings list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // --- Profile Overrides ---
            SectionHeader("PROFILE")

            FtpRow(
                value = ftpOverride,
                onUpdate = {
                    ftpOverride = it
                    preferences.ftpOverride = it
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            WeightRow(
                value = weightOverride,
                onUpdate = {
                    weightOverride = it
                    preferences.weightOverride = it
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            // --- Food Templates ---
            SectionHeader("FOOD TEMPLATES")

            templates.forEachIndexed { index, template ->
                TemplateRow(
                    template = template,
                    onSetDefault = {
                        val updated = templates.mapIndexed { i, t ->
                            t.copy(isDefault = i == index)
                        }
                        templates.clear()
                        templates.addAll(updated)
                        preferences.saveFoodTemplates(updated)
                    },
                    onDelete = {
                        templates.removeAt(index)
                        preferences.saveFoodTemplates(templates.toList())
                    },
                    borderColor = borderColor,
                    isCompact = isCompact,
                )
            }

            if (showAddTemplate) {
                AddTemplateRow(
                    name = newTemplateName,
                    carbs = newTemplateCarbs,
                    onNameChange = { newTemplateName = it },
                    onCarbsChange = { newTemplateCarbs = it.filter { c -> c.isDigit() } },
                    onAdd = {
                        val carbs = newTemplateCarbs.toIntOrNull()
                        if (newTemplateName.isNotBlank() && carbs != null && carbs > 0) {
                            val newTemplate = FoodTemplate(newTemplateName.trim(), carbs)
                            templates.add(newTemplate)
                            preferences.saveFoodTemplates(templates.toList())
                            newTemplateName = ""
                            newTemplateCarbs = ""
                            showAddTemplate = false
                        }
                    },
                    onCancel = { showAddTemplate = false },
                    borderColor = borderColor,
                    isCompact = isCompact,
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .garminBorder(borderColor)
                        .background(Background)
                        .clickable { showAddTemplate = true }
                        .height(if (isCompact) 36.dp else 42.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(Accent),
                    )
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(14.dp),
                    )
                    Text(
                        text = "Add Template",
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Accent,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            // --- Reminders ---
            SectionHeader("REMINDERS")

            CompactValueRow(
                label = "Eat interval",
                value = eatInterval,
                unit = "min",
                color = Primary,
                onIncrease = {
                    eatInterval = (eatInterval + 5).coerceAtMost(45)
                    preferences.eatIntervalMinutes = eatInterval
                },
                onDecrease = {
                    eatInterval = (eatInterval - 5).coerceAtLeast(10)
                    preferences.eatIntervalMinutes = eatInterval
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            CompactValueRow(
                label = "Drink interval",
                value = drinkInterval,
                unit = "min",
                color = Accent,
                onIncrease = {
                    drinkInterval = (drinkInterval + 5).coerceAtMost(30)
                    preferences.drinkIntervalMinutes = drinkInterval
                },
                onDecrease = {
                    drinkInterval = (drinkInterval - 5).coerceAtLeast(10)
                    preferences.drinkIntervalMinutes = drinkInterval
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            CompactValueRow(
                label = "Start after",
                value = reminderStart,
                unit = "min",
                color = OnSurfaceVariant,
                onIncrease = {
                    reminderStart = (reminderStart + 5).coerceAtMost(60)
                    preferences.reminderStartMinutes = reminderStart
                },
                onDecrease = {
                    reminderStart = (reminderStart - 5).coerceAtLeast(10)
                    preferences.reminderStartMinutes = reminderStart
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            // --- Alerts ---
            SectionHeader("ALERTS")

            ToggleRow(
                label = "Sound alerts",
                checked = soundEnabled,
                onToggle = {
                    soundEnabled = it
                    preferences.soundEnabled = it
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            // --- Data ---
            SectionHeader("DATA")

            ToggleRow(
                label = "FIT export",
                checked = fitExport,
                onToggle = {
                    fitExport = it
                    preferences.fitExportEnabled = it
                },
                borderColor = borderColor,
                isCompact = isCompact,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun NumericInputDialog(
    title: String,
    currentValue: String,
    unit: String,
    accentColor: Color,
    minValue: Int,
    maxValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var textValue by remember { mutableStateOf(currentValue) }
    val isValid = textValue.toIntOrNull()?.let { it in minValue..maxValue } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SurfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            BasicTextField(
                                value = textValue,
                                onValueChange = { newValue ->
                                    textValue = newValue.filter { it.isDigit() }.take(4)
                                },
                                modifier = Modifier.widthIn(min = 60.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor,
                                    textAlign = TextAlign.Center,
                                ),
                                cursorBrush = SolidColor(accentColor),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (textValue.isEmpty()) {
                                            Text(
                                                text = "---",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Black,
                                                color = OnSurfaceVariant,
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = unit,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor.copy(alpha = 0.8f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Range: $minValue - $maxValue $unit",
                    fontSize = 12.sp,
                    color = if (isValid) OnSurfaceVariant else Error,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { textValue.toIntOrNull()?.let { onConfirm(it) } },
                enabled = isValid,
                modifier = Modifier
                    .background(if (isValid) accentColor else SurfaceVariant)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = "SAVE",
                    color = if (isValid) Color.Black else OnSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
    )
}

@Composable
private fun FtpRow(
    value: Int,
    onUpdate: (Int) -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .clickable { showDialog = true }
            .height(if (isCompact) 48.dp else 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(Primary),
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "FTP",
                fontSize = if (isCompact) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (value > 0) "$value" else "---",
                    fontSize = if (isCompact) 20.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Primary,
                )
                Text(
                    text = "W",
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary.copy(alpha = 0.7f),
                )
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(14.dp),
                )
            }
        }
    }

    if (showDialog) {
        NumericInputDialog(
            title = "FTP",
            currentValue = if (value > 0) value.toString() else "",
            unit = "W",
            accentColor = Primary,
            minValue = 50,
            maxValue = 999,
            onDismiss = { showDialog = false },
            onConfirm = { newValue ->
                onUpdate(newValue)
                showDialog = false
            },
        )
    }
}

@Composable
private fun WeightRow(
    value: Float,
    onUpdate: (Float) -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .clickable { showDialog = true }
            .height(if (isCompact) 44.dp else 52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(Success),
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Weight",
                fontSize = if (isCompact) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (value > 0f) "${value.toInt()}" else "---",
                    fontSize = if (isCompact) 18.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Success,
                )
                Text(
                    text = "kg",
                    fontSize = if (isCompact) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Success.copy(alpha = 0.7f),
                )
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(14.dp),
                )
            }
        }
    }

    if (showDialog) {
        NumericInputDialog(
            title = "Weight",
            currentValue = if (value > 0f) value.toInt().toString() else "",
            unit = "kg",
            accentColor = Success,
            minValue = 30,
            maxValue = 200,
            onDismiss = { showDialog = false },
            onConfirm = { newValue ->
                onUpdate(newValue.toFloat())
                showDialog = false
            },
        )
    }
}

@Composable
private fun CompactValueRow(
    label: String,
    value: Int,
    unit: String,
    color: Color,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .height(if (isCompact) 42.dp else 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(color.copy(alpha = 0.6f)),
        )

        Text(
            text = label,
            fontSize = if (isCompact) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 4.dp),
            maxLines = 1,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 28.dp else 32.dp)
                    .background(SurfaceVariant)
                    .clickable(onClick = onDecrease),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u2212",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                )
            }

            Row(
                modifier = Modifier.width(if (isCompact) 52.dp else 58.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$value",
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    fontWeight = FontWeight.Black,
                    color = color,
                )
                Text(
                    text = unit,
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = color.copy(alpha = 0.7f),
                )
            }

            Box(
                modifier = Modifier
                    .size(if (isCompact) 28.dp else 32.dp)
                    .background(SurfaceVariant)
                    .clickable(onClick = onIncrease),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = OnSurface,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .clickable { onToggle(!checked) }
            .height(if (isCompact) 38.dp else 44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (checked) Success else SurfaceVariant),
        )

        Text(
            text = label,
            fontSize = if (isCompact) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 4.dp),
            maxLines = 1,
        )

        Box(
            modifier = Modifier
                .width(if (isCompact) 36.dp else 40.dp)
                .height(if (isCompact) 20.dp else 22.dp)
                .background(if (checked) Success else SurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (checked) "ON" else "OFF",
                fontSize = if (isCompact) 9.sp else 10.sp,
                fontWeight = FontWeight.Black,
                color = if (checked) Color.Black else OnSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(6.dp))
    }
}

@Composable
private fun TemplateRow(
    template: FoodTemplate,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .height(if (isCompact) 40.dp else 46.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (template.isDefault) Primary else SurfaceVariant),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 4.dp),
        ) {
            Text(
                text = template.name,
                fontSize = if (isCompact) 11.sp else 12.sp,
                fontWeight = if (template.isDefault) FontWeight.Bold else FontWeight.Medium,
                color = OnSurface,
                maxLines = 1,
            )
            Text(
                text = "${template.carbsGrams}g${if (template.isDefault) " (default)" else ""}",
                fontSize = if (isCompact) 8.sp else 9.sp,
                color = if (template.isDefault) Primary else OnSurfaceVariant,
            )
        }

        if (!template.isDefault) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 24.dp else 28.dp)
                    .background(SurfaceVariant)
                    .clickable(onClick = onSetDefault),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Set default",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(modifier = Modifier.width(3.dp))
        }

        Box(
            modifier = Modifier
                .size(if (isCompact) 24.dp else 28.dp)
                .background(Error.copy(alpha = 0.2f))
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Error,
                modifier = Modifier.size(14.dp),
            )
        }

        Spacer(modifier = Modifier.width(6.dp))
    }
}

@Composable
private fun AddTemplateRow(
    name: String,
    carbs: String,
    onNameChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit,
    borderColor: Color,
    isCompact: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .garminBorder(borderColor)
            .background(Background)
            .padding(horizontal = 8.dp, vertical = if (isCompact) 4.dp else 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        color = OnSurface,
                    ),
                    cursorBrush = SolidColor(Primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (name.isEmpty()) {
                                Text(
                                    text = "Name",
                                    fontSize = if (isCompact) 11.sp else 12.sp,
                                    color = OnSurfaceVariant,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                BasicTextField(
                    value = carbs,
                    onValueChange = onCarbsChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        color = OnSurface,
                    ),
                    cursorBrush = SolidColor(Primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (carbs.isEmpty()) {
                                Text(
                                    text = "g",
                                    fontSize = if (isCompact) 11.sp else 12.sp,
                                    color = OnSurfaceVariant,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .height(if (isCompact) 24.dp else 28.dp)
                    .background(SurfaceVariant)
                    .clickable(onClick = onCancel)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Cancel",
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .height(if (isCompact) 24.dp else 28.dp)
                    .background(
                        if (name.isNotBlank() && (carbs.toIntOrNull() ?: 0) > 0) Primary
                        else SurfaceVariant,
                    )
                    .clickable(onClick = onAdd)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Add",
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (name.isNotBlank() && (carbs.toIntOrNull() ?: 0) > 0) Color.Black
                    else OnSurfaceVariant,
                )
            }
        }
    }
}
