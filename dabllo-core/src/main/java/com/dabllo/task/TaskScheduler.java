package com.dabllo.task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.util.NamedThreadFactory;

/**
 * @author mixueqiang
 * @since Oct 8, 2013
 */
public class TaskScheduler {
  private static final Log LOG = LogFactory.getLog(TaskScheduler.class);
  private static final Set<String> tasks = new HashSet<String>();

  public static void register(String taskName, Runnable runnable, int initialDelayInSeconds, int delayInSeconds) {
    synchronized (tasks) {
      if (tasks.contains(taskName)) {
        LOG.warn("Task " + taskName + " registered failed: already existing task.");
        return;

      } else {
        tasks.add(taskName);
      }
    }

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(
        taskName + "Processor", true));
    executorService.scheduleWithFixedDelay(runnable, initialDelayInSeconds, delayInSeconds, TimeUnit.SECONDS);
    LOG.info("Task " + taskName + " registered OK.");
  }

}
