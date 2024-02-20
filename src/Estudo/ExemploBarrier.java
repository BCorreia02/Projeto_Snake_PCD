package Estudo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ExemploBarrier extends Thread {

    private CyclicBarrier barrier;

    private int result;

    private static int count;

    public ExemploBarrier(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    public void run() {
        try {
            if (Math.random() > 0.5) {
                result = 1;
                count++;
            }
            System.out.println("Task completed, waiting at the barrier");
            barrier.await(); // Waiting at the barrier
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Continue with the rest of the operations...
    }

    // Main class

    public static void main(String[] args) {
        int numOfThreads = 5;
        CyclicBarrier barrier = new CyclicBarrier(numOfThreads, new Runnable() {
            public void run() {
                if (count > 0) {
                    System.out.println("Result: " + count);
                }
            }
        });

        for (int i = 0; i < numOfThreads; i++) {
            new ExemploBarrier(barrier).start();
        }

    }
}
