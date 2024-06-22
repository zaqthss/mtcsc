package MTCSC.experiment;

import MTCSC.SCREEN;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_Uni;
import MTCSC.MTCSC_QCQP;
import MTCSC.MTCSC_2;
import MTCSC.MTCSC_AS;
import MTCSC.RCSWS;
import MTCSC.entity.TimeSeries2;
// import MTCSC.entity.TimePoint2;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class DynamicSpeed {
    public static void main(String[] args) {
        // Need to uncomment the corresponding mode of transportation : walk, run, bicycle
        // walk
        double sMax_1 = 2.2000; //1.66520m/s
        double sMin_1 = -2.2000;
        double sMax_2 = 2.2000; //1.668520m/s
        double sMin_2 = -2.2000;
        double S = 2.2000;
        String motion = "walk";
        // run
        // double sMax_1 = 4.4000; 
        // double sMin_1 = -4.4000;
        // double sMax_2 = 4.4000; 
        // double sMin_2 = -4.4000;
        // double S = 4.4000;
        // String motion = "run";
        // bicycle
        // double sMax_1 = 6.6000; 
        // double sMin_1 = -6.6000;
        // double sMax_2 = 6.6000; 
        // double sMin_2 = -6.6000;
        // double S = 6.6000;
        // String motion = "bicycle";
        Assist assist = new Assist();
        // String inputFileName = "DynamicSpeed/dstest.data";  // dynamic speed test dataset
        String inputFileName = "DynamicSpeed/dynamicSpeed2.data";  

        int T1 = 100;
        int T2 = 100;
        int methodNum = 9;
        double[] totalRMS = new double[methodNum];
        double[] totalCOST = new double[methodNum];
        int[] totalNUM = new int[methodNum];
        long[] totalTIME = new long[methodNum];

        // MTCSC
        TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
        // double rmsDirty_twoPlus = assist.RMS2(dirtySeries);
        MTCSC_2 tp = new MTCSC_2(dirtySeries, S, T2);
        long time100 = System.currentTimeMillis();
        TimeSeries2 resultSeries_twoPlus = tp.mainScreen();
        long time200 = System.currentTimeMillis();
        double rms_twoPlus = assist.RMS2(resultSeries_twoPlus);
        double cost_twoPlus = assist.Cost22(resultSeries_twoPlus);
        int num_twoPlus = assist.pointNum22(resultSeries_twoPlus);
        totalRMS[1] += rms_twoPlus;
        totalCOST[1] += cost_twoPlus;
        totalNUM[1] += num_twoPlus;
        totalTIME[1] = totalTIME[1] + time200-time100;

        // MTCSC-A
        dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_twoPlusDS = assist.RMS2(dirtySeries);
        MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.025, 0.75, 150, 0.75, 5);
        // My2_plus_ds tpDS = new My2_plus_ds(dirtySeries, S, T2, 0.025, 1.0, 50); // dstest.data
        long time_twoPlusDS1 = System.currentTimeMillis();
        TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
        long time_twoPlusDS2 = System.currentTimeMillis();
        double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
        double cost_twoPlusDS = assist.Cost22(resultSeries_twoPlusDS);
        int num_twoPlusDS = assist.pointNum22(resultSeries_twoPlusDS);
        totalRMS[2] += rms_twoPlusDS;
        totalCOST[2] += cost_twoPlusDS;
        totalNUM[2] += num_twoPlusDS;
        totalTIME[2] = totalTIME[2] + time_twoPlusDS2-time_twoPlusDS1;

        // MTCSC-Uni
        dirtySeries = assist.readData2(inputFileName, ",");
        TimeSeries dirtySeries_1 = assist.getXY(dirtySeries, 0);
        TimeSeries dirtySeries_2 = assist.getXY(dirtySeries, 1);
        double rmsDirty_My1 = assist.RMS1(dirtySeries_1, dirtySeries_2);
        // my1_1
        MTCSC_Uni my1_1 = new MTCSC_Uni(dirtySeries_1, sMax_1, sMin_1, T2);
        long time_my1 = System.currentTimeMillis();
        TimeSeries resultSeries_my1_1 = my1_1.mainScreen();
        long time_my2 = System.currentTimeMillis();
        // my1_2
        MTCSC_Uni my1_2 = new MTCSC_Uni(dirtySeries_2, sMax_2, sMin_2, T2);
        long time_my11 = System.currentTimeMillis();
        TimeSeries resultSeries_my1_2 = my1_2.mainScreen();
        long time_my22 = System.currentTimeMillis();
        double rms_My1 = assist.RMS1(resultSeries_my1_1, resultSeries_my1_2);
        double cost_My1 = assist.Cost11(resultSeries_my1_1, resultSeries_my1_2);
        int num_My1 = assist.pointNum11(resultSeries_my1_1, resultSeries_my1_2);
        totalRMS[3] += rms_My1;
        totalCOST[3] += cost_My1;
        totalNUM[3] += num_My1;
        totalTIME[3] = totalTIME[3] +time_my22-time_my11+time_my2-time_my1;

        // rcsws
        dirtySeries = assist.readData2(inputFileName, ",");
        // double rmsDirty_rcsws = assist.RMS2(dirtySeries);
        RCSWS rcsws = new RCSWS(dirtySeries, S);
        long time_rcsws1 = System.currentTimeMillis();
        TimeSeries2 resultSeries_rcsws = rcsws.clean(50);
        long time_rcsws2 = System.currentTimeMillis();
        double rms_rcsws = assist.RMS2(resultSeries_rcsws);
        double cost_rcsws = assist.Cost22(resultSeries_rcsws);
        int num_rcsws = assist.pointNum22(resultSeries_rcsws);
        totalRMS[4] += rms_rcsws;
        totalCOST[4] += cost_rcsws;
        totalNUM[4] += num_rcsws;
        totalTIME[4] = totalTIME[4] + time_rcsws2-time_rcsws1;

        // Screen
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        // double rmsDirty_12 = assist.RMS1(dirtySeries_1, dirtySeries_2);
        // Screen_1
        SCREEN screen_1 = new SCREEN(dirtySeries_1, sMax_1, sMin_1, T1);
        long time3 = System.currentTimeMillis();
        TimeSeries resultSeries_1 = screen_1.mainScreen();
        long time4 = System.currentTimeMillis();
        // Screen_2
        SCREEN screen_2 = new SCREEN(dirtySeries_2, sMax_2, sMin_2, T1);
        long time5 = System.currentTimeMillis();
        TimeSeries resultSeries_2 = screen_2.mainScreen();
        long time6 = System.currentTimeMillis();

        double rms_12 = assist.RMS1(resultSeries_1, resultSeries_2);
        double cost_12 = assist.Cost11(resultSeries_1, resultSeries_2);
        int num_1 = assist.pointNum11(resultSeries_1, resultSeries_2);
        totalRMS[5] += rms_12;
        totalCOST[5] += cost_12;
        totalNUM[5] += num_1;
        totalTIME[5] = totalTIME[5] + time6-time5+time4-time3;

        // SpeedAcc
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        // double rmsDirty_SpeedAcc = assist.RMS1(dirtySeries_1, dirtySeries_2);
        // SpeedAcc_1
        SpeedAcc LSA_1 = new SpeedAcc(dirtySeries_1, T1, sMax_1, sMin_1, 2000, -2000);
        long time_SpeedAcc1 = System.currentTimeMillis();
        TimeSeries resultSeries_LocalSpeedAcc_1 = LSA_1.mainSliUp();
        long time_SpeedAcc2 = System.currentTimeMillis();
        // SpeedAcc_2
        SpeedAcc LSA_2 = new SpeedAcc(dirtySeries_2, T1, sMax_2, sMin_2, 2000, -2000);
        long time_SpeedAcc3 = System.currentTimeMillis();
        TimeSeries resultSeries_LocalSpeedAcc_2 = LSA_2.mainSliUp();
        long time_SpeedAcc4 = System.currentTimeMillis();

        double rms_SpeedAcc = assist.RMS1(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
        double cost_SpeedAcc = assist.Cost11(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
        int num_SpeedAcc = assist.pointNum11(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
        totalRMS[6] += rms_SpeedAcc;
        totalCOST[6] += cost_SpeedAcc;
        totalNUM[6] += num_SpeedAcc;
        totalTIME[6] = totalTIME[6] + time_SpeedAcc4-time_SpeedAcc3+time_SpeedAcc2-time_SpeedAcc1;

        // Lsgreedy
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        double rmsDirty_lsgreedy = assist.RMS1(dirtySeries_1, dirtySeries_2);
        // Lsgreedy_1
        Lsgreedy lsgreedy_1 = new Lsgreedy(dirtySeries_1);
        long time333 = System.currentTimeMillis();
        TimeSeries resultSeries_lsgreedy_1 = lsgreedy_1.repair();
        long time444 = System.currentTimeMillis();
        // Lsgreedy_2
        Lsgreedy lsgreedy_2 = new Lsgreedy(dirtySeries_2);
        long time555 = System.currentTimeMillis();
        TimeSeries resultSeries_lsgreedy_2 = lsgreedy_2.repair();
        long time666 = System.currentTimeMillis();

        double rms_lsgreedy = assist.RMS1(resultSeries_lsgreedy_1, resultSeries_lsgreedy_2);
        double cost_lsgreedy = assist.Cost11(resultSeries_lsgreedy_1, resultSeries_lsgreedy_2);
        int num_lsgreedy = assist.pointNum11(resultSeries_lsgreedy_1, resultSeries_lsgreedy_2);
        totalRMS[7] += rms_lsgreedy;
        totalCOST[7] += cost_lsgreedy;
        totalNUM[7] += num_lsgreedy;
        totalTIME[7] = totalTIME[7] + time666-time555+time444-time333;

        // expsmooth
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        // double rmsDirty_expsmooth = assist.RMS1(dirtySeries_1, dirtySeries_2);
        EWMA exp_1 = new EWMA(dirtySeries_1, 1.133);
        long time17 = System.currentTimeMillis();
        TimeSeries resultSeries_5 = exp_1.mainExp();
        long time18 = System.currentTimeMillis();
        // expsmooth_1
        EWMA exp_2 = new EWMA(dirtySeries_2, 1.133);
        long time19 = System.currentTimeMillis();
        TimeSeries resultSeries_6 = exp_2.mainExp();
        long time20 = System.currentTimeMillis();
        // expsmooth_2
        double rms_expsmooth = assist.RMS1(resultSeries_5, resultSeries_6);
        double cost_expsmooth = assist.Cost11(resultSeries_5, resultSeries_6);
        int num_expsmooth = assist.pointNum11(resultSeries_5, resultSeries_6);
        totalRMS[8] += rms_expsmooth;
        totalCOST[8] += cost_expsmooth;
        totalNUM[8] += num_expsmooth;
        totalTIME[8] = totalTIME[8] + time20-time19+time18-time17;

        // HTD-Cleaning
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        // double rmsDirty_HTD = assist.RMS1(dirtySeries_1, dirtySeries_2);
        int maxnum = 1000;
        // HTD1
        HTD htd_1 = new HTD(dirtySeries_1, sMax_1, sMin_1, 1, maxnum);
        long time_HTD1 = System.currentTimeMillis();
        TimeSeries resultSeries_HTD_1 = htd_1.clean();
        long time_HTD2 = System.currentTimeMillis();
        // HTD2
        HTD htd_2 = new HTD(dirtySeries_2, sMax_2, sMin_2, 1, maxnum);
        long time_HTD3 = System.currentTimeMillis();
        TimeSeries resultSeries_HTD_2 = htd_2.clean();
        long time_HTD4 = System.currentTimeMillis();
        double rms_HTD = assist.RMS1(resultSeries_HTD_1, resultSeries_HTD_2);
        double cost_HTD = assist.Cost11(resultSeries_HTD_1, resultSeries_HTD_2);
        int num_HTD = assist.pointNum11(resultSeries_HTD_1, resultSeries_HTD_2);
        totalRMS[9] += rms_HTD;
        totalCOST[9] += cost_HTD;
        totalNUM[9] += num_HTD;
        totalTIME[9] = totalTIME[9] + time_HTD2-time_HTD1+time_HTD4-time_HTD3;

        String[][] data = new String[5][10];
        data[0] = new String[]{" ","MTCSC","MTCSC-A","MTCSC-Uni","RCSWS","SCREEN","SpeedAcc","LsGreedy","EWMA","HTD"};
        data[1][0] = "RMS";
        data[2][0] = "Cost";
        data[3][0] = "Number";
        data[4][0] = "Time";
        for(int j=0; j<methodNum; j++){
            data[1][j+1] = Double.toString(totalRMS[j]);
            data[2][j+1] = Double.toString(totalCOST[j]);
            data[3][j+1] = Double.toString(totalNUM[j]);
            data[4][j+1] = Double.toString(totalTIME[j]);
        }
        String writefilename = "result/"+inputFileName+"_"+motion+".csv";
        assist.writeCSV(writefilename, data);

        // two-dimensional
        // System.out.println("two-dimensional:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_two);
        // System.out.println("    Repair RMS error is " + rms_two);
        // System.out.println("    Cost is " + cost_two);
        // System.out.println("    Time is " + (time2-time1));
        // System.out.println("    The number of modified points is " + num_two);
        
        // two-plus
        // System.out.println("two-plus:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_twoPlus);
        // System.out.println("    Repair RMS error is " + rms_twoPlus);
        // System.out.println("    Cost is " + cost_twoPlus);
        // System.out.println("    Time is " + (time200-time100));
        // System.out.println("    The number of modified points is " + num_twoPlus);

        // two-plus-DS
        // System.out.println("two-plus-ds:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_twoPlusDS);
        // System.out.println("    Repair RMS error is " + rms_twoPlusDS);
        // System.out.println("    Cost is " + cost_twoPlusDS);
        // System.out.println("    Time is " + (time_twoPlusDS2-time_twoPlusDS1));
        // System.out.println("    The number of modified points is " + num_twoPlusDS);

        // My1
        // System.out.println("My1:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_My1);
        // System.out.println("    Repair RMS error is " + rms_My1);
        // System.out.println("    Cost is " + cost_My1);
        // System.out.println("    Time is " + (time_my22-time_my11+time_my2-time_my1));
        // System.out.println("    The number of modified points is " + num_My1);

        // rcsws
        // System.out.println("rcsws:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_rcsws);
        // System.out.println("    Repair RMS error is " + rms_rcsws);
        // System.out.println("    Cost is " + cost_rcsws);
        // System.out.println("    Time is " + (time_rcsws2-time_rcsws1));
        // System.out.println("    The number of modified points is " + num_rcsws);
        
        // // Screen
        // System.out.println("Screen:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_12);
        // System.out.println("    Repair RMS error is " + rms_12);
        // System.out.println("    Cost is " + cost_12);
        // System.out.println("    Time is " + (time4-time3+time6-time5));
        // System.out.println("    The number of modified points is " + num_1);

        // // SpeedAcc
        // System.out.println("SpeedAcc:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_SpeedAcc);
        // System.out.println("    Repair RMS error is " + rms_SpeedAcc);
        // System.out.println("    Cost is " + cost_SpeedAcc);
        // System.out.println("    Time is " + (time_SpeedAcc4-time_SpeedAcc3+time_SpeedAcc2-time_SpeedAcc1));
        // System.out.println("    The number of modified points is " + num_SpeedAcc);

        // Lsgreedy
        // System.out.println("Lsgreedy:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_lsgreedy);
        // System.out.println("    Repair RMS error is " + rms_lsgreedy);
        // System.out.println("    Cost is " + cost_lsgreedy);
        // System.out.println("    Time is " + (time444-time333+time666-time555));
        // System.out.println("    The number of modified points is " + num_lsgreedy);

        //expsmooth
        // System.out.println("ExpSmooth:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_expsmooth);
        // System.out.println("    Repair RMS error is " + rms_expsmooth);
        // System.out.println("    Cost is " + cost_expsmooth);
        // System.out.println("    Time is " + (time18-time17+time20-time19));
        // System.out.println("    The number of modified points is " + num_expsmooth);

        //HTD
        // System.out.println("HTD:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_HTD);
        // System.out.println("    Repair RMS error is " + rms_HTD);
        // System.out.println("    Cost is " + cost_HTD);
        // System.out.println("    Time is " + (time_HTD2-time_HTD1+time_HTD4-time_HTD3));
        // System.out.println("    The number of modified points is " + num_HTD);
    }
}