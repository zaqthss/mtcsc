package MTCSC.experiment.rms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CAEM_tao_dataSize_together {

    public static void main(String[] args) {
        String csvFile1 = "noise/MultiDimension/CAE-M_tao_results/tao_test.csv";
        String csvFile2 = "noise/MultiDimension/CAE-M_tao_results/dataSize/together/renorm-pre_";

        double[] totalRMS = new double[10];
        for(int i=0; i<10; i++){
            int seed = 1;
            
            int expTime = 10;
            int size = 5000*(i+1);
            System.out.print("Data size is " + size + "  ");

            for(int j=0; j<expTime; j++,seed++){
                csvFile2 = "noise/MultiDimension/CAE-M_tao_results/dataSize/together/renorm-pre_";
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
        double x_cost = 0, y_cost = 0, z_cost = 0;
        try (BufferedReader reader1 = new BufferedReader(new FileReader(csvFile1));
             BufferedReader reader2 = new BufferedReader(new FileReader(csvFile2))) {
            String line1, line2;

            int i=0;
            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null && i<size) {
                String[] parts1 = line1.split(",");
                String[] parts2 = line2.split(",");

                double value1 = Double.parseDouble(parts1[1])*10.0; 
                double value2 = Double.parseDouble(parts1[2]); 
                double value3 = Double.parseDouble(parts1[3])*40.0; 

                double value11 = Double.parseDouble(parts2[0]); 
                double value22 = Double.parseDouble(parts2[1]); 
                double value33 = Double.parseDouble(parts2[2]); 

                x_cost = Math.pow(value1 - value11, 2);
                y_cost = Math.pow(value2 - value22, 2);
                z_cost = Math.pow(value3 - value33, 2);
                cost = cost + x_cost + y_cost + z_cost;
                length++;
                i++;
            }
        }

        // Calculate RMS
        cost = cost / length;
        return Math.sqrt(cost);
    }
}
