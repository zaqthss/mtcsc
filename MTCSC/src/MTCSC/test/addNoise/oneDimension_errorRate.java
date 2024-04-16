package MTCSC.test.addNoise;

import java.text.DecimalFormat;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class oneDimension_errorRate {
    public static void main(String[] args) {
        Assist assist = new Assist();  
        String inputFileName = "ILD/tem43k.data"; // 43k temperature
        // String directory = "tem";
        String directory = "tem_noLabel";
        
        
        

        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05+0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            System.out.println("Dirty rate is " + drate);
            int expTime = 10;
            int size = 5000;
            for(int j=0; j<expTime; j++,seed++){
                TimeSeries dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                String writefilename = "one/"+directory+"/errorRate/"+String.valueOf(drate)+"_"+String.valueOf(seed)+".csv";
                assist.saveDataFromNoise(writefilename, dirtySeries);
            }
        }
    }
}