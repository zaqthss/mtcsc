package cn.edu.thu.MTCSC;

import gurobi.*;
import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import java.util.ArrayList;


public class MTCSC_QCQP {

  private TimeSeries2 timeseries;
  private TimePoint2 kp;
  private int T;       // the window size
  private double SMAX;  // maximum speed

  /**
   *
   * @param timeseries timeseries
   * @param sMax maximum allowed speed
   * @param t the window size
   */
  public MTCSC_QCQP(TimeSeries2 timeseries, double sMax, int t) {
    setTimeSeries(timeseries);
    setSMAX(sMax);
    setT(t);
  }

  public void setTimeSeries(TimeSeries2 timeSeries) {
    this.timeseries = timeSeries;
  }

  public void setT(int t) {
    this.T = t;
  }

  public void setSMAX(double SMAX) {
    this.SMAX = SMAX;
  }

  /**
   *
   * @return timeseries after repair
   */
  public TimeSeries2 mainScreen() {
    ArrayList<TimePoint2> totalList = timeseries.getTimeseries();
    int size = totalList.size();

    long preEnd = -1, curEnd;
    // the startTime in the window, the real end time in the window, the maximum allowed
    long wStartTime, wEndTime, wGoalTime;
    long curTime=0;
    TimePoint2 prePoint = null;    // the last fixed point
    TimePoint2 tp;

    TimeSeries2 tempSeries = new TimeSeries2();
    ArrayList<TimePoint2> tempList;

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

  // calculate the distance
  private double distance(TimePoint2 prePoint, TimePoint2 kp) {
    double distance = 0;
    double[] xy_pp = new double[2];
    double[] xy_kp = new double[2];
    xy_pp = prePoint.getModify();
    xy_kp = kp.getModify();
    distance = (xy_pp[0] - xy_kp[0]) * (xy_pp[0] - xy_kp[0]) + (xy_pp[1] - xy_kp[1]) * (xy_pp[1] - xy_kp[1]);
    return distance;
  }

  public static double calculateDistance(TimePoint2 tp1, TimePoint2 tp2) {
    double[] xy1 = tp1.getModify();
    double[] xy2 = tp2.getModify();
    return Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
  }

  // judge whether to modify
  private boolean judgeModify(double[]preVal, double[]maxVal, double[]kpVal,
                              long preTime, long maxTime, long kpTime) {
    boolean judge = true;
    double c1,c2;
    c1 = (preVal[0]-kpVal[0])*(preVal[0]-kpVal[0])+(preVal[1]-kpVal[1])*(preVal[1]-kpVal[1]);
    c2 = (maxVal[0]-kpVal[0])*(maxVal[0]-kpVal[0])+(maxVal[1]-kpVal[1])*(maxVal[1]-kpVal[1]);
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
  private void local(TimeSeries2 timeSeries, TimePoint2 prePoint) {
    ArrayList<TimePoint2> tempList = timeSeries.getTimeseries();
    
    // get bound
    long preTime = prePoint.getTimestamp();
    double[] preVal = new double[2];
    preVal = prePoint.getModify();
    long kpTime = kp.getTimestamp();
    double[] kpVal = new double[2];
    kpVal = kp.getModify();

    // form candidates
    int length = tempList.size();
    int[] top = new int[length+1]; //Default is 0,-1
    int[] len = new int[length+1]; //Default is 0, take the longest to form candidate 
    
    TimePoint2 tp1, tp2;
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
        long tt = tp1.getTimestamp();
        if (distance(prePoint, tp1) <= ((tt-preTime)*(tt-preTime) * SMAX * SMAX)) {
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
                    //   // distance(prePoint, tp1) > ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)
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
                System.out.println("error");
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
    TimePoint2 maxPoint = tempList.get(maxIndex);
    long maxTime = maxPoint.getTimestamp();
    double[] maxVal = new double[2];
    maxVal = maxPoint.getModify();
    
    // whether to repair
    boolean judge = judgeModify(preVal, maxVal, kpVal, preTime, maxTime, kpTime);
    // use QCQP to repair
    if (judge) {
      // QCQP
      double[] xy_modify = new double[2];
      try {
        GRBEnv env = new GRBEnv(true);
        env.start();
        GRBModel model = new GRBModel(env);
        model.set(GRB.IntParam.OutputFlag, 0);  
        // model.set(GRB.IntParam.NonConvex, 2);
        // Create variables 
        GRBVar x = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x");
        GRBVar y = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y");
        
        x.set(GRB.DoubleAttr.Start, kpVal[0]);
        y.set(GRB.DoubleAttr.Start, kpVal[1]);
        // Set objective
        GRBQuadExpr obj = new GRBQuadExpr();
        obj.addTerm(1.0, x, x);
        obj.addTerm(-(2.0*kpVal[0]), x);
        obj.addTerm(1.0, y, y);
        obj.addTerm(-(2.0*kpVal[1]), y);
        model.setObjective(obj, GRB.MINIMIZE);
        // Add constraint:prePoint
        GRBQuadExpr qexpr = new GRBQuadExpr();
        qexpr.addTerm(1.0, x, x);   
        qexpr.addTerm(-(2.0*preVal[0]), x);   
        qexpr.addTerm(1.0, y, y);   
        qexpr.addTerm(-(2.0*preVal[1]), y);   
        model.addQConstr(qexpr, GRB.LESS_EQUAL, ((kpTime-preTime)*(kpTime-preTime)*SMAX*SMAX - preVal[0]*preVal[0] - preVal[1]*preVal[1]), "qc1");
        // Add constraint:
        if (topIndex != 0) {
          qexpr = new GRBQuadExpr();
          qexpr.addTerm(1.0, x, x);   
          qexpr.addTerm(-(2.0*maxVal[0]), x);   
          qexpr.addTerm(1.0, y, y);   
          qexpr.addTerm(-(2.0*maxVal[1]), y);   
          model.addQConstr(qexpr, GRB.LESS_EQUAL, ((maxTime-kpTime)*(maxTime-kpTime)*SMAX*SMAX - maxVal[0]*maxVal[0] - maxVal[1]*maxVal[1]), "qc2");
        }
        // Optimize model
        model.optimize();
        // the result
        xy_modify[0] = x.get(GRB.DoubleAttr.X);
        xy_modify[1] = y.get(GRB.DoubleAttr.X);
        // Dispose of model and environment
        model.dispose();
        env.dispose();
      } catch (GRBException e) {
        System.out.println("Error code: " + e.getErrorCode() + ". " +
                           e.getMessage());
        }
      if(xy_modify[0]!=0 && xy_modify[1]!=0) {
          kp.setModify(xy_modify);
      }
      // kp.setModify(xy_modify);
    }

  }
}

