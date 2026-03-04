package com.nomride.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomride.engine.CarbBalanceTracker
import com.nomride.model.FoodTemplate
import com.nomride.model.IntakeEntry
import com.nomride.ui.theme.Background
import com.nomride.ui.theme.NomRideTheme
import com.nomride.ui.theme.OnSurface
import com.nomride.ui.theme.OnSurfaceVariant
import com.nomride.ui.theme.Primary
import com.nomride.ui.theme.SurfaceVariant
import com.nomride.ui.theme.Accent
import com.nomride.ui.theme.garminBorder
import com.nomride.util.Preferences

class QuickLogActivity : ComponentActivity() {
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = NomRideExtensionHolder.preferences ?: Preferences(applicationContext)

        val mode = intent?.getStringExtra("mode") ?: "food"

        setContent {
            NomRideTheme {
                if (mode == "water") {
                    WaterLogScreen(
                        onLog = { ml -> logWater(ml) },
                        onCancel = { finish() },
                    )
                } else {
                    FoodLogScreen(
                        templates = preferences.loadFoodTemplates(),
                        onLog = { template -> logFood(template) },
                        onLogCustom = { grams -> logCustom(grams) },
                        onCancel = { finish() },
                    )
                }
            }
        }
    }

    private fun logFood(template: FoodTemplate) {
        val tracker = NomRideExtensionHolder.tracker
        if (tracker == null) {
            Toast.makeText(this, "NomRide not running", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val entry = IntakeEntry(
            timestampMs = System.currentTimeMillis(),
            carbsGrams = template.carbsGrams.toDouble(),
            type = IntakeEntry.IntakeType.FOOD,
            templateName = template.name,
        )
        tracker.logIntake(entry)
        finish()
    }

    private fun logCustom(grams: Int) {
        val tracker = NomRideExtensionHolder.tracker
        if (tracker == null) {
            Toast.makeText(this, "NomRide not running", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val entry = IntakeEntry(
            timestampMs = System.currentTimeMillis(),
            carbsGrams = grams.toDouble(),
            type = IntakeEntry.IntakeType.FOOD,
            templateName = "Custom ${grams}g",
        )
        tracker.logIntake(entry)
        finish()
    }

    private fun logWater(ml: Int) {
        val tracker = NomRideExtensionHolder.tracker
        if (tracker == null) {
            Toast.makeText(this, "NomRide not running", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        tracker.logWater(ml.toDouble())
        finish()
    }
}

object NomRideExtensionHolder {
    var tracker: CarbBalanceTracker? = null
    var preferences: Preferences? = null
}

@Composable
fun FoodLogScreen(
    templates: List<FoodTemplate>,
    onLog: (FoodTemplate) -> Unit,
    onLogCustom: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isCompact = screenHeight < 420.dp
    val buttonHeight = if (isCompact) 64.dp else 80.dp
    val borderColor = SurfaceVariant.copy(alpha = 0.5f)

    var showCustom by remember { mutableStateOf(false) }
    var customGrams by remember { mutableIntStateOf(25) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 2.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onCancel,
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
                text = if (showCustom) "CUSTOM CARBS" else "LOG FOOD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                letterSpacing = 1.sp,
            )
        }

        if (showCustom) {
            // Custom gram input with +/- buttons
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = if (isCompact) 8.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Carbs",
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Decrease -5
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 48.dp else 56.dp)
                            .background(SurfaceVariant)
                            .clickable { customGrams = (customGrams - 5).coerceAtLeast(5) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "\u2212",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                        )
                    }

                    // Value display
                    Column(
                        modifier = Modifier.width(if (isCompact) 100.dp else 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$customGrams",
                            fontSize = if (isCompact) 36.sp else 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "g",
                            fontSize = if (isCompact) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary.copy(alpha = 0.7f),
                        )
                    }

                    // Increase +5
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 48.dp else 56.dp)
                            .background(SurfaceVariant)
                            .clickable { customGrams = (customGrams + 5).coerceAtMost(200) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "+5",
                            tint = OnSurface,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 44.dp else 52.dp)
                            .background(SurfaceVariant)
                            .clickable { showCustom = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "BACK",
                            fontSize = if (isCompact) 12.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 44.dp else 52.dp)
                            .background(Primary)
                            .clickable { onLogCustom(customGrams) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "LOG ${customGrams}g",
                            fontSize = if (isCompact) 12.sp else 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                        )
                    }
                }
            }
        } else {
            // Template buttons list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                templates.forEach { template ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .garminBorder(borderColor)
                            .background(Background)
                            .clickable { onLog(template) }
                            .height(buttonHeight),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight()
                                    .background(Primary),
                            )

                            Text(
                                text = template.name,
                                fontSize = if (isCompact) 14.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                            )

                            Text(
                                text = "${template.carbsGrams}g",
                                fontSize = if (isCompact) 20.sp else 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Primary,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        }
                    }
                }
            }

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isCompact) 44.dp else 52.dp)
                        .background(Accent)
                        .clickable { showCustom = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "CUSTOM",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isCompact) 44.dp else 52.dp)
                        .background(SurfaceVariant)
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "CANCEL",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun WaterLogScreen(
    onLog: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isCompact = screenHeight < 420.dp
    val waterOptions = listOf(150, 200, 250, 500, 750)
    val borderColor = SurfaceVariant.copy(alpha = 0.5f)
    val waterBlue = Color(0xFF1565C0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(waterBlue)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 2.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = "LOG WATER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp,
            )
        }

        // Water options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            waterOptions.forEach { ml ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .garminBorder(borderColor)
                        .background(Background)
                        .clickable { onLog(ml) }
                        .height(if (isCompact) 64.dp else 80.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(waterBlue),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "$ml",
                            fontSize = if (isCompact) 28.sp else 36.sp,
                            fontWeight = FontWeight.Black,
                            color = waterBlue,
                        )
                        Text(
                            text = "ml",
                            fontSize = if (isCompact) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = waterBlue.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 4.dp),
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Cancel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .height(if (isCompact) 44.dp else 52.dp)
                .background(SurfaceVariant)
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "CANCEL",
                fontSize = if (isCompact) 12.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
            )
        }
    }
}
