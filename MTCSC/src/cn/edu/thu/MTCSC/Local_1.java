package cn.edu.thu.MTCSC;

import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import java.util.ArrayList;


public class Local_1 {
    private TimeSeries timeseries;
    private TimePoint kp;
    private long T;       // the window size
    private double SMAX;  // maximum speed
    private double SMIN;  // minimum speed

    /**
     *
     * @param timeseries timeseries
     * @param sMax maximum allowed speed
     * @param t the window size
     */
    public Local_1(TimeSeries timeseries, double sMax, double sMin, long t) {
        setTimeSeries(timeseries);
        setT(t);
        setSMAX(sMax);
        setSMIN(sMin);
    }
  
    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeseries = timeSeries;
    }
  
    public void setT(Long t) {
        this.T = t;
    }
  
    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
    }

    public void setSMIN(double SMIN) {
        this.SMIN = SMIN;
    }
  
    /**
     *
     * @return timeseries after repair
     */
    public TimeSeries mainScreen() {
        ArrayList<TimePoint> totalList = timeseries.getTimeseries();
        int size = totalList.size();
    
        long preEnd = -1, curEnd;
        // the startTime in the window, the real end time in the window, the maximum allowed
        long wStartTime, wEndTime, wGoalTime;
        long curTime=0;
        TimePoint prePoint = null;    // the last fixed point
        TimePoint tp;
    
        TimeSeries tempSeries = new TimeSeries();
        ArrayList<TimePoint> tempList;
    
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
                    prePoint.setStatus(1);
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
  
    // calculate the distance
    private double distance(TimePoint prePoint, TimePoint tp) {
        double preVal = prePoint.getModify();
        double kpVal = tp.getModify();
        double distance = kpVal - preVal;
        return distance;
    }
  
    /**
     * @param timeSeries timeseries in a window
     * @param prePoint the former modified point
     */
    private void local(TimeSeries timeSeries, TimePoint prePoint) {
        ArrayList<TimePoint> tempList = timeSeries.getTimeseries();
      
        // get bound
        long preTime = prePoint.getTimestamp();
        double preVal = prePoint.getModify();
        long kpTime = kp.getTimestamp();
        // double kpVal = kp.getModify();
        // double lowerBound = preVal + SMIN * (kpTime - preTime);
        // double upperBound = preVal + SMAX * (kpTime - preTime);

        int length = tempList.size();
        TimePoint tp1;
        if(distance(prePoint, kp) <= ((kp.getTimestamp()-preTime)*SMAX) && distance(prePoint, kp) >= ((kp.getTimestamp()-preTime)*SMIN)){
            return ;
        }
        else{
            if (length == 1) {
                double modify = prePoint.getModify();
                kp.setModify(modify);
                return ;
            }
            else{
                for (int i = 1; i < length; ++i) {
                    tp1 = tempList.get(i);
                    long t1 = tp1.getTimestamp();
                    if (distance(prePoint, tp1) <= ((t1-preTime)*SMAX) && distance(prePoint, tp1) >= ((t1-preTime)*SMIN)) {
                        double maxVal = tp1.getModify();
                        // double lowerBound_max = maxVal + SMAX * (kpTime - t1);
                        // double upperBound_max = maxVal + SMIN * (kpTime - t1);
                        // lowerBound = lowerBound > lowerBound_max ? lowerBound : lowerBound_max;
                        // upperBound = upperBound < upperBound_max ? upperBound : upperBound_max;
                        // // whether to repair
                        // if (upperBound < kpVal || lowerBound > kpVal){
                        //     double pre_dis = kpTime - preTime;
                        //     double pre_next_dis = t1 - preTime;
                        //     double rate = pre_dis / pre_next_dis;
                        //     double modify = (maxVal - preVal) * rate + preVal;
                        //     kp.setModify(modify);
                        // }
                        double pre_dis = kpTime - preTime;
                        double pre_next_dis = t1 - preTime;
                        double rate = pre_dis / pre_next_dis;
                        double modify = (maxVal - preVal) * rate + preVal;
                        kp.setModify(modify);
                        return ;
                    }
                }
                kp.setModify(prePoint.getModify());
            }
        }
        
    }
}