package com.nomride.karoo

import com.nomride.engine.CarbBalanceTracker
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarbsBurnedDataType(
    private val tracker: CarbBalanceTracker,
    extension: String,
) : DataTypeImpl(extension, "carbs-burned") {
    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            tracker.stateFlow.collect { state ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to kotlin.math.round(state.totalBurned)),
                        ),
                    ),
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }
}
