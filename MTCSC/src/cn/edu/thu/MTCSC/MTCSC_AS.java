package cn.edu.thu.MTCSC;

import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import java.util.ArrayList;
import java.util.Collections;


public class MTCSC_AS {

  private TimeSeries2 timeseries;
  private TimePoint2 kp;
  private int T;       // the window size
  private double SMAX;  // maximum speed
  // private ArrayList<Double> SpeedList = new ArrayList<Double>(); // speed windows
  // private ArrayList<Double> SortedSpeedList = new ArrayList<Double>(); // speed windows
  private ArrayList<Double> preSpeedList = new ArrayList<Double>();
  private ArrayList<Double> nowSpeedList = new ArrayList<Double>();
  private ArrayList<Integer> preSpeedDistribution = new ArrayList<Integer>();
  private ArrayList<Integer> nowSpeedDistribution = new ArrayList<Integer>();
  // private int Index = 95; // speed rate
  private double Drate = 0.025;
  private double Beta = 0.95;
  private double Threshold = 1.0;
  private int SWSize = 200;
  private int Bucket = 5;

  /**
   *
   * @param timeseries timeseries
   * @param sMax maximum allowed speed
   * @param t the window size
   */
  public MTCSC_AS(TimeSeries2 timeseries, double sMax, int t, double drate, double threshold, int swsize, double beta, int bucket) {
    setTimeSeries(timeseries);
    setSMAX(sMax);
    setT(t);
    // setIndex(drate);
    setDrate(drate);
    setBeta(beta);
    setThreshold(threshold);
    setSWSize(swsize);
    setBucket(bucket);
    setSpeedList();
  }

  public void setTimeSeries(TimeSeries2 timeSeries) {
    this.timeseries = timeSeries;
  }

  public void setT(int t) {
    this.T = t;
  }

  public void setSMAX(double SMAX) {
    this.SMAX = SMAX;
  }

  public void setDrate(double drate) {
    this.Drate = drate;
  }

  public void setBeta(double beta) {
    this.Beta = beta;
  }

  public void setBucket(int bucket) {
    this.Bucket = bucket;
  }

  public void setThreshold(double threshold) {
    this.Threshold = threshold;
  }

  public void setSWSize(int swsize) {
    this.SWSize = swsize;
  }

  public void setSpeedList() {
    for(int i=0; i<(this.Bucket+1); i++){
      this.preSpeedDistribution.add(0);
      this.nowSpeedDistribution.add(0);
    }
  }

