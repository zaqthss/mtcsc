package cn.edu.thu.MTCSC.experiment;

import java.text.DecimalFormat;
import cn.edu.thu.MTCSC.EWMA;
import cn.edu.thu.MTCSC.HTD;
import cn.edu.thu.MTCSC.SpeedAcc;
import cn.edu.thu.MTCSC.Lsgreedy;
import cn.edu.thu.MTCSC.MTCSC_Uni;
import cn.edu.thu.MTCSC.Global_1;
import cn.edu.thu.MTCSC.Local_1;
import cn.edu.thu.MTCSC.SCREEN;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Assist;


public class stock {
    public static void main(String[] args) {
        Assist assist = new Assist();

        String inputFileName = "STOCK/stock12k.data";
        double sMax = 5;
        double sMin = -5;
        long TT = 1;
        long T = 100;
        
        int methodNum = 8;
        double[] totalDrate = new double[10];
        double[][] totalRMS = new double[10][methodNum];
        double[][] totalCOST = new double[10][methodNum];
        double[][] totalNUM = new double[10][methodNum];
        double[][] totalTIME = new double[10][methodNum];
        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05+0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            double totalDirtyRMS = 0;
            
            totalDrate[i] = drate;
            System.out.println("Dirty rate is " + drate);
            int expTime = 10;
            for(int j=0; j<expTime; j++,seed++){
                TimeSeries dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                double rmsDirty = assist.calcRMS(dirtySeries);
                totalDirtyRMS += rmsDirty;
                // assist.getStock(dirtySeries);
                // double[] speed = assist.getSpeed(dirtySeries, 1.0);
                // sMin = speed[0]; sMax = speed[1];

                // MyGlobal
                rmsDirty = assist.calcRMS(dirtySeries);
                Global_1 myglobal = new Global_1(dirtySeries, sMax, sMin);
                long time_global1 = System.currentTimeMillis();
                TimeSeries resultSeries_global = myglobal.clean();
                long time_global2 = System.currentTimeMillis();
                double rms_global = assist.calcRMS(resultSeries_global);
                double cost_global = assist.calcCost(resultSeries_global);
                int num_global = assist.pointNum1(resultSeries_global);
                totalRMS[i][0] += rms_global;
                totalCOST[i][0] += cost_global;
                totalNUM[i][0] += num_global;
                totalTIME[i][0] = totalTIME[i][0] + time_global2-time_global1;
                // System.out.println("MyGlobal:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_global);
                // System.out.println("    Cost is " + cost_global);
                // System.out.println("    The number of modified points is " + num_global);
                // System.out.println("    Time is " + (time_global2-time_global1));

                // MyLocal
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                Local_1 mylocal = new Local_1(dirtySeries, sMax, sMin, T);
                long time_local1 = System.currentTimeMillis();
                TimeSeries resultSeries_local = mylocal.mainScreen();
                long time_local2 = System.currentTimeMillis();
                double rms_local = assist.calcRMS(resultSeries_local);
                double cost_local = assist.calcCost(resultSeries_local);
                int num_local = assist.pointNum1(resultSeries_local);
                totalRMS[i][1] += rms_local;
                totalCOST[i][1] += cost_local;
                totalNUM[i][1] += num_local;
                totalTIME[i][1] = totalTIME[i][1] + time_local2-time_local1;

                // System.out.println("MyLocal:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_local);
                // System.out.println("    Cost is " + cost_local);
                // System.out.println("    The number of modified points is " + num_local);
                // System.out.println("    Time is " + (time_local2-time_local1));

                // My1
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                MTCSC_Uni my1 = new MTCSC_Uni(dirtySeries, sMax, sMin, T);
                long time_my1 = System.currentTimeMillis();
                TimeSeries resultSeries_my1 = my1.mainScreen();
                long time_my2 = System.currentTimeMillis();
                double rms_my1 = assist.calcRMS(resultSeries_my1);
                double cost_my1 = assist.calcCost(resultSeries_my1);
                int num_my1 = assist.pointNum1(resultSeries_my1);
                totalRMS[i][2] += rms_my1;
                totalCOST[i][2] += cost_my1;
                totalNUM[i][2] += num_my1;
                totalTIME[i][2] = totalTIME[i][2] + time_my2-time_my1;
                // System.out.println("My:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_my1);
                // System.out.println("    Cost is " + cost_my1);
                // System.out.println("    The number of modified points is " + num_my1);
                // System.out.println("    Time is " + (time_my2-time_my1));

                // MTCSC
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                SCREEN MTCSC = new SCREEN(dirtySeries, sMax, sMin, TT);
                long time_MTCSC1 = System.currentTimeMillis();
                TimeSeries resultSeries_MTCSC = MTCSC.mainScreen();
                long time_MTCSC2 = System.currentTimeMillis();
                double rms_MTCSC = assist.calcRMS(resultSeries_MTCSC);
                double cost_MTCSC = assist.calcCost(resultSeries_MTCSC);
                int num_MTCSC = assist.pointNum1(resultSeries_MTCSC);
                totalRMS[i][3] += rms_MTCSC;
                totalCOST[i][3] += cost_MTCSC;
                totalNUM[i][3] += num_MTCSC;
                totalTIME[i][3] = totalTIME[i][3] + time_MTCSC2-time_MTCSC1;

                // System.out.println("SCREEN:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_MTCSC);
                // System.out.println("    Cost is " + cost_MTCSC);
                // System.out.println("    The number of modified points is " + num_MTCSC);
                // System.out.println("    Time is " + (time_MTCSC2-time_MTCSC1));

                // LocalSpeedAcc
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                SpeedAcc LSA = new SpeedAcc(dirtySeries, TT, sMax, sMin, 4000, -4000);
                long time_LocalSpeedAcc1 = System.currentTimeMillis();
                TimeSeries resultSeries_LocalSpeedAcc = LSA.mainSliUp();
                long time_LocalSpeedAcc2 = System.currentTimeMillis();
                double rms_LocalSpeedAcc = assist.calcRMS(resultSeries_LocalSpeedAcc);
                double cost_LocalSpeedAcc = assist.calcCost(resultSeries_LocalSpeedAcc);
                int num_LocalSpeedAcc = assist.pointNum1(resultSeries_LocalSpeedAcc);
                totalRMS[i][4] += rms_LocalSpeedAcc;
                totalCOST[i][4] += cost_LocalSpeedAcc;
                totalNUM[i][4] += num_LocalSpeedAcc;
                totalTIME[i][4] = totalTIME[i][4] + time_LocalSpeedAcc2-time_LocalSpeedAcc1;

                // System.out.println("LocalSpeedAcc:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_LocalSpeedAcc);
                // System.out.println("    Cost is " + cost_LocalSpeedAcc);
                // System.out.println("    The number of modified points is " + num_LocalSpeedAcc);
                // System.out.println("    Time is " + (time_LocalSpeedAcc2-time_LocalSpeedAcc1));

                // Lsgreedy
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                Lsgreedy lsgreedy = new Lsgreedy(dirtySeries);
                long time_lsgreedy1 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy = lsgreedy.repair();
                long time_lsgreedy2 = System.currentTimeMillis();
                double rms_lsgreedy = assist.calcRMS(resultSeries_lsgreedy);
                double cost_lsgreedy = assist.calcCost(resultSeries_lsgreedy);
                int num_lsgreedy = assist.pointNum1(resultSeries_lsgreedy);
                totalRMS[i][5] += rms_lsgreedy;
                totalCOST[i][5] += cost_lsgreedy;
                totalNUM[i][5] += num_lsgreedy;
                totalTIME[i][5] = totalTIME[i][5] + time_lsgreedy2-time_lsgreedy1;

                // System.out.println("Lsgreedy:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_lsgreedy);
                // System.out.println("    Cost is " + cost_lsgreedy);
                // System.out.println("    The number of modified points is " + num_lsgreedy);
                // System.out.println("    Time is " + (time_lsgreedy2-time_lsgreedy1));

                // expsmooth
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                EWMA exp = new EWMA(dirtySeries, 0.042);
                long time_exp1 = System.currentTimeMillis();
                TimeSeries resultSeries_exp = exp.mainExp();
                long time_exp2 = System.currentTimeMillis();
                double rms_exp = assist.calcRMS(resultSeries_exp);
                double cost_exp = assist.calcCost(resultSeries_exp);
                int num_exp = assist.pointNum1(resultSeries_exp);
                totalRMS[i][6] += rms_exp;
                totalCOST[i][6] += cost_exp;
                totalNUM[i][6] += num_exp;
                totalTIME[i][6] = totalTIME[i][6] + time_exp2-time_exp1;

                // System.out.println("Expsmooth:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_exp);
                // System.out.println("    Cost is " + cost_exp);
                // System.out.println("    The number of modified points is " + num_exp);
                // System.out.println("    Time is " + (time_exp2-time_exp1));

                // HTD-Cleaning
                dirtySeries = assist.readData(inputFileName, ",");
                dirtySeries = assist.addNoise_stock(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                HTD htd = new HTD(dirtySeries, sMax, sMin, 1, 1000);
                long time_HTD1 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD = htd.clean();
                long time_HTD2 = System.currentTimeMillis();
                double rms_HTD = assist.calcRMS_HTD(resultSeries_HTD);
                double cost_HTD = assist.calcCost(resultSeries_HTD);
                int num_HTD = assist.pointNum1(resultSeries_HTD);
                totalRMS[i][7] += rms_HTD;
                totalCOST[i][7] += cost_HTD;
                totalNUM[i][7] += num_HTD;
                totalTIME[i][7] = totalTIME[i][7] + time_HTD2-time_HTD1;
            }
            totalDirtyRMS /= expTime;
            System.out.println("Dirty RMS error is " + totalDirtyRMS);
            for(int j=0; j<methodNum; j++){
                totalRMS[i][j] /= expTime;
                totalCOST[i][j] /= expTime;
                totalNUM[i][j] /= expTime;
                totalTIME[i][j] /= expTime;
                // if(j==1){
                //     System.out.println("MyLocal:");
                //     System.out.println("    Repair RMS error is " + totalRMS[i][j]);
                //     System.out.println("    Cost is " + totalCOST[i][j]);
                //     System.out.println("    The number of modified points is " + totalNUM[i][j]);
                //     System.out.println("    Time is " + totalTIME[i][j]);
                // }
            }
        }
        String[] name = new String[]{" ","Global","Local","Cluster","SCREEN","SpeedAcc","LsGreedy","EWMA","HTD"};
        String writefilename = "result/One/stock/RMS.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalRMS);
        writefilename = "result/One/stock/COST.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalCOST);
        writefilename = "result/One/stock/NUM.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalNUM);
        writefilename = "result/One/stock/TIME.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalTIME);
    }
}