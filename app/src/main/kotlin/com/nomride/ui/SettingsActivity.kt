package com.nomride.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomride.BuildConfig
import com.nomride.model.FoodTemplate
import com.nomride.ui.theme.Accent
import com.nomride.ui.theme.AccentLight
import com.nomride.ui.theme.Background
import com.nomride.ui.theme.Error
import com.nomride.ui.theme.Food
import com.nomride.ui.theme.NomRideTheme
import com.nomride.ui.theme.OnAccent
import com.nomride.ui.theme.OnSurface
import com.nomride.ui.theme.OnSurfaceVariant
import com.nomride.ui.theme.Surface
import com.nomride.ui.theme.SurfaceElevated
import com.nomride.ui.theme.SurfaceVariant
import com.nomride.ui.theme.Water
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
    var newTemplateEmoji by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Accent)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 4.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnAccent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = "NOMRIDE SETTINGS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = OnAccent,
                letterSpacing = 1.sp,
            )
        }

        // Scrollable settings
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp),
        ) {
            // === PROFILE ===
            SettingsSection(title = "PROFILE") {
                FtpRow(
                    value = ftpOverride,
                    onUpdate = {
                        ftpOverride = it
                        preferences.ftpOverride = it
                    },
                    isCompact = isCompact,
                )
                CardDivider()
                WeightRow(
                    value = weightOverride,
                    onUpdate = {
                        weightOverride = it
                        preferences.weightOverride = it
                    },
                    isCompact = isCompact,
                )
            }

            // === FOOD TEMPLATES ===
            SettingsSection(title = "FOOD TEMPLATES") {
                templates.forEachIndexed { index, template ->
                    if (index > 0) CardDivider()
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
                        isCompact = isCompact,
                    )
                }

                if (templates.isNotEmpty()) CardDivider()

                if (showAddTemplate) {
                    AddTemplateRow(
                        name = newTemplateName,
                        carbs = newTemplateCarbs,
                        emoji = newTemplateEmoji,
                        onNameChange = { newTemplateName = it },
                        onCarbsChange = { newTemplateCarbs = it.filter { c -> c.isDigit() } },
                        onEmojiChange = { newTemplateEmoji = it },
                        onAdd = {
                            val carbs = newTemplateCarbs.toIntOrNull()
                            if (newTemplateName.isNotBlank() && carbs != null && carbs > 0) {
                                val newTemplate = FoodTemplate(
                                    name = newTemplateName.trim(),
                                    carbsGrams = carbs,
                                    emoji = newTemplateEmoji.trim(),
                                )
                                templates.add(newTemplate)
                                preferences.saveFoodTemplates(templates.toList())
                                newTemplateName = ""
                                newTemplateCarbs = ""
                                newTemplateEmoji = ""
                                showAddTemplate = false
                            }
                        },
                        onCancel = {
                            showAddTemplate = false
                            newTemplateName = ""
                            newTemplateCarbs = ""
                            newTemplateEmoji = ""
                        },
                        isCompact = isCompact,
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Food.copy(alpha = 0.1f))
                            .clickable { showAddTemplate = true }
                            .padding(horizontal = 12.dp, vertical = if (isCompact) 10.dp else 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Food,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Template",
                            fontSize = if (isCompact) 11.sp else 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Food,
                        )
                    }
                }
            }

            // === REMINDERS ===
            SettingsSection(title = "REMINDERS") {
                CompactValueRow(
                    label = "Eat interval",
                    value = eatInterval,
                    unit = "min",
                    color = Accent,
                    onIncrease = {
                        eatInterval = (eatInterval + 5).coerceAtMost(45)
                        preferences.eatIntervalMinutes = eatInterval
                    },
                    onDecrease = {
                        eatInterval = (eatInterval - 5).coerceAtLeast(10)
                        preferences.eatIntervalMinutes = eatInterval
                    },
                    isCompact = isCompact,
                )
                CardDivider()
                CompactValueRow(
                    label = "Drink interval",
                    value = drinkInterval,
                    unit = "min",
                    color = Water,
                    onIncrease = {
                        drinkInterval = (drinkInterval + 5).coerceAtMost(30)
                        preferences.drinkIntervalMinutes = drinkInterval
                    },
                    onDecrease = {
                        drinkInterval = (drinkInterval - 5).coerceAtLeast(10)
                        preferences.drinkIntervalMinutes = drinkInterval
                    },
                    isCompact = isCompact,
                )
                CardDivider()
                CompactValueRow(
                    label = "First alert after",
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
                    isCompact = isCompact,
                )
            }

            // === ALERTS & DATA ===
            SettingsSection(title = "OPTIONS") {
                ToggleRow(
                    label = "Sound alerts",
                    checked = soundEnabled,
                    activeColor = Accent,
                    onToggle = {
                        soundEnabled = it
                        preferences.soundEnabled = it
                    },
                    isCompact = isCompact,
                )
                CardDivider()
                ToggleRow(
                    label = "FIT export",
                    checked = fitExport,
                    activeColor = Accent,
                    onToggle = {
                        fitExport = it
                        preferences.fitExportEnabled = it
                    },
                    isCompact = isCompact,
                )
            }

            // === ABOUT ===
            AboutSection(isCompact = isCompact)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// =============================================================================
