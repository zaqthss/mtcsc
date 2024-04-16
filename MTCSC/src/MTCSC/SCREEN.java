package MTCSC;

import MTCSC.entity.TimePoint;
import MTCSC.entity.TimeSeries;
import java.util.ArrayList;
import java.util.Collections;


public class SCREEN {

  private TimeSeries timeseries;
  private TimePoint kp;

  private long T;       // the window size
  private double SMAX;  // maximum speed
  private double SMIN;  // minimum speed

  /**
   *
   * @param timeseries timeseries
   * @param sMax maximum allowed speed
   * @param sMin minimum allowed speed
   * @param t the window size
   */
  public SCREEN(TimeSeries timeseries, double sMax, double sMin, long t) {
    setTimeSeries(timeseries);
    setT(t);
    setSMAX(sMax);
    setSMIN(sMin);
  }

  public void setTimeSeries(TimeSeries timeSeries) {
    this.timeseries = timeSeries;
  }

  public void setT(long t) {
    this.T = t;
  }

  public void setSMAX(double sMax) {
    this.SMAX = sMax;
  }

  public void setSMIN(double sMin) {
    this.SMIN = sMin;
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
          // tanxin(tempSeries);
          
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
      // tanxin(tempSeries);
			prePoint = kp;
			tempList.remove(0);
		}

    // form resultSeries
    // TimeSeries resultSeries = new TimeSeries();
    // long timestamp;
    // double modify;
    // double truth;

    // for (TimePoint timePoint : timeseries.getTimeseries()) {
    //   timestamp = timePoint.getTimestamp();
    //   modify = timePoint.getModify();
    //   truth = timePoint.getTruth();
    //   tp = new TimePoint(timestamp, modify, truth);
    //   resultSeries.addPoint(tp);
    // }

    // return resultSeries;
    return timeseries;
  }

  /**
   * Algorithm 1
   *
   * @param timeSeries timeseries in a window
   * @param prePoint the former modified point
   */
  private void local(TimeSeries timeSeries, TimePoint prePoint) {
    ArrayList<TimePoint> tempList = timeSeries.getTimeseries();
    // get bound
    long preTime = prePoint.getTimestamp();
    double preVal = prePoint.getModify();
    long kpTime = kp.getTimestamp();

    double lowerBound = preVal + SMIN * (kpTime - preTime);
    double upperBound = preVal + SMAX * (kpTime - preTime);

    // form candidates
    ArrayList<Double> xkList = new ArrayList<>();
    int len = tempList.size();

    xkList.add(kp.getModify());

    TimePoint tp;
    for (int i = 1; i < len; ++i) {
      tp = tempList.get(i);
      double val = tp.getModify();
      long dTime = kpTime - tp.getTimestamp();
      xkList.add(val + SMIN * dTime);
      xkList.add(val + SMAX * dTime);
    }

    Collections.sort(xkList);
    double xMid = xkList.get(len - 1);
    double modify = xMid;
    if (upperBound < xMid) {
      modify = upperBound;
    } else if (lowerBound > xMid) {
      modify = lowerBound;
    }

    kp.setModify(modify);
  }

  // private void tanxin(TimeSeries timeSeries) {
  //   ArrayList<TimePoint> tempList = timeSeries.getTimeseries();
  //   double kpVal = kp.getModify();
  //   long kpTime = kp.getTimestamp();
  //   double lowerBound = 0;
  //   double upperBound = 0;
  //   TimePoint tp;
  //   long nowTime = 0;
  //   int len = tempList.size();
  //   for (int i = 1; i < len; ++i) {
  //     tp = tempList.get(i);
  //     nowTime = tp.getTimestamp();
  //     lowerBound = kpVal + SMIN * (nowTime - kpTime);
  //     upperBound = kpVal + SMAX * (nowTime - kpTime);
  //     double modify = tp.getModify();
  //     if (upperBound < modify) {
  //       modify = upperBound;
  //       tp.setModify(modify);
  //     } else if (lowerBound > modify) {
  //       modify = lowerBound;
  //       tp.setModify(modify);
  //     }
  //   }
  // }
}

