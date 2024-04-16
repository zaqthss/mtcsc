package cn.edu.thu.MTCSC;
import gurobi.*;
import java.util.ArrayList;
import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;

// one dimension MILP
public class OneMILP {
    private TimeSeries timeSeries;
	private ArrayList<TimePoint> orgList;
    private double SMAX;  // maximum speed
  	private double SMIN;  // minimum speed
    private long T;
	
	public OneMILP(TimeSeries timeSeries, double sMax, double sMin, long t) {
		setTimeSeries(timeSeries);
		orgList = this.timeSeries.getTimeseries();
        this.SMAX = sMax;
		this.SMIN = sMin;
        this.T = t;
	}

	public void setTimeSeries(TimeSeries timeSeries) {
		this.timeSeries = timeSeries;
	}

    public TimeSeries mainGlobal() {
		buildMILP();
        return this.timeSeries;
    }

    private void buildMILP() {
        int len = orgList.size();
        double M = 1000;
        double[] xvals = new double[len];
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

        int nrows = pairnum * 2;		
		double[] rhs = new double[nrows];	
		char[] sense = new char[nrows];		

		TimePoint tpi, tpj;
        double vali;
		int index = 0; // indicates the row index

		for (int i = 0; i < len; ++i) {
			tpi = orgList.get(i);
			timestampi = tpi.getTimestamp();
			vali = tpi.getOrgval();

			for (int j = i + 1; j < len; ++j) {
				tpj = orgList.get(j);
				timestampj = tpj.getTimestamp();
				if (timestampj - timestampi <= T) {
					// xj-xi <= Smax * (tj-ti)
					rhs[index] = this.SMAX * (timestampj - timestampi);
					sense[index] = GRB.LESS_EQUAL;
					index++;

					// xj-xi >= Smin * (tj-ti)
					rhs[index] = this.SMIN * (timestampj - timestampi);
					sense[index] = GRB.GREATER_EQUAL;
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
            model.set(GRB.IntParam.OutputFlag, 0);  
            // model.set(GRB.IntParam.NonConvex, 2);

            // Create variables 
            GRBVar[] x = new GRBVar[len];
            GRBVar[] z = new GRBVar[len];
            for (int i = 0; i < len; ++i) {
                x[i] = model.addVar(-GRB.INFINITY, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x"+i);
                z[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z"+i);
            }
            
            // Set objective: min ... notes:default is min
            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < len; ++i) {
                obj.addTerm(1.0, z[i]);
            }
            model.setObjective(obj, GRB.MINIMIZE);

            // Add constraint
            GRBLinExpr expr = new GRBLinExpr();
            int tempindex = 0;
            // distance constraint
            for (int i = 0; i < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    // xj-xi <= Smax * (tj-ti) 
                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, x[j]);   // xj
                    expr.addTerm(-1.0, x[i]);   // -xi
                    model.addConstr(expr, sense[tempindex], rhs[tempindex], "c"+tempindex);
                    tempindex++;
                    // xj-xi >= Smin * (tj-ti)
                    model.addConstr(expr, sense[tempindex], rhs[tempindex], "c"+tempindex);
                    tempindex++;
                }
            }
            // constraint
            for (int i = 0; i < len; ++i) {
                vali = orgList.get(i).getOrgval();
                expr = new GRBLinExpr();
                expr.addTerm(1.0, x[i]);
                expr.addTerm(-1.0*M, z[i]);
                model.addConstr(expr, GRB.LESS_EQUAL, vali, "qc"+tempindex);
                tempindex++;

                expr = new GRBLinExpr();
                expr.addTerm(1.0, x[i]);
                expr.addTerm(1.0*M, z[i]);
                model.addConstr(expr, GRB.GREATER_EQUAL, vali, "qc"+tempindex);
                tempindex++;
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
        double orgval = 0.0;
        for (int i = 0; i < len; ++i) {
			tpi = orgList.get(i);
            orgval = tpi.getOrgval();
            if (xvals[i] - orgval < 0.001 && xvals[i] - orgval > -0.001)
                xvals[i] = orgval;
			tpi.setModify(xvals[i]);
		}
    }
}
