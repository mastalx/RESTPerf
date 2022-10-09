package ch.tie.perf.statistic

import ch.tie.perf.scenario.Measurement
import ch.tie.perf.scenario.Scenario
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.stream.Collectors

val filenamePrefixFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
private val LOGGER = LogManager.getLogger(StatisticsCollector::class.java)

class StatisticsCollector(private val stats: Statistics) {
    fun waitForEndAndPrintStats(tasks: List<Future<Scenario>>, experiment: String) {
        val fileNamePrefix = LocalDateTime.now().format(filenamePrefixFormat) + "_" + experiment + "_"
        waitForEndOfTasks(tasks)
        printStatistics(fileNamePrefix)
    }

    private fun waitForEndOfTasks(tasks: List<Future<Scenario>>) {
        val queue: Deque<Future<Scenario>> = LinkedList(tasks)
        while (!queue.isEmpty()) {
            val task = queue.pollFirst()
            try {
                val scenario = task.get()
                val spawnedTasks = scenario.spawnedTasks
                queue.addAll(spawnedTasks)
            } catch (e: InterruptedException) {
                LOGGER.error("error while getting task result", e)
            } catch (e: ExecutionException) {
                LOGGER.error("error while getting task result", e)
            }
        }
    }

    private fun printStatistics(prefix: String) {
        stats.getMeasurements()
                .stream()
                .collect(Collectors.groupingBy(Measurement::name))
                .forEach { (name: String, measurements: List<Measurement>) ->
                    val path = Paths.get("$prefix$name.csv")
                    try {
                        Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND,
                                StandardOpenOption.CREATE).use { writer ->
                            PrintWriter(writer).use { pw ->
                                measurements.stream()
                                        .sorted()
                                        .map { measurement: Measurement ->
                                            (measurement.name + ";" + measurement.id + ";" + measurement.timestamp
                                                    + ";" + measurement.duration)
                                        }
                                        .forEach { s: String? -> pw.println(s) }
                            }
                        }
                    } catch (ioe: IOException) {
                        LOGGER.error("cannot write statistics: ", ioe)
                    }
                }
    }
}