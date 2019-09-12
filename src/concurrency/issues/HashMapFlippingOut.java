package concurrency.issues;

import static concurrency.util.RunnerUtil.create;
import static concurrency.util.RunnerUtil.daemonize;
import static concurrency.util.RunnerUtil.join;
import static concurrency.util.RunnerUtil.start;

public class HashMapFlippingOut {
  private static final int ITERATIONS = 1000000;
  private static final int NR_OF_THREADS = 4;

  private static final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

  private static void dumpStack(Thread thread) {
    if (thread.isAlive()) {
      System.err.println(thread.getName() + ":");

      StackTraceElement[] stackTrace = thread.getStackTrace();
      for (StackTraceElement aStackTrace : stackTrace) {
        System.err.println(aStackTrace);
      }
      System.err.println();
    }
  }

  private static void check(Thread[] threads) {
    boolean hangingThreads = false;

    for (Thread thread : threads) {
      if (thread.isAlive()) {
        hangingThreads = true;
        dumpStack(thread);
      }
    }

    if (hangingThreads) {
      System.err.println("Concurrency issue detected");
      System.err.println("See stacktrace(s) of hanging thread(s) above");
      System.exit(1);
    } else {
      System.out.println("No problems detected");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Job job = new Job();

    Thread[] threads = create(job, NR_OF_THREADS);
    daemonize(threads);
    start(threads);

    join(2000, threads);

    check(threads);
  }

  private static class Job implements Runnable {

    public void run() {
      for (int i = 0; i < ITERATIONS; i++) {
        map.put(i, i);
      }
    }
  }

  @SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
  private static class HashMap<K, V> {
    private Entry<K, V>[] entries;

    private int hashMask;

    private int threshold;
    private int load;

    private HashMap() {
      int nrOfEntries = 8;
      hashMask = nrOfEntries - 1;

      entries = (Entry<K, V>[]) new Entry[nrOfEntries];

      threshold = nrOfEntries;
      load = 0;
    }

    public int size() {
      return load;
    }

    private void rehash() {
      int currentSize = entries.length;
      int newNrOfEntries = currentSize << 1;
      int newHashMask = newNrOfEntries - 1;

      Entry<K, V>[] newEntries = (Entry<K, V>[]) new Entry[newNrOfEntries];

      for (int i = currentSize - 1; i >= 0; --i) {
        Entry<K, V> e = entries[i];
        while (e != null) {
          int position = e.hash & newHashMask;

          Entry<K, V> next = e.next;
          if (position == i) {
            e.next = newEntries[i];
            newEntries[i] = e;
          } else {
            e.next = newEntries[i | currentSize];
            newEntries[i | currentSize] = e;
          }
          e = next;
        }
      }

      threshold <<= 1;
      entries = newEntries;
      hashMask = newHashMask;
    }

    private void ensureCapacity() {
      if (load > threshold) {
        rehash();
      }
    }

    public V put(K key, V value) {
      ensureCapacity();

      int hash = key.hashCode();
      int position = hash & hashMask;

      Entry<K, V> currentStartEntry = entries[position];
      if (currentStartEntry != null) {
        Entry<K, V> entry = currentStartEntry;
        do {
          if (hash == entry.hash && entry.key.equals(key)) {
            V oldValue = entry.value;
            entry.value = value;
            return oldValue;
          }
        } while ((entry = entry.next) != null);
      }

      entries[position] = new Entry<K, V>(key, value, hash, currentStartEntry);
      load++;

      return null;
    }

    public V get(K key) {
      int hash = key.hashCode();
      int position = hash & hashMask;

      Entry<K, V> entry = entries[position];
      while (entry != null) {
        if (hash == entry.hash && key.equals(entry.key)) return entry.value;

        entry = entry.next;
      }

      return null;
    }

    private static class Entry<K, V> {
      private final int hash;
      private final K key;
      private V value;
      private Entry<K, V> next;

      private Entry(K key, V value, int hash, Entry<K, V> next) {
        this.key = key;
        this.value = value;
        this.hash = hash;
        this.next = next;
      }
    }
  }
}
