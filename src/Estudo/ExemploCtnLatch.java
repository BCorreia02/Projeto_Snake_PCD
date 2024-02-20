package Estudo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class ExemploCtnLatch extends Thread {

    static CountDownLatch ctn = new CountDownLatch(5);
    private static Semaphore sem = new Semaphore(3);
    static List<String> s = new ArrayList<>();

    public ExemploCtnLatch() {
    }

    public void run() {
        try {
            sem.acquire();
            if (Math.random() > 0.5) {
                s.add("item");
                System.out.println("Task completed: Thread " + Thread.currentThread().getId());
            }

            ctn.countDown(); // Waiting at the barrier
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sem.release();
        }

        // Continue with the rest of the operations...
    }

    // Main class

    public static void main(String[] args) {
        int numOfThreads = 5;

        for (int i = 0; i < numOfThreads; i++) {
            new ExemploCtnLatch().start();
        }
        try {
            ctn.await();
            System.out.println("All tasks finished, List: " + s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
