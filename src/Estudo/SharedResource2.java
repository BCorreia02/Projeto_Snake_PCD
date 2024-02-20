package Estudo;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class SharedResource2 {
    public void use() {
        System.out.println(Thread.currentThread().getName() + " is using the shared resource");
        try {
            Thread.sleep(100); // Simulate some work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static public class Worker implements Runnable {
        private final SharedResource2 resource;
        private final ReentrantLock lock;
        private final Condition accessCondition;
        private final CyclicBarrier barrier;
        private static int activeAccesses = 0;
        private static final int MAX_CONCURRENT_ACCESSES = 3;

        public Worker(SharedResource2 resource, ReentrantLock lock, Condition accessCondition, CyclicBarrier barrier) {
            this.resource = resource;
            this.lock = lock;
            this.accessCondition = accessCondition;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    lock.lock();
                    try {
                        while (activeAccesses == MAX_CONCURRENT_ACCESSES) {
                            accessCondition.await();
                        }
                        activeAccesses++;
                        resource.use();
                        activeAccesses--;
                        accessCondition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                }
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int totalThreads = 10;
        SharedResource2 resource = new SharedResource2();
        ReentrantLock lock = new ReentrantLock();
        Condition accessCondition = lock.newCondition();
        CyclicBarrier barrier = new CyclicBarrier(totalThreads,
                () -> System.out.println("All threads have finished their work"));

        for (int i = 0; i < totalThreads; i++) {
            new Thread(new Worker(resource, lock, accessCondition, barrier), "Thread-" + i).start();
        }
    }
}
