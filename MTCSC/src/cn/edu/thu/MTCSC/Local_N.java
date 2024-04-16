package cn.edu.thu.MTCSC;

import cn.edu.thu.MTCSC.entity.TimePointN;
import cn.edu.thu.MTCSC.entity.TimeSeriesN;
import java.util.ArrayList;


public class Local_N {
    private int N;
    private TimeSeriesN timeseries;
    private TimePointN kp;
    private long T;       // the window size
    private double SMAX;  // maximum speed

    /**
     *
     * @param timeseries timeseries
     * @param sMax maximum allowed speed
     * @param t the window size
     */
    public Local_N(TimeSeriesN timeseries, double sMax, long t, int n) {
        setTimeSeries(timeseries);
        setT(t);
        setSMAX(sMax);
        setN(n);
    }
  
    public void setTimeSeries(TimeSeriesN timeSeries) {
        this.timeseries = timeSeries;
    }
  
    public void setT(Long t) {
        this.T = t;
    }
  
    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
    }

    public void setN(int n) {
        this.N = n;
    }
  
    /**
     *
     * @return timeseries after repair
     */
    public TimeSeriesN mainScreen() {
        ArrayList<TimePointN> totalList = timeseries.getTimeseries();
        int size = totalList.size();
    
        long preEnd = -1, curEnd;
        // the startTime in the window, the real end time in the window, the maximum allowed
        long wStartTime, wEndTime, wGoalTime;
        long curTime=0;
        TimePointN prePoint = null;    // the last fixed point
        TimePointN tp;
    
        TimeSeriesN tempSeries = new TimeSeriesN();
        ArrayList<TimePointN> tempList;
    
        int readIndex = 1; // the point should be read in
    
        // initial
        tp = totalList.get(0);
        tempSeries.addPoint(tp);
        wStartTime = tp.getTimestamp();
        wEndTime = wStartTime;
        wGoalTime = wStartTime + T;
  
        while (readIndex < size) {
            tp = totalList.get(readIndex);
            curTime = tp.getTimestamp();
  
            // This point shouldn't be added until the repair is over
            if (curTime > wGoalTime) {
                while (true) {
                    tempList = tempSeries.getTimeseries();
                    if (tempList.size() == 0) {
                        // if all the points in tempList has been handled
                        tempSeries.addPoint(tp);  // the current point should be a new start
                        // prePoint = tp;
                        wGoalTime = curTime + T;
                        wEndTime = curTime;
                        break;
                    }
  
                    kp = tempList.get(0);
                    wStartTime = kp.getTimestamp();
                    wGoalTime = wStartTime + T;
  
                    if (curTime <= wGoalTime) {
                        // then should read in new points
                        tempSeries.addPoint(tp);
                        wEndTime = curTime;
                        break;
                    }
  
                    curEnd = wEndTime;
  
                    if (preEnd == -1) {
                        prePoint = kp;
                    }
                    local(tempSeries, prePoint);
            
                    prePoint = kp;
                    preEnd = curEnd;
  
                    // remove the keyPoint
                    tempSeries.getTimeseries().remove(0);
                } // end of while(true)
            } else {
                if (curTime > wEndTime) {
                    // suppose the sequence is in order, so it must happen
                    tempSeries.addPoint(tp);
                    wEndTime = curTime;
                }
            }
            readIndex++;  // read another one
        }
  
        // handle the last window
        while (tempSeries.getLength() > 0) {
            tempList = tempSeries.getTimeseries();
            kp = tempList.get(0);
            if (prePoint == null) {
                prePoint = kp;
            }
            local(tempSeries, prePoint);
            prePoint = kp;
            tempList.remove(0);
        }
        return timeseries;
    }

    public boolean judgeSpeed(TimePointN prePoint, TimePointN IPoint){
        ArrayList<Double> preValue = prePoint.getModify();
        ArrayList<Double> iValue = IPoint.getModify();
        long preTime = prePoint.getTimestamp();
        long iTime = IPoint.getTimestamp();
        double distance = 0, delta;
        for(int i=0; i<this.N; i++){
			delta = preValue.get(i) - iValue.get(i);
			distance = distance + delta * delta;
        }
        distance = Math.sqrt(distance);
        double speed = distance / (iTime - preTime);
        speed = Math.abs(speed);
        if(speed <= this.SMAX){
            return true;
        }
        else{
            return false;
        }
    }
  
    /**
     * @param timeSeries timeseries in a window
     * @param prePoint the former modified point
     */
    private void local(TimeSeriesN timeSeries, TimePointN prePoint) {
        ArrayList<TimePointN> tempList = timeSeries.getTimeseries();
      
        // get bound
        long preTime = prePoint.getTimestamp();
        ArrayList<Double> preVal = prePoint.getModify();
        long kpTime = kp.getTimestamp();
        // double kpVal = kp.getModify();
        // double lowerBound = preVal + SMIN * (kpTime - preTime);
        // double upperBound = preVal + SMAX * (kpTime - preTime);

        int length = tempList.size();
        TimePointN tp1;
        if(preTime==kpTime){
            return ;
        }
        if(judgeSpeed(prePoint, kp)){
            return ;
        }
        else{
            if (length == 1) {
                ArrayList<Double> modify = prePoint.getModify();
                kp.setModify(modify);
                return ;
            }
            else{
                for (int i = 1; i < length; ++i) {
                    tp1 = tempList.get(i);
                    long t1 = tp1.getTimestamp();
                    if (judgeSpeed(prePoint, tp1)){
                        ArrayList<Double> maxVal = tp1.getModify();
                        double pre_dis = kpTime - preTime;
                        double pre_next_dis = t1 - preTime;
                        double rate = pre_dis / pre_next_dis;
                        ArrayList<Double> modify = new ArrayList<Double>();
                        for(int j=0; j<this.N; j++){
                            double temp = (maxVal.get(j) - preVal.get(j)) * rate + preVal.get(j);
                            modify.add(temp);
                        }
                        kp.setModify(modify);
                        return ;
                    }
                }
                kp.setModify(prePoint.getModify());
            }
        }
        
    }
}