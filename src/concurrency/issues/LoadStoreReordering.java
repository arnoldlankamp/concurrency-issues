package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

/*
a = 0     |  b = 0
x[a] = b  |  y[b] = a
a = 1     |  b = 1
x[a] = b  |  y[b] = a

result: x[1] == 0 && y[1] == 0
*/
public class LoadStoreReordering {
  private static final int ITERATIONS = 1000000;

  private static void check(State state) {
    for (int a = 0; a < state.x.length; a++) {
      int b = state.x[a];
      int nextB = b + 1;
      if (nextB < state.y.length) {
        int currentOrFollowingA = state.y[nextB];
        if (currentOrFollowingA < a) {
          System.err.println("Concurrency issue detected");
          System.err.println(currentOrFollowingA + " < " + a);
          System.exit(1);
        }
      }
    }

    System.out.println("No problems detected");
  }

  public static void main(String[] args) throws InterruptedException {
    State state = new State();

    Job1 job1 = new Job1(state);
    Job2 job2 = new Job2(state);
    Thread[] threads = create(job1, job2);
    start(threads);

    join(threads);

    check(state);
  }

  private static class State {
    private final int[] x = new int[ITERATIONS];
    private final int[] y = new int[ITERATIONS];

    private int a;
    private int b;
  }

  private static class Job1 implements Runnable {
    private final State state;

    private Job1(State state) {
      this.state = state;
    }

    public void run() {
      for (int i = 0; i < state.x.length; i++) {
        state.a = i;
        state.x[i] = state.b;
      }
    }
  }

  private static class Job2 implements Runnable {
    private final State state;

    private Job2(State state) {
      this.state = state;
    }

    public void run() {
      for (int i = 0; i < state.y.length; i++) {
        state.b = i;
        state.y[i] = state.a;
      }
    }
  }
}
