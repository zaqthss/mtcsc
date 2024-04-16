package MTCSC.experiment;
import java.text.DecimalFormat;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_Uni;
import MTCSC.MTCSC_N;
import MTCSC.SCREEN;
import MTCSC.entity.TimeSeries;
import MTCSC.entity.TimeSeriesN;
import MTCSC.util.Assist;

public class TAO_dataSize_together {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "TAO/tao_test.csv";
        String directory = "TAO";

        double rate = 1.0;
        
        int T1 = 10;
        int T = 50;
        int n = 3;
        
        double[] speed = new double[2];
        double S = 2.8807;
        double sMax_1 = 1.400001;
        double sMin_1 = -1.600001;
        double sMax_2 = 1.610001;
        double sMin_2 = -1.670001;
        double sMax_3 = 1.750001;
        double sMin_3 = -1.440001;
        
        int methodNum = 7;
        double[] totalSize = new double[10];
        double[][] totalRMS = new double[10][methodNum];
        double[][] totalCOST = new double[10][methodNum];
        double[][] totalNUM = new double[10][methodNum];
        double[][] totalTIME = new double[10][methodNum];
        // Normalization or not
        boolean isNormalize = false;
        // boolean isNormalize = true;
        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.00");
            drate = Double.parseDouble(df.format(drate));
            double totalDirtyRMS = 0;
            
