package ch.tie.perf.scenario

class Measurement(val name: String, val id: Long, val timestamp: Long, val duration: Long) : Comparable<Measurement> {

    override fun compareTo(other: Measurement): Int {
        var retVal = name.compareTo(other.name)
        if (retVal != 0) {
            return retVal
        }
        retVal = timestamp.compareTo(other.timestamp)
        return if (retVal != 0) {
            retVal
        } else duration.compareTo(other.duration)
    }
}