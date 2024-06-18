package MTCSC.experiment;
import java.text.DecimalFormat;
import MTCSC.Global_N;
import MTCSC.Local_N;
import MTCSC.MTCSC_N;
import MTCSC.entity.TimeSeries;
import MTCSC.entity.TimeSeriesN;
import MTCSC.util.Assist;

public class Varing_dimensions {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String inputFileName = "ECG/ECG_5k.csv";
        double rate = 0.98;

        int T = 100;
        int n = 32;
        
        double[] speed = new double[2];
        double S = 2.1;
        double[] sMax = new double[32];
        double[] sMin = new double[32];
        
        int methodNum = 3;
        double[] totalDimension = {1, 2, 4, 8, 16, 32};
        double[][] totalRMS = new double[6][methodNum];
        double[][] totalCOST = new double[6][methodNum];
        double[][] totalNUM = new double[6][methodNum];
        double[][] totalTIME = new double[6][methodNum];
        double[] totalDirtyRMS = new double[methodNum];

        n = 1;
        for(int i=0; i<6; i++, n=n*2, S=3.0){
            int seed = 1;
            double drate= 0.05;
            DecimalFormat df = new DecimalFormat("#.00");
            drate = Double.parseDouble(df.format(drate));
            int expTime = 10;
            int size = 5000;
            System.out.println("Dimension is " + n);
            for(int j=0; j<expTime; j++, seed++){
                // MyN
                TimeSeriesN dirtySeries_1 = assist.readDataN(inputFileName, ",", 32, n, size);
                dirtySeries_1 = assist.addNoiseN_maxmin_together_ecg(dirtySeries_1, drate, seed, n);
                // TimeSeriesN speedSeries_1 = assist.readDataN(inputFileName, ",", 32, n, size);
                // S = assist.getSpeedN(speedSeries_1, 0.98, n);
                totalDirtyRMS[0] += assist.RMSN(dirtySeries_1, n);
                MTCSC_N myn_1 = new MTCSC_N(dirtySeries_1, S, T, n);
                long time1_1 = System.currentTimeMillis();
                TimeSeriesN resultSeries_1 = myn_1.mainScreen();
                long time2_1 = System.currentTimeMillis();
                double rms_MyN_1 = assist.RMSN(resultSeries_1, n);
                double cost_MyN_1 = assist.CostNN(resultSeries_1, n);
                int num_MyN_1 = assist.pointNumN(resultSeries_1, n);
                totalRMS[i][0] += rms_MyN_1;
                totalCOST[i][0] += cost_MyN_1;
                totalNUM[i][0] += num_MyN_1;
                totalTIME[i][0] = totalTIME[i][0] + time2_1 - time1_1;

                // Global
                dirtySeries_1 = assist.readDataN(inputFileName, ",", 32, n, size);
                dirtySeries_1 = assist.addNoiseN_maxmin_together_ecg(dirtySeries_1, drate, seed, n);
                Global_N global = new Global_N(dirtySeries_1, S, n);
                long time_global1 = System.currentTimeMillis();
                TimeSeriesN resultSeries_global = global.clean();
                long time_global2 = System.currentTimeMillis();
                double rms_global = assist.RMSN(resultSeries_global, n);
                double cost_global = assist.CostNN(resultSeries_global, n);
                int num_global = assist.pointNumN(resultSeries_global, n);
                totalRMS[i][1] += rms_global;
                totalCOST[i][1] += cost_global;
                totalNUM[i][1] += num_global;
                totalTIME[i][1] = totalTIME[i][1] + time_global2-time_global1;

                // Local
                dirtySeries_1 = assist.readDataN(inputFileName, ",", 32, n, size);
                dirtySeries_1 = assist.addNoiseN_maxmin_together_ecg(dirtySeries_1, drate, seed, n);
                Local_N local = new Local_N(dirtySeries_1, S, T, n);
                long time_local1 = System.currentTimeMillis();
                TimeSeriesN resultSeries_local = local.mainScreen();
                long time_local2 = System.currentTimeMillis();
                double rms_local = assist.RMSN(resultSeries_local, n);
                double cost_local = assist.CostNN(resultSeries_local, n);
                int num_local = assist.pointNumN(resultSeries_local, n);
                totalRMS[i][2] += rms_local;
                totalCOST[i][2] += cost_local;
                totalNUM[i][2] += num_local;
                totalTIME[i][2] = totalTIME[i][2] + time_local2-time_local1;
            }
            
            for(int j=0; j<methodNum; j++){
                totalDirtyRMS[j] /= expTime;
                totalRMS[i][j] /= expTime;
                totalCOST[i][j] /= expTime;
                totalNUM[i][j] /= expTime;
                totalTIME[i][j] /= expTime;
            }
        }

        String[] name = new String[]{" ", "MTCSC-C","MTCSC-G","MTCSC-L"};
        
        String writefilename = "result/VaringDimension/RMS.csv";
        assist.writeCSV(writefilename, name, totalDimension ,totalRMS);
        writefilename = "result/VaringDimension/COST.csv";
        assist.writeCSV(writefilename, name, totalDimension ,totalCOST);
        writefilename = "result/VaringDimension/NUM.csv";
        assist.writeCSV(writefilename, name, totalDimension ,totalNUM);
        writefilename = "result/VaringDimension/TIME.csv";
        assist.writeCSV(writefilename, name, totalDimension ,totalTIME);
    }
}
