package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractScenario implements Scenario {

  private final List<Future<Scenario>> taskList = Collections.synchronizedList(new ArrayList<>());

  public void addChildTask(Future<Scenario> childTask) {
    taskList.add(childTask);
  }

  @NotNull
  @Override
  public List<Future<Scenario>> getSpawnedTasks() {
    return taskList;
  }
}
