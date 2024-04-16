package cn.edu.thu.MTCSC.experiment;
import java.text.DecimalFormat;
import java.util.ArrayList;
import cn.edu.thu.MTCSC.EWMA;
import cn.edu.thu.MTCSC.HTD;
import cn.edu.thu.MTCSC.SpeedAcc;
import cn.edu.thu.MTCSC.Lsgreedy;
import cn.edu.thu.MTCSC.MTCSC_Uni;
import cn.edu.thu.MTCSC.MTCSC_N;
import cn.edu.thu.MTCSC.SCREEN;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.entity.TimeSeriesN;
import cn.edu.thu.MTCSC.util.Assist;

public class ECG_errorRate_together {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "ECG/ECG_5k.csv";
        String directory = "ECG";
        
        double rate = 0.98;

        int T1 = 10;
        int T = 100;
        int n = 32;
        
        double[] speed = new double[2];
        double S = 3.0;
        double[] sMax = new double[32];
        double[] sMin = new double[32];
        
        int methodNum = 7;
        double[] totalDrate = new double[10];
        double[][] totalRMS = new double[10][methodNum];
        double[][] totalCOST = new double[10][methodNum];
        double[][] totalNUM = new double[10][methodNum];
        double[][] totalTIME = new double[10][methodNum];

        for(int i=0; i<10; i++){
            int seed = 1;
            double drate= 0.05 + 0.025*i;
            DecimalFormat df = new DecimalFormat("#.000");
            drate = Double.parseDouble(df.format(drate));
            double totalDirtyRMS = 0;
            int expTime = 10;
            int size = 5000;
            totalDrate[i] = drate;
            System.out.println("Data rate is " + drate);
            for(int j=0; j<expTime; j++, seed++){
                // calculate the speed
                for(int h=0; h<n; h++){
                    TimeSeriesN speedSeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                    TimeSeries speedSeries_1 = assist.getN(speedSeries, h);
                    speed = assist.getSpeed(speedSeries_1, rate);
                    sMin[h] = speed[0]; sMax[h] = speed[1];
                }
                // TimeSeriesN speedSeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                // S= assist.getSpeedN(speedSeries, rate, n);
                // S= 5;

                TimeSeriesN dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
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
                
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                // My1
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    MTCSC_Uni my1_1 = new MTCSC_Uni(dirtySeries_1.get(h), sMax[h], sMin[h], T);
                    long time_my1 = System.currentTimeMillis();
                    my1_1.mainScreen();
                    long time_my2 = System.currentTimeMillis();
                    totalTIME[i][1] = totalTIME[i][1] + time_my2-time_my1;
                }
                double rms_my1 = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][1] += rms_my1;
                
