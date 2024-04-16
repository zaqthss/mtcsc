package cn.edu.thu.MTCSC.experiment;

import cn.edu.thu.MTCSC.MTCSC_AS;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import cn.edu.thu.MTCSC.util.Assist;

public class DynamicSpeed_beta_ws {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "DynamicSpeed/dynamicSpeed2.data";  
        int T2 = 100;

        // bicycle
        double S = 6.6000;

        int expTime = 50;
        double[] totalBeta = new double[expTime];
        double[] totalRMS = new double[expTime];
        double beta = 0;
        int ws = 0;
        for(int i=49; i<expTime; i++){
            beta = 0.01 * (i+1);
            totalBeta[i] = beta;
            // two-plus-ds
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            // 50
            // ws = 50;
            // My2_plus_ds tpDS = new My2_plus_ds(dirtySeries, S, T2, 0.025, 2, ws, beta, 5);
            // 150
            // ws = 150;
            // My2_plus_ds tpDS = new My2_plus_ds(dirtySeries, S, T2, 0.025, 0.7, ws, beta, 5);
            // 300
            ws = 300;
            MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, 1.5, ws, beta, 5);
            TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
            double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
            totalRMS[i] = rms_twoPlusDS;
            // two-plus-DS
            // System.out.println("Beta is :" + beta);
            // System.out.println("    Repair RMS error is " + rms_twoPlusDS);
            // double[] orgval = new double[2];
            // double[] modify = new double[2];
            // double[] truth = new double[2];
            // for(TimePoint2 tp1 : resultSeries_twoPlusDS.getTimeseries()) {
            // orgval = tp1.getOrgval();
            // modify = tp1.getModify();
            // truth = tp1.getTruth();
            //     if(orgval[0] != modify[0] || orgval[1] != modify[1]){
            //         System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            //     orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            //     truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            //     modify[1] + " , y_truth=" + truth[1]);
            //     }
            //     // if(Math.abs(truth[0]-modify[0]) >2|| Math.abs(truth[1]-modify[1]) >2){
            //     //     System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            //     // orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            //     // truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            //     // modify[1] + " , y_truth=" + truth[1]);
            //     // }
            // }
        }

        String writefilename = "result/DynamicSpeed_Beta/"+Integer.toString(ws)+"Beta.csv";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(writefilename));

            for (int i = 0; i < totalBeta.length; i++) {
                writer.println(totalBeta[i] + "," + totalRMS[i]);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}