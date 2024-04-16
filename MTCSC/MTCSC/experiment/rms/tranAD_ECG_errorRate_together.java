package cn.edu.thu.MTCSC.experiment.rms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class tranAD_ECG_errorRate_together {

    public static void main(String[] args) {
        String csvFile1 = "noise/ECG/ECG_test.csv";
        String csvFile2 = "noise/ECG/TranAD_ECG_results/renorm-pre_";

        double[] totalRMS = new double[10];
        for(int i=0; i<10; i++){
            int seed = 1;
            
            double drate= 0.05+0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            int expTime = 10;
            int size = 5000;
            System.out.print("Dirty rate is " + drate + "  ");

            for(int j=0; j<expTime; j++,seed++){
                csvFile2 = "noise/ECG/TranAD_ECG_results/renorm-pre_";
                csvFile2 += String.valueOf(drate) + "_" + String.valueOf(seed) + ".csv";
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

            int i=0;
            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null && i<size) {
                String[] parts1 = line1.split(",");
                String[] parts2 = line2.split(",");

                for(int j=0; j<32; j++){
                    double value1 = Double.parseDouble(parts1[j+1]);
                    double value2 = Double.parseDouble(parts2[j]); 
                    cost += Math.pow(value1 - value2, 2);
                }

                length++;
                i++;
            }
        }

        // Calculate RMS
        cost = cost / length;
        return Math.sqrt(cost);
    }
}
