package MTCSC.test.addNoise;

import java.text.DecimalFormat;
import MTCSC.entity.TimeSeries2;
import MTCSC.util.Assist;

public class two_errorRate_separate {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "ILD/temhum43k.data";
        
        // Normalization or not
        boolean isNormalize = false;
        // boolean isNormalize = true;
        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05+0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            int expTime = 10;
            int size = 5000;
            String writefilename = "";
            System.out.println("error rate is " + drate);
            // for (int j = 0; j < expTime; j++,seed++) {
            //     if(isNormalize){
            //         writefilename = "two/errorRate/separate/nor/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_nor.csv";
            //         TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
            //         TimeSeries2 dirtySeries = assist.addNoiseGPS_maxmin_separate(cleanSeries, drate, seed);
            //         assist.normalize(dirtySeries);
            //         assist.saveDataFromNoise(writefilename, dirtySeries);
            //     }
            //     else{
            //         writefilename = "two/errorRate/separate/ori/"+String.valueOf(drate)+"_"+String.valueOf(seed)+".csv";
            //         TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
            //         TimeSeries2 dirtySeries = assist.addNoiseGPS_maxmin_separate(cleanSeries, drate, seed);
            //         assist.saveDataFromNoise(writefilename, dirtySeries);
            //     }
            // }
            for (int j = 0; j < expTime; j++,seed++) {
                if(isNormalize){
                    writefilename = "two_noLabel/errorRate/separate/nor/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_nor.csv";
                    TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
                    TimeSeries2 dirtySeries = assist.addNoise2_maxmin_separate(cleanSeries, drate, seed);
                    assist.normalize(dirtySeries);
                    assist.saveDataFromNoise_noLabel(writefilename, dirtySeries);
                }
                else{
                    writefilename = "two_noLabel/errorRate/separate/ori/"+String.valueOf(drate)+"_"+String.valueOf(seed)+".csv";
                    TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
                    TimeSeries2 dirtySeries = assist.addNoise2_maxmin_separate(cleanSeries, drate, seed);
                    assist.saveDataFromNoise_noLabel(writefilename, dirtySeries);
                }
            }
        }
    }
}