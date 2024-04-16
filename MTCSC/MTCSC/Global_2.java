package cn.edu.thu.MTCSC;
import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import java.util.ArrayList;

public class Global_2 {
    private TimeSeries2 timeseries;
    private double S;  // maximum speed

    public Global_2(TimeSeries2 timeseries, double sMax) {
        setTimeSeries(timeseries);
        setSMAX(sMax);
    }
    
    public void setTimeSeries(TimeSeries2 timeSeries) {
        this.timeseries = timeSeries;
    }
    
    public void setSMAX(double SMAX) {
        this.S = SMAX;
    }

    public TimeSeries2 clean(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        outlierIndex = outlierDetection();

        return this.timeseries;
    }

    public ArrayList<Integer> outlierDetection(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        ArrayList<TimePoint2> totalList = timeseries.getTimeseries();
        int size = totalList.size();
        // initial outlierIndex
        for(int i=0; i<size; i++){
            outlierIndex.add(i);
        }
        int[] anomaly = new int[size];
        int[] normal = new int[size];
        // initial anomaly[] and normal[]
        for(int i=0; i<size; i++){
            anomaly[i] = i;
            normal[i] = -1;
        }
        // DP
        TimePoint2 JPoint = null; 
        TimePoint2 IPoint = null; 
        for(int j=0; j<size; j++){
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
        int index = anomaly[0] + size -1;
        int res = 1;
        for(int j=1; j<size; j++){
            if((anomaly[j] + size -j) < index){
                index = anomaly[j] + size -j;
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

    public boolean judgeSpeed(TimePoint2 JPoint, TimePoint2 IPoint){
        double[] xyJ = JPoint.getModify();
        double[] xyI = IPoint.getModify();
        long tJ = JPoint.getTimestamp();
        long tI = IPoint.getTimestamp();
        double distance = Math.sqrt(Math.pow(xyJ[0] - xyI[0], 2) + Math.pow(xyJ[1] - xyI[1], 2));
        double range = (tJ - tI) * this.S;
        if(distance > range)
            return false;
        else   
            return true;
    }
}
