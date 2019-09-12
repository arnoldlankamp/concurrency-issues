package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

/*
b = a    | a.i = 0
         | a.i = 1
x = a.i  | a.i = 2
y = b.i  | a.i = 3
z = a.i  | a.i = 4

result: x < y > z, x = z
(e.g. x = 1, y = 2, z = 1)
*/
public class ForwardSubstitution {
  private static final int ITERATIONS = 100000000;

  public static void main(String[] args) throws InterruptedException {
    State state = new State();

    Job1 job1 = new Job1(state);
    Job2 job2 = new Job2(state);
    Thread[] threads = create(job1, job2);
    start(threads);
    join(threads);

    System.out.println("No problems detected");
  }

  private static class State {
    private final Struct a;
    private final Struct b;

    private static class Struct {
      private int number = 0;
    }

    private State() {
      a = new Struct();
      b = a;
    }
  }

  private static class Job1 implements Runnable {
    private final State state;

    private Job1(State state) {
      this.state = state;
    }

    private static void check(int x, int y, int z) {
      if (y > z) {
        System.err.println("Concurrency issue detected");
        System.err.println("X=" + x + ", Y=" + y + ", Z=" + z);
        System.exit(1);
      }
    }

    private void read() {
      int x = state.a.number;
      int y = state.b.number;
      int z = state.a.number;

      check(x, y, z);
    }

    public void run() {
      for (int i = 0; i < ITERATIONS; i++) {
        read();
      }
    }
  }

  private static class Job2 implements Runnable {
    private final State state;

    private Job2(State state) {
      this.state = state;
    }

    public void run() {
      for (int i = 0; i < ITERATIONS; i++) {
        state.a.number++;
      }
    }
  }
}
