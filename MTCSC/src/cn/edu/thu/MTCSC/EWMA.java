package cn.edu.thu.MTCSC;

import java.util.ArrayList;
import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;


public class EWMA {
	private TimeSeries timeSeries;
	private ArrayList<TimePoint> tempList;
	private double alpha;	// the smooth factor
	
	public EWMA(TimeSeries timeSeries, double alpha) {
		setTimeSeries(timeSeries);
		setAlpha(alpha);
		tempList = this.timeSeries.getTimeseries();
	}

	public void setTimeSeries(TimeSeries timeSeries) {
		this.timeSeries = timeSeries;
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public TimeSeries mainExp() {
		// s_t = \alpha \cdot x_{t-1} + (1-\alpha) \cdot s_{t-1}.
		
		double s = tempList.get(0).getValue();
		double x = s;
		long oldstamp = tempList.get(0).getTimestamp();
		long curstamp = 0;
		
		for (TimePoint tp : tempList) {
			curstamp = tp.getTimestamp();
			while (curstamp > oldstamp) {
				s = alpha * x + (1 - alpha) * s;
				oldstamp++;
			}
			tp.setModify(s);
			x = tp.getValue();
		}
		
		return timeSeries;
	}
}