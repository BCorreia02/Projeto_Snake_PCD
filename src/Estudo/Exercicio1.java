package Estudo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exercicio1 {

    BlockingQueue<String> queue;
    Semaphore sem;
    Lock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    int capacity;
    ExecutorService pool;
    int numWorkers;

    public Exercicio1(int numSimultWorkers, int capacity, int numWorkers) {
        this.sem = new Semaphore(numSimultWorkers);
        this.capacity = capacity;
        queue = new ArrayBlockingQueue<>(capacity);
        pool = Executors.newFixedThreadPool(numSimultWorkers);
        this.numWorkers = numWorkers;
    }

    public void start() {
        for (int i = 0; i < numWorkers; i++) {
            new Worker("Put").start();
            new Worker("Take").start();
        }

        pool.shutdown();
    }

    public static void main(String[] args) {
        Exercicio1 exercise = new Exercicio1(5, 10, 10); // Example parameters
        exercise.start();
    }

    public class Worker extends Thread {

        private String job;

        public Worker(String job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    executev1();
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void execute() {
            lock.lock();
            try {
                if (job.equals("Put")) {
                    while (queue.size() == capacity)
                        notFull.await();

                    queue.put("String");
                    System.out.println("PUT");
                    notEmpty.signalAll();

                    Thread.sleep(2000);
                } else {
                    while (queue.size() == 0)
                        notEmpty.await();

                    queue.poll();
                    System.out.println("POLL");
                    notFull.signalAll();
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                lock.unlock();
            } finally {
                lock.unlock();
            }

        }

        public void executev1() {
            try {
                sem.acquire();
                if (job == "Put") {
                    while (queue.size() == capacity)
                        notFull.await();

                    queue.put("String");
                    System.out.println("PUT");
                    notEmpty.signalAll();
                    Thread.sleep(1000);
                } else {
                    while (queue.size() == 0)
                        notEmpty.await();

                    System.out.println("POLL");
                    queue.poll();
                    notFull.signalAll();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {

            } finally {
                sem.release();
            }

        }

    }

}
