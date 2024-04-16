package MTCSC.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import MTCSC.util.ComparatorTimestamp;

public class TimeSeries {
	private ArrayList<TimePoint> timeseries;
	private int classID;  // only for application of classification
	private int Num = 0;
	private ArrayList<Integer> NumList = new ArrayList<Integer>();
  
	public TimeSeries(ArrayList<TimePoint> timeseries) {
	  setTimeseries(timeseries);
	  setClassID(0);
	}
  
	public TimeSeries() {
	  setTimeseries(new ArrayList<TimePoint>());
	  setClassID(0);
	}
  
	public ArrayList<TimePoint> getTimeseries() {
	  return timeseries;
	}
  
	public void setTimeseries(ArrayList<TimePoint> timeseries) {
	  this.timeseries = timeseries;
	}
  
	public int getClassID() {
	  return classID;
	}
  
	public void setClassID(int classID) {
	  this.classID = classID;
	}
  
	public void addPoint(TimePoint tp) {
	  this.timeseries.add(tp);
	}
  
	public void addPointToPos(TimePoint tp, int index) {
	  this.timeseries.add(index, tp);
	}
  
	public void addSeries(TimeSeries timeseries) {
	  this.timeseries.addAll(timeseries.getTimeseries());
	}

	public int getNum() {
		return this.Num;
	}
	
	public void setNum(int num) {
		this.Num = num;
	}

	public ArrayList<Integer> getNumList() {
		return this.NumList;
	}
	
	public void setNumList(ArrayList<Integer> numList) {
		this.NumList = numList;
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
	public TimeSeries getSubTimeseries(long begin, long end, int index) {
	  ArrayList<TimePoint> window = new ArrayList<TimePoint>();
	  TimePoint tp = null;
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
  
	  TimeSeries newSeries = new TimeSeries(window);
  
	  return newSeries;
	}
  
	/**
	 * T[a:b] = t_a,t_a+1,...,t_b
	 * 
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public TimeSeries getSubPoints(int beginIndex, int endIndex) {
	  ArrayList<TimePoint> window = new ArrayList<TimePoint>();
	  int len = timeseries.size();
  
	  for (int i = beginIndex; i <= endIndex; ++i) {
		if (i < len)
		  window.add(timeseries.get(i));
		else
		  break;
	  }
  
	  TimeSeries newSeries = new TimeSeries(window);
  
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
  
	  for (TimePoint tp : timeseries) {
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
  
	  for (TimePoint tp : timeseries) {
		truth = tp.getTruth();
		tp.setTruth(truth);
		observe = tp.getOrgval();
		tp.setOrgval(observe);
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
	  
	  for (TimePoint tp : timeseries) {
		truth = tp.getTruth();
		tp.setTruth(truth);
		observe = tp.getOrgval();
		tp.setOrgval(observe);
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
	  
	  for (TimePoint tp : timeseries) {
		truth = tp.getTruth();
		tp.setTruth(truth);
		observe = tp.getOrgval();
		tp.setModify(observe);
	   
		tp.setStatus(0);
		tp.setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
		tp.setDeltas(0, 0);
	  }
  
	  Collections.sort(timeseries, new ComparatorTimestamp());
	}
  }
  