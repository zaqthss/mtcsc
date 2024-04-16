package cn.edu.thu.MTCSC.entity;


public class CGLine {
    private boolean kExists;
    public double k = 77885.201314f;
    public double b = 13145.207788f;
    public double extraX = 52077.881314f;


    /**
     *
     * @param k
     * @param b
     */
    public CGLine(double k, double b) {
        this.kExists = true;
        this.k = k;
        this.b = b;
    }

    /**
     *
     * @param p1
     * @param p2
     */
    public CGLine(CGPoint p1, CGPoint p2) {
        if ((p1.x - p2.x) != 0) {
            // System.out.println("y = k*x + b, k exits!!");
            this.kExists = true;
            this.k = (p1.y - p2.y) / (p1.x - p2.x);
            this.b = (p1.y - p1.x * k);
        } else {
            // System.out.println("y = k*x + b, k doesn't exists!!");
            this.kExists = false;
            this.extraX = p1.x;
        }
    //    System.out.print("p1(" + p1.x + ", " + p1.y + "), p2(" + p2.x + ", " + p2.y + ")expression for the linear equation is: ");
    //     if (kExists) {
    //         System.out.println("y = " + k + "*x + " + b);
    //     } else {
    //        System.out.println("x = " + extraX + "(Perpendicular to x-axis!)");
    //     }
    }

    /**
     *
     * @param p 
     * @param k 
     */
    public CGLine(double k, CGPoint p) {
        /**
         * (y-y') = k*(x-x')
         * y = k*x + y' - k*x'
         * k = k, b = y'-k*x'
         */
        this.kExists = true;
        this.k = k;
        this.b = p.y - k * p.x;
    }

    /**
     *
     * @param extraX
     */
    public CGLine(double extraX) {
        this.kExists = false;
        this.extraX = extraX;
    }

    @Override
    public String toString() {
        return "Line.toString()called, y = k*x + b, k=" + this.k +
                ", b=" + this.b +
                ", kExists=" + this.kExists +
                ", extraX=" + this.extraX;
    }

    public boolean iskExists() {
        return kExists;
    }

    public void setkExists(boolean kExists) {
        this.kExists = kExists;
    }
}
