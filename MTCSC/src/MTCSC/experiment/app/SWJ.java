package MTCSC.experiment.app;
import java.util.ArrayList;
import MTCSC.EWMA;
import MTCSC.HTD;
import MTCSC.SpeedAcc;
import MTCSC.Lsgreedy;
import MTCSC.MTCSC_N;
import MTCSC.MTCSC_Uni;
import MTCSC.SCREEN;
import MTCSC.entity.TimeSeriesN;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class SWJ {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String path = "data/UEA/StandWalkJump/";
        String filePath = "data/UEA/StandWalkJump/train_dim";
        String labelPath = "data/UEA/StandWalkJump/train_label.csv";
        int n = 4;
        ArrayList<TimeSeriesN> TimeSeriesList = new ArrayList<>();
        int seed = 1;
        double drate = 0.1;
        int T = 10;
        int length = 2500;
        ArrayList<Integer> label= assist.UEAGetLabel(labelPath);
        
        for(int i=0; i<10; i++, seed++){
            System.out.println("Seed : "+ seed);
            // Dirty
            double RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                double rms_MyN = assist.RMSN(TimeSeriesList.get(j), n);
                RMS += rms_MyN;
            }
            System.out.println("    Dirty : "+ RMS);
            assist.saveUEAmutil(path, seed, drate, TimeSeriesList, n, "Dirty", length);

            // MTCSC
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                double S= assist.getSpeedN(TimeSeriesList.get(j), 1, n);
                MTCSC_N myn = new MTCSC_N(TimeSeriesList.get(j), S, T, n);
                TimeSeriesN resultSeries = myn.mainScreen();
                double rms_MyN = assist.RMSN(resultSeries, n);
                RMS += rms_MyN;
            }
            System.out.println("    MTCSC : "+ RMS);
            assist.saveUEAmutil(path, seed, drate, TimeSeriesList, n, "MTCSC", length);

            // MTCSC-Uni
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    double[] speed = assist.getSpeed(assist.getN(TimeSeriesList.get(j), h), 1.0);
                    double sMin = speed[0]; double sMax = speed[1];
                    MTCSC_Uni my1_1 = new MTCSC_Uni(dirtySeries_1.get(h), sMax, sMin, T);
                    my1_1.mainScreen();
                }
                double rms_my1 = assist.RMSN(dirtySeries_1, n);
                RMS += rms_my1;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "MTCSC-Uni", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "MTCSC-Uni", length, false);
                }
            }
            System.out.println("    MTCSC-Uni : "+ RMS);
            
            // SCREEN
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    double[] speed = assist.getSpeed(assist.getN(TimeSeriesList.get(j), h), 1.0);
                    double sMin = speed[0]; double sMax = speed[1];
                    SCREEN screen = new SCREEN(dirtySeries_1.get(h), sMax, sMin, T);
                    screen.mainScreen();
                }
                double rms_screen = assist.RMSN(dirtySeries_1, n);
                RMS += rms_screen;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "SCREEN", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "SCREEN", length, false);
                }
            }
            System.out.println("    SCREEN : "+ RMS);

            // SpeedAcc
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    double[] speed = assist.getSpeed(assist.getN(TimeSeriesList.get(j), h), 1.0);
                    double sMin = speed[0]; double sMax = speed[1];
                    SpeedAcc LSA = new SpeedAcc(dirtySeries_1.get(h), T, sMax, sMin, 1500, -1500);
                    LSA.mainSliUp();
                }
                double rms_SpeedAcc = assist.RMSN(dirtySeries_1, n);
                RMS += rms_SpeedAcc;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "SpeedAcc", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "SpeedAcc", length, false);
                }
            }
            System.out.println("    SpeedAcc : "+ RMS);
            
            // LsGreedy
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    Lsgreedy lsgreedy = new Lsgreedy(dirtySeries_1.get(h));
                    lsgreedy.repair();
                }
                double rms_Lsgreedy = assist.RMSN(dirtySeries_1, n);
                RMS += rms_Lsgreedy;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "LsGreedy", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "LsGreedy", length, false);
                }
            }
            System.out.println("    Lsgreedy : "+ RMS);

            // EWMA
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    EWMA exp = new EWMA(dirtySeries_1.get(h), 1.0);
                    exp.mainExp();
                }
                double rms_EWMA = assist.RMSN(dirtySeries_1, n);
                RMS += rms_EWMA;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "EWMA", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "EWMA", length, false);
                }
            }
            System.out.println("    EWMA : "+ RMS);

            // HTD
            RMS = 0;
            TimeSeriesList = assist.read_UEA(filePath, n);
            for(int j=0; j<TimeSeriesList.size(); j++){
                addNoise(assist, label, j, TimeSeriesList, drate, seed+j, n);
                ArrayList<TimeSeries> dirtySeries_1 = new ArrayList<>();
                for(int h=0; h<n; h++){
                    dirtySeries_1.add(assist.getN(TimeSeriesList.get(j), h));
                    double[] speed = assist.getSpeed(assist.getN(TimeSeriesList.get(j), h), 1.0);
                    double sMin = speed[0]; double sMax = speed[1];
                    HTD htd = new HTD(dirtySeries_1.get(h), sMax, sMin, 1, 1000);
                    htd.clean();
                }
                double rms_HTD = assist.RMSN(dirtySeries_1, n);
                RMS += rms_HTD;
                if(j == 0){
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "HTD", length, true);
                }
                else{
                    assist.saveUEAuni(path, seed, drate, dirtySeries_1, n, "HTD", length, false);
                }
            }
            System.out.println("    HTD : "+ RMS); 
        }
    }

    public static void addNoise(Assist assist, ArrayList<Integer> label, int index, ArrayList<TimeSeriesN> TimeSeriesList, double drate, int seed, int n) {
        Double[] noiseMinMax1 = {-1.26, 5.76, -1.47, 5.12, -0.75, 3.2, -0.89, 4.54};
        Double[] noiseMinMax2 = {-1.24, 5.41, -0.93, 4.67, -1.23, 5.6, -1.18, 4.57};
        Double[] noiseMinMax3 = {-1.76, 4.91, -3.61, 5.59, -1.73, 2.95, -0.9, 1.91};
        
        if(label.get(index) == 1)
            assist.addNoiseN_maxmin_together_UEA(TimeSeriesList.get(index), drate, seed, n, noiseMinMax1);
        else if(label.get(index) == 2)
            assist.addNoiseN_maxmin_together_UEA(TimeSeriesList.get(index), drate, seed, n, noiseMinMax2);
        else if(label.get(index) == 3)
            assist.addNoiseN_maxmin_together_UEA(TimeSeriesList.get(index), drate, seed, n, noiseMinMax3);
    }
}
