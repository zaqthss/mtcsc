package cn.edu.thu.MTCSC;

import gurobi.*;
import java.util.ArrayList;
import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimeSeries2;



public class QCQP {
    private TimeSeries2 timeSeries;
	private ArrayList<TimePoint2> orgList;
    private double S;
    private long T;
	
	public QCQP(TimeSeries2 timeSeries, double s, long t) {
		setTimeSeries(timeSeries);
		orgList = this.timeSeries.getTimeseries();
        this.S = s;
        this.T = t;
	}

	public void setTimeSeries(TimeSeries2 timeSeries) {
		this.timeSeries = timeSeries;
	}

    public TimeSeries2 mainGlobal() {
		buildQCQP();
		
//		boolean valid = Assist.checkConstraint(timeSeries);
//		
//		if(!valid)
//			System.out.println("global error!!!");
		
		// params[0] = Assist.calcAccuracy(timeSeries);
		// params[1] = Assist.calcMNAD(timeSeries);
		// params[2] = Assist.calcRMS2(timeSeries);
		// params[3] = Assist.calcUnchanged(timeSeries);
		// params[4] = Assist.calcConflict(timeSeries);
		// params[5] = Assist.calcCost(timeSeries);
		
		return timeSeries;
	}

    /*
	 * using Gurobi.jar
	*/
	private void buildQCQP() {
        int len = orgList.size();
        double[] xvals = new double[len];
        double[] yvals = new double[len];
		// traverse from first to end
		int pairnum = 0;
		long timestampi, timestampj;
		for (int i = 0; i < len; ++i) {
			timestampi = orgList.get(i).getTimestamp();
			for (int j = i + 1; j < len; ++j) {
				if (orgList.get(j).getTimestamp() - timestampi <= this.T)
					pairnum++;
				else
					break;
			}
		}

		int nrows = pairnum;		// 约束总数
		double[] rhs = new double[nrows];	// 线性约束的右侧值
		char[] sense = new char[nrows];		// 约束条件

		TimePoint2 tpi, tpj;
		int index = 0; // indicates the row index

		for (int i = 0; i < len; ++i) {
			tpi = orgList.get(i);
			timestampi = tpi.getTimestamp();

			for (int j = i + 1; j < len; ++j) {
				tpj = orgList.get(j);
				timestampj = tpj.getTimestamp();

				if (timestampj - timestampi <= this.T) {
					// xj^2-2xixj+xi^2+yj^2-2yiyj+yi^2 <= (Smax * (tj-ti))^2
					rhs[index] = this.S*this.S*(timestampj - timestampi)*(timestampj - timestampi);
					sense[index] = GRB.LESS_EQUAL;
					index++;
				} else {
					break;
				}
			} // end of for j
		} // end of for i

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
            for (int i = 0; i < len; ++i) {
                x[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x"+i);
                y[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y"+i);
            }
            
            double[] val = new double[2];
            // Set objective: min ... notes:default is min
            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < len; ++i) {
                val = orgList.get(i).getOrgval();
                obj.addTerm(1.0, x[i], x[i]);
                obj.addTerm(-(2.0*val[0]), x[i]);
                obj.addTerm(1.0, y[i], y[i]);
                obj.addTerm(-(2.0*val[1]), y[i]);
            }
            model.setObjective(obj, GRB.MINIMIZE);

            // Add constraint
            GRBQuadExpr qexpr = new GRBQuadExpr();
            int tempindex = 0;
            for (int i = 0; i < len; ++i) {
                timestampi = orgList.get(i).getTimestamp();
                for (int j = i + 1; j < len; ++j) {
                    if (orgList.get(j).getTimestamp() - timestampi <= this.T) {
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
                    else
                        break;
                }
            }

            // Optimize model
            model.optimize();
            
            // the result
            for (int i = 0; i < len; ++i) {
				// display the result
                // System.out.println(x[i].get(GRB.StringAttr.VarName)
                // + " " +x[i].get(GRB.DoubleAttr.X));
                // System.out.println(y[i].get(GRB.StringAttr.VarName)
                // + " " +y[i].get(GRB.DoubleAttr.X));
				// save the result
                xvals[i] = x[i].get(GRB.DoubleAttr.X);
                yvals[i] = y[i].get(GRB.DoubleAttr.X);
            }
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // Dispose of model and environment
            model.dispose();
            env.dispose();

		} catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                               e.getMessage());
        }
        
        // modify
        double[] orgval = new double[2];
        for (int i = 0; i < len; ++i) {
			tpi = orgList.get(i);
            orgval = tpi.getOrgval();
            if (xvals[i] - orgval[0] < 0.001 && xvals[i] - orgval[0] > -0.001)
                xvals[i] = orgval[0];
            if (yvals[i] - orgval[1] < 0.001 && yvals[i] - orgval[1] > -0.001)
                yvals[i] = orgval[1];
			tpi.setModify(xvals[i], yvals[i]);
		}
    }

}