// SECTION CARD — wraps content in a rounded card with header
// =============================================================================

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Surface),
        ) {
            content()
        }
    }
}

@Composable
private fun AboutSection(isCompact: Boolean) {
    val context = LocalContext.current

    SettingsSection(title = "ABOUT") {
        // Version
        InfoRow(
            label = "Version",
            value = BuildConfig.VERSION_NAME,
            isCompact = isCompact,
        )
        CardDivider()
        // GitHub
        LinkRow(
            label = "GitHub",
            value = "yrkan/nomride",
            isCompact = isCompact,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yrkan/nomride")),
                )
            },
        )
        CardDivider()
        // Contact
        LinkRow(
            label = "Contact",
            value = "info@nomride.com",
            isCompact = isCompact,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:info@nomride.com")
                    },
                )
            },
        )
        CardDivider()
        // Privacy
        LinkRow(
            label = "Privacy Policy",
            isCompact = isCompact,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://nomride-landing.ykan.workers.dev/privacy")),
                )
            },
        )
        CardDivider()
        // License
        InfoRow(
            label = "License",
            value = "MIT",
            isCompact = isCompact,
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (isCompact) 10.dp else 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = if (isCompact) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
        )
        Text(
            text = value,
            fontSize = if (isCompact) 11.sp else 12.sp,
            color = OnSurfaceVariant,
        )
    }
}

@Composable
private fun LinkRow(
    label: String,
    value: String? = null,
    isCompact: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (isCompact) 10.dp else 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = if (isCompact) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    color = Accent,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "\u2197",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CardDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(1.dp)
            .background(SurfaceVariant),
    )
}

// =============================================================================
// NUMERIC INPUT DIALOG
// =============================================================================

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
        shape = RoundedCornerShape(12.dp),
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
                        .clip(RoundedCornerShape(8.dp))
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
                                .clip(RoundedCornerShape(6.dp))
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
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isValid) accentColor else SurfaceVariant)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = "SAVE",
                    color = if (isValid) Color.White else OnSurfaceVariant,
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

// =============================================================================
// ROW COMPONENTS — all inside rounded cards now
// =============================================================================