  // Update speed based on KL divergence
  public void addSpeed(TimePoint2 prePoint, TimePoint2 kpPoint){
    double[] observe1 = prePoint.getOrgval();
		double[] observe2 = kpPoint.getOrgval();
    double tempSpeed = Math.sqrt(Math.pow(observe1[0]-observe2[0], 2) + Math.pow(observe1[1]-observe2[1], 2)) / (kpPoint.getTimestamp()-prePoint.getTimestamp());
		int distribution = 0;
    // add point to preSpeedList and update the preSpeedDistribution
    if(this.preSpeedList.size() < this.SWSize){
      this.preSpeedList.add(tempSpeed);
      distribution = (int) Math.ceil(tempSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.preSpeedDistribution.get(distribution-1) + 1;
        this.preSpeedDistribution.set(distribution-1, temp);
      }
      else if(distribution == 0){
        int temp = this.preSpeedDistribution.get(0) + 1;
        this.preSpeedDistribution.set(0, temp);
      }
      else{
        int temp = this.preSpeedDistribution.get(this.Bucket) + 1;
        this.preSpeedDistribution.set(this.Bucket, temp);
      }
    }
    // add point to nowSpeedList and update the nowSpeedDistribution
    else if(this.preSpeedList.size() == this.SWSize && this.nowSpeedList.size() < this.SWSize){
      this.nowSpeedList.add(tempSpeed);
      distribution = (int) Math.ceil(tempSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.nowSpeedDistribution.get(distribution-1) + 1;
        this.nowSpeedDistribution.set(distribution-1, temp);
      }
      else if(distribution == 0){
        int temp = this.nowSpeedDistribution.get(0) + 1;
        this.nowSpeedDistribution.set(0, temp);
      }
      else{
        int temp = this.nowSpeedDistribution.get(this.Bucket) + 1;
        this.nowSpeedDistribution.set(this.Bucket, temp);
      }
    }
    else if(this.preSpeedList.size() == this.SWSize && this.nowSpeedList.size() == this.SWSize){
      // calculate the KL between preSpeedDistribution and nowSpeedDistribution
      double klDiv = KL();
      // remove the first point of the preSpeedList
      double firstSpeed = this.preSpeedList.get(0);
      distribution = (int) Math.ceil(firstSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.preSpeedDistribution.get(distribution-1) - 1;
        this.preSpeedDistribution.set(distribution-1, temp);
      }
      else if(distribution == 0){
        int temp = this.preSpeedDistribution.get(0) - 1;
        this.preSpeedDistribution.set(0, temp);
      }
      else{
        int temp = this.preSpeedDistribution.get(this.Bucket) - 1;
        this.preSpeedDistribution.set(this.Bucket, temp);
      }
      this.preSpeedList.remove(0);
      firstSpeed = this.nowSpeedList.get(0);
      this.preSpeedList.add(firstSpeed);
      distribution = (int) Math.ceil(firstSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        // add a point to the preSpeedList
        int temp = this.preSpeedDistribution.get(distribution-1) + 1;
        this.preSpeedDistribution.set(distribution-1, temp);
        // remove the first point of the nowSpeedList
        temp = this.nowSpeedDistribution.get(distribution-1) - 1;
        this.nowSpeedDistribution.set(distribution-1, temp);
      }
      else if(distribution == 0){
        int temp = this.preSpeedDistribution.get(0) + 1;
        this.preSpeedDistribution.set(0, temp);
        // remove the first point of the nowSpeedList
        temp = this.nowSpeedDistribution.get(0) - 1;
        this.nowSpeedDistribution.set(0, temp);
      }
      else{
        int temp = this.preSpeedDistribution.get(this.Bucket) + 1;
        this.preSpeedDistribution.set(this.Bucket, temp);
        temp = this.nowSpeedDistribution.get(this.Bucket) - 1;
        this.nowSpeedDistribution.set(this.Bucket, temp);
      }
      this.nowSpeedList.remove(0);
      // add a point to the nowSpeedList
      this.nowSpeedList.add(tempSpeed);
      distribution = (int) Math.ceil(tempSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.nowSpeedDistribution.get(distribution-1) + 1;
        this.nowSpeedDistribution.set(distribution-1, temp);
      }
      else if(distribution == 0){
        int temp = this.nowSpeedDistribution.get(0) + 1;
        this.nowSpeedDistribution.set(0, temp);
      }
      else{
        int temp = this.nowSpeedDistribution.get(this.Bucket) + 1;
        this.nowSpeedDistribution.set(this.Bucket, temp);
      }
      // beyond the threshold so update the speed
      if(klDiv > this.Threshold){
        updateSpeed();
        upSpeedDistribution();
      }
      // System.out.println("KL is " + klDiv);
    }
    else{
      System.out.println("error!!");
    }
  }

  public double KL(){
    // calculate frequency of preSpeedDistribution
    ArrayList<Double> preFrequency = new ArrayList<>();
    for (int count : preSpeedDistribution) {
      double frequency = (double) count / (double)this.SWSize; // 计算频率
      preFrequency.add(frequency); // 将频率添加到新的ArrayList中
    }

    // calculate frequency of nowSpeedDistribution
    ArrayList<Double> nowFrequency = new ArrayList<>();
    for (int count : nowSpeedDistribution) {
      double frequency = (double) count / (double)this.SWSize; // 计算频率
      nowFrequency.add(frequency); // 将频率添加到新的ArrayList中
    }

    // calculate the KL between preSpeedDistribution and nowSpeedDistribution
    double klDiv = 0.0;
    double epsilon = 1/ (double)this.SWSize; // 平滑处理中的小正数
    for(int i=0; i<(this.Bucket+1); i++){
      double pVal = preFrequency.get(i);
      double qVal = nowFrequency.get(i);
      if(pVal > 0){
        if(qVal != 0){
          klDiv += pVal * Math.log(pVal / qVal);
        }
        else{
          klDiv += pVal * Math.log(pVal / epsilon);
        }
      }
    }
    return klDiv;
  }

