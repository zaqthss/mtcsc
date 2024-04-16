package cn.edu.thu.MTCSC.test.addNoise;

import java.text.DecimalFormat;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Assist;

// 一维实验
public class oneDimension_dataSize {
    public static void main(String[] args) {
        Assist assist = new Assist();  
        // String inputFileName = "ILD/tem43k.data"; // 43k的温度
        // // String directory = "tem";
        // String directory = "tem_noLabel";

        
        String inputFileName = "ILD/hum43k.data"; // 43k的湿度
        // String directory = "hum";
        String directory = "hum_noLabel";

        
        for(int i=0; i<9; i++){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            
            int expTime = 10;
            int size = 5000*(i+1);
            System.out.println("Data Size is " + size);
            for(int j=0; j<expTime; j++,seed++){
                TimeSeries dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                String writefilename = "one/"+directory+"/dataSize/"+String.valueOf(size)+"_"+String.valueOf(seed)+".csv";
                assist.saveDataFromNoise(writefilename, dirtySeries);
            }
        }
    }
}
