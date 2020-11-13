import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Class stores Thread functionality to iterate random pair array to calculate arithmetic solutions.
 * */
public class CrystalThread implements Runnable{
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
