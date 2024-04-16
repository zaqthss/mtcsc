package cn.edu.thu.MTCSC.entity;

import java.util.ArrayList;

public class TimeSeriesN {
    private ArrayList<TimePointN> Timeseries;

	public TimeSeriesN(ArrayList<TimePointN> timeseries) {
		setTimeseries(timeseries);
	}

	public TimeSeriesN() {
		setTimeseries(new ArrayList<TimePointN>());
	}

	public ArrayList<TimePointN> getTimeseries() {
		return this.Timeseries;
	}

	public void setTimeseries(ArrayList<TimePointN> timeseries) {
		this.Timeseries = timeseries;
	}

    public void addPoint(TimePointN tp) {
		this.Timeseries.add(tp);
	}

	public int getLength() {
		return this.Timeseries.size();
	}

    public void removePoint(int index) {
		this.Timeseries.remove(index);
    }
	
}
