package cn.edu.thu.MTCSC;
import java.util.ArrayList;
import java.util.Random;

import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;

// HTDCleaning
public class HTD {
    private TimeSeries timeseries;
    private int Size;
    private double SMAX;  // maximum speed
    private double SMIN;  // minimum speed
    private int Seed = 1;
    private int maxNum = 5000;

    public HTD(TimeSeries timeseries, double sMax, double sMin, int seed, int maxnum) {
        setTimeSeries(timeseries);
        setSize(timeseries);
        setSMAX(sMax);
        setSMIN(sMin);
        setSeed(seed);
        setmaxNum(maxnum);
    }
    
    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeseries = timeSeries;
    }

    public void setSize(TimeSeries timeSeries) {
        ArrayList<TimePoint> totalList = timeseries.getTimeseries();
        int size = totalList.size();
        this.Size = size;
    }
    
    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
    }

    public void setSMIN(double SMIN) {
        this.SMIN = SMIN;
    }

    public void setSeed(int seed) {
        this.Seed = seed;
    }

    public void setmaxNum(int maxnum) {
        this.maxNum = maxnum;
    }

    public TimeSeries clean(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // Get the index of the error point
        outlierIndex = outlierDetection();
        // repair
        TimeSeries cleanSeries = outlierRepair(outlierIndex);

        return cleanSeries;
    }

    public ArrayList<Integer> outlierDetection(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        ArrayList<TimePoint> totalList = timeseries.getTimeseries();
        // initial outlierIndex
        for(int i=0; i<this.Size; i++){
            outlierIndex.add(i);
        }
        int[] anomaly = new int[this.Size];
        int[] normal = new int[this.Size];
        // initial anomaly[] and normal[]
        for(int i=0; i<this.Size; i++){
            anomaly[i] = i;
            normal[i] = -1;
        }
        // DP
        TimePoint JPoint = null; 
        TimePoint IPoint = null; 
        for(int j=0; j<this.Size; j++){
            for(int i=0; j>i; i++){
                JPoint = totalList.get(j);
                IPoint = totalList.get(i);
                if(judgeSpeed(JPoint, IPoint) && anomaly[j] > (anomaly[i]+(j-i-1))){
                    anomaly[j] = anomaly[i]+(j-i-1);
                    normal[j] = i;
                }
            }
        }
        // get the end point
        int index = anomaly[0] + this.Size -1;
        int res = 1;
        for(int j=1; j<this.Size; j++){
            if((anomaly[j] + this.Size -j) < index){
                index = anomaly[j] + this.Size -j;
                res = j;
            }
        }

        int tmpIndex = 0;
        tmpIndex = outlierIndex.indexOf(res);
        outlierIndex.remove(tmpIndex);
        while(normal[res] != -1){
            tmpIndex = outlierIndex.indexOf(normal[res]);
            outlierIndex.remove(tmpIndex);
            res = normal[res];
        }

        return outlierIndex;
    }

    public boolean judgeSpeed(TimePoint JPoint, TimePoint IPoint){
        double jValue = JPoint.getModify();
        double iValue = IPoint.getModify();
        long jTime = JPoint.getTimestamp();
        long iTime = IPoint.getTimestamp();
        double speed = (jValue - iValue) / (jTime - iTime);
        if(speed >= this.SMIN && speed <= this.SMAX){
            return true;
        }
        else{
            return false;
        }
    }

    public TimeSeries outlierRepair(ArrayList<Integer> outlierIndex){
        int num = 0;
        ArrayList<Integer> numList = new ArrayList<Integer>();
        // initial label
        boolean[] label = new boolean[this.Size];
        // 10% let false to true
        Random random = new Random(this.Seed);
        for(int i=0; i<this.Size; i++){
            if(outlierIndex.contains(i)){
                int randomNumber = random.nextInt(100);
                if (randomNumber < 10) {
                    label[i] = true;
                    num++;
                    numList.add(i);
                } else {
                    label[i] = false;
                }
            }
            else{
                label[i] = true;
            }
        }
        // boolean[] label = { true, true, false, true, true, false, false, false,
        //     true, false };
        // add label
        ArrayList<TimePoint> tpList = new ArrayList<>();
        ArrayList<TimePoint> totalList = timeseries.getTimeseries();
        for (int i = 0; i < this.Size; ++i) {
            TimePoint tempPoint = totalList.get(i);
            TimePoint tp = new TimePoint(tempPoint.getTimestamp(), tempPoint.getOrgval(), tempPoint.getTruth());
            tp.setLabel(label[i]);
            tpList.add(tp);
        }
        TimeSeries dirtySeries = new TimeSeries(tpList);
        dirtySeries.setNum(num);
        dirtySeries.setNumList(numList);

        // for(int i=0; i<tpList.size(); i++){
        //     double pre_dis=0;
        //     double pre_next_dis=0;
        //     if(!tpList.get(i).isLabel()){
        //         TimePoint tp1=tpList.get(i), tp2=tpList.get(i);
        //         for(int j=i-1;j>=0;j--){
        //             if(tpList.get(j).isLabel()){
        //                 tp1 = tpList.get(j);
        //                 pre_dis = tpList.get(i).getTimestamp() - tp1.getTimestamp();
        //                 break;
        //             }
        //         }
        //         for(int j=i+1;j<tpList.size();j++){
        //             if(tpList.get(j).isLabel()){
        //                 tp2 = tpList.get(j);
        //                 pre_next_dis = tp2.getTimestamp() - tp1.getTimestamp();
        //                 break;
        //             }
        //         }
        //         double rate = pre_dis / pre_next_dis;
        //         double modify = (tp2.getModify() - tp1.getModify()) * rate + tp1.getModify();
        //         tpList.get(i).setModify(modify);
        //     }
        // }
        // TimeSeries cleanSeries = new TimeSeries(tpList);
        // Assist assist = new Assist();
        // assist.saveDataFromTimeSeries("ILD/hum50k_1.data", cleanSeries);

        double[] params = new double[5];
        double delta = 0.05;
        int p = 3; 
        OLS ols = new OLS(dirtySeries, p, delta, this.maxNum);
        ols.mainOLS(params);


        // System.out.println("T,Ob,Tr,Mo,lable");
        // for (TimePoint tp1 : dirtySeries.getTimeseries()) {
        //     System.out.println(tp1.getTimestamp() + "," + tp1.getObserve() + ","
        //     + tp1.getTruth() + "," + tp1.getModify() + "," + tp1.isLabel());
        // }
        
        return dirtySeries;
    }
}