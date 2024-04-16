package cn.edu.thu.MTCSC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.edu.thu.MTCSC.entity.TimePointN;
import cn.edu.thu.MTCSC.entity.TimeSeriesN;

// the global optimal of my method
public class Global_N {
    private int N;
    private TimeSeriesN timeseries;
    private int Size;
    private double SMAX;  // maximum speed

    public Global_N(TimeSeriesN timeseries, double sMax, int n) {
        setTimeSeries(timeseries);
        setSize(timeseries);
        setSMAX(sMax);
        setN(n);
    }
    
    public void setTimeSeries(TimeSeriesN timeSeries) {
        this.timeseries = timeSeries;
    }

    public void setSize(TimeSeriesN timeSeries) {
        ArrayList<TimePointN> totalList = timeseries.getTimeseries();
        int size = totalList.size();
        this.Size = size;
    }
    
    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
    }

    public void setN(int n) {
        this.N = n;
    }

    public TimeSeriesN clean(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // Get the index of the error point
        outlierIndex = outlierDetection_2();

        // repair
        TimeSeriesN cleanSeries = outlierRepair(outlierIndex);

        return cleanSeries;
    }

    // outlierDetection
    public ArrayList<Integer> outlierDetection() {
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // initial outlierIndex
        for(int i=0; i<this.Size; i++){
            outlierIndex.add(i);
        }
        ArrayList<TimePointN> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return outlierIndex;

        int[] dp = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePointN JPoint = totalList.get(j);
                TimePointN IPoint = totalList.get(i);
                if (judgeSpeed(IPoint, JPoint) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    if (dp[i] >= maxLength) {
                        maxLength = dp[i];
                        endIndex = i;
                    }
                }
            }
        }

        int tmpIndex = outlierIndex.indexOf(endIndex);
        outlierIndex.remove(tmpIndex);
        maxLength--;
        for (int i = endIndex-1; i >= 0; i--) {
            TimePointN JPoint = totalList.get(endIndex);
            TimePointN IPoint = totalList.get(i);
            if (judgeSpeed(JPoint, IPoint) && dp[endIndex] == dp[i] + 1) {
                tmpIndex = outlierIndex.indexOf(i);
                outlierIndex.remove(tmpIndex);
                endIndex = i;
                maxLength--;
            }
        }
        
        return outlierIndex;
    }

    public ArrayList<Integer> outlierDetection_2() {
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // initial outlierIndex
        for(int i=0; i<this.Size; i++){
            outlierIndex.add(i);
        }
        ArrayList<TimePointN> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return outlierIndex;

        int[] dp = new int[this.Size];
        int[] preIndex = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePointN JPoint = totalList.get(j);
                TimePointN IPoint = totalList.get(i);
                if (judgeSpeed(IPoint, JPoint) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    preIndex[i] = j;
                }
            }
            if (dp[i] > maxLength) {
                maxLength = dp[i];
                endIndex = i;
            }
        }

        for(; maxLength>0; maxLength--){
            int tmpIndex = outlierIndex.indexOf(endIndex);
            outlierIndex.remove(tmpIndex);
            endIndex = preIndex[endIndex];
        }

        return outlierIndex;
    }

    public List<List<Integer>> allOutlierDetection() {
        List<List<Integer>> allOutlierIndex = new ArrayList<List<Integer>>();
        List<TimePointN> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return allOutlierIndex;

        int[] dp = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePointN JPoint = totalList.get(j);
                TimePointN IPoint = totalList.get(i);
                if (judgeSpeed(IPoint, JPoint) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    if (dp[i] >= maxLength) {
                        maxLength = dp[i];
                        endIndex = i;
                    }
                }
            }
        }

        for (int i = endIndex; i >= 0; i--) {
            if (dp[i] == maxLength) {
                List<Integer> indices = new ArrayList<>();
                indices.add(i);
                findAllLongestIncreasingSubsequences(totalList, dp, i, maxLength - 1, indices, allOutlierIndex);
            }
        }
        
        return allOutlierIndex;
    }

    // private void findAllLongestIncreasingSubsequences(List<TimePoint> totalList, int[] dp, int currentIndex, int length,
    //                     List<Integer> currentIndices, List<List<Integer>> allOutlierIndex) {
    //     if (length == 0) {
    //         List<Integer> copy = new ArrayList<>(currentIndices);
    //         List<Integer> outlierIndex = new ArrayList<>();
    //         for(int j=0; j<this.Size; j++){
    //             if(!copy.contains(j)){
    //                 outlierIndex.add(j);
    //             }
    //         }
    //         allOutlierIndex.add(outlierIndex);
    //         return;
    //     }

    //     for (int i = currentIndex - 1; i >= 0; i--) {
    //         TimePoint JPoint = totalList.get(currentIndex);
    //         TimePoint IPoint = totalList.get(i);
    //         if (judgeSpeed(JPoint, IPoint) && dp[currentIndex] == dp[i] + 1) {
    //             currentIndices.add(i);
    //             findAllLongestIncreasingSubsequences(totalList, dp, i, length - 1, currentIndices, allOutlierIndex);
    //             currentIndices.remove(currentIndices.size() - 1);
    //         }
    //     }
    // }

    private void findAllLongestIncreasingSubsequences(List<TimePointN> totalList, int[] dp, int currentIndex, int length,
                            List<Integer> currentIndices, List<List<Integer>> allIndices) {
        Queue<State> queue = new LinkedList<>();
        queue.offer(new State(currentIndex, length, new ArrayList<>(currentIndices)));

        while (!queue.isEmpty()) {
            State state = queue.poll();
            currentIndex = state.currentIndex;
            length = state.length;
            currentIndices = state.currentIndices;

            if (length == 0) {
                List<Integer> copy = new ArrayList<>(currentIndices);
                List<Integer> outlierIndex = new ArrayList<>();
                for(int j=0; j<this.Size; j++){
                    if(!copy.contains(j)){
                        outlierIndex.add(j);
                    }
                }
                allIndices.add(outlierIndex);
                continue;
            }

            for (int i = currentIndex - 1; i >= 0; i--) {
                TimePointN JPoint = totalList.get(currentIndex);
                TimePointN IPoint = totalList.get(i);
                if (judgeSpeed(JPoint, IPoint) && dp[currentIndex] == dp[i] + 1) {
                    currentIndices.add(i);
                    queue.offer(new State(i, length - 1, new ArrayList<>(currentIndices)));
                    currentIndices.remove(currentIndices.size() - 1);
                }
            }
        }
    }

    public boolean judgeSpeed(TimePointN JPoint, TimePointN IPoint){
        ArrayList<Double> jValue = JPoint.getModify();
        ArrayList<Double> iValue = IPoint.getModify();
        long jTime = JPoint.getTimestamp();
        long iTime = IPoint.getTimestamp();
        double distance = 0, delta;
        for(int i=0; i<this.N; i++){
			delta = jValue.get(i) - iValue.get(i);
			distance = distance + delta * delta;
        }
        distance = Math.sqrt(distance);
        double speed = distance / (jTime - iTime);
        speed = Math.abs(speed);
        if(speed <= this.SMAX){
            return true;
        }
        else{
            return false;
        }
    }

    public TimeSeriesN outlierRepair(ArrayList<Integer> outlierIndex){
        // initial label
        boolean[] label = new boolean[this.Size];
        for(int i=0; i<this.Size; i++){
            if(outlierIndex.contains(i)){
                label[i] = false;
            }
            else{
                label[i] = true;
            }
        }
        // add label
        ArrayList<TimePointN> tpList = new ArrayList<>();
        ArrayList<TimePointN> totalList = timeseries.getTimeseries();
        for (int i = 0; i < this.Size; ++i) {
            TimePointN tempPoint = totalList.get(i);
            TimePointN tp = new TimePointN(this.N, tempPoint.getTimestamp(), tempPoint.getOrgval(), tempPoint.getTruth());
            tp.setLabel(label[i]);
            tpList.add(tp);
        }
        for(int i=0; i<tpList.size(); i++){
            double pre_dis=0;
            double pre_next_dis=0;
            if(!tpList.get(i).isLabel()){
                TimePointN tp1=tpList.get(i), tp2=tpList.get(i);
                for(int j=i-1;j>=0;j--){
                    if(tpList.get(j).isLabel()){
                        tp1 = tpList.get(j);
                        pre_dis = tpList.get(i).getTimestamp() - tp1.getTimestamp();
                        break;
                    }
                }
                for(int j=i+1;j<tpList.size();j++){
                    if(tpList.get(j).isLabel()){
                        tp2 = tpList.get(j);
                        pre_next_dis = tp2.getTimestamp() - tp1.getTimestamp();
                        break;
                    }
                }
                double rate = pre_dis / pre_next_dis;
                ArrayList<Double> modify = new ArrayList<Double>();
                ArrayList<Double> maxmodify = tp2.getModify();
                ArrayList<Double> premodify = tp1.getModify();
                for(int j=0; j<this.N; j++){
                    double temp = (maxmodify.get(j) - premodify.get(j)) * rate + premodify.get(j);
                    modify.add(temp);
                }
                tpList.get(i).setModify(modify);
            }
        }
        TimeSeriesN cleanSeries = new TimeSeriesN(tpList);

        // System.out.println("T,Ob,Tr,Mo,lable");
        // for (TimePoint tp1 : dirtySeries.getTimeseries()) {
        //     System.out.println(tp1.getTimestamp() + "," + tp1.getObserve() + ","
        //     + tp1.getTruth() + "," + tp1.getModify() + "," + tp1.isLabel());
        // }
        
        return cleanSeries;
    }
}

class State {
    int currentIndex;
    int length;
    List<Integer> currentIndices;

    State(int currentIndex, int length, List<Integer> currentIndices) {
        this.currentIndex = currentIndex;
        this.length = length;
        this.currentIndices = currentIndices;
    }
}