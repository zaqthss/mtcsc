package cn.edu.thu.MTCSC.test;
import gurobi.*;

public class MILPTest {
    public static void main(String[] args) {
        int len = 8;
        double M = 1000;
        double[] xx = new double[]{0,10,20,3,4,5,6,7};
        double[] yy = new double[]{0,0,0,0,0,0,0,0};
        double[] tt = new double[]{0,1,2,3,4,5,6,7};
        double s = 1;
        int pairnum = 0;
		for (int i = 0; i < len; ++i) {
			for (int j = i + 1; j < len; ++j) {
					pairnum++;
			}
		}

        int nrows = pairnum;		// 约束总数
		double[] rhs = new double[nrows];	// 线性约束的右侧值
		char[] sense = new char[nrows];		// 约束条件
        int index = 0; // indicates the row index
        double timestampi, timestampj;
        for (int i = 0; i < len; ++i) {
			timestampi = tt[i];
			for (int j = i + 1; j < len; ++j) {
				timestampj = tt[j];
                // xj^2-2xixj+xi^2+yj^2-2yiyj+yi^2 <= (Smax * (tj-ti))^2
                rhs[index] = s*s*(timestampj - timestampi)*(timestampj - timestampi);
                sense[index] = GRB.LESS_EQUAL;
                index++;
			} // end of for j
		} // end of for i

        // double[] xvals = new double[len];
        // double[] yvals = new double[len];
        try {
            // use Gurobi
            // Create empty environment, set options, and start
            GRBEnv env = new GRBEnv(true);
            env.start();

            // Create empty model
            GRBModel model = new GRBModel(env);
            model.set(GRB.IntParam.OutputFlag, 0);  //关闭log输出
            // model.Params.NonConvex = 2,解决非凸问题。会按照求解MIP的逻辑来求解此类非凸问题
            // model.set(GRB.IntParam.NonConvex, 2);

            // Create variables 
            GRBVar[] x = new GRBVar[len];
            GRBVar[] y = new GRBVar[len];
            GRBVar[] z = new GRBVar[len];
            for (int i = 0; i < len; ++i) {
                x[i] = model.addVar(-GRB.INFINITY, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x"+i);
                y[i] = model.addVar(-GRB.INFINITY, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y"+i);
                z[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z"+i);
            }
            
            // Set objective: min ... notes:default is min
            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < len; ++i) {
                obj.addTerm(1.0, z[i]);
            }
            model.setObjective(obj, GRB.MINIMIZE);

            // Add constraint
            GRBQuadExpr qexpr = new GRBQuadExpr();
            int tempindex = 0;
            // 距离约束
            for (int i = 0; i < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    // xj^2-2xixj+xi^2+yj^2-2yiyj+yi^2 <= (Smax * (tj-ti))^2
                    qexpr = new GRBQuadExpr();
                    qexpr.addTerm(1.0, x[j], x[j]);   // xj^2
                    qexpr.addTerm(-2.0, x[i], x[j]);   // -2xixj
                    qexpr.addTerm(1.0, x[i], x[i]);   // xi^2
                    qexpr.addTerm(1.0, y[j], y[j]);   // yj^2
                    qexpr.addTerm(-2.0, y[i], y[j]);   // -2yiyj
                    qexpr.addTerm(1.0, y[i], y[i]);   // yi^2
                    model.addQConstr(qexpr, sense[tempindex], rhs[tempindex], "qc"+tempindex);
                    tempindex++;
                }
            }
            // 坐标变化约束
            for (int i = 0; i < len; ++i) {
                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, x[i]);
                qexpr.addTerm(-1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.LESS_EQUAL, xx[i], "qc"+tempindex);
                tempindex++;

                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, x[i]);
                qexpr.addTerm(1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.GREATER_EQUAL, xx[i], "qc"+tempindex);
                tempindex++;

                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, y[i]);
                qexpr.addTerm(-1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.LESS_EQUAL, yy[i], "qc"+tempindex);
                tempindex++;
                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, y[i]);
                qexpr.addTerm(1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.GREATER_EQUAL, yy[i], "qc"+tempindex);
                tempindex++;
            }

            // Optimize model
            model.optimize();
            
            // the result
            for (int i = 0; i < len; ++i) {
				// display the result
                System.out.println(x[i].get(GRB.StringAttr.VarName)
                + " " +x[i].get(GRB.DoubleAttr.X));
                System.out.println(y[i].get(GRB.StringAttr.VarName)
                + " " +y[i].get(GRB.DoubleAttr.X));
				// save the result
                // xvals[i] = x[i].get(GRB.DoubleAttr.X);
                // yvals[i] = y[i].get(GRB.DoubleAttr.X);
            }
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // Dispose of model and environment
            model.dispose();
            env.dispose();

		} catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                               e.getMessage());
          }

    }
}
