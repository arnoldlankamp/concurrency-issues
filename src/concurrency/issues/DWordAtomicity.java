package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class DWordAtomicity {
  private static final int ITERATIONS = 1000000;

  public static void main(String[] args) throws InterruptedException {
    State state = new State();

    Job[] jobs = new Job[8];
    for (int id = 0; id < jobs.length; id++) {
      jobs[id] = new Job(state, id + 1);
    }
    Thread[] threads = create(jobs);
    start(threads);
    join(threads);

    System.out.println("No problems detected");
  }

  private static class State {
    private long global;

    private static void validate(long theLong) {
      long high = theLong >>> 32;
      long low = 0xffffffffL & theLong;

      if (high != low) {
        System.err.println("Concurrency issue detected. Read: 0x" + Long.toHexString(theLong) + "L");
        System.exit(1);
      }
    }

    public void setGlobal(long id) {
      global = id;

      validate(global);
    }
  }

  private static class Job implements Runnable {
    private final State state;
    private final long id;

    private Job(State state, long number) {
      this.state = state;
      this.id = (number << 32) | number;

      System.out.println("Created job with id: 0x" + Long.toHexString(id) + "L");
    }

    public void run() {
      for (int i = 0; i < ITERATIONS; i++) {
        state.setGlobal(id);
      }
    }
  }
}
