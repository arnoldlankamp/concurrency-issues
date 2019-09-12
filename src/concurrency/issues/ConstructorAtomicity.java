package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.daemonize;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class ConstructorAtomicity {
  private static SomeObject ref = new SomeObject();

  public static void main(String[] args) throws InterruptedException {
    Job1 job1 = new Job1();
    Job2 job2 = new Job2();
    Thread[] threads = create(job1, job2);
    daemonize(threads);
    start(threads);

    join(5000, threads);

    System.out.println("No problems detected");
  }

  private static class SomeObject {
    private boolean bool;

    private SomeObject() {
      bool = true;
    }

    public boolean isTrue() {
      return bool;
    }
  }

  private static class Job1 implements Runnable {
    private void create() {
      ref = new SomeObject();
    }

    public void run() {
      while (true) {
        create();
      }
    }
  }

  private static class Job2 implements Runnable {
    private void check() {
      if (!ref.isTrue()) {
        System.out.println("Concurrency issue detected");
        System.out.println("Expected 'isTrue', to return true");
      }
    }

    public void run() {
      while (true) {
        check();
      }
    }
  }
}
