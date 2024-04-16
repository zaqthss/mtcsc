package MTCSC;

import MTCSC.entity.TimePointN;
import MTCSC.entity.TimeSeriesN;
import java.util.ArrayList;

public class MTCSC_N {
    private int N;
    private TimeSeriesN timeseries;
    private TimePointN kp;
    private int T;       // the window size
    private double SMAX;  // maximum speed

    /**
     *
     * @param timeseries timeseries
     * @param sMax maximum allowed speed
     * @param t the window size
     */
    public MTCSC_N(TimeSeriesN timeseries, double sMax, int t, int n) {
        setTimeSeries(timeseries);
        setSMAX(sMax);
        setT(t);
        setN(n);
    }

    public void setTimeSeries(TimeSeriesN timeSeries) {
        this.timeseries = timeSeries;
    }

    public void setT(int t) {
        this.T = t;
    }

    public void setN(int n) {
        this.N = n;
    }

    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
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
                // TimePoint2 tmpPoint = tempSeries.getTimeseries().get(tempSeries.getTimeseries().size()-1);
                // addSpeed(tmpPoint, tp);
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
            // TimePoint2 tmpPoint = tempSeries.getTimeseries().get(tempSeries.getTimeseries().size()-1);
            // addSpeed(tmpPoint, tp);
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
    private double distance(TimePointN prePoint, TimePointN kp) {
        double distance = 0;
        ArrayList<Double> pp_modify = prePoint.getModify();
        ArrayList<Double> kp_modify = kp.getModify();
        for(int i=0; i<this.N; i++){
            distance += Math.pow(pp_modify.get(i) - kp_modify.get(i), 2);
        }
        return distance;
    }

    // public static double calculateDistance(TimePointN tp1, TimePointN tp2) {
    //     double[] xy1 = tp1.getModify();
    //     double[] xy2 = tp2.getModify();
    //     return Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
    // }

    // judge whether to modify
    private boolean judgeModify(ArrayList<Double>preVal, ArrayList<Double>maxVal, ArrayList<Double>kpVal,
                                long preTime, long maxTime, long kpTime) {
        boolean judge = true;
        double c1=0, c2=0;
        for(int i=0; i<this.N; i++){
            c1 += Math.pow(preVal.get(i) - kpVal.get(i), 2);
        }
        for(int i=0; i<this.N; i++){
            c2 += Math.pow(maxVal.get(i) - kpVal.get(i), 2);
        }
        if (c1 <= (kpTime-preTime)*(kpTime-preTime)*SMAX*SMAX && 
            c2 <= (maxTime-kpTime)*(maxTime-kpTime)*SMAX*SMAX) {
            judge = false;
        }
        return judge;
    }

    /**
     * @param timeSeries timeseries in a window
     * @param prePoint the former modified point
     */
    private void local(TimeSeriesN timeSeries, TimePointN prePoint) {
        ArrayList<TimePointN> tempList = timeSeries.getTimeseries();
        
        // get bound
        long preTime = prePoint.getTimestamp();
        ArrayList<Double> preVal =  prePoint.getModify();
        long kpTime = kp.getTimestamp();
        ArrayList<Double> kpVal = kp.getModify();

        // form candidates
        int length = tempList.size();
        int[] top = new int[length+1]; //Default is 0,-1
        int[] len = new int[length+1]; //Default is 0, take the longest to form candidate 
        
        TimePointN tp1, tp2;
        if (length == 1) {
            if(judgeModify(preVal, preVal, kpVal, preTime, preTime, kpTime)){
                kp.setModify(preVal);
            }
            return;
        }
        int topIndex = 0;
        // find the first top
        for (int i = 1; i < length; ++i) {
            tp1 = tempList.get(i);
            long t1 = tp1.getTimestamp();
            if (distance(prePoint, tp1) <= ((t1-preTime)*(t1-preTime) * SMAX * SMAX)) {
                top[i] = -1;
                len[i] = 1;
                topIndex = i;
                break;
            }
        }
        // handle xk+topIndex+1 to xk+w
        boolean flag = false;
        for (int i = topIndex+1; i < length; ++i) {
            tp1 = tempList.get(i);
            tp2 = tempList.get(i-1);
            long t1 = tp1.getTimestamp();
            long t2 = tp2.getTimestamp();
            // if (distance(prePoint, tp1) < ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)) {
            //   break;
            // }
            // xi and xi-1 satisfy speed constraint
            if (distance(tp1, tp2) <= ((t1-t2)*(t1-t2) * SMAX * SMAX)) {
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
                    tp2 = tempList.get(j);
                    t2 = tp2.getTimestamp();
                    if ((distance(tp1, tp2) > ((t1-t2)*(t1-t2) * SMAX * SMAX) && top[j] > 0) || j==1) {
                        if (distance(prePoint, tp1) <= ((t1-preTime)*(t1-preTime) * SMAX * SMAX)) {
                            // if (distance(prePoint, tp1) > ((t1-kpTime-1)*(t1-kpTime-1) * SMAX * SMAX)) {
                            // if (distance(prePoint, tp1) > ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)) {
                            // // distance(prePoint, tp1) > ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)
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
                    else if (distance(tp1, tp2) > ((t1-t2)*(t1-t2) * SMAX * SMAX) && (top[j]==0 || top[j]==-1)) {
                        continue;
                    }
                    else if (distance(tp1, tp2) <= ((t1-t2)*(t1-t2) * SMAX * SMAX)) {
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
                        System.out.println("MyN.local() error");
                        return;
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
        TimePointN maxPoint = tempList.get(maxIndex);
        long maxTime = maxPoint.getTimestamp();
        ArrayList<Double> maxVal = maxPoint.getModify();
        
        // whether to repair
        boolean judge = judgeModify(preVal, maxVal, kpVal, preTime, maxTime, kpTime);

        // repair
        if (judge) {
            ArrayList<Double> N_modify = new ArrayList<Double>();
            double pre_dis = this.SMAX * (kpTime-preTime);
            // double next_dis = this.SMAX * (maxTime-kpTime);
            // double pre_next_dis = calculateDistance(maxPoint, prePoint);
            double pre_next_dis = this.SMAX * (maxTime-preTime);
            // double pre_kp_dis = calculateDistance(kp, prePoint);
            if(pre_next_dis > pre_dis){
                for(int i=0; i<this.N; i++){
                    double tmp = (maxVal.get(i) - preVal.get(i)) * (pre_dis / pre_next_dis) + preVal.get(i);
                    N_modify.add(tmp);
                }
            }
            else {
                for(int i=0; i<this.N; i++){
                    double tmp = (maxVal.get(i) + preVal.get(i)) / 2;
                    N_modify.add(tmp);
                }   
            }
            kp.setModify(N_modify);
        }
    }
}