package MTCSC.experiment;

import MTCSC.SCREEN;
import MTCSC.MTCSC_QCQP;
import MTCSC.MTCSC_2;
import MTCSC.RCSWS;
import java.text.DecimalFormat;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_Uni;
import MTCSC.entity.TimeSeries2;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class ILD_dataSize_together {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "ILD/temhum43k.data";
        int T1 = 10;
        int T2 = 100;

        double sMax_1=0.200001;
        double sMin_1=-0.200001;
        double sMax_2=0.200001;
        double sMin_2=-0.200001;
        double S=0.28285;
        int methodNum = 8;
        double[] totalSize = new double[9];
        double[][] totalRMS = new double[9][methodNum];
        double[][] totalCOST = new double[9][methodNum];
        double[][] totalNUM = new double[9][methodNum];
        double[][] totalTIME = new double[9][methodNum];
        // Normalization or not
        boolean isNormalize = false;
        // boolean isNormalize = true;
        for(int i=0; i<9; i++){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            double totalDirtyRMS = 0;
            int expTime = 10;
            int size = 5000*(i+1);
            totalSize[i] = size;
            System.out.println("Data Size is " + size);
            for (int j = 0; j < 10; j++,seed++) {
                if(isNormalize){
                    TimeSeries2 speedSeries = assist.readData2_size(inputFileName, ",", size);
                    assist.normalize(speedSeries);
                    TimeSeries speedSeries_1 = assist.getXY(speedSeries, 0);
                    TimeSeries speedSeries_2 = assist.getXY(speedSeries, 1);
                    double[] speed = new double[2];
                    double rate = 1.0;
                    S = assist.getSpeed2(speedSeries, rate);
                    speed = assist.getSpeed(speedSeries_1, rate);
                    sMin_1 = speed[0]; sMax_1 = speed[1];
                    speed = assist.getSpeed(speedSeries_2, rate);
                    sMin_2 = speed[0]; sMax_2 = speed[1];
                }

                // clean
                TimeSeries2 cleanSeries = assist.readData2_size(inputFileName, ",", size);
                TimeSeries2 dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                double rmsDirty_two = assist.RMS2(dirtySeries);
                totalDirtyRMS += rmsDirty_two;

                // MTCSC
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                double rmsDirty_twoPlus = assist.RMS2(dirtySeries);
                
                MTCSC_2 tp = new MTCSC_2(dirtySeries, S, T2);
                long time100 = System.currentTimeMillis();
                TimeSeries2 resultSeries_twoPlus = tp.mainScreen();
                long time200 = System.currentTimeMillis();
                double rms_twoPlus = assist.RMS2(resultSeries_twoPlus);
                double cost_twoPlus = assist.Cost22(resultSeries_twoPlus);
                int num_twoPlus = assist.pointNum22(resultSeries_twoPlus);
                totalRMS[i][0] += rms_twoPlus;
                totalCOST[i][0] += cost_twoPlus;
                totalNUM[i][0] += num_twoPlus;
                totalTIME[i][0] = totalTIME[i][0] + time200-time100;

                // MTCSC-Uni
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
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
                totalRMS[i][1] += rms_My1;
                totalCOST[i][1] += cost_My1;
                totalNUM[i][1] += num_My1;
                totalTIME[i][1] = totalTIME[i][1] +time_my22-time_my11+time_my2-time_my1;

                // RCSWS
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                double rmsDirty_rcsws = assist.RMS2(dirtySeries);
                RCSWS rcsws = new RCSWS(dirtySeries, S);
                long time_rcsws1 = System.currentTimeMillis();
                TimeSeries2 resultSeries_rcsws = rcsws.clean(50);
                long time_rcsws2 = System.currentTimeMillis();

                double rms_rcsws = assist.RMS2(resultSeries_rcsws);
                double cost_rcsws = assist.Cost22(resultSeries_rcsws);
                int num_rcsws = assist.pointNum22(resultSeries_rcsws);
                totalRMS[i][7] += rms_rcsws;
                totalCOST[i][7] += cost_rcsws;
                totalNUM[i][7] += num_rcsws;
                totalTIME[i][7] = totalTIME[i][7] + time_rcsws2-time_rcsws1;
            
                // Screen
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                dirtySeries_1 = assist.getXY(dirtySeries, 0);
                dirtySeries_2 = assist.getXY(dirtySeries, 1);
                double rmsDirty_12 = assist.RMS1(dirtySeries_1, dirtySeries_2);
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
                totalRMS[i][2] += rms_12;
                totalCOST[i][2] += cost_12;
                totalNUM[i][2] += num_1;
                totalTIME[i][2] = totalTIME[i][2] + time6-time5+time4-time3;
                
                // SpeedAcc
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                dirtySeries_1 = assist.getXY(dirtySeries, 0);
                dirtySeries_2 = assist.getXY(dirtySeries, 1);
                double rmsDirty_SpeedAcc = assist.RMS1(dirtySeries_1, dirtySeries_2);
                // SpeedAcc_1
                SpeedAcc LSA_1 = new SpeedAcc(dirtySeries_1, T1, sMax_1, sMin_1, 400, -400);
                long time_SpeedAcc1 = System.currentTimeMillis();
                TimeSeries resultSeries_LocalSpeedAcc_1 = LSA_1.mainSliUp();
                long time_SpeedAcc2 = System.currentTimeMillis();
                // SpeedAcc_2
                SpeedAcc LSA_2 = new SpeedAcc(dirtySeries_2, T1, sMax_2, sMin_2, 400, -400);
                long time_SpeedAcc3 = System.currentTimeMillis();
                TimeSeries resultSeries_LocalSpeedAcc_2 = LSA_2.mainSliUp();
                long time_SpeedAcc4 = System.currentTimeMillis();

                double rms_SpeedAcc = assist.RMS1(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
                double cost_SpeedAcc = assist.Cost11(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
                int num_SpeedAcc = assist.pointNum11(resultSeries_LocalSpeedAcc_1, resultSeries_LocalSpeedAcc_2);
                totalRMS[i][3] += rms_SpeedAcc;
                totalCOST[i][3] += cost_SpeedAcc;
                totalNUM[i][3] += num_SpeedAcc;
                totalTIME[i][3] = totalTIME[i][3] + time_SpeedAcc4-time_SpeedAcc3+time_SpeedAcc2-time_SpeedAcc1;

                // Lsgreedy_1
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                dirtySeries_1 = assist.getXY(dirtySeries, 0);
                dirtySeries_2 = assist.getXY(dirtySeries, 1);
                double rmsDirty_lsgreedy = assist.RMS1(dirtySeries_1, dirtySeries_2);
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
                totalRMS[i][4] += rms_lsgreedy;
                totalCOST[i][4] += cost_lsgreedy;
                totalNUM[i][4] += num_lsgreedy;
                totalTIME[i][4] = totalTIME[i][4] + time666-time555+time444-time333;

                //expsmooth
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                dirtySeries_1 = assist.getXY(dirtySeries, 0);
                dirtySeries_2 = assist.getXY(dirtySeries, 1);
                double rmsDirty_expsmooth = assist.RMS1(dirtySeries_1, dirtySeries_2);
                EWMA exp_1 = new EWMA(dirtySeries_1, 0.042);
                long time17 = System.currentTimeMillis();
                TimeSeries resultSeries_5 = exp_1.mainExp();
                long time18 = System.currentTimeMillis();

                EWMA exp_2 = new EWMA(dirtySeries_2, 0.042);
                long time19 = System.currentTimeMillis();
                TimeSeries resultSeries_6 = exp_2.mainExp();
                long time20 = System.currentTimeMillis();

                double rms_expsmooth = assist.RMS1(resultSeries_5, resultSeries_6);
                double cost_expsmooth = assist.Cost11(resultSeries_5, resultSeries_6);
                int num_expsmooth = assist.pointNum11(resultSeries_5, resultSeries_6);
                totalRMS[i][5] += rms_expsmooth;
                totalCOST[i][5] += cost_expsmooth;
                totalNUM[i][5] += num_expsmooth;
                totalTIME[i][5] = totalTIME[i][5] + time20-time19+time18-time17;

                // HTD
                cleanSeries = assist.readData2_size(inputFileName, ",", size);
                dirtySeries = assist.addNoise2_maxmin_together(cleanSeries, drate, seed);
                if(isNormalize){
                    assist.normalize(dirtySeries);
                }
                dirtySeries_1 = assist.getXY(dirtySeries, 0);
                dirtySeries_2 = assist.getXY(dirtySeries, 1);
                double rmsDirty_HTD = assist.RMS1(dirtySeries_1, dirtySeries_2);
                // HTD1
                HTD htd_1 = new HTD(dirtySeries_1, sMax_1, sMin_1, 1, 1000);
                long time_HTD1 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD_1 = htd_1.clean();
                long time_HTD2 = System.currentTimeMillis();
                // HTD2
                HTD htd_2 = new HTD(dirtySeries_2, sMax_2, sMin_2, 1, 1000);
                long time_HTD3 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD_2 = htd_2.clean();
                long time_HTD4 = System.currentTimeMillis();
                double rms_HTD = assist.RMS1(resultSeries_HTD_1, resultSeries_HTD_2);
                double cost_HTD = assist.Cost11(resultSeries_HTD_1, resultSeries_HTD_2);
                int num_HTD = assist.pointNum11(resultSeries_HTD_1, resultSeries_HTD_2);
                totalRMS[i][6] += rms_HTD;
                totalCOST[i][6] += cost_HTD;
                totalNUM[i][6] += num_HTD;
                totalTIME[i][6] = totalTIME[i][6] + time_HTD2-time_HTD1+time_HTD4-time_HTD3;
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
        String[] name = new String[]{" ","MTCSC","MTCSC-Uni","SCREEN","SpeedAcc","LsGreedy","EWMA","HTD","RCSWS"};
        if(isNormalize){
            String writefilename = "result/Two/TemHum/dataSize/together/RMS_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalRMS);
            writefilename = "result/Two/TemHum/dataSize/together/COST_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalCOST);
            writefilename = "result/Two/TemHum/dataSize/together/NUM_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalNUM);
            writefilename = "result/Two/TemHum/dataSize/together/TIME_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalTIME);
        }
        else{
            String writefilename = "result/Two/TemHum/dataSize/together/RMS.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalRMS);
            writefilename = "result/Two/TemHum/dataSize/together/COST.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalCOST);
            writefilename = "result/Two/TemHum/dataSize/together/NUM.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalNUM);
            writefilename = "result/Two/TemHum/dataSize/together/TIME.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalTIME);
        }
    }
}