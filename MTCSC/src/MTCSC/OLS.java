package MTCSC;

import Jama.Matrix;
import MTCSC.entity.TimePoint;
import MTCSC.entity.TimeSeries;

public class OLS extends BaseAnomaly {

  public OLS(TimeSeries timeSeries, int p, double delta, int maxNum) {
    super(timeSeries);
    tpList = timeSeries.getTimeseries();
    setP(p);
    setDelta(delta);
    setMaxNum(maxNum);
  }

  public void setSeg(int beginIndex, int endIndex, int p, double delta,
      int maxNum) {
    tpList = timeSeries.getSubPoints(beginIndex, endIndex).getTimeseries();
    setP(p);
    setDelta(delta);
    setMaxNum(maxNum);
  }

  public TimeSeries mainOLS(double[] params) {
    int size = tpList.size();
    int rowNum = size - p;
    double totalMove = 0;

    // form z
    double[] zs = new double[size];
    TimePoint tp = null;
    for (int i = 0; i < size; ++i) {
      tp = tpList.get(i);
      zs[i] = tp.isLabel() ? tp.getTruth() - tp.getOrgval() : 0;
    }

    // build x,y for params estimation
    double[][] x = new double[rowNum][p];
    double[][] y = new double[rowNum][1];
    for (int i = 0; i < rowNum; ++i) {
      y[i][0] = zs[p + i];
      for (int j = 0; j < p; ++j) {
        x[i][j] = zs[p + i - j - 1];
      }
    }

    // begin iteration
    int index = -1;
    Matrix xMatrix = new Matrix(x);
    Matrix yMatrix = new Matrix(y);
    int iterationNum = 0;
    double val = 0, preVal = 0;

    // int debugIndex = 478;
    // int debugIter = 728;

    Matrix phi = null;
    while (true) {
      iterationNum++;
      // if (iterationNum == debugIter) {
      // System.out.println("debug at iter" + debugIter);
      // }

      phi = learnParamsOLS(xMatrix, yMatrix);

      if (storePhi) {
        savePhi(phi);
      }

      Matrix yhatMatrix = combine(phi, xMatrix);

      index = repairAMin(yhatMatrix, yMatrix);
      // if (index + p == debugIndex) {
      // System.out.println("debug at " + debugIndex);
      // }
      if (index == -1) {
        break;
      }

      preVal = yMatrix.get(index, 0);
      val = yhatMatrix.get(index, 0);
      totalMove += Math.abs(preVal - val);
      // update y
      yMatrix.set(index, 0, val);
      // update x
      for (int j = 0; j < p; ++j) {
        int i = index + 1 + j; // p+i-j-1 \Leftrightarrow p+i = index+p
        if (i >= rowNum)
          break;
        if (i < 0)
          continue;

        xMatrix.set(i, j, val);
      }

      if (storePhi) {
        saveY(xMatrix, yMatrix);
      }

      if (iterationNum > maxNum)
        break;
    }

    // System.out.println("Stop after " + iterationNum + " iterations");

    double modify = 0;
    for (int i = 0; i < rowNum; ++i) {
      tp = tpList.get(p + i);
      if (tp.isLabel())
        // modify = tp.getTruth();
        modify = tp.getOrgval();
      else
        modify = tp.getOrgval() + yMatrix.get(i, 0);

      tp.setModify(modify);
    }

    return timeSeries;
  }
}
