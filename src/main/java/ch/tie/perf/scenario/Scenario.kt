package ch.tie.perf.scenario

import java.util.concurrent.Callable
import java.util.concurrent.Future

interface Scenario : Callable<Scenario> {
    val spawnedTasks: List<Future<Scenario>>
}