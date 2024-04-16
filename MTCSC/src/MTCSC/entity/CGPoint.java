package MTCSC.entity;

public class CGPoint {
    public double x;
    public double y;

    public CGPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double[] getXY(){
        double[] xy = new double[2];
		
		xy[0] = this.x;
		xy[1] = this.y;
		
		return xy;
    }
}
