package MTCSC.experiment.app;

import java.util.ArrayList;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_Uni;
import MTCSC.SCREEN;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class DSR {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String filePath = "data/UCR/DSR/DiatomSizeReduction_TRAIN.tsv";
        String dataName = "DSR";
        
        ArrayList<Integer> labels = assist.appGetLabel(filePath);

        double[] sMax = {0.074001, 0.081001, 0.067001, 0.065001};
        double[] sMin = {-0.074001, -0.081001, -0.067001, -0.065001};

        int seed = 1;
        double drate = 0.1;
        for(int j=0; j<10; j++, seed++){
            assist.appAddNoiseSave(filePath, drate, seed, dataName);
            ArrayList<TimeSeries> TimeSeriesList = new ArrayList<>();
            TimeSeries dirtySeries;
            String writePath = "";
            int label;
            long T = 10;

            double RMS = 0;
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                double rms_my1 = assist.calcRMS(dirtySeries);
                RMS += rms_my1;
            }
            System.out.println("Dirty rms is : " + RMS);
            
            // My1
            RMS = 0;
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                MTCSC_Uni my1 = new MTCSC_Uni(dirtySeries, sMax[label], sMin[label], T);
                // long time_my1 = System.currentTimeMillis();
                TimeSeries resultSeries_my1 = my1.mainScreen();
                // long time_my2 = System.currentTimeMillis();
                double rms_my1 = assist.calcRMS(resultSeries_my1);
                // double cost_my1 = assist.calcCost(resultSeries_my1);
                // int num_my1 = assist.pointNum1(resultSeries_my1);
                RMS += rms_my1;
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_My1.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);
            System.out.println("My1 rms is : " + RMS);

            // SCREEN
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                SCREEN screen = new SCREEN(dirtySeries, sMax[label], sMin[label], T);
                // long time_screen1 = System.currentTimeMillis();
                TimeSeries resultSeries_screen = screen.mainScreen();
                // long time_screen2 = System.currentTimeMillis();
                // double rms_screen = assist.calcRMS(resultSeries_screen);
                // double cost_screen = assist.calcCost(resultSeries_screen);
                // int num_screen = assist.pointNum1(resultSeries_screen);
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_Screen.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);

            // SpeedAcc
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                SpeedAcc LSA = new SpeedAcc(dirtySeries, T, sMax[label], sMin[label], 100, -100);
                // long time_LocalSpeedAcc1 = System.currentTimeMillis();
                TimeSeries resultSeries_LocalSpeedAcc = LSA.mainSliUp();
                // long time_LocalSpeedAcc2 = System.currentTimeMillis();
                // double rms_LocalSpeedAcc = assist.calcRMS(resultSeries_LocalSpeedAcc);
                // double cost_LocalSpeedAcc = assist.calcCost(resultSeries_LocalSpeedAcc);
                // int num_LocalSpeedAcc = assist.pointNum1(resultSeries_LocalSpeedAcc);
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_SpeedAcc.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);
            
            // Lsgreedy
            RMS = 0;
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                Lsgreedy lsgreedy = new Lsgreedy(dirtySeries);
                // long time_lsgreedy1 = System.currentTimeMillis();
                TimeSeries resultSeries_lsgreedy = lsgreedy.repair();
                // long time_lsgreedy2 = System.currentTimeMillis();
                double rms_lsgreedy = assist.calcRMS(resultSeries_lsgreedy);
                // double cost_lsgreedy = assist.calcCost(resultSeries_lsgreedy);
                // int num_lsgreedy = assist.pointNum1(resultSeries_lsgreedy);
                RMS += rms_lsgreedy;
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_Lsgreedy.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);
            System.out.println("Lsgreedy rms is : " + RMS);

            // Expsmooth
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                EWMA exp = new EWMA(dirtySeries, 0.042);
                // long time_exp1 = System.currentTimeMillis();
                TimeSeries resultSeries_exp = exp.mainExp();
                // long time_exp2 = System.currentTimeMillis();
                // double rms_exp = assist.calcRMS(resultSeries_exp);
                // double cost_exp = assist.calcCost(resultSeries_exp);
                // int num_exp = assist.pointNum1(resultSeries_exp);
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_Expsmooth.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);
            

            // HTD-Cleaning
            TimeSeriesList = assist.appAddNoiseTimeSeires(filePath, drate, seed);
            for(int i=0; i<TimeSeriesList.size(); i++){
                dirtySeries = TimeSeriesList.get(i);
                label = labels.get(i)-1;
                HTD htd = new HTD(dirtySeries, sMax[label], sMin[label], 1, 1000);
                // long time_HTD1 = System.currentTimeMillis();
                TimeSeries resultSeries_HTD = htd.clean();
                // long time_HTD2 = System.currentTimeMillis();
                // double rms_HTD = assist.calcRMS_HTD(resultSeries_HTD);
                // double cost_HTD = assist.calcCost(resultSeries_HTD);
                // int num_HTD = assist.pointNum1(resultSeries_HTD);
            }
            writePath = dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+"_HTD.tsv";
            assist.saveTSVFromTimeSeriesList(TimeSeriesList, writePath, labels);
        }
    }
}

