package cn.edu.thu.MTCSC.test;

import cn.edu.thu.MTCSC.MTCSC_2;
import cn.edu.thu.MTCSC.MTCSC_AS;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Assist;

public class dstTest {
    public static void main(String[] args) {
        Assist assist = new Assist();
        // String inputFileName = "trgps-high-final-e4.data";  // “高精度”真实采集的，把gps-high-final.data转化为XY坐标
        String inputFileName = "DynamicSpeed/dstest.data";  // 100数据点之后速度放大了3倍
        
        int T2 = 100;

        // get speed , 95%
        TimeSeries2 speedSeries = assist.readData2(inputFileName, ",");
        TimeSeries speedSeries_1 = assist.getXY(speedSeries, 0);
        TimeSeries speedSeries_2 = assist.getXY(speedSeries, 1);
        double[] speed = new double[2];
        double rate = 1;
        
        double sMax_1 = 2.169900; //1.66520m/s
        double sMin_1 = -2.169900;
        double sMax_2 = 2.169900; //1.668520m/s
        double sMin_2 = -2.169900;
        double S = 2.169900;
        // S = assist.getSpeed2(speedSeries, rate);
        // speed = assist.getSpeed(speedSeries_1, rate);
        // sMin_1 = speed[0]; sMax_1 = speed[1];
        // speed = assist.getSpeed(speedSeries_2, rate);
        // sMin_2 = speed[0]; sMax_2 = speed[1];

        TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_two = assist.RMS2(dirtySeries);

        // Screen2 
        // S = 0.047884; // 前4000速度
        double SS = 0.115446;  //  4000-5000速度
        double S5 = 0.081278;
        double S6 = 0.111062;
        double S7 = 0.055005;
        double S8 = 0.049852;
        double S9 = 0.074490;

        // two-plus
        dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_twoPlus = assist.RMS2(dirtySeries);
        MTCSC_2 tp = new MTCSC_2(dirtySeries, S, T2);
        long time100 = System.currentTimeMillis();
        TimeSeries2 resultSeries_twoPlus = tp.mainScreen();
        long time200 = System.currentTimeMillis();
        double rms_twoPlus = assist.RMS2(resultSeries_twoPlus);
        double cost_twoPlus = assist.Cost22(resultSeries_twoPlus);
        int num_twoPlus = assist.pointNum22(resultSeries_twoPlus);

        // two-plus
        System.out.println("two-plus:");
        System.out.println("    Dirty RMS error is " + rmsDirty_twoPlus);
        System.out.println("    Repair RMS error is " + rms_twoPlus);
        System.out.println("    Cost is " + cost_twoPlus);
        System.out.println("    Time is " + (time200-time100));
        System.out.println("    The number of modified points is " + num_twoPlus);

        // two-plus-ds
        dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_twoPlusDS = assist.RMS2(dirtySeries);
        MTCSC_AS tpDS = new MTCSC_AS(dirtySeries, S, T2, 0.05, 1.0, 50, 0.75, 5);
        long time_twoPlusDS1 = System.currentTimeMillis();
        TimeSeries2 resultSeries_twoPlusDS = tpDS.mainScreen();
        long time_twoPlusDS2 = System.currentTimeMillis();
        double rms_twoPlusDS = assist.RMS2(resultSeries_twoPlusDS);
        double cost_twoPlusDS = assist.Cost22(resultSeries_twoPlusDS);
        int num_twoPlusDS = assist.pointNum22(resultSeries_twoPlusDS);
        
        // two-plus-DS
        System.out.println("two-plus-ds:");
        System.out.println("    Dirty RMS error is " + rmsDirty_twoPlusDS);
        System.out.println("    Repair RMS error is " + rms_twoPlusDS);
        System.out.println("    Cost is " + cost_twoPlusDS);
        System.out.println("    Time is " + (time_twoPlusDS2-time_twoPlusDS1));
        System.out.println("    The number of modified points is " + num_twoPlusDS);
        
    }
}