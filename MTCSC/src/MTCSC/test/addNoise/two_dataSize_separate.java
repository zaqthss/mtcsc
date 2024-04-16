package MTCSC.test.addNoise;

import java.text.DecimalFormat;
import MTCSC.entity.TimeSeries2;
import MTCSC.util.Assist;

public class two_dataSize_separate {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "ILD/temhum43k.data";
        
        // Normalization or not
        boolean isNormalize = false;
        // boolean isNormalize = true;
        for(int i=0; i<9; i++){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            int expTime = 10;
            int size = 5000*(i+1);
            String writefilename = "";
            System.out.println("Data Size is " + size);
            // for (int j = 0; j < expTime; j++,seed++) {
            //     if(isNormalize){
            //         writefilename = "two/dataSize/separate/nor/"+String.valueOf(size)+"_"+String.valueOf(seed)+"_nor.csv";
            //         TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
            //         TimeSeries2 dirtySeries = assist.addNoiseGPS_maxmin_separate(cleanSeries, drate, seed);
            //         assist.normalize(dirtySeries);
            //         assist.saveDataFromNoise(writefilename, dirtySeries);
            //     }
            //     else{
            //         writefilename = "two/dataSize/separate/ori/"+String.valueOf(size)+"_"+String.valueOf(seed)+".csv";
            //         TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
            //         TimeSeries2 dirtySeries = assist.addNoiseGPS_maxmin_separate(cleanSeries, drate, seed);
            //         assist.saveDataFromNoise(writefilename, dirtySeries);
            //     }
            // }
            for (int j = 0; j < expTime; j++,seed++) {
                if(isNormalize){
                    writefilename = "two_noLabel/dataSize/separate/nor/"+String.valueOf(size)+"_"+String.valueOf(seed)+"_nor.csv";
                    TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
                    TimeSeries2 dirtySeries = assist.addNoise2_maxmin_separate(cleanSeries, drate, seed);
                    assist.normalize(dirtySeries);
                    assist.saveDataFromNoise_noLabel(writefilename, dirtySeries);
                }
                else{
                    writefilename = "two_noLabel/dataSize/separate/ori/"+String.valueOf(size)+"_"+String.valueOf(seed)+".csv";
                    TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
                    TimeSeries2 dirtySeries = assist.addNoise2_maxmin_separate(cleanSeries, drate, seed);
                    assist.saveDataFromNoise_noLabel(writefilename, dirtySeries);
                }
            }
        }
    }
}