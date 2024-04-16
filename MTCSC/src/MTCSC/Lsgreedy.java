package MTCSC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import MTCSC.entity.TimePoint;
import MTCSC.entity.TimeSeries;

public class Lsgreedy {
    private TimeSeries timeseries;
    ArrayList<TimePoint> AllList;
    private double center = 0, sigma;
    private final double eps = 1e-12;
    private int Size;
    private long[] time;
    private double[] original;

    public Lsgreedy(TimeSeries timeseries){
        setTimeSeries(timeseries);
        setSize(timeseries);
        setTime_Original_Repaired(timeseries);
        setParameters();
    }

    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeseries = timeSeries;
        this.AllList = timeseries.getTimeseries();
    }

    public void setSize(TimeSeries timeSeries) {
        this.Size = AllList.size();
    }

    public void setTime_Original_Repaired(TimeSeries timeSeries){
        this.time = new long[this.Size];
        this.original = new double[this.Size];
        for(int i=0; i<this.Size; i++){
            TimePoint tmpPoint = AllList.get(i);
            time[i] = tmpPoint.getTimestamp();
            original[i] = tmpPoint.getOrgval();
        }
    }

    public static double[] speed(double[] origin, long[] time) {
        int n = origin.length;
        double[] speed = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            speed[i] = (origin[i + 1] - origin[i]) / (time[i + 1] - time[i]);
        }
        return speed;
    }

    public static double[] variation(double[] origin) {
        int n = origin.length;
        double[] var = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            var[i] = origin[i + 1] - origin[i];
        }
        return var;
    }

    public static double median(double[] list) {
        Arrays.sort(list);
        int size = list.length;
        if(size % 2 != 1){
            return (list[size / 2 - 1] + list[size / 2]) / 2;
        }else {
            return list[(size - 1) / 2];
        }
    }

    public static double mad(double[] value) {
        double mid = median(value);
        double[] d = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            d[i] = Math.abs(value[i] - mid);
        }
        return 1.4826 * median(d);
    }

    private void setParameters() {
        double[] speed = speed(original, time);
        double[] speedchange = variation(speed);
        sigma = mad(speedchange);
    }

    public TimeSeries repair() {
        RepairNode[] table = new RepairNode[this.Size];
        PriorityQueue<RepairNode> heap = new PriorityQueue<>();
        for (int i = 1; i < this.Size - 1; i++) {
            RepairNode node = new RepairNode(i);
            table[i] = node;
            if (Math.abs(node.getU() - center) > 3 * sigma) {
                heap.add(node);
            }
        }
        while (true) {
            RepairNode top = heap.peek();
            if (top == null || Math.abs(top.getU() - center) < Math.max(eps, 3 * sigma)) {
                break;
            } // stop greedy algorithm when the heap is empty or all speed changes locate in center±3sigma
            top.modify();
            for (int i = Math.max(1, top.getIndex() - 1); i <= Math.min(this.Size - 2, top.getIndex() + 1); i++) {
                heap.remove(table[i]);
                RepairNode temp = new RepairNode(i);
                table[i] = temp;
                if (Math.abs(temp.getU() - center) > 3 * sigma) {
                    heap.add(temp);
                }
            }
        }
        return timeseries;
    }

    class RepairNode implements Comparable<RepairNode> {

        private final int index;
        private final double u; // speed variation

        public RepairNode(int index) {
            TimePoint prePoint = AllList.get(index-1);
            TimePoint iPoint = AllList.get(index);
            TimePoint nextPoint = AllList.get(index+1);
            this.index = index;
            double v1 = nextPoint.getModify() - iPoint.getModify();
            v1 = v1 / (time[index + 1] - time[index]);
            double v2 = iPoint.getModify() - prePoint.getModify();
            v2 = v2 / (time[index] - time[index - 1]);
            this.u = v1 - v2;
        }

        /**
         * modify values of repaired points, to make the difference of its speed variation and center is
         * 1 sigma
         */
        public void modify() {
            double temp;
            if (sigma < eps) {
                temp = Math.abs(u - center);
            } else {
                temp = Math.max(sigma, Math.abs(u - center) / 3);
            }
            temp *=
                    (double) (time[index + 1] - time[index])
                            * (time[index] - time[index - 1])
                            / (time[index + 1] - time[index - 1]);
            TimePoint iPoint = AllList.get(index);
            double modify = iPoint.getModify();
            if (this.u > center) {
                iPoint.setModify(modify+temp);
            } else {
                iPoint.setModify(modify-temp);
            }
        }

        @Override
        public int compareTo(RepairNode o) {
            double u1 = Math.abs(this.u - center);
            double u2 = Math.abs(o.u - center);
            if (u1 > u2) {
                return -1;
            } else if (u1 == u2) {
                return 0;
            } else {
                return 1;
            }
        }

        public int getIndex() {
            return index;
        }

        public double getU() {
            return u;
        }
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}
