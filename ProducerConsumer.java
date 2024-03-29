package producerconsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: 
 * Desc: Aplicacion del productor y consumidor
 */
public class ProducerConsumer extends Thread {

    static Queue<Integer> numbers = new LinkedList<>();
    private static final Lock lock = new ReentrantLock();
    static Random random = new Random();
    static int numThreads = 3;
    static int[] sums;

    static void ProduceNumbers() {
        int numToEnqueue = 0;
        
        for (int i = 0; i < 10; i++) {
            numToEnqueue = random.nextInt(10);
            System.out.println("Hilo del productor: agregando " + numToEnqueue + " productos a la cola.");
            numbers.add(numToEnqueue);

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Ocurrió una excepción: " + e.getMessage());
            }
        }
    }

    static void sumNumbers() {
        Instant startTime = Instant.now();

        int mySum = 0;
        int numToSum = 0;

        while ((Duration.between(startTime, Instant.now())).getSeconds() < 11) {
            lock.lock();
            try {
                numToSum = -1;
                if (numbers.peek() != null) {
                    numToSum = numbers.poll();                    
                }
            } finally {
                lock.unlock();
            }
            if (numToSum != -1) {
                mySum += numToSum;
                System.out.println("Hilo del consumidor #" + Thread.currentThread().getName()
                        + ": agregando " + numToSum + " al total.");
            }
        }
        int CurrentThread = Integer.parseInt(Thread.currentThread().getName());
        sums[CurrentThread] = mySum;
    }

    static boolean isNum = true;

    public static void main(String[] args) {
        BufferedReader userInput = new BufferedReader(
                new InputStreamReader(System.in));
        while (isNum) {
            try {
                System.out.print("Ingrese el número de hilos: ");
                numThreads = Integer.parseInt(userInput.readLine());
                isNum = false;
            } catch (IOException | NumberFormatException ex) {
                isNum = true;
            }
        }
        sums = new int[numThreads];

        Thread producingThread = new Thread(() -> {
            ProduceNumbers();
        });
        producingThread.start();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                sumNumbers();
            }, String.valueOf(i));
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                System.out.println("Ocurrió una excepción: " + ex.getMessage());
            }
        }
        int totalSum = 0;
        for (int i = 0; i < numThreads; i++) {
            totalSum += sums[i];
        }

        System.out.println("Se termino de agregar. El total es de " + totalSum + " productos");
        System.out.println("\n\n\t[!]Finalizo el programa.\n\n");
    }
}
