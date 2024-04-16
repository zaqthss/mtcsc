package cn.edu.thu.MTCSC.entity;

public class TimePointIMR {
  private long timestamp;
  private double truthVal; // the truth
  private double obsVal; // the observe value
  private double modify; // modify is in [minVal, maxVal]
  private int status; // status = 1, modified; status = 0, not modified
  
  private boolean label;  // true if labeled 

  private double observe; // restore the observe value, used for clear
  private double truth; // restore the truth value, used for clear

  private double minVal;  // modify must be in [minVal, maxVal]
  private double maxVal;

  // for SCREEN
  private double delta1; // delta = (time-kp.time)S1
  private double delta2; // delta2 = (time - kp.time)S2

  public TimePointIMR(long timestamp, double val) {
    setTimestamp(timestamp);
    
    setTruth(val);
    setTruthVal(val);
    
    setObsVal(val);
    setObserve(val);
    setModify(val);
    
    setStatus(0);
    setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
    
    setLabel(false);
  }
  
  public TimePointIMR(long timestamp, double truth, double observe) {
    setTimestamp(timestamp);
    
    setTruth(truth);
    setTruthVal(truth);
    
    setObsVal(observe);
    setObserve(observe);
    setModify(observe);
    
    setStatus(0);
    setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
    
    setLabel(false);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public double getTruthVal() {
    return truthVal;
  }

  public void setTruthVal(double orgval) {
    this.truthVal = orgval;
  }

  public double getObsVal() {
    return obsVal;
  }

  public void setObsVal(double observeval) {
    this.obsVal = observeval;
  }

  public boolean isLabel() {
    return label;
  }

  public void setLabel(boolean label) {
    this.label = label;
  }
  
  public double getObserve() {
    return observe;
  }

  public void setObserve(double value) {
    this.observe = value;
  }

  public double getTruth() {
    return truth;
  }

  public void setTruth(double truth) {
    this.truth = truth;
  }

  public double getModify() {
    return modify;
  }

  public void setModify(double modify) {
    this.modify = modify;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public double getMinVal() {
    return this.minVal;
  }

  public double getMaxVal() {
    return this.maxVal;
  }

  public void setRange(double minVal, double maxVal) {
    this.minVal = minVal;
    this.maxVal = maxVal;
  }

  public double getDelta1() {
    return delta1;
  }

  public void setDelta1(double delta1) {
    this.delta1 = delta1;
  }

  public double getDelta2() {
    return delta2;
  }

  public void setDelta2(double delta2) {
    this.delta2 = delta2;
  }

  public void setDeltas(double delta1, double delta2) {
    this.delta1 = delta1;
    this.delta2 = delta2;
  }
}
