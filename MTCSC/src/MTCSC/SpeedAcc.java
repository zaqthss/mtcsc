package MTCSC;

import java.io.IOException;
import java.util.ArrayList;
import MTCSC.entity.TimePoint;
import MTCSC.entity.TimeSeries;


public class SpeedAcc {
  private TimeSeries timeSeries;

  private long T;       // the window size
  private double SMAX;  // maximum speed
  private double SMIN;  // minimum speed

  private double AMAX;  // maximum accelerate
  private double AMIN;  // minimum accelerate

  public SpeedAcc(TimeSeries timeSeries, long t, double sMax, double sMin, double aMax, double aMin) {
    setTimeSeries(timeSeries);
    setT(t);
    setSMAX(sMax);
    setSMIN(sMin);
    setAMAX(aMax);
    setAMIN(aMin);
  }

  public void setTimeSeries(TimeSeries timeSeries) {
    this.timeSeries = timeSeries;
  }

  public void setT(long t) {
    this.T = t;
  }

  public void setSMAX(double sMax) {
    this.SMAX = sMax;
  }

  public void setSMIN(double sMin) {
    this.SMIN = sMin;
  }

  public void setAMAX(double aMax) {
    this.AMAX = aMax;
  }

  public void setAMIN(double aMin) {
    this.AMIN = aMin;
  }


