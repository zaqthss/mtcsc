package MTCSC.experiment;

import MTCSC.MTCSC_AS;
import MTCSC.entity.TimeSeries2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import MTCSC.util.Assist;

public class DynamicSpeed_beta_ws {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "DynamicSpeed/dynamicSpeed2.data";  
        int T2 = 100;

        // bicycle
        double S = 6.6000;

        int expTime = 99;
        double[] totalBeta = new double[expTime];
        double[] totalRMS = new double[expTime];
        double beta = 0;
        int ws = 0;
        for(int i=0; i<expTime; i++){
            beta = 0.01 * (i+1);
            totalBeta[i] = beta;
            // two-plus-ds
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            // 300
            ws = 300;
            MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, 1.5, ws, beta, 5);
            TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
            double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
            totalRMS[i] = rms_twoPlusDS;
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

        expTime = 99;
        totalBeta = new double[expTime];
        totalRMS = new double[expTime];
        beta = 0;
        ws = 0;
        for(int i=0; i<expTime; i++){
            beta = 0.01 * (i+1);
            totalBeta[i] = beta;
            // two-plus-ds
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            // 50
            ws = 50;
            MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, 2, ws, beta, 5);
            TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
            double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
            totalRMS[i] = rms_twoPlusDS;
        }

        writefilename = "result/DynamicSpeed_Beta/"+Integer.toString(ws)+"Beta.csv";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(writefilename));

            for (int i = 0; i < totalBeta.length; i++) {
                writer.println(totalBeta[i] + "," + totalRMS[i]);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        expTime = 99;
        totalBeta = new double[expTime];
        totalRMS = new double[expTime];
        beta = 0;
        ws = 0;
        for(int i=0; i<expTime; i++){
            beta = 0.01 * (i+1);
            totalBeta[i] = beta;
            // two-plus-ds
            TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
            ws = 150;
            MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, 0.7, ws, beta, 5);
            TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
            double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
            totalRMS[i] = rms_twoPlusDS;
        }

        writefilename = "result/DynamicSpeed_Beta/"+Integer.toString(ws)+"Beta.csv";
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