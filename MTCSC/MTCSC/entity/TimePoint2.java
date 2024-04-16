package cn.edu.thu.MTCSC.entity;

public class TimePoint2 {
	private long timestamp;
	private double x_orgval;		// the original one
    private double y_orgval;		// the original one
    private double x_modify;        
    private double y_modify;
	private double x_truth;
    private double y_truth;
	
	public TimePoint2(long timestamp, double x_value, double y_value, 
    double x_truth, double y_truth) {
		setTimestamp(timestamp);
		setOrgval(x_value, y_value);
		setModify(x_value, y_value);
		setTruth(x_truth, y_truth);
	}
	
	public void setX(double x_value) {
		this.x_orgval = x_value;
		this.x_modify = x_value;
	}

	public void setY(double y_value) {
		this.y_orgval = y_value;
		this.y_modify = y_value;
	}

	public void scaleX(double x_value, double x_true) {
		this.x_orgval = x_value;
		this.x_modify = x_value;
		this.x_truth = x_true;
	}

	public void scaleY(double y_value, double y_true) {
		this.y_orgval = y_value;
		this.y_modify = y_value;
		this.y_truth = y_true;
	}

    public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setOrgval(double x_orgval, double y_orgval) {
		this.x_orgval = x_orgval;
        this.y_orgval = y_orgval;
	}

	public double[] getOrgval() {
		double[] orgval = new double[2];
		
		orgval[0] = this.x_orgval;
		orgval[1] = this.y_orgval;
		
		return orgval;
	}

    public void setModify(double x_modify, double y_modify) {
		this.x_modify = x_modify;
        this.y_modify = y_modify;
	}

	public void setModify(double[] modify) {
		this.x_modify = modify[0];
        this.y_modify = modify[1];
	}

	public double[] getModify() {
		double[] modify = new double[2];
		
		modify[0] = this.x_modify;
		modify[1] = this.y_modify;
		
		return modify;
	}

	public void setTruth(double x_truth, double y_truth) {
		this.x_truth = x_truth;
        this.y_truth = y_truth;
	}

	public double[] getTruth() {
		double[] truth = new double[2];
		
		truth[0] = this.x_truth;
		truth[1] = this.y_truth;
		
        return truth;
	}

}
