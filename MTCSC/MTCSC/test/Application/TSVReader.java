package cn.edu.thu.MTCSC.test.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TSVReader {
    public static void main(String[] args) {
        String filePath = "data/UCR/TRAIN/ArrowHead_TRAIN.tsv";
        
        ArrayList<Integer> labels = new ArrayList<>();
        int typeNum = 0;
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        
        try {
            Scanner scanner = new Scanner(new File(filePath));
        
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                
                String label = parts[0];
                if(!labels.contains(Integer.parseInt(label))){
                    typeNum++;
                }
                labels.add(Integer.parseInt(label));
                ArrayList<Double> rowData = new ArrayList<>();
                for(int i=1; i<parts.length; i++){
                    rowData.add(Double.parseDouble(parts[i]));
                }
                data.add(rowData);
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // 0:max, 1:min
        double[][] MaxMin = new double[typeNum][2];
        for(int i=0; i<typeNum; i++){
            MaxMin[i][0] = Double.MIN_VALUE;
            MaxMin[i][1] = Double.MAX_VALUE;
        }
        for(int i=0; i<data.size(); i++){
            int label = labels.get(i);
            ArrayList<Double> rowData = data.get(i);
            for(Double num : rowData){
                MaxMin[label][0] = MaxMin[label][0] > num ? MaxMin[label][0] : num;
                MaxMin[label][1] = MaxMin[label][1] < num ? MaxMin[label][1] : num;
            }
        }
    }
}