  public void updateSpeed(){
    ArrayList<Double> tempSpeedList = new ArrayList<>(this.nowSpeedList);
    Collections.sort(tempSpeedList);
    // double rate = 1-2*this.Drate-0.05;
    double rate = 1-2*this.Drate;
    int len = tempSpeedList.size();
    int index = (int) (len * rate);
    // double s = tempSpeedList.get(index-1) *1.3;
    double s = tempSpeedList.get(index-1);
    s = s / this.Beta;
    this.SMAX = s;
  }

  public void upSpeedDistribution(){
    for(int i=0; i<(this.Bucket+1); i++){
      this.preSpeedDistribution.set(i, 0);
      this.nowSpeedDistribution.set(i, 0);
    }
    for(int i=0; i<this.preSpeedList.size(); i++){
      // update the preSpeedDistribution
      double tempSpeed = this.preSpeedList.get(i);
      int distribution = (int) Math.ceil(tempSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.preSpeedDistribution.get(distribution-1) + 1;
        this.preSpeedDistribution.set(distribution-1, temp);
      }
      else{
        int temp = this.preSpeedDistribution.get(this.Bucket) + 1;
        this.preSpeedDistribution.set(this.Bucket, temp);
      }
      // update the nowSpeedDistribution
      tempSpeed = this.nowSpeedList.get(i);
      distribution = (int) Math.ceil(tempSpeed / this.SMAX * this.Bucket);
      if(0 < distribution && distribution <= this.Bucket){
        int temp = this.nowSpeedDistribution.get(distribution-1) + 1;
        this.nowSpeedDistribution.set(distribution-1, temp);
      }
      else{
        int temp = this.nowSpeedDistribution.get(this.Bucket) + 1;
        this.nowSpeedDistribution.set(this.Bucket, temp);
      }
    }
  }

  // Update speed based on Bernoulli experiment
  // public void setIndex(double drate) {
  //   BinomialDistribution binomialDistribution = new BinomialDistribution(100, drate*2);
  //   for(int i=1; i<100; i++){
  //     double prob = binomialDistribution.cumulativeProbability(i-1) * 100;
  //     if(prob >= 100){
  //       this.Index = 100 - i;
  //       break;
  //     }
  //   }
  // }

  // public void addSpeed(TimePoint2 prePoint, TimePoint2 kpPoint){
  //   double[] observe1 = prePoint.getOrgval();
	// 	double[] observe2 = kpPoint.getOrgval();
  //   double tempSpeed = Math.sqrt((observe1[0]-observe2[0])*(observe1[0]-observe2[0]) + (observe1[1]-observe2[1])*(observe1[1]-observe2[1])) / (kpPoint.getTimestamp()-prePoint.getTimestamp());
	// 	this.SpeedList.add(tempSpeed);
  //   this.SortedSpeedList.add(tempSpeed);
  //   Collections.sort(SortedSpeedList);
  //   if(this.SpeedList.size() >= 100){
  //     if(this.SpeedList.size() > 100){
  //       double tmp = SpeedList.get(0);
  //       SpeedList.remove(0);
  //       SortedSpeedList.remove(SortedSpeedList.indexOf(tmp));
  //     }
  //     updateSpeed();
  //   }
  // }

  // public void updateSpeed(){
  //   Collections.sort(SortedSpeedList);
  //   double s = SortedSpeedList.get(this.Index-1);
  //   double p =Math.abs(s-this.SMAX) / this.SMAX;
  //   if(p>1){
  //     // int len = (int)((100-this.Index) *0.95 + this.Index-1);
  //     // this.SMAX = SortedSpeedList.get(len-1);
  //     this.SMAX = s * 1.2;
  //   }
  // }

