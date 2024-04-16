package MTCSC;
import MTCSC.entity.TimePoint2;
import MTCSC.entity.TimeSeries2;
import MTCSC.entity.CGGeometryLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.text.DecimalFormat;

public class RCSWS {
    private TimeSeries2 timeseries;
    private TimePoint2 kp;
    private double SMAX;  // maximum speed

    public RCSWS(TimeSeries2 timeseries, double sMax) {
        setTimeSeries(timeseries);
        setSMAX(sMax);
    }
    
    public void setTimeSeries(TimeSeries2 timeSeries) {
        this.timeseries = timeSeries;
    }
    
    public void setSMAX(double SMAX) {
        this.SMAX = SMAX;
    }

    // Calculate the distance between two points
    public static double calculateDistance(TimePoint2 tp, double[] xy) {
        double[] xy1 = tp.getModify();
        return Math.sqrt(Math.pow(xy1[0] - xy[0], 2) + Math.pow(xy1[1] - xy[1], 2));
    }

    public static double calculateDistance(TimePoint2 tp1, TimePoint2 tp2) {
        double[] xy1 = tp1.getModify();
        double[] xy2 = tp2.getModify();
        return Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
    }

    // Calculate the intersection point of two circles
    public double[][] calculateIntersectionPoints(TimePoint2 tp1, TimePoint2 tp2, long t) {
        double[] xy1 = tp1.getModify();
        double[] xy2 = tp2.getModify();
        long t1 = tp1.getTimestamp();
        long t2 = tp2.getTimestamp();
        double x1 = xy1[0], x2 = xy2[0], y1 = xy1[1], y2 = xy2[1];
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double radius1 = Math.abs((t1 - t)) * this.SMAX;
        double radius2 = Math.abs((t2 - t)) * this.SMAX;
        // double constrain = radius1 + radius2;
        // DecimalFormat df = new DecimalFormat("#.00");
        // distance = Double.parseDouble(df.format(distance));
        // constrain = Double.parseDouble(df.format(constrain));
        // // System.out.println("distance = " + distance);
        // // System.out.println("constrain = " + constrain);
        // 
        // if ((distance >= constrain) || (distance <= Math.abs(radius1 - radius2))) {
        //     System.out.println("Two circles do not intersect");
        //     return null;
        // } 
        if (((distance+0.000001) < radius1 + radius2) && (distance > (Math.abs(radius1 - radius2)+0.000001))) {
            double a = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(distance, 2)) / (2 * distance);
            double h = Math.sqrt(Math.pow(radius1, 2) - Math.pow(a, 2));

            double xIntersection1 = x1 + a * (x2 - x1) / distance + h * (y2 - y1) / distance;
            double yIntersection1 = y1 + a * (y2 - y1) / distance - h * (x2 - x1) / distance;

            double xIntersection2 = x1 + a * (x2 - x1) / distance - h * (y2 - y1) / distance;
            double yIntersection2 = y1 + a * (y2 - y1) / distance + h * (x2 - x1) / distance;
            return new double[][]{{xIntersection1, yIntersection1}, {xIntersection2, yIntersection2}};
        }
        else{
            System.out.println("Two circles do not intersect");
            return null;
        }
    }

    // Calculate the intersection point of a straight line and a circle
    public double[][] calculateLineCircleIntersection(TimePoint2 p1, TimePoint2 kp) {
        CGGeometryLib x = new CGGeometryLib();
        double[][]xy = x.getLineCircleNode(p1, kp, this.SMAX);
        return xy;
    }

    public boolean judgeSpeedConstrain(TimePoint2 tp1, TimePoint2 tp2){
        double[] xy1 = tp1.getModify();
        double[] xy2 = tp2.getModify();
        long t1 = tp1.getTimestamp();
        long t2 = tp2.getTimestamp();
        double distance = Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
        double range = (t2 - t1) * this.SMAX;
        if(distance > range)
            return false;
        else   
            return true;
    }

    public boolean judgeSpeedConstrain(TimePoint2 tp1, double[] p, long t){
        double[] xy1 = tp1.getModify();
        long t1 = tp1.getTimestamp();
        double distance = Math.sqrt(Math.pow(xy1[0] - p[0], 2) + Math.pow(xy1[1] - p[1], 2));
        double range = Math.abs((t1 - t)) * this.SMAX;
        if(distance > range)
            return false;
        else   
            return true;
    }

    public boolean judgeIntersection(TimePoint2 tp1, TimePoint2 tp2, long t){
        double[] xy1 = tp1.getModify();
        double[] xy2 = tp2.getModify();
        long t1 = tp1.getTimestamp();
        long t2 = tp2.getTimestamp();
        double x1 = xy1[0], x2 = xy2[0], y1 = xy1[1], y2 = xy2[1];
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double radius1 = Math.abs((t1 - t)) * this.SMAX;
        double radius2 = Math.abs((t2 - t)) * this.SMAX;
        // double constrain = radius1 + radius2;
        // DecimalFormat df = new DecimalFormat("#.00");
        // distance = Double.parseDouble(df.format(distance));
        // constrain = Double.parseDouble(df.format(constrain));
        // // Check if circles intersect
        // if ((distance < constrain) && (distance > Math.abs(radius1 - radius2))) {
        //     return true;
        // }
        if (((distance+0.000001) < radius1 + radius2) && (distance > (Math.abs(radius1 - radius2)+0.000001))) {
            return true;
        }
        return false;
    }

    public TimeSeries2 clean(int wSize){
        ArrayList<TimePoint2> totalList = this.timeseries.getTimeseries();
        int size = totalList.size();
        TimePoint2 prePoint = totalList.get(0);
        int readIndex = 1;
        TimePoint2 keyPoint = totalList.get(readIndex);
        int tempIndex = readIndex+1;
        TimePoint2 tempPoint = totalList.get(tempIndex);
        
        ArrayList<Double> proList = new ArrayList<>();
        while(readIndex < size-1){
            // PI-1 and PI do not comply with speed constraints
            if(!judgeSpeedConstrain(prePoint, keyPoint)){
                //Identify the first subsequent point that intersects with the pre Point velocity constraint range
                boolean flag = true;
                while(!judgeIntersection(prePoint, tempPoint, keyPoint.getTimestamp())){
                    if(tempIndex+1 < size){
                        tempIndex++;
                        tempPoint = totalList.get(tempIndex);
                    }
                    else{
                        flag = false;
                        break;
                    }
                }
                //Determine whether p1 satisfies the constraint. If it does, fix it directly. Otherwise, calculate p2 and p3
                double[][] xy = calculateLineCircleIntersection(prePoint, keyPoint);
                double[] p1 = new double[2];
                if(calculateDistance(keyPoint, xy[0]) <= calculateDistance(keyPoint, xy[1])){
                    p1 = xy[0];
                }
                else{
                    p1 = xy[1];
                }
                // if(p1==null){
                //     System.out.println("p1==null");
                // }
                // Formula 5, Case 1
                if((judgeSpeedConstrain(tempPoint, p1, keyPoint.getTimestamp()))||(flag==false)){
                    keyPoint.setModify(p1);
                }
                else{
                    double[][] p23 = calculateIntersectionPoints(prePoint, tempPoint, keyPoint.getTimestamp());
                    double distance12 = calculateDistance(keyPoint, p23[0]);
                    double distance13 = calculateDistance(keyPoint, p23[1]);
                    // Formula 5, Case 2
                    if(distance12 <= distance13){
                        keyPoint.setModify(p23[0]);
                    }
                    // Formula 5, Case 3
                    else{
                        keyPoint.setModify(p23[1]);
                    }
                }
            }
            // Compliant with speed constraints, using sliding window maximum likelihood estimation
            // else{
            //     tempIndex = readIndex+1;
            //     if(readIndex+1 < size)
            //         tempPoint = totalList.get(tempIndex);
            //     double speed = 0;
            //     if(proList.size() == (wSize/2)){
            //         speed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
            //         boolean isInrange = isInRange(proList, speed);
            //         if(!isInrange){
            //             double[] modify = new double[2];
            //             double repair_dis = proList.get(proList.size() - 1) * (keyPoint.getTimestamp()-prePoint.getTimestamp());
            //             double pre_next_dis = calculateDistance(tempPoint,prePoint);
            //             double[] xy1 = new double[2];
            //             double[] xy2 = new double[2];
            //             xy1 = prePoint.getModify();
            //             xy2 = tempPoint.getModify();
            //             modify[0] = ((xy1[0] - xy2[0]) * (repair_dis / pre_next_dis) + xy1[0]);
            //             modify[1] = ((xy1[1] - xy2[1]) * (repair_dis / pre_next_dis) + xy1[1]);
            //             keyPoint.setModify(modify);
            //         }
            //         proList.remove(0);
            //         speed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
            //         proList.add(speed);
            //     }
            //     // windows size is 50
            //     if(proList.size() < (wSize/2)){
            //         speed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
            //         proList.add(speed);
            //     }
            // }
            // Complies with speed constraints and does not require cleaning
            // Update the previous node, current node, and next node
            if (Double.isNaN(keyPoint.getModify()[0]) || Double.isNaN(keyPoint.getModify()[1])) {
                System.out.println("NaN");
            }
            prePoint = keyPoint;
            readIndex++;
            keyPoint = totalList.get(readIndex);
            tempIndex = readIndex+1;
            if(readIndex+1 < size)
                tempPoint = totalList.get(readIndex+1);
        }

        // Maximum likelihood estimation of sliding window
        // int firstIndex = 0;
        // double firstSpeed = 0;
        // prePoint = totalList.get(firstIndex);
        // keyPoint = totalList.get(firstIndex+1);
        // firstSpeed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
        // int preIndex = 0;
        // readIndex = 1;
        // int midIndex = 0;
        // double speed = 0;
        // double midspeed = 0;
        // TimePoint2 midPoint;
        // while(readIndex < size-1 && readIndex <= wSize){
        //     prePoint = totalList.get(preIndex);
        //     keyPoint = totalList.get(readIndex);
        //     speed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
        //     proList.add(speed);
        //     if (readIndex == (wSize/2)){
        //         midIndex = readIndex;
        //         midspeed = speed;
        //     }
        //     preIndex = readIndex;
        //     readIndex++;
        // }
        // if(midIndex == 0){

        // }
        // else{
        //     while(readIndex < size-1){
        //         Collections.sort(proList);
        //         boolean isInrange = isInRange(proList, midspeed, wSize);
        //         if(!isInrange){
        //             midPoint = totalList.get(midIndex);
        //             prePoint = totalList.get(midIndex-1);
        //             TimePoint2 preprePoint = totalList.get(midIndex-2);
        //             double[] modify = new double[2];
        //             double tempSpeed = calculateDistance(preprePoint,prePoint)/(prePoint.getTimestamp()-preprePoint.getTimestamp());
        //             double repair_dis = tempSpeed * (midPoint.getTimestamp()-prePoint.getTimestamp());
        //             tempPoint = totalList.get(midIndex+1);
        //             double pre_next_dis = calculateDistance(tempPoint,prePoint);
        //             double[] xy1 = new double[2];
        //             double[] xy2 = new double[2];
        //             xy1 = prePoint.getModify();
        //             xy2 = tempPoint.getModify();
        //             modify[0] = ((xy2[0] - xy1[0]) * (repair_dis / pre_next_dis) + xy1[0]);
        //             modify[1] = ((xy2[1] - xy1[1]) * (repair_dis / pre_next_dis) + xy1[1]);
        //             midPoint.setModify(modify);
        //         }
        //         prePoint = totalList.get(preIndex);
        //         keyPoint = totalList.get(readIndex);
        //         speed = calculateDistance(prePoint,keyPoint)/(keyPoint.getTimestamp()-prePoint.getTimestamp());
        //         proList.add(speed);
        //         int index = 0;
        //         for(int i = 0; i<proList.size(); i++){
        //             if(proList.get(i) == firstSpeed){
        //                 index = i;
        //                 break;
        //             }
        //         }
        //         proList.remove(index);
        //         midIndex++;
        //         preIndex = readIndex;
        //         readIndex++;
        //         firstIndex++;
        //         firstSpeed = calculateDistance(totalList.get(firstIndex),totalList.get(firstIndex+1))/(totalList.get(firstIndex+1).getTimestamp()-totalList.get(firstIndex).getTimestamp());
        //     }
        // }
        return this.timeseries;
    }

    public static boolean isInRange(ArrayList<Double> list, double value, int wsize) {
        int index = 0;
        for(int i = 0; i<list.size(); i++){
            if(list.get(i) == value){
                index = i;
            }
        }
        double pro = 0.8;
        pro = (1-pro)/2;
        double minIndex = wsize * pro;
        double maxIndex = wsize * (1-pro);
        if(index > minIndex && index < maxIndex){
            return true;
        }
        else{
            return false;
        }
    }

    // Calculate the intersection point of a straight line and a circle
    public static double[] calculateLineCircleIntersection(double xCenter, double yCenter, double radius,
                                                             double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dr = Math.sqrt(dx * dx + dy * dy);
        double D = x1 * y2 - x2 * y1;

        double discriminant = radius * radius * dr * dr - D * D;

        // Determine if there are intersections
        if (discriminant >= 0) {
            double xPart1 = D * dy;
            double yPart1 = -D * dx;
            double xPart2 = Math.signum(dy) * dx * Math.sqrt(discriminant);
            double yPart2 = Math.abs(dy) * Math.sqrt(discriminant);

            double xIntersection = (xPart1 + xPart2) / (dr * dr);
            double yIntersection = (yPart1 + yPart2) / (dr * dr);

            return new double[]{xIntersection + xCenter, yIntersection + yCenter};
        } else {
            return null;
        }
        // double[] intersectionPoints = calculateLineCircleIntersection(xCenter, yCenter, radius, x1, y1, x2, y2);
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // Calculate the intersection point of two circles
    public static double[][] calculateIntersectionPoints(double x1, double y1, double radius1,
                                                          double x2, double y2, double radius2) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        // Check if circles intersect
        if (distance <= radius1 + radius2 && distance >= Math.abs(radius1 - radius2)) {
            double a = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(distance, 2)) / (2 * distance);
            double h = Math.sqrt(Math.pow(radius1, 2) - Math.pow(a, 2));

            double xIntersection1 = x1 + a * (x2 - x1) / distance + h * (y2 - y1) / distance;
            double yIntersection1 = y1 + a * (y2 - y1) / distance - h * (x2 - x1) / distance;

            double xIntersection2 = x1 + a * (x2 - x1) / distance - h * (y2 - y1) / distance;
            double yIntersection2 = y1 + a * (y2 - y1) / distance + h * (x2 - x1) / distance;
            return new double[][]{{xIntersection1, yIntersection1}, {xIntersection2, yIntersection2}};
        } else {
            return null;
        }
        //double[][] intersectionPoints = calculateIntersectionPoints(x1, y1, radius1, x2, y2, radius2);
    }
}