  /**
   * 
   * @param params
   * @param heuristic
   * @return
 * @throws IOException 
   */
  public TimeSeries mainSliUp(){
    // System.out.println("hello sliding heuristic !!!");
    ArrayList<TimePoint> totalList = timeSeries.getTimeseries();
    int total = totalList.size();
    
    int kn=0;
    int ki=kn+1;
    
    int [] kT=new int[total];
    while (kn<total&& ki<total )
    {
    		if ( totalList.get(ki).getTimestamp()-totalList.get(kn).getTimestamp() > this.T )
    		{
    			kT[kn]=ki;
    			kn++;
    			ki=kn+1;
    		}
    		else
    			ki++;
    }
    for (int i=0; i<total; i++)
    {
    	if (kT[i]==0)
    	{
    		kT[i]=total-1;
    	}
    }
    

	double [] xkmin=new double [total];
	double [] xkmax=new double [total];
	
	double [] xkminS=new double [total];
	double [] xkmaxS=new double [total];
	
	 xkmin[0]=Double.MIN_VALUE;
    	 xkmax[0]=Double.MAX_VALUE;
    	 
    	 TimePoint [] tpk =new TimePoint[total];
    	 for (int i=0; i<total; i++)
    	 {
    		 tpk[i]=totalList.get(i);
    	 }
//    	 System.out.println("a"+total);

    	 for (int k=0; k<2; k++)
    	 {
    		 ArrayList<Double> xK=new ArrayList<Double>();
    		 if (k!=0)
    		 {
    		 xkmin[k]=tpk[k-1].getModify()+this.SMIN *(tpk[k].getTimestamp() - tpk[k-1].getTimestamp());
    	    	 xkmax[k]=tpk[k-1].getModify()+this.SMAX *(tpk[k].getTimestamp() - tpk[k-1].getTimestamp());
//    	    	 System.out.println("MINMAX");
//    	    	 System.out.println(xkmin[k]);
//    	    	 System.out.println(xkmax[k]);
    		 }
    		 
    		 for (int i=k+1; i<kT[k]; i++)////////////////////
        	 {
        		 
        		 if (totalList.get(i).getTimestamp()-totalList.get(k).getTimestamp() <= this.T)
        		 {
  			
        			 double valSpeedMin = tpk[i].getOrgval() + this.SMIN *(tpk[k].getTimestamp() - tpk[i].getTimestamp());
         	     double valSpeedMax = tpk[i].getOrgval() + this.SMAX *(tpk[k].getTimestamp() - tpk[i].getTimestamp());
         	     xK.add(valSpeedMin);
         	     xK.add(valSpeedMax);
        		 }
        	 }
        	 xK.add(tpk[k].getOrgval());
        	 xK.sort(null);
        	 
//        	 System.out.println("sort");
//        	 System.out.println(xK.size());
//        	 for (int i=0; i<xK.size(); i++)
//        	 {
//        		 System.out.println(xK.get(i));
//        	 }

        	 double xKmid=xK.get(xK.size()/2);
        	 if (xkmax[k]<xKmid)
        	 {
        		 tpk[k].setModify(xkmax[k]);
        	 }
        	 else if (xkmin[k]>xKmid)
        	 {
        		 tpk[k].setModify(xkmin[k]);
        	 }
        	 else
        	 {
        		 tpk[k].setModify(xKmid);
        	 }
//        	 System.out.println("b");
        	 
    	 }
    	 
    	//  long time1 = System.currentTimeMillis();
    	//  long time2 = System.currentTimeMillis();
    	 for (int k=2; k<total; k++)
    	 {
    		 ArrayList<Double> xK=new ArrayList<Double>();
    		
//    		 tpk[k]=totalList.get(k);
    		 xkmin[k]=(this.AMIN *(tpk[k].getTimestamp()-tpk[k-1].getTimestamp())+
   	           	  (tpk[k-1].getModify()-tpk[k-2].getModify())/(tpk[k-1].getTimestamp()-tpk[k-2].getTimestamp()))*(tpk[k].getTimestamp()-tpk[k-1].getTimestamp())
   	    	    		  +tpk[k-1].getModify();   // formular 12
    		 xkmax[k]=(this.AMAX *(tpk[k].getTimestamp()-tpk[k-1].getTimestamp())+
    	           	  (tpk[k-1].getModify()-tpk[k-2].getModify())/(tpk[k-1].getTimestamp()-tpk[k-2].getTimestamp()))*(tpk[k].getTimestamp()-tpk[k-1].getTimestamp())
    	    	    		  +tpk[k-1].getModify();   // formular 13
    		 
    		 xkminS[k]=tpk[k-1].getModify()+this.SMIN *(tpk[k].getTimestamp() - tpk[k-1].getTimestamp());
	    	 xkmaxS[k]=tpk[k-1].getModify()+this.SMAX *(tpk[k].getTimestamp() - tpk[k-1].getTimestamp());
	    	 
	    	  xkmin[k]=xkmin[k] > xkminS[k] ? xkmin[k] : xkminS[k];
 	      xkmax[k]=xkmax[k] < xkmaxS[k] ? xkmax[k] : xkmaxS[k];
// 	     System.out.println("MINMAX");
//    	 System.out.println(xkmin[k]);
//    	 System.out.println(xkmax[k]);
// 	     time2 = System.currentTimeMillis();
//		 System.out.println(k+"	jsemi	"+(time2-time1));
// 	     time1 = System.currentTimeMillis();
    		 for (int j=k+1;j<kT[k]; j++)
    		 {
    			 
//    			 System.out.println("c");
//    			 tpk[j]=totalList.get(j);
    			 for (int i=j+1; i<kT[k]; i++)////////////////////
            	 {
//    				 System.out.println("d");
    				
            		 if (totalList.get(i).getTimestamp()-totalList.get(k).getTimestamp() <= this.T)////////////////
            		 {
//            		if (i!=j)
//            		{
//            			 double a=this.AMIN *(tpk[i].getTimestamp()-tpk[j].getTimestamp());
//            			 double b=(tpk[i].getObserveval()-tpk[j].getObserveval())/(tpk[i].getTimestamp()-tpk[j].getTimestamp());
//            			 double c=tpk[j].getObserveval();
//            			 double d=a-b+c;
//            			 System.out.println(a);
//            			 System.out.println(b);
//            			 System.out.println(c);
//            			 System.out.println(d);
            			 
//            			 System.out.println((this.AMIN *(tpk[i].getTimestamp()-tpk[j].getTimestamp())-
//            		           	  (tpk[i].getObserveval()-tpk[j].getObserveval())/(tpk[i].getTimestamp()-tpk[j].getTimestamp()))*(tpk[j].getTimestamp()-tpk[k].getTimestamp())
//            		    	    		  +tpk[j].getObserveval());
            			 
            			double valAccMin = (this.AMIN *(tpk[i].getTimestamp()-tpk[j].getTimestamp())-
             		           	  (tpk[i].getOrgval()-tpk[j].getOrgval())/(tpk[i].getTimestamp()-tpk[j].getTimestamp()))*(tpk[j].getTimestamp()-tpk[k].getTimestamp())
             		    	    		  +tpk[j].getOrgval();
               	     double valAccMax = (this.AMAX*(tpk[i].getTimestamp()-tpk[j].getTimestamp())-
            		           	  (tpk[i].getOrgval()-tpk[j].getOrgval())/(tpk[i].getTimestamp()-tpk[j].getTimestamp()))*(tpk[j].getTimestamp()-tpk[k].getTimestamp())
            		    	    		  +tpk[j].getOrgval();
//               	     System.out.println(valAccMin);
//               	    System.out.println(valAccMax);
               	     xK.add(valAccMin);
               	     xK.add(valAccMax);
//               	    double valSpeedMin = tpk[i].getObserveval() + this.SMIN *(tpk[k].getTimestamp() - tpk[i].getTimestamp());////////////////
//              	     double valSpeedMax = tpk[i].getObserveval() + this.SMAX *(tpk[k].getTimestamp() - tpk[i].getTimestamp());/////////////////
              	   double valSpeedMin = tpk[j].getOrgval() + this.SMIN *(tpk[k].getTimestamp() - tpk[j].getTimestamp());////////////////
            	     double valSpeedMax = tpk[j].getOrgval() + this.SMAX *(tpk[k].getTimestamp() - tpk[j].getTimestamp());
              	     xK.add(valSpeedMin);
              	     xK.add(valSpeedMax);
//            		}
//            			 TimePoint tpi = totalList.get(i);
            			 
//            	     System.out.println(valSpeedMin);
//              	    System.out.println(valSpeedMax);
            		 }
//            		 time2 = System.currentTimeMillis();
//        	    	 System.out.println("i"+(time2-time1));
            	 }
//    			 time2 = System.currentTimeMillis();
//        		 System.out.println(k+"	ji	"+(time2-time1));
    		 
//    	    	 System.out.println("j"+(time2-time1));
    		 }
//    		 time2 = System.currentTimeMillis();
//    		 System.out.println(k+"	j	"+(time2-time1));
    		 xK.add(tpk[k].getOrgval());
//    		 System.out.println("INI");
//        	 System.out.println(xK.size());
//        	 for (int i=0; i<xK.size(); i++)
//        	 {
//        		 System.out.println(xK.get(i));
//        	 }
        	 xK.sort(null);
//        	 System.out.println("sort");
//        	 System.out.println(xK.size());
//        	 for (int i=0; i<xK.size(); i++)
//        	 {
//        		 System.out.println(xK.get(i));
//        	 }
//        	 time2 = System.currentTimeMillis();
//	    	 System.out.println(k+"	sort	"+(time2-time1));
        	 double xKmid=xK.get(xK.size()/2);
        	 if (xkmax[k]<xKmid)
        	 {
        		 tpk[k].setModify(xkmax[k]);
        	 }
        	 else if (xkmin[k]>xKmid)
        	 {
        		 tpk[k].setModify(xkmin[k]);
        	 }
        	 else
        	 {
        		 tpk[k].setModify(xKmid);
        	 }
//        	 time2 = System.currentTimeMillis();
//	    	 System.out.println(k+"	set	"+(time2-time1));
    		 
//        	 System.out.println("e"+k+"/"+total);
    		 
    	 }
    	//  time2 = System.currentTimeMillis();
//    	 System.out.println("all"+(time2-time1));
    	 
    	//  File outFile_avg = new File("/Users/gaofei/Documents/stockLocalASAfter.csv");
			
		// 	BufferedWriter outf_avg = new BufferedWriter(new FileWriter(outFile_avg));
		// 	for(TimePoint tp1 : timeSeries.getTimeseries()) {
		// 		String dayout=tp1.getTimestamp() + "," + 
		// 				tp1.getObserveval() + "," + tp1.getModify()+ "," + "," + tp1.getOrgval();
		// 		outf_avg.write(dayout);
		// 		outf_avg.newLine();
		// 	}
		// 	outf_avg.write("over");
		// 	outf_avg.newLine();
		// 	outf_avg.close();
    	 

    // params[0] = Assist.calcAccuracy(timeSeries);
    // params[1] = Assist.calcMNAD(timeSeries);
    // params[2] = Assist.calcRMS(timeSeries);
    // params[3] = Assist.calcUnchanged(timeSeries);
    // params[4] = Assist.calcConflict(timeSeries);
    // params[5] = Assist.calcCost(timeSeries);

    return timeSeries;
  }

}
