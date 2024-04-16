package cn.edu.thu.MTCSC.test;

import cn.edu.thu.MTCSC.EWMA;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Assist;

public class example1_ewma {
    public static void main(String[] args) {
        Assist assist = new Assist();

        String inputFileName = "example/example1.data";

        // expsmooth
        int index = 0;
        double best = 0;
        double minrms = Double.MAX_VALUE;
        double alpha = 0.0;
        for(int i=0; i<50000; i++)
        {
            alpha = 0.001*i;
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            TimeSeries dirtySeries_1 = assist.getXY(dirtySeries, 0);
            TimeSeries dirtySeries_2 = assist.getXY(dirtySeries, 1);
            EWMA exp_1 = new EWMA(dirtySeries_1, alpha);
            TimeSeries resultSeries_5 = exp_1.mainExp();
            // expsmooth_1
            EWMA exp_2 = new EWMA(dirtySeries_2, alpha);
            TimeSeries resultSeries_6 = exp_2.mainExp();
            // expsmooth_2
            double rms_expsmooth = assist.RMS1(resultSeries_5, resultSeries_6);
            if(rms_expsmooth < minrms){
                minrms = rms_expsmooth;
                index = i;
                best = alpha;
            }
        }
        
        // assist.saveDataFromTimeSeries(outputdirectory+"EWMA.csv", resultSeries_5, resultSeries_6);
        //expsmooth
        System.out.println("ExpSmooth:");
        System.out.println("    minRms is " + minrms);
        System.out.println("    index is " + index);
        System.out.println("    alpha is " + best);
    }
}