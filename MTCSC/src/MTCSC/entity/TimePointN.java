package MTCSC.entity;

import java.util.ArrayList;

public class TimePointN {
	private int Size;
	private long timestamp;
	private ArrayList<Double> Orgval;		// the original one
    private ArrayList<Double> Modify;        
	private ArrayList<Double> Truth;
	private boolean label = false;
	
	public TimePointN(int n, long timestamp, ArrayList<Double> orgval, ArrayList<Double> truth) {
		this.Size = n;
		setTimestamp(timestamp);
		setOrgval(orgval);
		setModify(orgval);
		setTruth(truth);
	}
	
	public void setN(int index, double orgval, double modify, double truth) {
		this.Orgval.set(index, orgval);
        this.Modify.set(index, modify);
		this.Truth.set(index, truth);
	}

	public void setN(int index, double orgval, double modify) {
		this.Orgval.set(index, orgval);
        this.Modify.set(index, modify);
	}

	public void setN(int index, double orgval) {
		this.Orgval.set(index, orgval);
        this.Modify.set(index, orgval);
	}

    public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setOrgval(ArrayList<Double> orgval) {
		this.Orgval = orgval;
	}

	public ArrayList<Double> getOrgval() {
		ArrayList<Double> orgval = this.Orgval;
		return orgval;
	}

    public void setModify(ArrayList<Double> modify) {
		this.Modify = modify;
	}

	public ArrayList<Double> getModify() {
		ArrayList<Double> modify = this.Modify;	
		return modify;
	}

	public void setTruth(ArrayList<Double> truth) {
		this.Truth = truth;
	}

	public ArrayList<Double> getTruth() {
		ArrayList<Double> truth = this.Truth;
        return truth;
	}

	public boolean isLabel() {
		return label;
	}
	
	public void setLabel(boolean label) {
		this.label = label;
	}

}
