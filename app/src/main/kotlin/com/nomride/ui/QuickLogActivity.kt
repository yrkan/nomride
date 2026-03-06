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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomride.engine.CarbBalanceTracker
import com.nomride.model.FoodTemplate
import com.nomride.model.IntakeEntry
import com.nomride.ui.theme.Accent
import com.nomride.ui.theme.Background
import com.nomride.ui.theme.Food
import com.nomride.ui.theme.NomRideTheme
import com.nomride.ui.theme.OnAccent
import com.nomride.ui.theme.OnSurface
import com.nomride.ui.theme.OnSurfaceVariant
import com.nomride.ui.theme.Surface
import com.nomride.ui.theme.SurfaceVariant
import com.nomride.ui.theme.Water
import com.nomride.ui.theme.WaterDark
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
            templateEmoji = template.emoji.ifEmpty { null },
        )
        tracker.logIntake(entry)
        Toast.makeText(this, "Logged ${template.name} (${template.carbsGrams}g)", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Logged ${grams}g carbs", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Logged ${ml}ml water", Toast.LENGTH_SHORT).show()
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

    var showCustom by remember { mutableStateOf(false) }
    var customGrams by remember { mutableIntStateOf(25) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header — Green for food
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Food)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 4.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = if (showCustom) ({ showCustom = false }) else onCancel,
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
            // Custom gram input
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = if (isCompact) 12.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "CARBS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.sp,
                )

                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 20.dp))

                // +/- row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 52.dp else 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface)
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

                    Column(
                        modifier = Modifier.width(if (isCompact) 100.dp else 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$customGrams",
                            fontSize = if (isCompact) 42.sp else 56.sp,
                            fontWeight = FontWeight.Black,
                            color = Food,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "grams",
                            fontSize = if (isCompact) 12.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Food.copy(alpha = 0.6f),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 52.dp else 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface)
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

                Spacer(modifier = Modifier.height(if (isCompact) 20.dp else 32.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 48.dp else 56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface)
                            .clickable { showCustom = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "BACK",
                            fontSize = if (isCompact) 13.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 48.dp else 56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Food)
                            .clickable { onLogCustom(customGrams) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "LOG ${customGrams}g",
                            fontSize = if (isCompact) 13.sp else 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                        )
                    }
                }
            }
        } else {
            // Template list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                templates.forEach { template ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface)
                            .clickable { onLog(template) }
                            .padding(horizontal = 16.dp, vertical = if (isCompact) 14.dp else 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = template.name,
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            modifier = Modifier.weight(1f),
                        )

                        Text(
                            text = "${template.carbsGrams}",
                            fontSize = if (isCompact) 22.sp else 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Food,
                        )
                        Text(
                            text = "g",
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Food.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isCompact) 48.dp else 56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Accent)
                        .clickable { showCustom = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "CUSTOM",
                        fontSize = if (isCompact) 13.sp else 14.sp,
                        fontWeight = FontWeight.Black,
                        color = OnAccent,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isCompact) 48.dp else 56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface)
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "CANCEL",
                        fontSize = if (isCompact) 13.sp else 14.sp,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Header — Blue for water
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WaterDark)
                .padding(horizontal = 4.dp, vertical = if (isCompact) 4.dp else 6.dp),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = if (isCompact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            waterOptions.forEach { ml ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface)
                        .clickable { onLog(ml) }
                        .padding(vertical = if (isCompact) 14.dp else 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$ml",
                            fontSize = if (isCompact) 28.sp else 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Water,
                        )
                        Text(
                            text = " ml",
                            fontSize = if (isCompact) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Water.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }

        // Cancel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(if (isCompact) 48.dp else 56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface)
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "CANCEL",
                fontSize = if (isCompact) 13.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
            )
        }
    }
}