  /**
   *
   * @return timeseries after repair
   */
  public TimeSeries2 mainScreen() {
    ArrayList<TimePoint2> totalList = timeseries.getTimeseries();
    int size = totalList.size();

    long preEnd = -1, curEnd;
    // the startTime in the window, the real end time in the window, the maximum allowed
    long wStartTime, wEndTime, wGoalTime;
    long curTime=0;
    TimePoint2 prePoint = null;    // the last fixed point
    TimePoint2 tp;

    TimeSeries2 tempSeries = new TimeSeries2();
    ArrayList<TimePoint2> tempList;

    int readIndex = 1; // the point should be read in

    // initial
    tp = totalList.get(0);
    tempSeries.addPoint(tp);
    wStartTime = tp.getTimestamp();
    wEndTime = wStartTime;
    wGoalTime = wStartTime + T;

    while (readIndex < size) {
      tp = totalList.get(readIndex);
      curTime = tp.getTimestamp();

      // This point shouldn't be added until the repair is over
      if (curTime > wGoalTime) {
        while (true) {
          tempList = tempSeries.getTimeseries();
          if (tempList.size() == 0) {
            // if all the points in tempList has been handled
            tempSeries.addPoint(tp);  // the current point should be a new start
            // prePoint = tp;
            wGoalTime = curTime + T;
            wEndTime = curTime;
            break;
          }

          kp = tempList.get(0);
          wStartTime = kp.getTimestamp();
          wGoalTime = wStartTime + T;

          if (curTime <= wGoalTime) {
            // then should read in new points
            TimePoint2 tmpPoint = tempSeries.getTimeseries().get(tempSeries.getTimeseries().size()-1);
            addSpeed(tmpPoint, tp);
            tempSeries.addPoint(tp);
            wEndTime = curTime;
            break;
          }

          curEnd = wEndTime;

          if (preEnd == -1) {
            prePoint = kp;

          }
          local(tempSeries, prePoint);
          prePoint = kp;
          preEnd = curEnd;

          // remove the keyPoint
          tempSeries.getTimeseries().remove(0);
        } // end of while(true)
      } else {
        if (curTime > wEndTime) {
          // suppose the sequence is in order, so it must happen
          TimePoint2 tmpPoint = tempSeries.getTimeseries().get(tempSeries.getTimeseries().size()-1);
          addSpeed(tmpPoint, tp);
          tempSeries.addPoint(tp);
          wEndTime = curTime;
        }
      }

      readIndex++;  // read another one
    }

    // handle the last window
    while (tempSeries.getLength() > 0) {
		  tempList = tempSeries.getTimeseries();
		  kp = tempList.get(0);
		  if (prePoint == null) {
			  prePoint = kp;
		  }
      local(tempSeries, prePoint);
		  prePoint = kp;
		  tempList.remove(0);
	  }

    return timeseries;
  }

  // calculate the distance
  private double distance(TimePoint2 prePoint, TimePoint2 kp) {
    double distance = 0;
    double[] xy_pp = new double[2];
    double[] xy_kp = new double[2];
    xy_pp = prePoint.getModify();
    xy_kp = kp.getModify();
    distance = (xy_pp[0] - xy_kp[0]) * (xy_pp[0] - xy_kp[0]) + (xy_pp[1] - xy_kp[1]) * (xy_pp[1] - xy_kp[1]);
    return distance;
  }

  public static double calculateDistance(TimePoint2 tp1, TimePoint2 tp2) {
    double[] xy1 = tp1.getModify();
    double[] xy2 = tp2.getModify();
    return Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
  }

