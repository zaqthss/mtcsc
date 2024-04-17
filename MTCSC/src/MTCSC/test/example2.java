package MTCSC.test;

import MTCSC.SCREEN;
import MTCSC.TwoMILP;

import java.util.ArrayList;

import MTCSC.EWMA;
import MTCSC.QCQP;
import MTCSC.Global_2;
import MTCSC.MTCSC_QCQP;
import MTCSC.MTCSC_2;
import MTCSC.entity.TimeSeries2;
import MTCSC.entity.TimePoint;
import MTCSC.entity.TimePoint2;
import MTCSC.entity.TimeSeries;
import MTCSC.util.Assist;

public class example2 {
    public static void main(String[] args) {
        Assist assist = new Assist();

        String inputFileName = "example/example2.data";
        double sMax_1 = 1.000; //1.66520m/s
        double sMin_1 = -1.000;
        double sMax_2 = 1.000; //1.668520m/s
        double sMin_2 = -1.000;
        double S = 1.000;
        

        int T1 = 3;
        int T2 = 3;

        // My2 (QCQP streaming)
        TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_two = assist.RMS2(dirtySeries);
        MTCSC_QCQP MTCSC2 = new MTCSC_QCQP(dirtySeries, S, T2);
        long time1 = System.currentTimeMillis();
        TimeSeries2 resultSeries_two = MTCSC2.mainScreen();
        long time2 = System.currentTimeMillis();
        double rms_two = assist.RMS2(resultSeries_two);
        double cost_two = assist.Cost22(resultSeries_two);
        int num_two = assist.pointNum22(resultSeries_two);

        // QCQP
        dirtySeries = assist.readData2(inputFileName, ",");
        QCQP globalGurobi = new QCQP(dirtySeries, 1, 10000);
        double rmsDirty_QCQP = assist.RMS2(dirtySeries);
        long timeQCQP1 = System.currentTimeMillis();
		TimeSeries2 resultSeries_QCQP = globalGurobi.mainGlobal();
        long timeQCQP2 = System.currentTimeMillis();
        double rms_QCQP = assist.RMS2(resultSeries_QCQP);
        double cost_QCQP = assist.Cost22(resultSeries_QCQP);
        int num_QCQP = assist.pointNum22(resultSeries_QCQP);

        // TwoMILP
        dirtySeries = assist.readData2(inputFileName, ",");
        TwoMILP tmilp = new TwoMILP(dirtySeries, 1, 10000);
        double rmsDirty_TwoMILP = assist.RMS2(dirtySeries);
        long timeTwoMILP1 = System.currentTimeMillis();
		TimeSeries2 resultSeries_TwoMILP = tmilp.mainGlobal();
        long timeTwoMILP2 = System.currentTimeMillis();
        double rms_TwoMILP = assist.RMS2(resultSeries_TwoMILP);
        double cost_TwoMILP = assist.Cost22(resultSeries_TwoMILP);
        int num_TwoMILP = assist.pointNum22(resultSeries_TwoMILP);

        // MyGlobal
        dirtySeries = assist.readData2(inputFileName, ",");
        Global_2 gt = new Global_2(dirtySeries, 1);
        ArrayList<Integer> outlierIndex = gt.outlierDetection();
        System.out.println("two-MyGlobal:");
        for(int index : outlierIndex){
            System.out.println("outlierIndex is:" + index);
        }

        // My2-plus
        dirtySeries = assist.readData2(inputFileName, ",");
        double rmsDirty_twoPlus = assist.RMS2(dirtySeries);
        MTCSC_2 tp = new MTCSC_2(dirtySeries, S, T2);
        long time100 = System.currentTimeMillis();
        TimeSeries2 resultSeries_twoPlus = tp.mainScreen();
        long time200 = System.currentTimeMillis();
        double rms_twoPlus = assist.RMS2(resultSeries_twoPlus);
        double cost_twoPlus = assist.Cost22(resultSeries_twoPlus);
        int num_twoPlus = assist.pointNum22(resultSeries_twoPlus);

        // Screen
        dirtySeries = assist.readData2(inputFileName, ",");
        TimeSeries dirtySeries_1 = assist.getXY(dirtySeries, 0);
        TimeSeries dirtySeries_2 = assist.getXY(dirtySeries, 1);
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

        // expsmooth
        dirtySeries = assist.readData2(inputFileName, ",");
        dirtySeries_1 = assist.getXY(dirtySeries, 0);
        dirtySeries_2 = assist.getXY(dirtySeries, 1);
        double rmsDirty_expsmooth = assist.RMS1(dirtySeries_1, dirtySeries_2);
        EWMA exp_1 = new EWMA(dirtySeries_1, 0.042);
        long time17 = System.currentTimeMillis();
        TimeSeries resultSeries_5 = exp_1.mainExp();
        long time18 = System.currentTimeMillis();
        // expsmooth_1
        EWMA exp_2 = new EWMA(dirtySeries_2, 0.042);
        long time19 = System.currentTimeMillis();
        TimeSeries resultSeries_6 = exp_2.mainExp();
        long time20 = System.currentTimeMillis();
        // expsmooth_2
        double rms_expsmooth = assist.RMS1(resultSeries_5, resultSeries_6);
        double cost_expsmooth = assist.Cost11(resultSeries_5, resultSeries_6);
        int num_expsmooth = assist.pointNum11(resultSeries_5, resultSeries_6);


        double[] orgval = new double[2];
    	double[] modify = new double[2];
    	double[] truth = new double[2];
		
        // two-dimensional
        // System.out.println("two-dimensional:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_two);
        // System.out.println("    Repair RMS error is " + rms_two);
        // System.out.println("    Cost is " + cost_two);
        // System.out.println("    Time is " + (time2-time1));
        // System.out.println("    The number of modified points is " + num_two);
        for(TimePoint2 tp1 : resultSeries_two.getTimeseries()) {
            orgval = tp1.getOrgval();
            modify = tp1.getModify();
            truth = tp1.getTruth();
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            modify[1] + " , y_truth=" + truth[1]);
        }

