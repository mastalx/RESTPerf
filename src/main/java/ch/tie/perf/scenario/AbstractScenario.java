package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


public abstract class AbstractScenario implements Scenario {

  private final List<Future<Scenario>> taskList = new ArrayList<>();

  public void addChildTask(Future<Scenario> childTask) {
    taskList.add(childTask);
  }

  @Override
  public List<Future<Scenario>> getSpawnedTasks() {
    return taskList;
  }
}
