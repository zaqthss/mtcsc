package cn.edu.thu.MTCSC.entity;

import java.util.Comparator;
import cn.edu.thu.MTCSC.entity.TimePointIMR;


public class ComparatorTimestamp implements Comparator<TimePointIMR> {

  @Override
  public int compare(TimePointIMR tp1, TimePointIMR tp2) {
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
