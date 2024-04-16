package MTCSC.experiment.rms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class tranAD_tem_dataSize {

    public static void main(String[] args) {
        String csvFile1 = "noise/one/TranAD_tem_results/45000.csv";
        String csvFile2 = "noise/one/TranAD_tem_results/dataSize/renorm-pre_";

        double[] totalRMS = new double[10];
        for(int i=0; i<9; i++){
            int seed = 1;
            
            int expTime = 10;
            int size = 5000*(i+1);
            System.out.print("Data size is " + size + "  ");

            for(int j=0; j<expTime; j++,seed++){
                csvFile2 = "noise/one/TranAD_tem_results/dataSize/renorm-pre_";
                csvFile2 += String.valueOf(size) + "_" + String.valueOf(seed) + ".csv";
                try {
                    double rms = calculateRMS(csvFile1, csvFile2, size);
                    totalRMS[i] += rms;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            totalRMS[i] /= expTime;
            System.out.println("RMS is " + totalRMS[i]);
        }
    }

    private static double calculateRMS(String csvFile1, String csvFile2, int size) throws IOException {
        double cost = 0;
        int length = 0;

        try (BufferedReader reader1 = new BufferedReader(new FileReader(csvFile1));
             BufferedReader reader2 = new BufferedReader(new FileReader(csvFile2))) {
            String line1, line2;

            // Skip header lines if present
            reader2.readLine(); // Skipping header of file2
            
            int i=0;
            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null && i<size) {
                String[] parts1 = line1.split(",");
                String[] parts2 = line2.split(",");

                double value1 = Double.parseDouble(parts1[2]); // Third column of file1
                double value2 = Double.parseDouble(parts2[0]); // Third column of file2

                double delta = value1 - value2;
                cost += delta * delta;
                length++;
            }
        }


        // Calculate RMS
        cost = cost / length;
        return Math.sqrt(cost);
    }
}
