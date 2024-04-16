package cn.edu.thu.MTCSC.test.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TSVReader {
    public static void main(String[] args) {
        // 指定.tsv文件路径
        String filePath = "data/UCR/TRAIN/ArrowHead_TRAIN.tsv";
        
        // 用于存储label的数组
        ArrayList<Integer> labels = new ArrayList<>();
        int typeNum = 0;
        // 用于存储数据的ArrayList
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        
        try {
            // 创建Scanner对象来读取文件
            Scanner scanner = new Scanner(new File(filePath));
            
            // 逐行读取文件内容
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // 使用制表符分割每行
                String[] parts = line.split("\t");
                
                // 第一个部分是label
                String label = parts[0];
                if(!labels.contains(Integer.parseInt(label))){
                    typeNum++;
                }
                labels.add(Integer.parseInt(label));
                // 其余部分是数据，使用空格分割
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