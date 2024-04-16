package cn.edu.thu.MTCSC;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Jama.Matrix;
import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.util.Constants;

public class BaseAnomaly {
  protected TimeSeries timeSeries;
  protected ArrayList<TimePoint> tpList;  // how to fill the value
  protected int p;  // AR(p) model
  protected double delta; // converge
  protected int maxNum; // max iteration number
  
  protected ArrayList<String> phiList;
  protected ArrayList<String> strList;
  protected boolean storePhi = false; // if true, then store all the phi
  
  public BaseAnomaly(TimeSeries timeSeries) {
    setTimeSeries(timeSeries);
    phiList = new ArrayList<>();
    strList = new ArrayList<>();
  }
  
  public void setTimeSeries(TimeSeries timeSeries) {
    this.timeSeries = timeSeries;
  }
  
  public TimeSeries getTimeSeries() {
    return timeSeries;
  }
  
  public void setP(int p) {
    this.p = p;
  }
  
  public void setDelta(double delta) {
    this.delta = delta;
  }
  
  public void setMaxNum(int maxNum) {
    this.maxNum = maxNum;
  }
  
  public void setStorePhi(boolean isStorePhi) {
    this.storePhi = isStorePhi;
  }
  
  /**
   * learn the parameter
   * can use incremental computation for x and y
   */
  protected Matrix learnParamsOLS(Matrix xMatrix, Matrix yMatrix) {
    Matrix phi = new Matrix(p, 1);

    Matrix xMatrixT = xMatrix.transpose();
    
    Matrix middleMatrix = xMatrixT.times(xMatrix);
    phi = middleMatrix.inverse().times(xMatrixT).times(yMatrix);
    
    return phi;
  }
  
  protected Matrix learnParamsIC(Matrix A, Matrix B) {
    Matrix phi = new Matrix(p, 1);
    
    phi = A.inverse().times(B);
    
    return phi;
  }
  
  /**
   * use phi to combine
   */
  protected Matrix combine(Matrix phi, Matrix xMatrix) {
    Matrix yhatMatrix = xMatrix.times(phi);
    
    return yhatMatrix;
  }
  
  /**
   * Absolute minimum
   */
  protected int repairAMin(Matrix yhatMatrix, Matrix yMatrix) {
    int rowNum = yhatMatrix.getRowDimension();
    Matrix residualMatrix = yhatMatrix.minus(yMatrix);
    
    double aMin = Constants.MINVAL;
    int targetIndex = -1;
    double yhat, yhatabs;
    
    for (int i = 0; i < rowNum; ++i) {
      if (tpList.get(i + p).isLabel()) {
        continue;
      }
      if (Math.abs(residualMatrix.get(i, 0)) < delta) {
        continue;
      }
      
      yhat = yhatMatrix.get(i, 0);
      yhatabs = Math.abs(yhat);
      
//      if (yhatabs < aMin && yhatabs > 0) {
      if (yhatabs < aMin) { // no need to > 0
        aMin = yhatabs;
        targetIndex = i; 
      }
    }
    
    return targetIndex;
  }
  
  protected void savePhi(Matrix phi) {
 // only save the last one
    phiList.clear();
    StringBuilder phisb = new StringBuilder();
    
    for (int pi = 0; pi < p; ++pi) {
      phisb.append(phi.get(pi, 0));
      phisb.append(",");
    }
    phisb.deleteCharAt(phisb.length() - 1);
    phiList.add(phisb.toString());
  }
  
  protected void saveY(Matrix xMatrix, Matrix yMatrix) {
    // only save the last one
    strList.clear();
    StringBuilder sb = new StringBuilder();
    int rowNum = yMatrix.getRowDimension();
    
    for (int i = p - 1; i > -1; --i) {
      sb.append(xMatrix.get(0, i));
      sb.append(",");
    }
    for (int i = 0; i < rowNum; ++i) {
      sb.append(yMatrix.get(i, 0));
      sb.append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    strList.add(sb.toString());
  }
  
  public void writePhitoFile(String outPath) {
    
    try {
      FileWriter fw = new FileWriter(outPath);
      PrintWriter pw = new PrintWriter(fw);
      
      for (String phi : phiList) {
        pw.println(phi);
//        System.out.println(phi);
      }
      
      pw.close();
      fw.close();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void writeYtoFile(String outPath) {
    
    try {
      FileWriter fw = new FileWriter(outPath);
      PrintWriter pw = new PrintWriter(fw);
      
      for (String y : strList) {
        pw.println(y);
//        System.out.println(phi);
      }
      
      pw.close();
      fw.close();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
