package MTCSC.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import MTCSC.util.ComparatorTimestamp2;

public class TimeSeries2 {
	private ArrayList<TimePoint2> timeseries;

	public TimeSeries2(ArrayList<TimePoint2> timeseries) {
		setTimeseries(timeseries);
	}

	public TimeSeries2() {
		setTimeseries(new ArrayList<TimePoint2>());
	}

	public ArrayList<TimePoint2> getTimeseries() {
		return timeseries;
	}

	public void setTimeseries(ArrayList<TimePoint2> timeseries) {
		this.timeseries = timeseries;
	}

	public void addPoint(TimePoint2 tp) {
		this.timeseries.add(tp);
	}

	public void removePoint(int index) {
		this.timeseries.remove(index);
	}

	public void addPointToPos(TimePoint2 tp, int index) {
		this.timeseries.add(index, tp);
	}

	public void addSeries(TimeSeries2 timeseries) {
		this.timeseries.addAll(timeseries.getTimeseries());
	}

	/**
	 * get the timepoints with [begin, end]
	 * 
	 * @param begin
	 *            timestamp
	 * @param end
	 *            timestamp
	 * @param index
	 *            begin index
	 * @return
	 */
	public TimeSeries2 getSubTimeseries(long begin, long end, int index) {
		ArrayList<TimePoint2> window = new ArrayList<TimePoint2>();
		TimePoint2 tp = null;
		long timestamp = 0;
		int len = timeseries.size();

		/**
		 * once there is an exceed value, break, the remains will be in
		 * heuristic part
		 */
		for (int i = index; i < len; ++i) {
			tp = timeseries.get(i);
			timestamp = tp.getTimestamp();
			if (timestamp >= begin && timestamp <= end)
				window.add(tp);
			else if (timestamp > end)
				break;
		}

		TimeSeries2 newSeries = new TimeSeries2(window);

		return newSeries;
	}

	/**
	 * T[a:b] = t_a,t_a+1,...,t_b
	 * 
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public TimeSeries2 getSubPoints(int beginIndex, int endIndex) {
		ArrayList<TimePoint2> window = new ArrayList<TimePoint2>();
		int len = timeseries.size();

		for (int i = beginIndex; i <= endIndex; ++i) {
			if (i < len)
				window.add(timeseries.get(i));
			else
				break;
		}

		TimeSeries2 newSeries = new TimeSeries2(window);

		return newSeries;
	}

	/**
	 * get the first point
	 * 
	 * @return
	 */
	public double[] getMedianVal() {

		return timeseries.get(0).getOrgval();
	}

	/**
	 * return the real median of such timeseries
	 * 
	 * @return
	 */
	public double[] getRealMedian() {
		int len = timeseries.size();
		double[][] vals = new double[len][2];

		for (int i = 0; i < len; ++i) {
			vals[i] = timeseries.get(i).getOrgval();
		}

		Arrays.sort(vals);

		if (len % 2 == 1) {
			return vals[len / 2];
		} else {
			// return ((vals[len / 2 - 1] + vals[len / 2]) / 2);
			return vals[len / 2 - 1];
		}
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

		for (TimePoint2 tp : timeseries) {
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
		int index = -1;

		for (index = 0; index < timeseries.size(); ++index) {
			if (timeseries.get(index).getTimestamp() == timestamp)
				return index;
		}

		return index;
	}

	/**
	 * clear all the modifications
	 */
	public void clear() {
		double[] val = new double[2];
        double x_val, y_val;
		for (TimePoint2 tp : timeseries) {
			val = tp.getOrgval();
            x_val = val[0];
            y_val = val[1];
			tp.setOrgval(x_val, y_val);
			tp.setModify(x_val, y_val);
		}

		Collections.sort(timeseries, new ComparatorTimestamp2());
	}
}
