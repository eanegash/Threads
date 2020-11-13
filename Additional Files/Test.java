/*
* Author: Ermias Negash
*
* Overview:
*
* Implementation: Utilized Thread Pool. Though this route is overkill for 2 threads, its just an interesting implementation.
*
* */

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

    // Variables used to calculate number and range of random pairs
    private static final int size = 20;
    private static final int rangeMax = 20;
    private static final int rangeMin= 0;

    private static final String fileName = "solution.csv";

    //Variables used when generating two instances of calculators
    private static final String n1 = "Crystal 1";
    private static final String n2 = "Crystal 2";

    public static void main(String[] args) {
        // Generate instance FileManipulation class
        FileManipulation manipulateFile = new FileManipulation();
        //Delete File if in directory.
        manipulateFile.deleteFile();

        ReentrantLock bufferLock = new ReentrantLock();
        //ArrayList(s) store 20 pairs of random numbers
        ArrayList<Double> listOfLists = new ArrayList<>(size);

        //Generate instance of the Test class.
        Test testing = new Test();

        // Object calls method to create Array
        testing.createArrayList(listOfLists);

        //Object calls method to create and execute threads.
        testing.threadPool(bufferLock, listOfLists);
    }

    /*
    * Method: Create and execute threads. Once tasks completed terminates threads (shutdown service).
    * parameter(s): executorService
    * return None
    * */
    public void threadPool(ReentrantLock bufferLock, ArrayList<Double> listOfLists) {
        // Creation of a fixed thread poll, utilizing ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CrystalThread thread1 = new CrystalThread(bufferLock, listOfLists, n1);
        CrystalThread thread2 = new CrystalThread(bufferLock, listOfLists, n2);

        executorService.execute(thread1);
        executorService.execute(thread2);

        // Initiate orderly shutdown. Waits for executing tasks to complete before attempting to stop tasks.
        executorService.shutdown();

        // Once we've determined executor and thread tasks have completed begin file manipulation.
        while(true){
            if(executorService.isTerminated() && executorService.isShutdown()){
                // Generate instance FileManipulation class
                FileManipulation handleFile = new FileManipulation();
                //Parse data from File: solution.csv
                handleFile.parseFile(fileName);
                break;
            }
        }

    }

    /*
    * Method creates and stores the random pairs of numbers in a Array.
    * Parameter(s): None
    * Return: None
    * */
    public void createArrayList(ArrayList<Double> listOfLists) {
        for (int i = 0; i < size; i++) {
            listOfLists.add((rangeMin + (rangeMax - rangeMin) * Math.random()));
        }
    }
}



/*
 * REVIEW: Parallel Testing and automated testing. Does this relate to testing performed
 * across Browser Stack and App (Mobile)? Does Applitools application create multiple tests in
 * parallel across various browsers/devices? How does the tool utilize threading? Synchronization?
 * ArrayBlockQueue? Pool Threads? MemCache/Listeners? Thread LoadBalancer? Submit method utilizing a
 * callable object of type future (unlikely if utilizing UI)?
 * */
