package MTCSC.test;

import MTCSC.MTCSC_2;
import MTCSC.entity.TimeSeries2;
import MTCSC.entity.TimePoint2;
import MTCSC.util.Assist;

public class example3 {
    public static void main(String[] args) {
        Assist assist = new Assist();

        String inputFileName = "example/example3.data";
        double S = 1.000;

        int T2 = 10;
        // My2-plus
        TimeSeries2 dirtySeries = assist.readData2(inputFileName, ",");
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
        double[] orgval = new double[2];
    	double[] modify = new double[2];
    	double[] truth = new double[2];
        for(TimePoint2 tp1 : resultSeries_twoPlus.getTimeseries()) {
            orgval = tp1.getOrgval();
            modify = tp1.getModify();
            truth = tp1.getTruth();
            System.out.println(tp1.getTimestamp() + ", x_orgval=" +
            orgval[0] + " ,x_modify=" + modify[0] + " , x_truth=" +
            truth[0] + " , y_orgval=" + orgval[1] + " ,y_modify=" +
            modify[1] + " , y_truth=" + truth[1]);
        }
    }
}