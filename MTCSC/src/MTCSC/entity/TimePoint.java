package MTCSC.entity;

import MTCSC.util.Constants.POS;

public class TimePoint {
	private long timestamp;
	private double orgval;		// the original one
	private double truth;
	private double value;
	private double delta1;		// delta = (time-kp.time)S1
	private double delta2;		// delta2 = (time - kp.time)S2
	private double modify;		// modify is in [upperbound, lowerbound]
	private double upperbound;	// candidate bound
	private double lowerbound;
	private POS pos;			// XL/XR/XM
	private int status;			// status = 1, modified; status = 0, not modified
	private boolean label;  // true if labeled 
	private double minVal;  // modify must be in [minVal, maxVal]
	private double maxVal;
	
	public TimePoint(long timestamp, double value, double truth) {
		setTimestamp(timestamp);
		setOrgval(value);
		setValue(value);
		setModify(value);
		setTruth(truth);
		this.upperbound = Double.MAX_VALUE;
		this.lowerbound = -this.upperbound;
		setLabel(false);
		setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
		setStatus(0);
	}

	public TimePoint(long timestamp, double value) {
		setTimestamp(timestamp);
		setOrgval(value);
		setValue(value);
		setModify(value);
		setTruth(value);
		this.upperbound = Double.MAX_VALUE;
		this.lowerbound = -this.upperbound;
		setLabel(false);
		setRange(-Double.MAX_VALUE, Double.MAX_VALUE);
		setStatus(0);
	}
	
	public void setRange(double minVal, double maxVal) {
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	public boolean isLabel() {
		return label;
	}
	
	public void setLabel(boolean label) {
		this.label = label;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getOrgval() {
		return orgval;
	}

	public void setTruth(double truth) {
		this.truth = truth;
	}

	public double getTruth() {
		return truth;
	}

	public void setOrgval(double orgval) {
		this.orgval = orgval;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getDelta1() {
		return delta1;
	}

	public void setDelta1(double delta) {
		this.delta1 = delta;
	}
	
	public double getDelta2() {
		return delta2;
	}

	public void setDelta2(double delta) {
		this.delta2 = delta;
	}
	
	public double[] getDelta() {
		double[] deltas = new double[2];
		
		deltas[0] = delta1;
		deltas[1] = delta2;
		
		return deltas;
	}
	
	public void setDeltas(double[] deltas) {
		this.delta1 = deltas[0];
		this.delta2 = deltas[1];
	}
	
	public void setDeltas(double delta1, double delta2) {
		this.delta1 = delta1;
		this.delta2 = delta2;
	}

	public double getModify() {
		return modify;
	}

	public void setModify(double modify) {
		this.modify = modify;
	}

	public double getUpperbound() {
		return upperbound;
	}

	public void setUpperbound(double upperbound) {
		this.upperbound = upperbound;
	}

	public double getLowerbound() {
		return lowerbound;
	}

	public void setLowerbound(double lowerbound) {
		this.lowerbound = lowerbound;
	}

	public POS getPos() {
		return pos;
	}

	public void setPos(POS pos) {
		this.pos = pos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
