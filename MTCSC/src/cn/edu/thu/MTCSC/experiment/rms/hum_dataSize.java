package cn.edu.thu.MTCSC.experiment.rms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class hum_dataSize {

    public static void main(String[] args) {
        String csvFile1 = "noise/one/FCVAE_pre_hum_result/dataSize/";
        String csvFile2 = "noise/one/FCVAE_pre_hum_result/dataSize/";

        double[] totalRMS = new double[9];
        for(int i=0; i<9; i++){
            int seed = 1;
            
            int expTime = 10;
            int size = 5000*(i+1);
            System.out.println("Data size is " + size);
            csvFile1 = "noise/one/FCVAE_pre_hum_result/dataSize/";
            csvFile1 += String.valueOf(size) + ".csv";
            for(int j=0; j<expTime; j++,seed++){
                csvFile2 = "noise/one/FCVAE_pre_hum_result/dataSize/";
                csvFile2 += String.valueOf(size) + "_" + String.valueOf(seed) + ".csv";
                try {
                    double rms = calculateRMS(csvFile1, csvFile2);
                    totalRMS[i] += rms;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            totalRMS[i] /= expTime;
            System.out.println("RMS is " + totalRMS[i]);
        }
    }

    private static double calculateRMS(String csvFile1, String csvFile2) throws IOException {
        double cost = 0;
        int length = 0;

        try (BufferedReader reader1 = new BufferedReader(new FileReader(csvFile1));
             BufferedReader reader2 = new BufferedReader(new FileReader(csvFile2))) {
            String line1, line2;

            // Skip header lines if present
            reader2.readLine(); // Skipping header of file2

            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
                String[] parts1 = line1.split(",");
                String[] parts2 = line2.split(",");

                double value1 = Double.parseDouble(parts1[2]); // Third column of file1
                double value2 = Double.parseDouble(parts2[2]); // Third column of file2

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
