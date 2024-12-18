package MTCSC.test.addNoise;

import java.text.DecimalFormat;
import MTCSC.entity.TimeSeriesN;
import MTCSC.util.Assist;

public class N_dataSize_separate {
    public static void main(String[] args) {
        Assist assist = new Assist();
        // String inputFileName = "TAO/tao_all_final.csv";
        // String inputFileName = "TAO/tao_50k_final.csv";
        String inputFileName = "TAO/tao_test.csv";
        int n = 3;
        // Normalization or not
        boolean isNormalize = false;
        // boolean isNormalize = true;
        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.00");
            drate = Double.parseDouble(df.format(drate));
            int expTime = 10;
            int size = 5000*(i+1);
            String writefilename = "";
            System.out.println("Data size is " + size);
            for(int j=0; j<expTime; j++, seed++){
                if(isNormalize){
                    writefilename = "MultiDimension/dataSize/separate/nor/"+String.valueOf(size)+"_"+String.valueOf(seed)+"_nor.csv";
                    TimeSeriesN cleanSeries = assist.readDataN_size(inputFileName, ",", n, size);
                    TimeSeriesN dirtySeries = assist.addNoiseN_maxmin_separate(cleanSeries, drate, seed, n);
                    assist.normalizeN(dirtySeries, n);
                    assist.saveDataFromNoise(writefilename, dirtySeries, n);
                } 
                else{
                    writefilename = "MultiDimension/dataSize/separate/ori/"+String.valueOf(size)+"_"+String.valueOf(seed)+".csv";
                    TimeSeriesN cleanSeries = assist.readDataN_size(inputFileName, ",", n, size);
                    TimeSeriesN dirtySeries = assist.addNoiseN_maxmin_separate(cleanSeries, drate, seed, n);
                    assist.saveDataFromNoise(writefilename, dirtySeries, n);
                }  
            }
            // for(int j=0; j<expTime; j++, seed++){
            //     if(isNormalize){
            //         writefilename = "MultiDimension_noLabel/dataSize/separate/nor/"+String.valueOf(size)+"_"+String.valueOf(seed)+"_nor.csv";
            //         TimeSeriesN cleanSeries = assist.readDataN_size(inputFileName, ",", n, size);
            //         TimeSeriesN dirtySeries = assist.addNoiseN_maxmin_separate(cleanSeries, drate, seed, n);
            //         assist.normalizeN(dirtySeries, n);
            //         assist.saveDataFromNoise_noLabel(writefilename, dirtySeries, n);
            //     } 
            //     else{
            //         writefilename = "MultiDimension_noLabel/dataSize/separate/ori/"+String.valueOf(size)+"_"+String.valueOf(seed)+".csv";
            //         TimeSeriesN cleanSeries = assist.readDataN_size(inputFileName, ",", n, size);
            //         TimeSeriesN dirtySeries = assist.addNoiseN_maxmin_separate(cleanSeries, drate, seed, n);
            //         assist.saveDataFromNoise_noLabel(writefilename, dirtySeries, n);
            //     }  
            // } 
        }
    }
}
