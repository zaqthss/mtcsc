package MTCSC.entity;

import java.util.ArrayList;
import java.util.Collections;
import MTCSC.entity.ComparatorTimestamp;

public class TimeSeriesIMR {
  private ArrayList<TimePointIMR> timeseries;
  private int classID;  // only for application of classification

  public TimeSeriesIMR(ArrayList<TimePointIMR> timeseries) {
    setTimeseries(timeseries);
    setClassID(0);
  }

  public TimeSeriesIMR() {
    setTimeseries(new ArrayList<TimePointIMR>());
    setClassID(0);
  }

  public ArrayList<TimePointIMR> getTimeseries() {
    return timeseries;
  }

  public void setTimeseries(ArrayList<TimePointIMR> timeseries) {
    this.timeseries = timeseries;
  }

  public int getClassID() {
    return classID;
  }

  public void setClassID(int classID) {
    this.classID = classID;
  }

  public void addPoint(TimePointIMR tp) {
    this.timeseries.add(tp);
  }

  public void addPointToPos(TimePointIMR tp, int index) {
    this.timeseries.add(index, tp);
  }

  public void addSeries(TimeSeriesIMR timeseries) {
    this.timeseries.addAll(timeseries.getTimeseries());
  }

  /**
   * get the timepoints with [begin, end]
   * 
   * @param begin
   *          timestamp
   * @param end
   *          timestamp
   * @param index
   *          begin index
   * @return
   */
  public TimeSeriesIMR getSubTimeseries(long begin, long end, int index) {
    ArrayList<TimePointIMR> window = new ArrayList<TimePointIMR>();
    TimePointIMR tp = null;
    long timestamp = 0;
    int len = timeseries.size();

    /**
     * once there is an exceed value, break, the remains will be in heuristic
     * part
     */
    for (int i = index; i < len; ++i) {
      tp = timeseries.get(i);
      timestamp = tp.getTimestamp();
      if (timestamp >= begin && timestamp <= end)
        window.add(tp);
      else if (timestamp > end)
        break;
    }

    TimeSeriesIMR newSeries = new TimeSeriesIMR(window);

    return newSeries;
  }

  /**
   * T[a:b] = t_a,t_a+1,...,t_b
   * 
   * @param beginIndex
   * @param endIndex
   * @return
   */
  public TimeSeriesIMR getSubPoints(int beginIndex, int endIndex) {
    ArrayList<TimePointIMR> window = new ArrayList<TimePointIMR>();
    int len = timeseries.size();

    for (int i = beginIndex; i <= endIndex; ++i) {
      if (i < len)
        window.add(timeseries.get(i));
      else
        break;
    }

    TimeSeriesIMR newSeries = new TimeSeriesIMR(window);

    return newSeries;
  }

  public int getLength() {
    return timeseries.size();
  }

  /**
   * find the point with the specific timestamp or one bigger
   * 
   * @param timestamp
   * @return
   */
  public boolean findByTimestamp(long timestamp, int[] params) {
    int index = 0;

    for (TimePointIMR tp : timeseries) {
      if (tp.getTimestamp() == timestamp) {
        params[0] = index;
        return true;
      }
      if (tp.getTimestamp() > timestamp) {
        params[0] = index;
        return false;
      }
      index++;
    }
    // all of the timestamp is smaller than it
    params[0] = timeseries.size();
    return false;
  }

  /**
   * find the current position of timestamp
   * 
   * @param timestamp
   * @return
   */
  public int findRealPos(long timestamp) {
    int targetIndex = -1;

    for (int index = 0; index < timeseries.size(); ++index) {
      if (timeseries.get(index).getTimestamp() == timestamp)
        return index;
    }

    return targetIndex;
  }

  /**
   * clear all the modifications
   */
  public void clear() {
    double truth, observe;

    for (TimePointIMR tp : timeseries) {
      truth = tp.getTruth();
      tp.setTruthVal(truth);
      observe = tp.getObserve();
      tp.setObsVal(observe);
      tp.setModify(observe);
     
      tp.setStatus(0);
      tp.setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
      tp.setDeltas(0, 0);
    }

    Collections.sort(timeseries, new ComparatorTimestamp());
  }
  
  /**
   * clear all the modifications
   */
  public void gpsclear() {
    double truth, observe;
    
    for (TimePointIMR tp : timeseries) {
      truth = tp.getTruth();
      tp.setTruthVal(truth);
      observe = tp.getObserve();
      tp.setObsVal(observe);
      tp.setModify(observe);
     
      tp.setStatus(0);
      tp.setLabel(false);
      tp.setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
      tp.setDeltas(0, 0);
    }

    Collections.sort(timeseries, new ComparatorTimestamp());
  }
  
  /**
   * remain all the noise and label information
   */
  public void clearModify() {
    double truth, observe;
    
    for (TimePointIMR tp : timeseries) {
      truth = tp.getTruth();
      tp.setTruthVal(truth);
      observe = tp.getObsVal();
      tp.setModify(observe);
     
      tp.setStatus(0);
      tp.setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
      tp.setDeltas(0, 0);
    }

    Collections.sort(timeseries, new ComparatorTimestamp());
  }
}
