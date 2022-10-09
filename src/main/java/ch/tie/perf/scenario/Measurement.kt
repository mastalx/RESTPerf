package ch.tie.perf.scenario

class Measurement(val name: String, val id: Long, val timestamp: Long, val duration: Long) : Comparable<Measurement> {

    override fun compareTo(other: Measurement): Int {
        val nameComp = name.compareTo(other.name)
        if (nameComp != 0) {
            return nameComp
        }
        val timeComp = timestamp.compareTo(other.timestamp)
        return if (timeComp != 0) timeComp else duration.compareTo(other.duration)
    }
}