package MTCSC.experiment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import MTCSC.MTCSC_AS;
import MTCSC.entity.TimeSeries2;
// import MTCSC.entity.TimePoint2;
import MTCSC.util.Assist;

public class DynamicSpeed_threshold {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "DynamicSpeed/dynamicSpeed2.data";  
        int T2 = 100;

        // bicycle
        double S = 6.6000;

        int expTime = 50;
        double[] totalThreshold = new double[expTime];
        double[] totalRMS = new double[expTime];
        double[] totalCOST = new double[expTime];
        int[] totalNUM = new int[expTime];
        long[] totalTIME = new long[expTime];
        double threshold = 0.1;
        for(int i=0; i<expTime; i++){
            threshold = 0.1 * (i+1);
            totalThreshold[i] = threshold;
            // two-plus-ds
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            double rmsDirty_twoPlusDS = assist.RMS2(dirtySeries);
            MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, threshold, 150, 0.75, 5);
            long time_twoPlusDS1 = System.currentTimeMillis();
            TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
            long time_twoPlusDS2 = System.currentTimeMillis();
            double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
            double cost_twoPlusDS = assist.Cost22(resultSeries_twoPlusDS);
            int num_twoPlusDS = assist.pointNum22(resultSeries_twoPlusDS);
            totalRMS[i] = rms_twoPlusDS;
            totalCOST[i] = cost_twoPlusDS;
            totalNUM[i] = num_twoPlusDS;
            totalTIME[i] = time_twoPlusDS2-time_twoPlusDS1;
            // two-plus-DS
            System.out.println("Threshold is :" + threshold);
            System.out.println("    Dirty RMS error is " + rmsDirty_twoPlusDS);
            System.out.println("    Repair RMS error is " + rms_twoPlusDS);
            System.out.println("    Cost is " + cost_twoPlusDS);
            System.out.println("    Time is " + (time_twoPlusDS2-time_twoPlusDS1));
            System.out.println("    The number of modified points is " + num_twoPlusDS);
        }

        
        // double[] orgval = new double[2];
        // double[] modify = new double[2];
        // double[] truth = new double[2];
        // for(TimePoint2 tp1 : resultSeries_twoPlusDS.getTimeseries()) {
        // orgval = tp1.getOrgval();
        // modify = tp1.getModify();
        // truth = tp1.getTruth();
        //     // if(orgval[0] != modify[0] || orgval[1] != modify[1]){
        //     //     System.out.println(tp1.getTimestamp() + ", x_orgval=" +
        //     // orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
        //     // truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
        //     // modify[1] + " , y_truth=" + truth[1]);
        //     // }
        //     if(Math.abs(truth[0]-modify[0]) >1|| Math.abs(truth[1]-modify[1]) >1){
        //         System.out.println(tp1.getTimestamp() + ", x_orgval=" +
        //     orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
        //     truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
        //     modify[1] + " , y_truth=" + truth[1]);
        //     }
        // }

        // String[][] data = new String[5][expTime+1];
        // data[1][0] = "RMS";
        // data[2][0] = "Cost";
        // data[3][0] = "Number";
        // data[4][0] = "Time";
        // for(int j=0; j<expTime; j++){
        //     data[0][j+1] = Double.toString(totalThreshold[j]);
        //     data[1][j+1] = Double.toString(totalRMS[j]);
        //     data[2][j+1] = Double.toString(totalCOST[j]);
        //     data[3][j+1] = Double.toString(totalNUM[j]);
        //     data[4][j+1] = Double.toString(totalTIME[j]);
        // }
        // String writefilename = "result/DynamicSpeed_Threshold/Threshold.csv";
        // assist.writeCSV(writefilename, data);

        String writefilename = "result/DynamicSpeed_Threshold/Threshold.csv";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(writefilename));

            for (int i = 0; i < totalThreshold.length; i++) {
                writer.println(totalThreshold[i] + "," + totalRMS[i]);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}