        // QCQP
        System.out.println("QCQP:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_QCQP);
        // System.out.println("    Repair RMS error is " + rms_QCQP);
        // System.out.println("    Cost is " + cost_QCQP);
        // System.out.println("    Time is " + (timeQCQP2-timeQCQP1));
        System.out.println("    The number of modified points is " + num_QCQP);
        for(TimePoint2 tp1 : resultSeries_QCQP.getTimeseries()) {
            orgval = tp1.getOrgval();
            modify = tp1.getModify();
            truth = tp1.getTruth();
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            modify[1] + " , y_truth=" + truth[1]);
        }

        // TwoMILP
        System.out.println("TwoMILP:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_TwoMILP);
        // System.out.println("    Repair RMS error is " + rms_TwoMILP);
        // System.out.println("    Cost is " + cost_TwoMILP);
        // System.out.println("    Time is " + (timeTwoMILP2-timeTwoMILP1));
        System.out.println("    The number of modified points is " + num_TwoMILP);
        for(TimePoint2 tp1 : resultSeries_TwoMILP.getTimeseries()) {
            orgval = tp1.getOrgval();
            modify = tp1.getModify();
            truth = tp1.getTruth();
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            modify[1] + " , y_truth=" + truth[1]);
        }

        // two-plus
        System.out.println("two-plus:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_twoPlus);
        // System.out.println("    Repair RMS error is " + rms_twoPlus);
        // System.out.println("    Cost is " + cost_twoPlus);
        // System.out.println("    Time is " + (time200-time100));
        System.out.println("    The number of modified points is " + num_twoPlus);
        for(TimePoint2 tp1 : resultSeries_twoPlus.getTimeseries()) {
            orgval = tp1.getOrgval();
            modify = tp1.getModify();
            truth = tp1.getTruth();
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            modify[1] + " , y_truth=" + truth[1]);
        }

        // rcsws
        // System.out.println("rcsws:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_rcsws);
        // System.out.println("    Repair RMS error is " + rms_rcsws);
        // System.out.println("    Cost is " + cost_rcsws);
        // System.out.println("    Time is " + (time_rcsws2-time_rcsws1));
        // System.out.println("    The number of modified points is " + num_rcsws);

        // Screen
        System.out.println("Screen:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_12);
        // System.out.println("    Repair RMS error is " + rms_12);
        // System.out.println("    Cost is " + cost_12);
        // System.out.println("    Time is " + (time4-time3+time6-time5));
        System.out.println("    The number of modified points is " + num_1);
        TimePoint tp1, tp2;
        for(int i=0; i<resultSeries_1.getTimeseries().size(); i++){
            tp1 = resultSeries_1.getTimeseries().get(i);
            tp2 = resultSeries_2.getTimeseries().get(i);
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            tp1.getOrgval() + " ,x_modify=" + tp1.getModify() + " , x_truth=" +
            tp1.getTruth() + " , y_orgval=" + tp2.getOrgval() + " ,y_modify=" +
            tp2.getModify() + " , y_truth=" + tp2.getTruth());
        }

        //expsmooth
        System.out.println("ExpSmooth:");
        // System.out.println("    Dirty RMS error is " + rmsDirty_expsmooth);
        // System.out.println("    Repair RMS error is " + rms_expsmooth);
        // System.out.println("    Cost is " + cost_expsmooth);
        // System.out.println("    Time is " + (time18-time17+time20-time19));
        System.out.println("    The number of modified points is " + num_expsmooth);
        for(int i=0; i<resultSeries_5.getTimeseries().size(); i++){
            tp1 = resultSeries_5.getTimeseries().get(i);
            tp2 = resultSeries_6.getTimeseries().get(i);
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            tp1.getOrgval() + " ,x_modify=" + tp1.getModify() + " , x_truth=" +
            tp1.getTruth() + " , y_orgval=" + tp2.getOrgval() + " ,y_modify=" +
            tp2.getModify() + " , y_truth=" + tp2.getTruth());
        }
    }
}