package cn.edu.thu.MTCSC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;

// the global optimal of my method
public class Global_1 {
    private TimeSeries timeseries;
    private int Size;
    private double SMAX;  // maximum speed
    private double SMIN;  // minimum speed

    public Global_1(TimeSeries timeseries, double sMax, double sMin) {
        setTimeSeries(timeseries);
        setSize(timeseries);
        setSMAX(sMax);
        setSMIN(sMin);
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

    public TimeSeries clean(){
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // 异常检测，获取错误点的下标
        outlierIndex = outlierDetection_2();

        // 异常修复
        TimeSeries cleanSeries = outlierRepair(outlierIndex);

        return cleanSeries;
    }

    // 最长递增子序列:只有一个最长序列
    public ArrayList<Integer> outlierDetection() {
        ArrayList<Integer> outlierIndex = new ArrayList<Integer>();
        // 初始化outlierIndex
        for(int i=0; i<this.Size; i++){
            outlierIndex.add(i);
        }
        ArrayList<TimePoint> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return outlierIndex;

        int[] dp = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePoint JPoint = totalList.get(j);
                TimePoint IPoint = totalList.get(i);
                if (judgeSpeed(IPoint, JPoint) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    if (dp[i] >= maxLength) {
                        maxLength = dp[i];
                        endIndex = i;
                    }
                }
            }
        }

        // 回溯寻找最长递增子序列的所有下标
        int tmpIndex = outlierIndex.indexOf(endIndex);
        outlierIndex.remove(tmpIndex);
        maxLength--;
        for (int i = endIndex-1; i >= 0; i--) {
            TimePoint JPoint = totalList.get(endIndex);
            TimePoint IPoint = totalList.get(i);
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
        // 初始化outlierIndex
        for(int i=0; i<this.Size; i++){
            outlierIndex.add(i);
        }
        ArrayList<TimePoint> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return outlierIndex;

        int[] dp = new int[this.Size];
        int[] preIndex = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePoint JPoint = totalList.get(j);
                TimePoint IPoint = totalList.get(i);
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

    // 最长递增子序列:返回所有最长序列
    public List<List<Integer>> allOutlierDetection() {
        List<List<Integer>> allOutlierIndex = new ArrayList<List<Integer>>();
        List<TimePoint> totalList = this.timeseries.getTimeseries();
        if (totalList.size() == 0)
            return allOutlierIndex;

        int[] dp = new int[this.Size];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 0; i < this.Size; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                TimePoint JPoint = totalList.get(j);
                TimePoint IPoint = totalList.get(i);
                if (judgeSpeed(IPoint, JPoint) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    if (dp[i] >= maxLength) {
                        maxLength = dp[i];
                        endIndex = i;
                    }
                }
            }
        }

        // 回溯寻找所有最长递增子序列的下标
        for (int i = endIndex; i >= 0; i--) {
            if (dp[i] == maxLength) {
                List<Integer> indices = new ArrayList<>();
                indices.add(i);
                findAllLongestIncreasingSubsequences(totalList, dp, i, maxLength - 1, indices, allOutlierIndex);
            }
        }
        
        return allOutlierIndex;
    }

    // 回溯寻找所有最长递增子序列的下标，对于stock10k数据太大，堆栈溢出了
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

    // 回溯寻找所有最长递增子序列的下标，使用while循环，不会堆栈溢出
    private void findAllLongestIncreasingSubsequences(List<TimePoint> totalList, int[] dp, int currentIndex, int length,
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
                TimePoint JPoint = totalList.get(currentIndex);
                TimePoint IPoint = totalList.get(i);
                if (judgeSpeed(JPoint, IPoint) && dp[currentIndex] == dp[i] + 1) {
                    currentIndices.add(i);
                    queue.offer(new State(i, length - 1, new ArrayList<>(currentIndices)));
                    currentIndices.remove(currentIndices.size() - 1);
                }
            }
        }
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
        // 初始化标签数组
        boolean[] label = new boolean[this.Size];
        for(int i=0; i<this.Size; i++){
            if(outlierIndex.contains(i)){
                label[i] = false;
            }
            else{
                label[i] = true;
            }
        }
        // 加标签
        ArrayList<TimePoint> tpList = new ArrayList<>();
        ArrayList<TimePoint> totalList = timeseries.getTimeseries();
        for (int i = 0; i < this.Size; ++i) {
            TimePoint tempPoint = totalList.get(i);
            TimePoint tp = new TimePoint(tempPoint.getTimestamp(), tempPoint.getOrgval(), tempPoint.getTruth());
            tp.setLabel(label[i]);
            tpList.add(tp);
        }
        for(int i=0; i<tpList.size(); i++){
            double pre_dis=0;
            double pre_next_dis=0;
            if(!tpList.get(i).isLabel()){
                TimePoint tp1=tpList.get(i), tp2=tpList.get(i);
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
                double modify = (tp2.getModify() - tp1.getModify()) * rate + tp1.getModify();
                tpList.get(i).setModify(modify);
            }
        }
        TimeSeries cleanSeries = new TimeSeries(tpList);

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