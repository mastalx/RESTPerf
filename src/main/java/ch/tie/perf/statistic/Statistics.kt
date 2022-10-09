package ch.tie.perf.statistic

import ch.tie.perf.scenario.Measurement
import java.util.*
import java.util.concurrent.TimeUnit

class Statistics {
    private val measurements = Collections.synchronizedList(ArrayList<Measurement>())
    fun updateStatistics(name: String, requestId: Long, duration: Long) {
        val measurement = Measurement(name, requestId, System.currentTimeMillis(),
                TimeUnit.NANOSECONDS.toMillis(duration))
        measurements.add(measurement)
    }

    fun getMeasurements(): List<Measurement> {
        return measurements
    }
}