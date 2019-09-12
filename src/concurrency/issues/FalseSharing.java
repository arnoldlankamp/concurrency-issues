package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.currentTimeMs;
import static concurrency.util.RunnerUtil.elapsedTimeMs;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class FalseSharing {
  private static final int ITERATIONS = 1 << 28; // 256M
  private static final int NUMBER_OF_THREADS = 1;

  public static void main(String[] args) throws InterruptedException {
    Job[] jobs = new Job[NUMBER_OF_THREADS];
    for (int i = 0; i < jobs.length; i++) {
      jobs[i] = new Job(ITERATIONS / NUMBER_OF_THREADS);
    }

    Thread[] threads = create(jobs);

    long startTime = currentTimeMs();

    start(threads);
    join(threads);

    long elapsedTime = elapsedTimeMs(startTime);
    System.out.println("duration = " + elapsedTime + "ms");
  }

  @SuppressWarnings("all")
  private static class Job implements Runnable {
    // Comment some / all to the lines below in / out to see if it makes a difference
    // private volatile long pl1 = 1;
    // private volatile long pl2 = 1;
    // private volatile long pl3 = 1;
    // private volatile long pl4 = 1;
    // private volatile long pl5 = 1;
    // private volatile long pl6 = 1;
    // private volatile long pl7 = 1;
    // private volatile long pl8 = 1;
    private volatile int count; // The volatile keyword here serves no purpose other then to prevent the JVM from
                                // optimizing away the loop below, so don't remove it, otherwise the example doesn't
                                // function properly

    private Job(int count) {
      this.count = count;
    }

    public void run() {
      while (count > 0) {
        count--;
      }
    }
  }
}
