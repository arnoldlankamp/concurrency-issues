package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class OperationAtomicity {
  private static final int NR_OF_THREADS = 2;
  private static final int ITERATIONS = 1000000;

  private static void check(State state) {
    int expectedResult = ITERATIONS * NR_OF_THREADS;
    if (state.getCount() != expectedResult) {
      System.err.println("Concurrency issue detected");
      System.err.println("Expected the counter to be: " + expectedResult + ", but was: " + state.getCount());
      System.exit(1);
    } else {
      System.out.println("No problems detected");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    State state = new State();
    Job job = new Job(state, ITERATIONS);

    Thread[] threads = create(job, NR_OF_THREADS);
    start(threads);
    join(threads);

    check(state);
  }

  private static class State {
    private int counter;

    private void increment() {
      counter++;
    }

    private int getCount() {
      return counter;
    }
  }

  private static class Job implements Runnable {
    private final State state;
    private int iterations;

    private Job(State state, int iterations) {
      this.state = state;
      this.iterations = iterations;
    }

    public void run() {
      for (int i = 0; i < iterations; i++) {
        state.increment();
      }
    }
  }
}