                dirtySeries_1.clear();
                // Screen
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    SCREEN screen_1 = new SCREEN(dirtySeries_1.get(h), sMax[h], sMin[h], T);
                    long time_screen1 = System.currentTimeMillis();
                    screen_1.mainScreen();
                    long time_screen2 = System.currentTimeMillis();
                    totalTIME[i][2] = totalTIME[i][2] + time_screen2-time_screen1;
                }
                double rms_screen = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][2] += rms_screen;

                dirtySeries_1.clear();
                // LocalSpeedAcc
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    SpeedAcc LSA = new SpeedAcc(dirtySeries_1.get(h), T1, sMax[h], sMin[h], 1500, -1500);
                    long time_LSA1 = System.currentTimeMillis();
                    LSA.mainSliUp();
                    long time_LSA2 = System.currentTimeMillis();
                    totalTIME[i][3] = totalTIME[i][3] + time_LSA2-time_LSA1;
                }
                double rms_SpeedAcc = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][3] += rms_SpeedAcc;
                
                dirtySeries_1.clear();
                // Lsgreedy
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    Lsgreedy lsgreedy_1 = new Lsgreedy(dirtySeries_1.get(h));
                    long time_lsgreedy1 = System.currentTimeMillis();
                    lsgreedy_1.repair();
                    long time_lsgreedy2 = System.currentTimeMillis();
                    totalTIME[i][4] = totalTIME[i][4] + time_lsgreedy2-time_lsgreedy1;
                }
                double rms_lsgreedy = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][4] += rms_lsgreedy;
            
                dirtySeries_1.clear();
                // expsmooth
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    EWMA exp = new EWMA(dirtySeries_1.get(h), 0.042);
                    long time_exp1 = System.currentTimeMillis();
                    exp.mainExp();
                    long time_exp2 = System.currentTimeMillis();
                    totalTIME[i][5] = totalTIME[i][5] + time_exp2-time_exp1;
                }
                double rms_exp = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][5] += rms_exp;
                
                dirtySeries_1.clear();
                // HTD-Cleaning
                dirtySeries = assist.readDataN_ECG(inputFileName, ",", n, size);
                dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(dirtySeries, h));
                    HTD htd = new HTD(dirtySeries_1.get(h), sMax[h], sMin[h], 1, 1000);
                    long time_HTD1 = System.currentTimeMillis();
                    htd.clean();
                    long time_HTD2 = System.currentTimeMillis();
                    totalTIME[i][6] = totalTIME[i][6] + time_HTD2-time_HTD1;
                }
                double rms_HTD = assist.RMSN(dirtySeries_1, n);
                totalRMS[i][6] += rms_HTD;
                // dirtySeries = assist.readDataN_size(inputFileName, ",", n, size);
                // dirtySeries = assist.addNoiseN_maxmin_together(dirtySeries, drate, seed, n);
                // if(isNormalize){
                //     assist.normalizeN(dirtySeries, n);
                // }
                // dirtySeries_1 = assist.getN(dirtySeries, 0);
                // dirtySeries_2 = assist.getN(dirtySeries, 1);
                // dirtySeries_3 = assist.getN(dirtySeries, 2);
                // double rmsDirty_HTD = assist.RMS1(dirtySeries_1, dirtySeries_2, dirtySeries_3);
                // int maxnum = 1000;
                // // HTD1
                // HTDCleaning htd_1 = new HTDCleaning(dirtySeries_1, sMax_1, sMin_1, 1, maxnum);
                // long time_HTD1 = System.currentTimeMillis();
                // TimeSeries resultSeries_HTD_1 = htd_1.clean();
                // long time_HTD2 = System.currentTimeMillis();
                // // HTD2
                // HTDCleaning htd_2 = new HTDCleaning(dirtySeries_2, sMax_2, sMin_2, 1, maxnum);
                // long time_HTD3 = System.currentTimeMillis();
                // TimeSeries resultSeries_HTD_2 = htd_2.clean();
                // long time_HTD4 = System.currentTimeMillis();
                // // HTD3
                // HTDCleaning htd_3 = new HTDCleaning(dirtySeries_3, sMax_3, sMin_3, 1, maxnum);
                // long time_HTD5 = System.currentTimeMillis();
                // TimeSeries resultSeries_HTD_3 = htd_3.clean();
                // long time_HTD6 = System.currentTimeMillis();
                // double rms_HTD = assist.RMS1(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                // double cost_HTD = assist.Cost33(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                // int num_HTD = assist.pointNum111(resultSeries_HTD_1, resultSeries_HTD_2, resultSeries_HTD_3);
                // totalRMS[i][6] += rms_HTD;
                // totalCOST[i][6] += cost_HTD;
                // totalNUM[i][6] += num_HTD;
                // totalTIME[i][6] = totalTIME[i][6] + time_HTD2-time_HTD1+time_HTD4-time_HTD3+time_HTD6-time_HTD5;
                
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
        
        String writefilename = "result/MultiDimension/"+directory+"/errorRate/together/RMS.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalRMS);
        writefilename = "result/MultiDimension/"+directory+"/errorRate/together/COST.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalCOST);
        writefilename = "result/MultiDimension/"+directory+"/errorRate/together/NUM.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalNUM);
        writefilename = "result/MultiDimension/"+directory+"/errorRate/together/TIME.csv";
        assist.writeCSV(writefilename, name, totalDrate ,totalTIME);
        
    }
}
