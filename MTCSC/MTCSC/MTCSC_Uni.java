package cn.edu.thu.MTCSC;

import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import java.util.ArrayList;


public class MTCSC_Uni {

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
    public MTCSC_Uni(TimeSeries timeseries, double sMax, double sMin, long t) {
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
    private double distance(TimePoint prePoint, TimePoint kp) {
        double preVal = prePoint.getModify();
        double kpVal = kp.getModify();
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
        double kpVal = kp.getModify();
        double lowerBound = preVal + SMIN * (kpTime - preTime);
        double upperBound = preVal + SMAX * (kpTime - preTime);

        // form candidates
        int length = tempList.size();
        int[] top = new int[length+1]; //Default is 0,-1
        int[] len = new int[length+1]; //Default is 0, take the longest to form candidate 
        
        TimePoint tp1, tp2;
        if (length == 1) {
            if(lowerBound>kpVal || upperBound<kpVal){
                double modify = preVal;
                kp.setModify(modify);
            }
            return ;
        }
        
        int topIndex = 0;
        // find the first top
        for (int i = 1; i < length; ++i) {
            tp1 = tempList.get(i);
            long t1 = tp1.getTimestamp();
            if (distance(prePoint, tp1) <= ((t1-preTime)*SMAX) && distance(prePoint, tp1) >= ((t1-preTime)*SMIN)) {
                top[i] = -1;
                len[i] = 1;
                topIndex = i;
                break;
            }
        }
      
      // if (topIndex>2) {
      //   topIndex = 0;
      // }
      
        // handle xk+topIndex+1 to xk+w
        boolean flag = false;
        for (int i = topIndex+1; i < length; ++i) {
            tp1 = tempList.get(i);
            tp2 = tempList.get(i-1);
            long t1 = tp1.getTimestamp();
            long t2 = tp2.getTimestamp();
        
            // xi and xi-1 satisfy speed constraint
            if (distance(tp2, tp1) <= ((t1-t2)*SMAX) && distance(tp2, tp1) >= ((t1-t2)*SMIN)) {
                if (top[i-1] == -1) {
                    top[i] = i-1;
                    len[i-1]++;
                }
                else if (top[i-1] >0){
                    top[i] = top[i-1];
                    len[top[i-1]]++;
                }
            }
            else { // xi vialote the speed constraint with xi-1 
                for (int j = i-1; j >= 1; j--) {
                    double tpVal= tp1.getModify();
                    tp2 = tempList.get(j);
                    t2 = tp2.getTimestamp();
                    if ((((distance(tp2, tp1) > (t1-t2)*SMAX) || (distance(tp2, tp1) < (t1-t2)*SMIN)) && top[j] > 0) || j==1) {
                        if (distance(prePoint, tp1) <= ((t1-preTime)*SMAX) && distance(prePoint, tp1) >= ((t1-preTime)*SMIN)) {
                            // if ((lowerBound<((kpTime-t1)*SMAX+tpVal)) && (upperBound>((kpTime-t1)*SMIN+tpVal))) {
                            //     top[i] = -1;
                            //     len[i] = 1;
                            // }
                            // else {
                            //     flag = true;
                            // }
                            top[i] = -1;
                            len[i] = 1;
                        }
                        break;
                    }
                    else if (((distance(tp2, tp1) > (t1-t2)*SMAX) || (distance(tp2, tp1) < (t1-t2)*SMIN)) && (top[j]==0 || top[j]==-1)) {
                        continue;
                    }
                    else if ((distance(tp2, tp1) <= (t1-t2)*SMAX) && (distance(tp2, tp1) >= (t1-t2)*SMIN)) {
                        if (top[j] == -1) {
                            top[i] = j;
                            len[j]++;
                        }
                        else if (top[j] > 0){
                            top[i] = top[j];
                            len[top[j]]++;
                        }
                        break;
                    }
                    else {
                        System.out.println("error");
                        continue;
                        //   return;
                    }
                }
            }
            if (flag) {
                break;
            }
        }
  
        // find maxpoint 
        int maxIndex = topIndex;
        for (int i = maxIndex; i < length; ++i) {
            if (len[i] > len[maxIndex]) {
                maxIndex = i;
            }
        }
        TimePoint maxPoint = tempList.get(maxIndex);
        long maxTime = maxPoint.getTimestamp();
        double maxVal = maxPoint.getModify();
        double lowerBound_max = maxVal + SMAX * (kpTime - maxTime);
        double upperBound_max = maxVal + SMIN * (kpTime - maxTime);
      
        double modify = kpVal;
        // Determine the scope of repair candidates
        if (topIndex != 0) {
            lowerBound = lowerBound > lowerBound_max ? lowerBound : lowerBound_max;
            upperBound = upperBound < upperBound_max ? upperBound : upperBound_max;
        }

        // repair
        if (upperBound < kpVal || lowerBound > kpVal){
            // if(topIndex == 0){
            //   modify = (upperBound + lowerBound)/2;
            // }
            // else {
            //   modify = (maxVal - preVal) * ((kpTime - preTime) / (maxTime - preTime)) + preVal;
            // }
            double pre_dis = kpTime - preTime;
            double pre_next_dis = maxTime-preTime;
            double rate = pre_dis / pre_next_dis;
            modify = (maxVal - preVal) * rate + preVal;
            kp.setModify(modify);
        }
    }
}