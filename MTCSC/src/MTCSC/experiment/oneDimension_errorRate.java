package MTCSC.experiment;

import java.text.DecimalFormat;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_Uni;
import MTCSC.SCREEN;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class oneDimension_errorRate {
    public static void main(String[] args) {
        Assist assist = new Assist();  
        String inputFileName = "ILD/tem43k.data"; // 43k temperature
        String directory = "tem";
        double sMax = 0.050001;
        double sMin = -0.050001;
        

        long TT = 10;
        long T = 100;
        
        int methodNum = 6;
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
            int size = 5000;
            for(int j=0; j<expTime; j++,seed++){
                TimeSeries dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                double rmsDirty = assist.calcRMS(dirtySeries);
                totalDirtyRMS += rmsDirty;
                // System.out.println("Dirty RMS error is " + rmsDirty);
                // double[] speed = assist.getSpeed(dirtySeries, 1.0);
                // sMin = speed[0]; sMax = speed[1];

                // my
                MTCSC_Uni my = new MTCSC_Uni(dirtySeries, sMax, sMin, T);
                long time_my1 = System.currentTimeMillis();
                TimeSeries resultSeries_my = my.mainScreen();
                long time_my2 = System.currentTimeMillis();
                double rms_my = assist.calcRMS(resultSeries_my);
                double cost_my = assist.calcCost(resultSeries_my);
                int num_my = assist.pointNum1(resultSeries_my);
                totalRMS[i][0] += rms_my;
                totalCOST[i][0] += cost_my;
                totalNUM[i][0] += num_my;
                totalTIME[i][0] = totalTIME[i][0] + time_my2-time_my1;
                // System.out.println("My:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_my);
                // System.out.println("    Cost is " + cost_my);
                // System.out.println("    The number of modified points is " + num_my);
                // System.out.println("    Time is " + (time_my2-time_my1));

                // SCREEN
                dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                SCREEN screen = new SCREEN(dirtySeries, sMax, sMin, TT);
                long time_screen1 = System.currentTimeMillis();
                TimeSeries resultSeries_screen = screen.mainScreen();
                long time_screen2 = System.currentTimeMillis();
                double rms_screen = assist.calcRMS(resultSeries_screen);
                double cost_screen = assist.calcCost(resultSeries_screen);
                int num_screen = assist.pointNum1(resultSeries_screen);
                totalRMS[i][1] += rms_screen;
                totalCOST[i][1] += cost_screen;
                totalNUM[i][1] += num_screen;
                totalTIME[i][1] = totalTIME[i][1] + time_screen2-time_screen1;

                // System.out.println("SCREEN:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_MTCSC);
                // System.out.println("    Cost is " + cost_MTCSC);
                // System.out.println("    The number of modified points is " + num_MTCSC);
                // System.out.println("    Time is " + (time_MTCSC2-time_MTCSC1));

                // LocalSpeedAcc
                dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                SpeedAcc LSA = new SpeedAcc(dirtySeries, TT, sMax, sMin, 400, -400);
                long time_LocalSpeedAcc1 = System.currentTimeMillis();
                TimeSeries resultSeries_LocalSpeedAcc = LSA.mainSliUp();
                long time_LocalSpeedAcc2 = System.currentTimeMillis();
                double rms_LocalSpeedAcc = assist.calcRMS(resultSeries_LocalSpeedAcc);
                double cost_LocalSpeedAcc = assist.calcCost(resultSeries_LocalSpeedAcc);
                int num_LocalSpeedAcc = assist.pointNum1(resultSeries_LocalSpeedAcc);
                totalRMS[i][2] += rms_LocalSpeedAcc;
                totalCOST[i][2] += cost_LocalSpeedAcc;
                totalNUM[i][2] += num_LocalSpeedAcc;
                totalTIME[i][2] = totalTIME[i][2] + time_LocalSpeedAcc2-time_LocalSpeedAcc1;

                // System.out.println("LocalSpeedAcc:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_LocalSpeedAcc);
                // System.out.println("    Cost is " + cost_LocalSpeedAcc);
                // System.out.println("    The number of modified points is " + num_LocalSpeedAcc);
                // System.out.println("    Time is " + (time_LocalSpeedAcc2-time_LocalSpeedAcc1));

                // Lsgreedy
                dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                Lsgreedy lsgreedy = new Lsgreedy(dirtySeries);
                long time_lsgreedy1 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy = lsgreedy.repair();
                long time_lsgreedy2 = System.currentTimeMillis();
                double rms_lsgreedy = assist.calcRMS(resultSeries_lsgreedy);
                double cost_lsgreedy = assist.calcCost(resultSeries_lsgreedy);
                int num_lsgreedy = assist.pointNum1(resultSeries_lsgreedy);
                totalRMS[i][3] += rms_lsgreedy;
                totalCOST[i][3] += cost_lsgreedy;
                totalNUM[i][3] += num_lsgreedy;
                totalTIME[i][3] = totalTIME[i][3] + time_lsgreedy2-time_lsgreedy1;

                // System.out.println("Lsgreedy:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_lsgreedy);
                // System.out.println("    Cost is " + cost_lsgreedy);
                // System.out.println("    The number of modified points is " + num_lsgreedy);
                // System.out.println("    Time is " + (time_lsgreedy2-time_lsgreedy1));

                // expsmooth
                dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                EWMA exp = new EWMA(dirtySeries, 0.042);
                long time_exp1 = System.currentTimeMillis();
                TimeSeries resultSeries_exp = exp.mainExp();
                long time_exp2 = System.currentTimeMillis();
                double rms_exp = assist.calcRMS(resultSeries_exp);
                double cost_exp = assist.calcCost(resultSeries_exp);
                int num_exp = assist.pointNum1(resultSeries_exp);
                totalRMS[i][4] += rms_exp;
                totalCOST[i][4] += cost_exp;
                totalNUM[i][4] += num_exp;
                totalTIME[i][4] = totalTIME[i][4] + time_exp2-time_exp1;

                // System.out.println("Expsmooth:");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_exp);
                // System.out.println("    Cost is " + cost_exp);
                // System.out.println("    The number of modified points is " + num_exp);
                // System.out.println("    Time is " + (time_exp2-time_exp1));

                // HTD-Cleaning
                dirtySeries = assist.readData_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise(dirtySeries, drate, seed);
                rmsDirty = assist.calcRMS(dirtySeries);
                HTD htd = new HTD(dirtySeries, sMax, sMin, 1, 1000);
                long time_HTD1 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD = htd.clean();
                long time_HTD2 = System.currentTimeMillis();
                double rms_HTD = assist.calcRMS_HTD(resultSeries_HTD);
                double cost_HTD = assist.calcCost(resultSeries_HTD);
                int num_HTD = assist.pointNum1(resultSeries_HTD);
                totalRMS[i][5] += rms_HTD;
                totalCOST[i][5] += cost_HTD;
                totalNUM[i][5] += num_HTD;
                totalTIME[i][5] = totalTIME[i][5] + time_HTD2-time_HTD1;
            }
            totalDirtyRMS /= expTime;
            System.out.println("Dirty RMS error is " + totalDirtyRMS);
            for(int j=0; j<methodNum; j++){
                totalRMS[i][j] /= expTime;
                totalCOST[i][j] /= expTime;
                totalNUM[i][j] /= expTime;
                totalTIME[i][j] /= expTime;
            }
        }
        String[] name = new String[]{" ","MTCSC","SCREEN","SpeedAcc","LsGreedy","EWMA","HTD"};
        String writefilename = "result/One/"+directory+"/errorRate/RMS.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalRMS);
        writefilename = "result/One/"+directory+"/errorRate/COST.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalCOST);
        writefilename = "result/One/"+directory+"/errorRate/NUM.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalNUM);
        writefilename = "result/One/"+directory+"/errorRate/TIME.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalTIME);
    }
}