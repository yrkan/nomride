package com.nomride.karoo

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.nomride.engine.CarbBalanceTracker
import com.nomride.glance.QuickLogStatusView
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
class QuickLogStatusDataType(
    private val tracker: CarbBalanceTracker,
    extension: String,
) : DataTypeImpl(extension, "quick-log-status") {
    private val glance = GlanceRemoteViews()

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            tracker.stateFlow.collect { state ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to state.totalEaten),
                        ),
                    ),
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val configJob = CoroutineScope(Dispatchers.IO).launch {
            emitter.onNext(UpdateGraphicConfig(showHeader = false))
        }
        val viewJob = CoroutineScope(Dispatchers.IO).launch {
            if (config.preview) {
                val result = glance.compose(context, DpSize.Unspecified) {
                    QuickLogStatusView(
                        lastIntakeName = "Energy Gel",
                        lastIntakeEmoji = "⚡",
                        lastIntakeTimestampMs = System.currentTimeMillis() - 8 * 60_000,
                        balance = -47.0,
                        burnRateGph = 45.0,
                        viewConfig = config,
                    )
                }
                emitter.updateView(result.remoteViews)
            } else {
                tracker.stateFlow.collect { state ->
                    val result = glance.compose(context, DpSize.Unspecified) {
                        QuickLogStatusView(
                            lastIntakeName = state.lastIntakeName,
                            lastIntakeEmoji = state.lastIntakeEmoji,
                            lastIntakeTimestampMs = state.lastIntakeTimestampMs,
                            balance = state.balance,
                            burnRateGph = state.burnRateGph,
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
