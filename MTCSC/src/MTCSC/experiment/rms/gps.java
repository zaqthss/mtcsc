package MTCSC.experiment.rms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class gps {

    public static void main(String[] args) {
        String csvFile1 = "noise/gps/walk_final_xy-e4.csv";
        // String csvFile2 = "noise/gps/TranAD.csv";
        String csvFile2 = "noise/gps/CAE-M.csv";

        double rms = 0;
        try {
            rms = calculateRMS(csvFile1, csvFile2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("RMS is " + rms);
    }

    private static double calculateRMS(String csvFile1, String csvFile2) throws IOException {
        double cost = 0;
        int length = 0;
        double x_cost = 0, y_cost = 0;
        try (BufferedReader reader1 = new BufferedReader(new FileReader(csvFile1));
             BufferedReader reader2 = new BufferedReader(new FileReader(csvFile2))) {
            String line1, line2;

            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
                String[] parts1 = line1.split(",");
                String[] parts2 = line2.split(",");

                double value1 = Double.parseDouble(parts1[1]); 
                double value2 = Double.parseDouble(parts1[2]); 

                double value11 = Double.parseDouble(parts2[0]); 
                double value22 = Double.parseDouble(parts2[1]); 

                x_cost = Math.pow(value1 - value11, 2);
                y_cost = Math.pow(value2 - value22, 2);
                cost = cost + x_cost + y_cost;
                length++;
            }
        }

        // Calculate RMS
        cost = cost / length;
        return Math.sqrt(cost);
    }
}
