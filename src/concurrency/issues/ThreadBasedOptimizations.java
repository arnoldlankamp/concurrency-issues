package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class ThreadBasedOptimizations {

  private static void check(Thread thread1) {
    if (thread1.isAlive()) {
      System.err.println("Concurrency issue detected");
      System.err.println("Job1 still running");
      System.exit(1);
    } else {
      System.out.println("No problems detected");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Job job = new Job();
    Thread thread = create(job);
    start(thread);

    Thread.sleep(1000);

    job.stop();

    join(1000, thread);
    check(thread);
  }

  private static class Job implements Runnable {
    private boolean running;

    private void doStuff() {
      // Whatever
    }

    public void run() {
      running = true;

      while (running) {
        doStuff();
      }
    }

    private void stop() {
      running = false;
    }
  }
}
