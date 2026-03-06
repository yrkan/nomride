package com.nomride.karoo

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.nomride.engine.CarbBalanceTracker
import com.nomride.glance.HydrationView
import com.nomride.model.IntakeEntry
import com.nomride.model.RideNutritionState
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class HydrationDataType(
    private val tracker: CarbBalanceTracker,
    extension: String,
) : DataTypeImpl(extension, "hydration") {
    private val glance = GlanceRemoteViews()

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            tracker.stateFlow.collect { state ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to state.totalWaterMl),
                        ),
                    ),
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    private fun computeMlPerHour(state: RideNutritionState): Double {
        val waterEntries = state.intakeLog.filter { it.type == IntakeEntry.IntakeType.WATER }
        if (waterEntries.isEmpty() || state.totalWaterMl <= 0) return 0.0
        val firstTimestamp = waterEntries.minOf { it.timestampMs }
        val elapsedMs = System.currentTimeMillis() - firstTimestamp
        if (elapsedMs < 60_000) return 0.0
        val hours = elapsedMs / 3_600_000.0
        return state.totalWaterMl / hours
    }

    private fun computeSipCount(state: RideNutritionState): Int {
        return state.intakeLog.count { it.type == IntakeEntry.IntakeType.WATER }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val configJob = CoroutineScope(Dispatchers.IO).launch {
            emitter.onNext(UpdateGraphicConfig(showHeader = false))
        }
        val viewJob = CoroutineScope(Dispatchers.IO).launch {
            if (config.preview) {
                val result = glance.compose(context, DpSize.Unspecified) {
                    HydrationView(
                        totalWaterMl = 750.0,
                        mlPerHour = 500.0,
                        sipCount = 3,
                        viewConfig = config,
                    )
                }
                emitter.updateView(result.remoteViews)
            } else {
                tracker.stateFlow.collect { state ->
                    val result = glance.compose(context, DpSize.Unspecified) {
                        HydrationView(
                            totalWaterMl = state.totalWaterMl,
                            mlPerHour = computeMlPerHour(state),
                            sipCount = computeSipCount(state),
                            viewConfig = config,
                        )
                    }
                    emitter.updateView(result.remoteViews)
                }
            }
        }
        emitter.setCancellable {
            configJob.cancel()
            viewJob.cancel()
        }
    }
}
