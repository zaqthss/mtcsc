package cn.edu.thu.MTCSC;
import gurobi.*;
import java.util.ArrayList;
import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimeSeries2;


public class TwoMILP {
    private TimeSeries2 timeSeries;
	private ArrayList<TimePoint2> orgList;
    private double S;
    private long T;
	
	public TwoMILP(TimeSeries2 timeSeries, double s, long t) {
		setTimeSeries(timeSeries);
		orgList = this.timeSeries.getTimeseries();
        this.S = s;
        this.T = t;
	}

	public void setTimeSeries(TimeSeries2 timeSeries) {
		this.timeSeries = timeSeries;
	}

    public TimeSeries2 mainGlobal() {
		buildMILP();
        return this.timeSeries;
    }

    private void buildMILP() {
        int len = orgList.size();
        double M = 1000;
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

        int nrows = pairnum;		
		double[] rhs = new double[nrows];	
		char[] sense = new char[nrows];		

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
            model.set(GRB.IntParam.OutputFlag, 0);  
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
            // distance constraint
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
            // Coordinate change constraint
            double[] val = new double[2];
            for (int i = 0; i < len; ++i) {
                val = orgList.get(i).getOrgval();
                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, x[i]);
                qexpr.addTerm(-1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.LESS_EQUAL, val[0], "qc"+tempindex);
                tempindex++;

                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, x[i]);
                qexpr.addTerm(1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.GREATER_EQUAL, val[0], "qc"+tempindex);
                tempindex++;

                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, y[i]);
                qexpr.addTerm(-1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.LESS_EQUAL, val[1], "qc"+tempindex);
                tempindex++;
                qexpr = new GRBQuadExpr();
                qexpr.addTerm(1.0, y[i]);
                qexpr.addTerm(1.0*M, z[i]);
                model.addQConstr(qexpr, GRB.GREATER_EQUAL, val[1], "qc"+tempindex);
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
                yvals[i] = y[i].get(GRB.DoubleAttr.X);
                if(z[i].get(GRB.DoubleAttr.X) != 0){
                    System.out.println("z[" + i + "] is change");
                }
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
            boolean change = true;
            if (xvals[i] - orgval[0] < 0.001 && xvals[i] - orgval[0] > -0.001){
                xvals[i] = orgval[0];
                change = false;
            }   
            if (yvals[i] - orgval[1] < 0.001 && yvals[i] - orgval[1] > -0.001){
                yvals[i] = orgval[1];
                change = false;
            }
            if(change){
                tpi.setModify(xvals[i], yvals[i]);
            }
		}
    }
}