  // judge whether to modify
  private boolean judgeModify(double[]preVal, double[]maxVal, double[]kpVal,
                              long preTime, long maxTime, long kpTime) {
    boolean judge = true;
    double c1,c2;
    c1 = (preVal[0]-kpVal[0])*(preVal[0]-kpVal[0])+(preVal[1]-kpVal[1])*(preVal[1]-kpVal[1]);
    c2 = (maxVal[0]-kpVal[0])*(maxVal[0]-kpVal[0])+(maxVal[1]-kpVal[1])*(maxVal[1]-kpVal[1]);
    if (c1 <= (kpTime-preTime)*(kpTime-preTime)*SMAX*SMAX && 
        c2 <= (maxTime-kpTime)*(maxTime-kpTime)*SMAX*SMAX) {
      judge = false;
    }
    return judge;
  }

  /**
   * @param timeSeries timeseries in a window
   * @param prePoint the former modified point
   */
  private void local(TimeSeries2 timeSeries, TimePoint2 prePoint) {
    ArrayList<TimePoint2> tempList = timeSeries.getTimeseries();
    
    // get bound
    long preTime = prePoint.getTimestamp();
    double[] preVal = new double[2];
    preVal = prePoint.getModify();
    long kpTime = kp.getTimestamp();
    double[] kpVal = new double[2];
    kpVal = kp.getModify();

    // form candidates
    int length = tempList.size();
    int[] top = new int[length+1]; //Default is 0,-1为头,其他为prePoint的index
    int[] len = new int[length+1]; //Default is 0, take the longest to form candidate 
    
    TimePoint2 tp1, tp2;
    if (length == 1) {
      if(judgeModify(preVal, preVal, kpVal, preTime, preTime, kpTime)){
        kp.setModify(preVal);
      }
      return;
    }
    // if(kpTime == 937){
    //   double a = 0;
    // }
    int topIndex = 0;
    // find the first top
    for (int i = 1; i < length; ++i) {
        tp1 = tempList.get(i);
        long t1 = tp1.getTimestamp();
        if (distance(prePoint, tp1) <= ((t1-preTime)*(t1-preTime) * SMAX * SMAX)) {
            top[i] = -1;
            len[i] = 1;
            topIndex = i;
            break;
        }
        // 找出连续错误
        // if(i == 1 && (i+1) < length){
        //   tp2 = tempList.get(i+1);
        //   // xi+1 符合 xi的速度约束
        //   while (distance(tp1, tp2) <= ((tp2.getTimestamp()-tp1.getTimestamp())*(tp2.getTimestamp()-tp1.getTimestamp()) * SMAX * SMAX) && (i+2) < length) {
        //     i++;
        //     tp1 = tempList.get(i);
        //     tp2 = tempList.get(i+1);
        //   }
        // }
    }
    // handle xk+topIndex+1 to xk+w
    boolean flag = false;
    for (int i = topIndex+1; i < length; ++i) {
      tp1 = tempList.get(i);
      tp2 = tempList.get(i-1);
      long t1 = tp1.getTimestamp();
      long t2 = tp2.getTimestamp();
      // if (distance(prePoint, tp1) < ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)) {
      //   break;
      // }
      // xi 符合 xi-1的速度约束
      if (distance(tp1, tp2) <= ((t1-t2)*(t1-t2) * SMAX * SMAX)) {
        if (top[i-1] == -1) {
            top[i] = i-1;
            len[i-1]++;
        }
        else if (top[i-1] >0){
            top[i] = top[i-1];
            len[top[i-1]]++;
        }
      }
      else { // xi 不符合 xi-1的速度约束
        // 寻找是否符合前面的速度约束，进行归类
        for (int j = i-1; j >= 1; j--) {
            tp2 = tempList.get(j);
            t2 = tp2.getTimestamp();
            if ((distance(tp1, tp2) > ((t1-t2)*(t1-t2) * SMAX * SMAX) && top[j] > 0) || j==1) {
                if (distance(prePoint, tp1) <= ((t1-preTime)*(t1-preTime) * SMAX * SMAX)) {
                    // if (distance(prePoint, tp1) > ((t1-kpTime-1)*(t1-kpTime-1) * SMAX * SMAX)) {
                    // if (distance(prePoint, tp1) > ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)) {
                    //   // distance(prePoint, tp1) > ((t1-2*kpTime+preTime)*(t1-2*kpTime+preTime) * SMAX * SMAX)才是正确的
                    //     top[i] = -1;
                    //     len[i] = 1;
                    // }
                    // else {
                    //     flag = true;
                    // }
                    top[i] = -1;
                    len[i] = 1;
                }
                break;
            }
            else if (distance(tp1, tp2) > ((t1-t2)*(t1-t2) * SMAX * SMAX) && (top[j]==0 || top[j]==-1)) {
                continue;
            }
            else if (distance(tp1, tp2) <= ((t1-t2)*(t1-t2) * SMAX * SMAX)) {
                if (top[j] == -1) {
                    top[i] = j;
                    len[j]++;
                }
                else if (top[j] > 0){
                    top[i] = top[j];
                    len[top[j]]++;
                }
                break;
            }
            else {
                System.out.println("My2.local()出现意料之外情况");
                return;
            }
        }
      }
      if (flag) {
        break;
      }
    }

    // 找出候选集
    int maxIndex = topIndex;
    for (int i = maxIndex; i < length; ++i) {
        if (len[i] > len[maxIndex]) {
            maxIndex = i;
        }
    }
    TimePoint2 maxPoint = tempList.get(maxIndex);
    long maxTime = maxPoint.getTimestamp();
    double[] maxVal = new double[2];
    maxVal = maxPoint.getModify();
    
    // 避免满足约束，但由于精度进行了修改
    boolean judge = judgeModify(preVal, maxVal, kpVal, preTime, maxTime, kpTime);

    // 舍弃最小修复原则进行修复
    if (judge) {
      double[] xy_modify = new double[2];
      double pre_dis = this.SMAX * (kpTime-preTime);
      // double next_dis = this.SMAX * (maxTime-kpTime);
      // double pre_next_dis = calculateDistance(maxPoint, prePoint);
      double pre_next_dis = this.SMAX * (maxTime-preTime);
      // double pre_kp_dis = calculateDistance(kp, prePoint);
      if(pre_next_dis > pre_dis){
        xy_modify[0] = (maxVal[0] - preVal[0]) * (pre_dis / pre_next_dis) + preVal[0];
        xy_modify[1] = (maxVal[1] - preVal[1]) * (pre_dis / pre_next_dis) + preVal[1];
 
        // if(pre_next_dis > next_dis){
        //   xy_modify[0] = (xy_modify[0] + ((maxVal[0] - preVal[0]) * ((pre_next_dis - next_dis) / pre_next_dis) + preVal[0])) / 2;
        //   xy_modify[1] = (xy_modify[1] + ((maxVal[1] - preVal[1]) * ((pre_next_dis - next_dis) / pre_next_dis) + preVal[1])) / 2;
        // }
      }
      else {
      // else if(pre_next_dis > pre_dis && pre_next_dis > next_dis){
        xy_modify[0] = (maxVal[0] + preVal[0]) / 2;
        xy_modify[1] = (maxVal[1] + preVal[1]) / 2;
        
      }
      // else if(pre_next_dis > pre_dis && pre_next_dis < next_dis){
      //   xy_modify[0] = (kpVal[0] - preVal[0]) * (pre_dis / pre_kp_dis) + preVal[0];
      //   xy_modify[1] = (kpVal[1] - preVal[1]) * (pre_dis / pre_kp_dis) + preVal[1];
      // }
      
      // xy_modify[0] = (maxVal[0] - preVal[0]) * (kpTime-preTime) / (maxTime-kpTime) + preVal[0];
      // xy_modify[1] = (maxVal[1] - preVal[1]) * (kpTime-preTime) / (maxTime-kpTime) + preVal[1];
      // if(xy_modify[0]!=0 && xy_modify[1]!=0) {
      //   kp.setModify(xy_modify);
      // }
      kp.setModify(xy_modify);
    }
    // kpVal = kp.getModify();
    // judge = judgeModify(preVal, maxVal, kpVal, preTime, maxTime, kpTime);
    // double a = 0;
  }
}