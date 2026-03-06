package com.nomride.util

import android.content.Context
import android.content.SharedPreferences
import com.nomride.model.FoodTemplate
import com.nomride.model.RideNutritionState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class Preferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nomride_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    // FTP override (0 = use Karoo profile)
    var ftpOverride: Int
        get() = prefs.getInt(KEY_FTP_OVERRIDE, 0)
        set(value) = prefs.edit().putInt(KEY_FTP_OVERRIDE, value).apply()

    // Weight override (0 = use Karoo profile)
    var weightOverride: Float
        get() = prefs.getFloat(KEY_WEIGHT_OVERRIDE, 0f)
        set(value) = prefs.edit().putFloat(KEY_WEIGHT_OVERRIDE, value).apply()

    // Eat reminder interval in minutes
    var eatIntervalMinutes: Int
        get() = prefs.getInt(KEY_EAT_INTERVAL, 20)
        set(value) = prefs.edit().putInt(KEY_EAT_INTERVAL, value).apply()

    // Drink reminder interval in minutes
    var drinkIntervalMinutes: Int
        get() = prefs.getInt(KEY_DRINK_INTERVAL, 15)
        set(value) = prefs.edit().putInt(KEY_DRINK_INTERVAL, value).apply()

    // Sound alerts enabled
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    // FIT export enabled
    var fitExportEnabled: Boolean
        get() = prefs.getBoolean(KEY_FIT_EXPORT, false)
        set(value) = prefs.edit().putBoolean(KEY_FIT_EXPORT, value).apply()

    // Start reminders after N minutes
    var reminderStartMinutes: Int
        get() = prefs.getInt(KEY_REMINDER_START, 30)
        set(value) = prefs.edit().putInt(KEY_REMINDER_START, value).apply()

    fun loadFoodTemplates(): List<FoodTemplate> {
        val str = prefs.getString(KEY_FOOD_TEMPLATES, null) ?: return FoodTemplate.DEFAULTS
        return try {
            json.decodeFromString(str)
        } catch (e: Exception) {
            Timber.w(e, "Failed to load food templates")
            FoodTemplate.DEFAULTS
        }
    }

    fun saveFoodTemplates(templates: List<FoodTemplate>) {
        prefs.edit().putString(KEY_FOOD_TEMPLATES, json.encodeToString(templates)).apply()
    }

    fun getDefaultTemplate(): FoodTemplate {
        return loadFoodTemplates().firstOrNull { it.isDefault } ?: FoodTemplate.DEFAULTS.first()
    }

    fun saveNutritionState(state: RideNutritionState) {
        prefs.edit().putString(KEY_NUTRITION_STATE, json.encodeToString(state)).apply()
    }

    fun loadNutritionState(): RideNutritionState? {
        val str = prefs.getString(KEY_NUTRITION_STATE, null) ?: return null
        return try {
            json.decodeFromString(str)
        } catch (e: Exception) {
            Timber.w(e, "Failed to load nutrition state")
            null
        }
    }

    fun clearNutritionState() {
        prefs.edit().remove(KEY_NUTRITION_STATE).apply()
    }

    companion object {
        private const val KEY_FTP_OVERRIDE = "ftp_override"
        private const val KEY_WEIGHT_OVERRIDE = "weight_override"
        private const val KEY_EAT_INTERVAL = "eat_interval"
        private const val KEY_DRINK_INTERVAL = "drink_interval"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_FIT_EXPORT = "fit_export"
        private const val KEY_REMINDER_START = "reminder_start"
        private const val KEY_FOOD_TEMPLATES = "food_templates"
        private const val KEY_NUTRITION_STATE = "nutrition_state"
    }
}
