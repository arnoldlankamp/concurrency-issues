package concurrency.util;

public class RunnerUtil {

  public static Thread create(Runnable runnable) {
    return new Thread(runnable);
  }

  public static Thread[] create(Runnable runnable, int numberOfThreads) {
    Thread[] threads = new Thread[numberOfThreads];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = create(runnable);
    }
    return threads;
  }

  public static Thread[] create(Runnable... runnables) {
    Thread[] threads = new Thread[runnables.length];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = create(runnables[i]);
    }
    return threads;
  }

  public static void daemonize(Thread... threads) {
    for (Thread thread : threads) {
      thread.setDaemon(true);
    }
  }

  public static void start(Thread... threads) {
    for (Thread thread : threads) {
      thread.start();
    }
  }

  public static void join(Thread... threads) throws InterruptedException {
    for (Thread thread : threads) {
      thread.join();
    }
  }

  public static void join(long timeout, Thread... threads) throws InterruptedException {
    long now = currentTimeMs();
    long deadline = now + timeout;

    int index = 0;
    while (now < deadline && index < threads.length) {
      long timeLeft = deadline - now;
      threads[index++].join(timeLeft);
      now = currentTimeMs();
    }
  }

  private static long currentTimeNs() {
    return System.nanoTime();
  }

  public static long currentTimeMs() {
    return currentTimeNs() / 1000000;
  }

  private static long elapsedTimeNs(long nanoTime) {
    return (currentTimeNs() - nanoTime);
  }

  public static long elapsedTimeMs(long msTime) {
    return elapsedTimeNs(msTime * 1000000) / 1000000;
  }
}
