package cn.edu.thu.MTCSC.entity;


public class CGGeometryLib {
    /**
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double pow(double d1, double d2) {
        return Math.pow(d1, d2);
    }

    public static double sqrt(double d) {
        return Math.sqrt(d);
    }

    public static double sin(double theta) {
        return Math.sin(theta);
    }

    public static double cos(double theta) {
        return Math.cos(theta);
    }

    /**
     *
     * @param p1     
     * @param p2     
     * @param coc    
     * @param radius 
     * @return 
     */
    public double[][] getLineCircleNode(TimePoint2 ptp1, TimePoint2 kp, double s) {
        double[] xy1 = ptp1.getOrgval();
        double[] xy2 = kp.getOrgval();
        long t1 = ptp1.getTimestamp();
        long t2 = kp.getTimestamp();

        CGPoint p1 = new CGPoint(xy1[0],xy1[1]);
        CGPoint p2 = new CGPoint(xy2[0],xy2[1]);
        CGPoint coc = new CGPoint(xy1[0],xy1[1]);
        double radius = Math.abs((t2 - t1)) * s;

        CGPoint[] target = new CGPoint[2];
        CGLine l1 = new CGLine(p1, p2);
        if (l1.iskExists()) {
            if (l1.k != 0) {
                // After mathematical operations, obtain the expression of a system of binary linear equations
                double A = pow(l1.k, 2) + 1;
                double B = 2 * (l1.k * l1.b - l1.k * coc.y - coc.x);
                double C = pow(coc.x, 2) + pow((l1.b - coc.y), 2) - pow(radius, 2);
                double delta = pow(B, 2) - 4 * A * C;

                if (delta < 0) {    
                    // After practical testing, there is a certain probability of entering this branch, and special treatment must be carried out
                    target[0] = new CGPoint(coc.x, coc.y - radius);
                    target[1] = new CGPoint(coc.x, coc.y + radius);
                } else {
                    double x1 = (-B + sqrt(delta)) / (2 * A);
                    double y1 = l1.k * x1 + l1.b;
                    target[0] = new CGPoint(x1, y1);

                    double x2 = (-B - sqrt(delta)) / (2 * A);
                    double y2 = l1.k * x2 + l1.b;
                    target[1] = new CGPoint(x2, y2);
                }
            } else {
                target[0] = new CGPoint(coc.x - radius, coc.y);
                target[1] = new CGPoint(coc.x + radius, coc.y);
            }
        } else {
            target[0] = new CGPoint(coc.x, coc.y - radius);
            target[1] = new CGPoint(coc.x, coc.y + radius);
        }
        double[] temp = new double[2];
        double[][] xy = new double[2][2];
        temp = target[0].getXY();
        xy[0][0] = temp[0];
        xy[0][1] = temp[1];
        temp = target[1].getXY();
        xy[1][0] = temp[0];
        xy[1][1] = temp[1];
        return xy;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // CGPoint p1 = new CGPoint(28.61,74.43);
        // CGPoint p2 = new CGPoint(27.7,74.42);
        // CGPoint coc = new CGPoint(28.61,74.43);
        // double[] whatIWanted = getLineCircleNode(p1, p2, coc,0.5001);
        // for (int i = 0; i < whatIWanted.length; i++) {
        //     System.out.println(whatIWanted[i]);
        // }
    }
}