            int expTime = 10;
            int size = 5000*(i+1);
            totalSize[i] = size;
            System.out.println("Data size is " + size);
            for(int j=0; j<expTime; j++, seed++){
                if(isNormalize){
                    TimeSeriesN speedSeries = assist.readDataN_size(inputFileName, ",", n, size);
                    assist.normalizeN(speedSeries, n);
                    S= assist.getSpeedN(speedSeries, rate, n);
                    TimeSeries speedSeries_1 = assist.getN(speedSeries, 0);
                    TimeSeries speedSeries_2 = assist.getN(speedSeries, 1);
                    TimeSeries speedSeries_3 = assist.getN(speedSeries, 2);
                    speed = assist.getSpeed(speedSeries_1, rate);
                    sMin_1 = speed[0]; sMax_1 = speed[1];
                    speed = assist.getSpeed(speedSeries_2, rate);
                    sMin_2 = speed[0]; sMax_2 = speed[1];
                    speed = assist.getSpeed(speedSeries_3, rate);
                    sMin_3 = speed[0]; sMax_3 = speed[1];
                }  

                TimeSeriesN dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                double rmsDirty = assist.RMSN(dirtySeries, n);
                totalDirtyRMS += rmsDirty;
                
                // MyN
                MTCSC_N myn = new MTCSC_N(dirtySeries, S, T, n);
                long time1 = System.currentTimeMillis();
                TimeSeriesN resultSeries = myn.mainScreen();
                long time2 = System.currentTimeMillis();
                double rms_MyN = assist.RMSN(resultSeries, n);
                double cost_MyN = assist.CostNN(resultSeries, n);
                int num_MyN = assist.pointNumN(resultSeries, n);
                totalRMS[i][0] += rms_MyN;
                totalCOST[i][0] += cost_MyN;
                totalNUM[i][0] += num_MyN;
                totalTIME[i][0] = totalTIME[i][0] + time2 - time1;

                // ArrayList<Double> modify = new ArrayList<>();
                // ArrayList<Double> truth = new ArrayList<>();
                // for(TimePointN tp : resultSeries.getTimeseries()){
                //     modify = tp.getModify();
                //     truth = tp.getTruth();
                //     if((Math.abs(modify.get(0)-truth.get(0)) > 1) || (Math.abs(modify.get(1)-truth.get(1)) > 1) ||(Math.abs(modify.get(2)-truth.get(2)) > 1)){
                //         System.out.println(tp.getTimestamp()+" "+modify.get(0)+" "+truth.get(0)+" "+modify.get(1)+" "+truth.get(1)+" "+modify.get(2)+" "+truth.get(2));
                //     }
                // }
                
                // My1
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                TimeSeries dirtySeries_1 = assist.getN(dirtySeries, 0);
                TimeSeries dirtySeries_2 = assist.getN(dirtySeries, 1);
                TimeSeries dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_My1 = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // my1_1
                MTCSC_Uni my1_1 = new MTCSC_Uni(dirtySeries_1, sMax_1, sMin_1, T);
                long time_my1 = System.currentTimeMillis();
                TimeSeries resultSeries_my1_1 = my1_1.mainScreen();
                long time_my2 = System.currentTimeMillis();
                // my1_2
                MTCSC_Uni my1_2 = new MTCSC_Uni(dirtySeries_2, sMax_2, sMin_2, T);
                long time_my11 = System.currentTimeMillis();
                TimeSeries resultSeries_my1_2 = my1_2.mainScreen();
                long time_my22 = System.currentTimeMillis();
                // my1_3
                MTCSC_Uni my1_3 = new MTCSC_Uni(dirtySeries_3, sMax_3, sMin_3, T);
                long time_my111 = System.currentTimeMillis();
                TimeSeries resultSeries_my1_3 = my1_3.mainScreen();
                long time_my222 = System.currentTimeMillis();
                double rms_My1 = assist.RMS1(resultSeries_my1_1, resultSeries_my1_2, resultSeries_my1_3);
                double cost_My1 = assist.Cost33(resultSeries_my1_1, resultSeries_my1_2, resultSeries_my1_3);
                int num_My1 = assist.pointNum111(resultSeries_my1_1, resultSeries_my1_2, resultSeries_my1_3);
                totalRMS[i][1] += rms_My1;
                totalCOST[i][1] += cost_My1;
                totalNUM[i][1] += num_My1;
                totalTIME[i][1] = totalTIME[i][1] + time_my222-time_my111+time_my22-time_my11+time_my2-time_my1;

                // Screen
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                dirtySeries_1 = assist.getN(dirtySeries, 0);
                dirtySeries_2 = assist.getN(dirtySeries, 1);
                dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_Screen = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // Screen_1
                SCREEN MTCSC_1 = new SCREEN(dirtySeries_1, sMax_1, sMin_1, T1);
                long time3 = System.currentTimeMillis();
                TimeSeries resultSeries_1 = MTCSC_1.mainScreen();
                long time4 = System.currentTimeMillis();
                // Screen_2
                SCREEN MTCSC_2 = new SCREEN(dirtySeries_2, sMax_2, sMin_2, T1);
                long time5 = System.currentTimeMillis();
                TimeSeries resultSeries_2 = MTCSC_2.mainScreen();
                long time6 = System.currentTimeMillis();
                // Screen_3
                SCREEN MTCSC_3 = new SCREEN(dirtySeries_3, sMax_3, sMin_3, T1);
                long time7 = System.currentTimeMillis();
                TimeSeries resultSeries_3 = MTCSC_3.mainScreen();
                long time8 = System.currentTimeMillis();
                double rms_Screen = assist.RMS1(resultSeries_1, resultSeries_2, resultSeries_3);
                double cost_Screen = assist.Cost33(resultSeries_1, resultSeries_2, resultSeries_3);
                int num_Screen = assist.pointNum111(resultSeries_1, resultSeries_2, resultSeries_3);
                totalRMS[i][2] += rms_Screen;
                totalCOST[i][2] += cost_Screen;
                totalNUM[i][2] += num_Screen;
                totalTIME[i][2] = totalTIME[i][2] + time8-time7+time6-time5+time4-time3;

                // LocalSpeedAcc
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                dirtySeries_1 = assist.getN(dirtySeries, 0);
                dirtySeries_2 = assist.getN(dirtySeries, 1);
                dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_SpeedAcc = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // SpeedAcc_1
                SpeedAcc LSA_1 = new SpeedAcc(dirtySeries_1, T1, sMax_1, sMin_1, 4000, -4000);
                long time_SpeedAcc1 = System.currentTimeMillis();
                TimeSeries resultSeries_SpeedAcc_1 = LSA_1.mainSliUp();
                long time_SpeedAcc2 = System.currentTimeMillis();
                // SpeedAcc_2
                SpeedAcc LSA_2 = new SpeedAcc(dirtySeries_2, T1, sMax_2, sMin_2, 4000, -4000);
                long time_SpeedAcc3 = System.currentTimeMillis();
                TimeSeries resultSeries_SpeedAcc_2 = LSA_2.mainSliUp();
                long time_SpeedAcc4 = System.currentTimeMillis();
                // SpeedAcc_3
                SpeedAcc LSA_3 = new SpeedAcc(dirtySeries_3, T1, sMax_3, sMin_3, 4000, -4000);
                long time_SpeedAcc5 = System.currentTimeMillis();
                TimeSeries resultSeries_SpeedAcc_3 = LSA_3.mainSliUp();
                long time_SpeedAcc6 = System.currentTimeMillis();
                double rms_SpeedAcc = assist.RMS1(resultSeries_SpeedAcc_1,resultSeries_SpeedAcc_2,resultSeries_SpeedAcc_3);
                double cost_SpeedAcc = assist.Cost33(resultSeries_SpeedAcc_1,resultSeries_SpeedAcc_2,resultSeries_SpeedAcc_3);
                int num_SpeedAcc = assist.pointNum111(resultSeries_SpeedAcc_1,resultSeries_SpeedAcc_2,resultSeries_SpeedAcc_3);
                totalRMS[i][3] += rms_SpeedAcc;
                totalCOST[i][3] += cost_SpeedAcc;
                totalNUM[i][3] += num_SpeedAcc;
                totalTIME[i][3] = totalTIME[i][3] + time_SpeedAcc6-time_SpeedAcc5+time_SpeedAcc4-time_SpeedAcc3+time_SpeedAcc2-time_SpeedAcc1;

                // Lsgreedy
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                dirtySeries_1 = assist.getN(dirtySeries, 0);
                dirtySeries_2 = assist.getN(dirtySeries, 1);
                dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_Lsgreedy = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // Lsgreedy_1
                Lsgreedy lsgreedy_1 = new Lsgreedy(dirtySeries_1);
                long time_lsgreedy1 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy_1 = lsgreedy_1.repair();
                long time_lsgreedy2 = System.currentTimeMillis();
                // Lsgreedy_2
                Lsgreedy lsgreedy_2 = new Lsgreedy(dirtySeries_2);
                long time_lsgreedy3 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy_2 = lsgreedy_2.repair();
                long time_lsgreedy4 = System.currentTimeMillis();
                // Lsgreedy_3
                Lsgreedy lsgreedy_3 = new Lsgreedy(dirtySeries_3);
                long time_lsgreedy5 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy_3 = lsgreedy_3.repair();
                long time_lsgreedy6 = System.currentTimeMillis();
                double rms_lsgreedy = assist.RMS1(resultSeries_lsgreedy_1,resultSeries_lsgreedy_2,resultSeries_lsgreedy_3);
                double cost_lsgreedy = assist.Cost33(resultSeries_lsgreedy_1,resultSeries_lsgreedy_2,resultSeries_lsgreedy_3);
                int num_lsgreedy = assist.pointNum111(resultSeries_lsgreedy_1,resultSeries_lsgreedy_2,resultSeries_lsgreedy_3);
                totalRMS[i][4] += rms_lsgreedy;
                totalCOST[i][4] += cost_lsgreedy;
                totalNUM[i][4] += num_lsgreedy;
                totalTIME[i][4] = totalTIME[i][4] + time_lsgreedy2-time_lsgreedy1+time_lsgreedy6-time_lsgreedy5+time_lsgreedy4-time_lsgreedy3;


                // expsmooth
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                dirtySeries_1 = assist.getN(dirtySeries, 0);
                dirtySeries_2 = assist.getN(dirtySeries, 1);
                dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_expsmooth = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // expsmooth_1
                EWMA exp_1 = new EWMA(dirtySeries_1, 0.042);
                long time_exp1 = System.currentTimeMillis();
                TimeSeries resultSeries_exp_1 = exp_1.mainExp();
                long time_exp2 = System.currentTimeMillis();
                // expsmooth_2
                EWMA exp_2 = new EWMA(dirtySeries_2, 0.042);
                long time_exp3 = System.currentTimeMillis();
                TimeSeries resultSeries_exp_2 = exp_2.mainExp();
                long time_exp4 = System.currentTimeMillis();
                // expsmooth_3
                EWMA exp_3 = new EWMA(dirtySeries_3, 0.042);
                long time_exp5 = System.currentTimeMillis();
                TimeSeries resultSeries_exp_3 = exp_3.mainExp();
                long time_exp6 = System.currentTimeMillis();
                double rms_exp = assist.RMS1(resultSeries_exp_1,resultSeries_exp_2,resultSeries_exp_3);
                double cost_exp = assist.Cost33(resultSeries_exp_1,resultSeries_exp_2,resultSeries_exp_3);
                int num_exp = assist.pointNum111(resultSeries_exp_1,resultSeries_exp_2,resultSeries_exp_3);
                totalRMS[i][5] += rms_exp;
                totalCOST[i][5] += cost_exp;
                totalNUM[i][5] += num_exp;
                totalTIME[i][5] = totalTIME[i][5] + time_exp2-time_exp1+time_exp6-time_exp5+time_exp4-time_exp3;

                // HTD-Cleaning
                dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                if(isNormalize){
                    assist.normalizeN(dirtySeries, n);
                }
                dirtySeries_1 = assist.getN(dirtySeries, 0);
                dirtySeries_2 = assist.getN(dirtySeries, 1);
                dirtySeries_3 = assist.getN(dirtySeries, 2);
                double rmsDirty_HTD = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
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
                // HTD3
                HTD htd_3 = new HTD(dirtySeries_3, sMax_3, sMin_3, 1, maxnum);
                long time_HTD5 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD_3 = htd_3.clean();
                long time_HTD6 = System.currentTimeMillis();
                double rms_HTD = assist.RMS1(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                double cost_HTD = assist.Cost33(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                int num_HTD = assist.pointNum111(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                totalRMS[i][6] += rms_HTD;
                totalCOST[i][6] += cost_HTD;
                totalNUM[i][6] += num_HTD;
                totalTIME[i][6] = totalTIME[i][6] + time_HTD2-time_HTD1+time_HTD4-time_HTD3+time_HTD6-time_HTD5;
                
                // System.out.println("Seed : " + seed);
                // System.out.println("  MyN : ");
                // System.out.println("    Dirty RMS error is " + rmsDirty);
                // System.out.println("    Repair RMS error is " + rms_MyN);
                // System.out.println("    Cost is " + cost_MyN);
                // System.out.println("    The number of modified points is " + num_MyN);
                // System.out.println("    Time is " + (time2-time1));

                // System.out.println("  My1:");
                // System.out.println("    Dirty RMS error is " + rmsDirty_My1);
                // System.out.println("    Repair RMS error is " + rms_My1);
                // System.out.println("    Cost is " + cost_My1);
                // System.out.println("    The number of modified points is " + num_My1);
                // System.out.println("    Time is " + (time_my2-time_my1+time_my22-time_my11+time_my222-time_my111));
                
                // System.out.println("  Screen : ");
                // System.out.println("    Dirty RMS error is " + rmsDirty_Screen);
                // System.out.println("    Repair RMS error is " + rms_Screen);
                // System.out.println("    Cost is " + cost_Screen);
                // System.out.println("    The number of modified points is " + num_Screen);
                // System.out.println("    Time is " + (time4-time3+time6-time5+time8-time7));

                // System.out.println("  LocalSpeedAcc : ");
                // System.out.println("    Dirty RMS error is " + rmsDirty_SpeedAcc);
                // System.out.println("    Repair RMS error is " + rms_SpeedAcc);
                // System.out.println("    Cost is " + cost_SpeedAcc);
                // System.out.println("    The number of modified points is " + num_SpeedAcc);
                // System.out.println("    Time is " + (time_SpeedAcc6-time_SpeedAcc5+time_SpeedAcc4-time_SpeedAcc3+time_SpeedAcc2-time_SpeedAcc1));

                // System.out.println("Lsgreedy:");
                // System.out.println("    Dirty RMS error is " + rmsDirty_Lsgreedy);
                // System.out.println("    Repair RMS error is " + rms_lsgreedy);
                // System.out.println("    Cost is " + cost_lsgreedy);
                // System.out.println("    The number of modified points is " + num_lsgreedy);
                // System.out.println("    Time is " + (time_lsgreedy2-time_lsgreedy1+time_lsgreedy6-time_lsgreedy5+time_lsgreedy4-time_lsgreedy3));

                // System.out.println("Expsmooth:");
                // System.out.println("    Dirty RMS error is " + rmsDirty_expsmooth);
                // System.out.println("    Repair RMS error is " + rms_exp);
                // System.out.println("    Cost is " + cost_exp);
                // System.out.println("    The number of modified points is " + num_exp);
                // System.out.println("    Time is " + (time_exp2-time_exp1+time_exp6-time_exp5+time_exp4-time_exp3));
                
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
        String[] name = new String[]{" ", "MTCSC","MTCSC-Uni","SCREEN","SpeedAcc", "LsGreedy","EWMA","HTD"};
        if(isNormalize){
            String writefilename = "result/MultiDimension/"+directory+"/dataSize/together/RMS_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalRMS);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/COST_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalCOST);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/NUM_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalNUM);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/TIME_nor.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalTIME);
        }
        else{
            String writefilename = "result/MultiDimension/"+directory+"/dataSize/together/RMS.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalRMS);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/COST.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalCOST);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/NUM.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalNUM);
            writefilename = "result/MultiDimension/"+directory+"/dataSize/together/TIME.csv";
            assist.writeCSV(writefilename, name, totalSize ,totalTIME);
        }
        
    }
}
