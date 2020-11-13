import java.io.*;
import java.util.HashMap;

/*
 * Class stores Methods to manipulate solution.csv file.
 * */
public class FileManipulation {
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
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Method: Read from the Solution.txt file, output to console file data, and determine better Success Rate.
     * Uses Try and Resources rather than Try and Finally.
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