@Composable
private fun FtpRow(
    value: Int,
    onUpdate: (Int) -> Unit,
    isCompact: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(horizontal = 12.dp, vertical = if (isCompact) 12.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "FTP",
                fontSize = if (isCompact) 12.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
            )
            if (value == 0) {
                Text(
                    text = "From Karoo profile",
                    fontSize = if (isCompact) 9.sp else 10.sp,
                    color = OnSurfaceVariant,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (value > 0) "$value" else "---",
                fontSize = if (isCompact) 20.sp else 24.sp,
                fontWeight = FontWeight.Black,
                color = Accent,
            )
            Text(
                text = " W",
                fontSize = if (isCompact) 12.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = Accent.copy(alpha = 0.6f),
            )
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(14.dp),
            )
        }
    }

    if (showDialog) {
        NumericInputDialog(
            title = "FTP",
            currentValue = if (value > 0) value.toString() else "",
            unit = "W",
            accentColor = Accent,
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
    isCompact: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(horizontal = 12.dp, vertical = if (isCompact) 12.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Weight",
                fontSize = if (isCompact) 12.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
            )
            if (value == 0f) {
                Text(
                    text = "From Karoo profile",
                    fontSize = if (isCompact) 9.sp else 10.sp,
                    color = OnSurfaceVariant,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (value > 0f) "${value.toInt()}" else "---",
                fontSize = if (isCompact) 18.sp else 22.sp,
                fontWeight = FontWeight.Black,
                color = AccentLight,
            )
            Text(
                text = " kg",
                fontSize = if (isCompact) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                color = AccentLight.copy(alpha = 0.6f),
            )
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(14.dp),
            )
        }
    }

    if (showDialog) {
        NumericInputDialog(
            title = "Weight",
            currentValue = if (value > 0f) value.toInt().toString() else "",
            unit = "kg",
            accentColor = AccentLight,
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
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = if (isCompact) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(6.dp))
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
                    fontSize = if (isCompact) 16.sp else 18.sp,
                    fontWeight = FontWeight.Black,
                    color = color,
                )
                Text(
                    text = unit,
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = color.copy(alpha = 0.6f),
                )
            }

            Box(
                modifier = Modifier
                    .size(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(6.dp))
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
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    activeColor: Color,
    onToggle: (Boolean) -> Unit,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!checked) }
            .padding(horizontal = 12.dp, vertical = if (isCompact) 10.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = if (isCompact) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) activeColor else SurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (checked) "ON" else "OFF",
                fontSize = if (isCompact) 10.sp else 11.sp,
                fontWeight = FontWeight.Black,
                color = if (checked) Color.White else OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TemplateRow(
    template: FoodTemplate,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (template.isDefault) Icons.Default.Star else Icons.Outlined.Star,
            contentDescription = null,
            tint = if (template.isDefault) Food else SurfaceVariant,
            modifier = Modifier.size(if (isCompact) 14.dp else 16.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = template.displayName,
                fontSize = if (isCompact) 12.sp else 13.sp,
                fontWeight = if (template.isDefault) FontWeight.Bold else FontWeight.Medium,
                color = OnSurface,
                maxLines = 1,
            )
            Text(
                text = "${template.carbsGrams}g${if (template.isDefault) " · Quick Gel default" else ""}",
                fontSize = if (isCompact) 10.sp else 11.sp,
                color = if (template.isDefault) Food else OnSurfaceVariant,
            )
        }

        if (!template.isDefault) {
            Icon(
                Icons.Outlined.Star,
                contentDescription = "Set as Quick Gel default",
                tint = OnSurfaceVariant,
                modifier = Modifier
                    .size(if (isCompact) 22.dp else 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onSetDefault)
                    .padding(2.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .size(if (isCompact) 28.dp else 32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Error.copy(alpha = 0.15f))
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
    }
}

@Composable
private fun AddTemplateRow(
    name: String,
    carbs: String,
    emoji: String,
    onNameChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit,
    isCompact: Boolean,
) {
    val fontSize = if (isCompact) 12.sp else 13.sp
    val fieldPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(if (isCompact) 40.dp else 48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceVariant)
                    .padding(fieldPadding),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = emoji,
                    onValueChange = { v -> onEmojiChange(v.take(2)) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isCompact) 16.sp else 18.sp,
                        color = OnSurface,
                        textAlign = TextAlign.Center,
                    ),
                    cursorBrush = SolidColor(Food),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            if (emoji.isEmpty()) {
                                Text(
                                    text = "😀",
                                    fontSize = if (isCompact) 16.sp else 18.sp,
                                    color = OnSurfaceVariant.copy(alpha = 0.4f),
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
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceVariant)
                    .padding(fieldPadding),
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = fontSize,
                        color = OnSurface,
                    ),
                    cursorBrush = SolidColor(Food),
                    decorationBox = { innerTextField ->
                        Box {
                            if (name.isEmpty()) {
                                Text("Name", fontSize = fontSize, color = OnSurfaceVariant)
                            }
                            innerTextField()
                        }
                    },
                )
            }
            Box(
                modifier = Modifier
                    .width(if (isCompact) 64.dp else 72.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceVariant)
                    .padding(fieldPadding),
            ) {
                BasicTextField(
                    value = carbs,
                    onValueChange = onCarbsChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = fontSize,
                        color = OnSurface,
                    ),
                    cursorBrush = SolidColor(Food),
                    decorationBox = { innerTextField ->
                        Box {
                            if (carbs.isEmpty()) {
                                Text("Carbs g", fontSize = fontSize, color = OnSurfaceVariant)
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .height(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceVariant)
                    .clickable(onClick = onCancel)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Cancel",
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                )
            }
            val canAdd = name.isNotBlank() && (carbs.toIntOrNull() ?: 0) > 0
            Box(
                modifier = Modifier
                    .height(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (canAdd) Food else SurfaceVariant)
                    .clickable(enabled = canAdd, onClick = onAdd)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Add",
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canAdd) Color.Black else OnSurfaceVariant,
                )
            }
        }
    }
}
