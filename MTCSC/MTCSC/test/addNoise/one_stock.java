package cn.edu.thu.MTCSC.test.addNoise;

import java.text.DecimalFormat;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Assist;

public class one_stock {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "STOCK/stock12k.data";
        String directory = "stock";
        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05+0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            
            System.out.println("Dirty rate is " + drate);
            int expTime = 10;
            for(int j=0; j<expTime; j++,seed++){
                TimeSeries dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                String writefilename = "one/"+directory+"/errorRate/"+String.valueOf(drate)+"_"+String.valueOf(seed)+".csv";
                assist.saveDataFromNoise(writefilename, dirtySeries);
            }
        }
    }
}