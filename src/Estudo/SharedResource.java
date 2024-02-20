package Estudo;

import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class SharedResource {
    // This method represents the shared resource
    public void use() {
        System.out.println(Thread.currentThread().getName() + " is using the shared resource");
        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static public class Worker implements Runnable {
        private final SharedResource resource;
        private final Semaphore semaphore;
        private final CyclicBarrier barrier;

        public Worker(SharedResource resource, Semaphore semaphore, CyclicBarrier barrier) {
            this.resource = resource;
            this.semaphore = semaphore;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    semaphore.acquire(); // Acquire a permit before accessing the resource
                    resource.use();
                    semaphore.release(); // Release the permit after using the resource
                }
                barrier.await(); // Wait at the barrier after completing the work
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int totalThreads = 10;
        SharedResource resource = new SharedResource();
        Semaphore semaphore = new Semaphore(3); // Semaphore with 3 permits
        CyclicBarrier barrier = new CyclicBarrier(totalThreads,
                () -> System.out.println("All threads have finished their work"));

        for (int i = 0; i < totalThreads; i++) {
            new Thread(new Worker(resource, semaphore, barrier), "Thread-" + i).start();
        }
    }
}
