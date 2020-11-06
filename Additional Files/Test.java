/*
* Author: Ermias Negash
*
* Overview:
*
* Implementation: Utilized Thread Pool. Though this route is overkill for 2 threads, its just an interesting implementation.
*
* */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
* Class stores Thread functionality to iterate random pair array to calculate arithmetic solutions.
* */
class CrystalThread implements Runnable{
    //Class Variables
    private final ReentrantLock bufferLock;
    private final ArrayList<Double> listOfLists;
    private final String name;

    /*
    * Constructor
    * */
    public CrystalThread(ReentrantLock bufferLock, ArrayList<Double> listOfLists, String name){
        this.bufferLock = bufferLock;
        this.listOfLists = listOfLists;
        this.name = name;
    }

    /*
    * Method: Run method of the CrystalThread class. Handles multiple invocation.
    * parameter(s): None
    * return: None
    * */
    @Override
    public void run() {
        int count = 0;
        int a = 0;
        float successRate = 0;

        Calculator calc = new Calculator(this.name);
        FileManipulation fmanip = new FileManipulation();

        // Threads iterate through random pairs generated.
        for(int i=0; i <= this.listOfLists.size(); i=i+2){
            this.bufferLock.lock();
            try {
                //Calculate the Success Rate = (Thread Number of Successful Calculations / size)
                if(count == 1 && i == this.listOfLists.size()){
                    successRate = successRate/a;
                    fmanip.writeToFile(calc.getName(), successRate);
                    break;
                }
                if(count == 0){
                    System.out.println("\nCalculator " + calc.getName() + ":");
                    count++;
                }
                // Call method to perform arithmetic
                successRate = arithmetic(this.listOfLists.get(i), this.listOfLists.get(i+1), calc, successRate);
                a++;
            } finally {
                this.bufferLock.unlock();
            }
        }
    }

    /*
    * Method handles call to Calculator class
    * parameter(s): x and y are iterated index values in random pair Array. calc is the object of the Calculator class.
    * return: None
    * */
    public float arithmetic(Double x, Double y, Calculator calc, float successRate){
        double temp = calc.add(x, y);
        double value = x+y;

        if(temp == value){
            System.out.println(x + " + " + y + " = " + temp + "\t\t(correct)");
            successRate++;
        } else {
            System.out.println(x + " + " + y + " = " + temp + "\t\t(error)");
        }
        return successRate;
    }
}

/*
* Class stores Methods to manipulate solution.csv file.
* */
class FileManipulation{
    /*
     * Method: Writes to the solution.csv file.
     * parameter(s): cName stores the Name of the Crystal. successRate stores Crystal Success Rate.
     * return: None
     * */
    public void writeToFile(String cName, float successRate){
        File f1 = new File("resource/solution.csv");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(f1.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(cName + "," + successRate + "\n");
            bw.close();
        } catch (IOException e){
            System.out.println("ERROR: ");
            e.printStackTrace();
        } finally {
            try {
                if(fileWriter != null){
                    System.out.println("Attempting to close file writer.");
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Method: Read from the Solution.txt file, output to console file data, and determine better Success Rate.
     * parameter(s): csvFile stores the name of the file located in relative path.
     * return:
    * */
    public void parseFile(String csvFile) {
        String c;
        String DEFAULT_DELIMITER = ",";
        HashMap<String, Float> cSuccessRate = new HashMap<>();

        try(BufferedReader br = new BufferedReader((new FileReader(csvFile)))){
            while((c = br.readLine()) != null){
                String[] line = c.split(DEFAULT_DELIMITER);
                cSuccessRate.put(line[0], Float.parseFloat(line[1]));
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        betterCrystal(cSuccessRate);
    }

    /*
    * Method: Will determine the winner using the parsed data from the solution.csv file.
    * parameter(s): cSuccessRate represents key-value pair of the line values in csv file.
    * return: None
    * */
    public void betterCrystal(HashMap<String, Float> cSuccessRate){
        //Output to Console Crystal # and Success Rate.
        System.out.println("\nCrystal 1: "+ cSuccessRate.get("Crystal 1"));
        System.out.println("Crystal 2: "+ cSuccessRate.get("Crystal 2"));

        // Determines the Winner
        if(cSuccessRate.get("Crystal 1") > cSuccessRate.get("Crystal 2")){
            System.out.println("Crystal 1 is better.");
        } else if (cSuccessRate.get("Crystal 1").equals(cSuccessRate.get("Crystal 2"))) {
            System.out.println("Crystals have same success rate.");
        } else {
            System.out.println("Crystal 2 is better.");
        }
    }

    /*
    * Method: Will delete file, solution.csv, in current directory.
    * parameter: None
    * return: None
    * */
    public void deleteFile(){
        File file = new File("solution.csv");
        if(file.delete()){
            System.out.println("Deleted file: " + file.getName());
        } else {
            System.out.println("Failed to delete file.");
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
