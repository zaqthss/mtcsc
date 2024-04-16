package MTCSC.util;

import java.util.Comparator;

import MTCSC.entity.TimePoint2;

public class ComparatorTimestamp2 implements Comparator<TimePoint2> {
	
	@Override
	public int compare(TimePoint2 tp1, TimePoint2 tp2) {
		// TODO Auto-generated method stub
        
		if (tp1.getTimestamp() > tp2.getTimestamp()) {    
            return 1;    
        } else if (tp1.getTimestamp() < tp2.getTimestamp()) {    
            return -1;    
        } else {    
            return 0;    
        }
	}
}
