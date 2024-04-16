package cn.edu.thu.MTCSC.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import cn.edu.thu.MTCSC.entity.TimePoint;
import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.entity.TimePoint2;
import cn.edu.thu.MTCSC.entity.TimePointN;
import cn.edu.thu.MTCSC.entity.TimeSeries2;
import cn.edu.thu.MTCSC.entity.TimeSeriesN;

public class Assist {
  	public String PATH = "data/";
	/**
	 * Basic attributes: timestamp, dirty, truth
	 *
	 * @param filename filename
	 * @param index which column besides timestamp should be read
	 * @param splitOp to split up the lines
	 * @return data in timeseries form
	 */
	public TimeSeries readData(String filename, String splitOp) {
		TimeSeries timeSeries = new TimeSeries();

		try {
		FileReader fr = new FileReader(PATH + filename);
		BufferedReader br = new BufferedReader(fr);

		String line;
		long timestamp;
		double value;
		double truth;
		TimePoint tp;

		// vals[1]是观测值,vals[2]是正确值
		while ((line = br.readLine()) != null) {
			String[] vals = line.split(splitOp);
			timestamp = Long.parseLong(vals[0]);
			value = Double.parseDouble(vals[1]);
			truth = Double.parseDouble(vals[2]);
			tp = new TimePoint(timestamp, value, truth);
			timeSeries.addPoint(tp);
		}

		br.close();
		fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public TimeSeries readData_size(String filename, String splitOp, int size) {
		TimeSeries timeSeries = new TimeSeries();

		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);

			String line;
			long timestamp;
			double value;
			double truth;
			TimePoint tp;
			int i = 0;
			// vals[1]是观测值,vals[2]是正确值
			while ((line = br.readLine()) != null && i<size) {
				String[] vals = line.split(splitOp);
				timestamp = Long.parseLong(vals[0]);
				value = Double.parseDouble(vals[1]);
				truth = Double.parseDouble(vals[2]);
				tp = new TimePoint(timestamp, value, truth);
				timeSeries.addPoint(tp);
				i++;
			}

			br.close();
			fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public TimeSeries2 readData2(String filename, String splitOp) {
		TimeSeries2 timeSeries = new TimeSeries2();

		try {
		FileReader fr = new FileReader(PATH + filename);
		BufferedReader br = new BufferedReader(fr);

		String line;
		long timestamp;
		double x_value, y_value;
		double x_truth, y_truth;
		TimePoint2 tp;

		// vals[1]是x_value，vals[2]是y_value, vals[3]是x_truth，vals[4]是y_truth
		while ((line = br.readLine()) != null) {
			String[] vals = line.split(splitOp);
			timestamp = Long.parseLong(vals[0]);
			x_value = Double.parseDouble(vals[1]);
			y_value = Double.parseDouble(vals[2]);
			x_truth = Double.parseDouble(vals[3]);
			y_truth = Double.parseDouble(vals[4]);

			tp = new TimePoint2(timestamp, x_value, y_value, x_truth, y_truth);
			timeSeries.addPoint(tp);
		}

		br.close();
		fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public TimeSeriesN readDataN(String filename, String splitOp, int n) {
		TimeSeriesN timeSeries = new TimeSeriesN();

		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);

			String line;
			long timestamp;
			ArrayList<Double> value = new ArrayList<Double>();
			ArrayList<Double> truth = new ArrayList<Double>();
			TimePointN tp;

			// vals[1]-vals[n]是value，vals[n+1]-vals[2n]truth
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(splitOp);
				value = new ArrayList<Double>();
				truth = new ArrayList<Double>();
				timestamp = Long.parseLong(vals[0]);
				for(int i=1; i<=n; i++){
					value.add(Double.parseDouble(vals[i]));
				}
				for(int i=1; i<=n; i++){
					truth.add(Double.parseDouble(vals[n+i]));
				}
				tp = new TimePointN(n, timestamp, value, truth);
				timeSeries.addPoint(tp);
			}

			br.close();
			fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public TimeSeriesN readDataN_size(String filename, String splitOp, int n, int size) {
		TimeSeriesN timeSeries = new TimeSeriesN();

		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);

			String line;
			long timestamp;
			ArrayList<Double> value = new ArrayList<Double>();
			ArrayList<Double> truth = new ArrayList<Double>();
			TimePointN tp;
			int j = 0;
			// vals[1]-vals[n]是value，vals[n+1]-vals[2n]truth
			while ((line = br.readLine()) != null && j < size) {
				String[] vals = line.split(splitOp);
				value = new ArrayList<Double>();
				truth = new ArrayList<Double>();
				timestamp = Long.parseLong(vals[0]);
				for(int i=1; i<=n; i++){
					// value.add(Double.parseDouble(vals[i]));
					if(i==1){
						value.add(Double.parseDouble(vals[i])*10);
					}
					else if(i==2){
						value.add(Double.parseDouble(vals[i]));
					}
					else if(i==3){
						value.add(Double.parseDouble(vals[i])*40);
					}
				}
				for(int i=1; i<=n; i++){
					// truth.add(Double.parseDouble(vals[n+i]));
					if(i==1){
						truth.add(Double.parseDouble(vals[n+i])*10);
					}
					else if(i==2){
						truth.add(Double.parseDouble(vals[n+i]));
					}
					else if(i==3){
						truth.add(Double.parseDouble(vals[n+i])*40);
					}
				}
				tp = new TimePointN(n, timestamp, value, truth);
				timeSeries.addPoint(tp);
				j++;
			}

			br.close();
			fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public TimeSeriesN readDataN_ECG(String filename, String splitOp, int n, int size) {
		TimeSeriesN timeSeries = new TimeSeriesN();

		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);

			String line;
			long timestamp;
			ArrayList<Double> value = new ArrayList<Double>();
			ArrayList<Double> truth = new ArrayList<Double>();
			TimePointN tp;
			int j = 0;
			// vals[1]-vals[n]是value，vals[n+1]-vals[2n]truth
			while ((line = br.readLine()) != null && j < size) {
				String[] vals = line.split(splitOp);
				value = new ArrayList<Double>();
				truth = new ArrayList<Double>();
				timestamp = Long.parseLong(vals[0]);
				for(int i=1; i<=n; i++){
					value.add(Double.parseDouble(vals[i]));
				}
				for(int i=1; i<=n; i++){
					truth.add(Double.parseDouble(vals[n+i]));
				}
				tp = new TimePointN(n, timestamp, value, truth);
				timeSeries.addPoint(tp);
				j++;
			}

			br.close();
			fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public void normalize(TimeSeries2 timeSeries){
		ArrayList<TimePoint2> orgList = timeSeries.getTimeseries();

		double[] MinMax = new double[4];
		MinMax = getMinMax(orgList);

		for(TimePoint2 timePoint : orgList){
			double[] ob = timePoint.getOrgval();
			double[] truth = timePoint.getTruth();
			double value = 0, tempTrue = 0;
			value = (ob[0] - MinMax[0]) / (MinMax[1] - MinMax[0]);
			tempTrue = (truth[0] - MinMax[0]) / (MinMax[1] - MinMax[0]);
			timePoint.scaleX(value, tempTrue);
			value = (ob[1] - MinMax[2]) / (MinMax[3] - MinMax[2]);
			tempTrue = (truth[1] - MinMax[2]) / (MinMax[3] - MinMax[2]);
			timePoint.scaleY(value, tempTrue);
		}
	}

	public void normalizeN(TimeSeriesN timeSeries, int n){
		ArrayList<TimePointN> orgList = timeSeries.getTimeseries();
		ArrayList<Double> MinMax = getMinMax(orgList, n);

		// double MUL = 10.0;
		double MUL = 10.0;

		for(TimePointN timePoint : orgList){
			ArrayList<Double> ob = timePoint.getOrgval();
			ArrayList<Double> truth = timePoint.getTruth();
			double value = 0, tempTrue = 0;
			
			for(int i=0; i<n; i++){
				value = (ob.get(i) - MinMax.get(2*i)) / (MinMax.get(2*i+1) - MinMax.get(2*i)) * MUL;
				tempTrue = (truth.get(i) - MinMax.get(2*i)) / (MinMax.get(2*i+1) - MinMax.get(2*i)) * MUL;
				timePoint.setN(i, value, value, tempTrue);
			}
		}
	}

	// Obtain a specified amount of data
	public TimeSeries2 readData2_size(String filename, String splitOp, int size) {
		TimeSeries2 timeSeries = new TimeSeries2();

		try {
		FileReader fr = new FileReader(PATH + filename);
		BufferedReader br = new BufferedReader(fr);

		String line;
		long timestamp;
		double x_value, y_value;
		double x_truth, y_truth;
		TimePoint2 tp;

		int i = 0;
		// vals[1]是x_value，vals[2]是y_value, vals[3]是x_truth，vals[4]是y_truth
		while ((line = br.readLine()) != null && i < size) {
			String[] vals = line.split(splitOp);
			timestamp = Long.parseLong(vals[0]);
			x_value = Double.parseDouble(vals[1])*4.0;
			y_value = Double.parseDouble(vals[2]);
			x_truth = Double.parseDouble(vals[3])*4.0;
			y_truth = Double.parseDouble(vals[4]);

			tp = new TimePoint2(timestamp, x_value, y_value, x_truth, y_truth);
			timeSeries.addPoint(tp);
			i++;
		}

		br.close();
		fr.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return timeSeries;
	}

	public void writeCSV(String writefilename, String[][] data){
		try {
			FileWriter fw = new FileWriter(writefilename);
			PrintWriter pw = new PrintWriter(fw);

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					pw.print(data[i][j]);
					if (j < data[i].length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.flush();
			pw.close();
			fw.close();

			// System.out.println("CSV文件已创建并写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCSV(String writefilename, String[] name, double[] value, double[][] data){
		try {
			FileWriter fw = new FileWriter(writefilename);
			PrintWriter pw = new PrintWriter(fw);

			pw.print(name[0]);
			pw.print(",");
			for (int i = 0; i < value.length; i++){
				pw.print(Double.toString(value[i]));
				if (i < value.length - 1) {
					pw.print(",");
				}
			}
			pw.println();
			for (int j = 0; j < data[0].length; j++) {
				pw.print(name[j+1]);
				pw.print(",");
				for (int i = 0; i < data.length; i++) {
					pw.print(Double.toString(data[i][j]));
					if (i < data.length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.flush();
			pw.close();
			fw.close();

			// System.out.println("CSV文件已创建并写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCSV(String writefilename, String[] name, int[] value, double[][] data){
		try {
			FileWriter fw = new FileWriter(writefilename);
			PrintWriter pw = new PrintWriter(fw);

			pw.print(name[0]);
			pw.print(",");
			for (int i = 0; i < value.length; i++){
				pw.print(Integer.toString(value[i]));
				if (i < value.length - 1) {
					pw.print(",");
				}
			}
			pw.println();
			for (int j = 0; j < data[0].length; j++) {
				pw.print(name[j+1]);
				pw.print(",");
				for (int i = 0; i < data.length; i++) {
					pw.print(Double.toString(data[i][j]));
					if (i < data.length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.flush();
			pw.close();
			fw.close();

			// System.out.println("CSV文件已创建并写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void writeCSV(String writefilename, String[] name, double[] value, long[][] data){
		try {
			FileWriter fw = new FileWriter(writefilename);
			PrintWriter pw = new PrintWriter(fw);

			pw.print(name[0]);
			pw.print(",");
			for (int i = 0; i < value.length; i++){
				pw.print(Double.toString(value[i]));
				if (i < value.length - 1) {
					pw.print(",");
				}
			}
			pw.println();
			for (int j = 0; j < data[0].length; j++) {
				pw.print(name[j+1]);
				pw.print(",");
				for (int i = 0; i < data.length; i++) {
					pw.print(Long.toString(data[i][j]));
					if (i < data.length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.flush();
			pw.close();
			fw.close();

			// System.out.println("CSV文件已创建并写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCSV(String writefilename, String[] name, int[] value, long[][] data){
		try {
			FileWriter fw = new FileWriter(writefilename);
			PrintWriter pw = new PrintWriter(fw);

			pw.print(name[0]);
			pw.print(",");
			for (int i = 0; i < value.length; i++){
				pw.print(Integer.toString(value[i]));
				if (i < value.length - 1) {
					pw.print(",");
				}
			}
			pw.println();
			for (int j = 0; j < data[0].length; j++) {
				pw.print(name[j+1]);
				pw.print(",");
				for (int i = 0; i < data.length; i++) {
					pw.print(Long.toString(data[i][j]));
					if (i < data.length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.flush();
			pw.close();
			fw.close();

			// System.out.println("CSV文件已创建并写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getLongitude(String readfilename, String splitOp, String writefilename) {
		try {
			FileReader fr = new FileReader(PATH + readfilename);
			BufferedReader br = new BufferedReader(fr);
			FileWriter wr = new FileWriter(PATH + writefilename);

			String line;
			long timestamp;
			double value;
			double truth;
	
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(splitOp);
				timestamp = Long.parseLong(vals[0]);
				value = Double.parseDouble(vals[2]);
				truth = Double.parseDouble(vals[4]);
				// value = (Double.parseDouble(vals[1]));
				// truth = (Double.parseDouble(vals[3]));	
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(value)+ "," +String.valueOf(truth)+"\n");
			}
			br.close();
			fr.close();
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double[] getMin(String filename, String splitOp) {
		double[] min = {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE}; 
		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);
	
			String line;
			double x_value, y_value;
			double x_truth, y_truth;
	
			// vals[1]是x_value，vals[2]是y_value, vals[3]是x_truth，vals[4]是y_truth
			while ((line = br.readLine()) != null) {
			String[] vals = line.split(splitOp);
			x_value = Double.parseDouble(vals[1]);
			y_value = Double.parseDouble(vals[2]);
			x_truth = Double.parseDouble(vals[3]);
			y_truth = Double.parseDouble(vals[4]);
	
			min[0] = min[0] < x_value ? min[0] : x_value;
			min[1] = min[1] < y_value ? min[1] : y_value;
			min[2] = min[2] < x_truth ? min[2] : x_truth;
			min[3] = min[3] < y_truth ? min[3] : y_truth;
			}
			min[0] = min[0] < min[2] ? min[0] : min[2];
			min[1] = min[1] < min[3] ? min[1] : min[3];
	
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return min;
	}

	public void adjustAccuracy(String readfilename, String splitOp, String writefilename) {
		try {
			FileReader fr = new FileReader(PATH + readfilename);
			BufferedReader br = new BufferedReader(fr);
			FileWriter wr = new FileWriter(PATH + writefilename);

			String line;
			long timestamp;
			double x_value, y_value;
			double x_truth, y_truth;
			double[] min = new double[4];
			min = getMin(readfilename, ",");
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(splitOp);
				timestamp = Long.parseLong(vals[0]);
				// x_value = (Double.parseDouble(vals[1])-min[0])*10*10*10*10;
				// y_value = (Double.parseDouble(vals[2])-min[1])*10*10*10*10;
				// x_truth = (Double.parseDouble(vals[3])-min[0])*10*10*10*10;
				// y_truth = (Double.parseDouble(vals[4])-min[1])*10*10*10*10;
				DecimalFormat df = new DecimalFormat("#.0000");
				x_value = (Double.parseDouble(vals[1])-min[0]);
				y_value = (Double.parseDouble(vals[2])-min[1]);
				x_truth = (Double.parseDouble(vals[3])-min[0]);
				y_truth = (Double.parseDouble(vals[4])-min[1]);
				x_value = Double.parseDouble(df.format(x_value));
				x_truth = Double.parseDouble(df.format(x_truth));
				y_value = Double.parseDouble(df.format(y_value));
				y_truth = Double.parseDouble(df.format(y_truth));	
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(x_value)+ ","
						+String.valueOf(y_value)+"," +String.valueOf(x_truth)+ ","
						+String.valueOf(y_truth)+"\n");
			}
			br.close();
			fr.close();
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * RMS sqrt(|modify - truth|^2 / len)
	 *
	 * @param truthSeries truth
	 * @param resultSeries after repair
	 * @return RMS error
	 */
	public  double calcRMS(TimeSeries truthSeries, TimeSeries resultSeries) {
		double cost = 0;
		double delta;
		int len = truthSeries.getLength();

		for (int i = 0; i < len; ++i) {
		delta = resultSeries.getTimeseries().get(i).getModify()
			- truthSeries.getTimeseries().get(i).getValue();

		cost += delta * delta;
		}
		cost /= len;

		return Math.sqrt(cost);
	}
	
	/**
	 * Cost (|modify - error| /len)
	 * 
	 * @param timeseries
	 * @return
	 */
	public double calcCost(TimeSeries timeseries) {
		double cost = 0;
		double delta;
		for (TimePoint tp : timeseries.getTimeseries()) {
			delta = tp.getModify() - tp.getValue();
			cost += Math.abs(delta);
		}
		cost /= timeseries.getLength();

		return cost;
	}

	public double calcCost2(TimeSeries2 timeseries) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double[] delta = new double[2];
		double[] modify = new double[2];
		double[] error = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			error = tp.getOrgval();
			delta[0] = modify[0] - error[0];
			delta[1] = modify[1] - error[1];
			x_cost += delta[0] * delta[0];
			y_cost += delta[1] * delta[1];
		}
		cost = x_cost + y_cost;
		cost /= timeseries.getLength();

		return Math.sqrt(cost);
	}

	public  double Cost1(TimeSeries timeseries_1, TimeSeries timeseries_2) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double delta;
		for (TimePoint tp : timeseries_1.getTimeseries()) {
			delta = tp.getModify() - tp.getValue();
			x_cost += delta * delta;
		}
		for (TimePoint tp : timeseries_2.getTimeseries()) {
			delta = tp.getModify() - tp.getValue();
			y_cost += delta * delta;
		}
		cost = x_cost + y_cost;
		cost /= timeseries_1.getLength();
		return Math.sqrt(cost);
	}

	public  double Cost2(TimeSeries2 timeseries) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double[] delta = new double[2];
		double[] modify = new double[2];
		double[] error = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			error = tp.getOrgval();
			delta[0] = modify[0] - error[0];
			delta[1] = modify[1] - error[1];
			x_cost += delta[0] * delta[0];
			y_cost += delta[1] * delta[1];
		}
		cost = x_cost + y_cost;
		cost /= timeseries.getLength();
		return Math.sqrt(cost);
	}

	public double Cost11(TimeSeries timeseries_1, TimeSeries timeseries_2) {
		double x_cost = 0, y_cost = 0, cost = 0;
		TimePoint tp1, tp2;
		ArrayList<TimePoint> arrayList_1 = timeseries_1.getTimeseries();
		ArrayList<TimePoint> arrayList_2 = timeseries_2.getTimeseries();
		int len = timeseries_1.getLength();
		for (int i = 0; i < len; i++) {
			tp1 = arrayList_1.get(i);
			tp2 = arrayList_2.get(i);
			x_cost = tp1.getModify() - tp1.getValue();
			y_cost = tp2.getModify() - tp2.getValue();
			cost = cost + Math.sqrt(x_cost * x_cost + y_cost * y_cost);
		}
		cost /= len;
		return cost;
	}

	/**
	 * Cost (|modify - error| /len)
	 */
	public double Cost22(TimeSeries2 timeseries) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double[] delta = new double[2];
		double[] modify = new double[2];
		double[] error = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			error = tp.getOrgval();
			delta[0] = modify[0] - error[0];
			delta[1] = modify[1] - error[1];
			x_cost = delta[0] * delta[0];
			y_cost = delta[1] * delta[1];
			cost = cost + Math.sqrt(x_cost + y_cost);
		}
		cost /= timeseries.getLength();
		return cost;
	}

	public  double Cost33(TimeSeries timeseries_1, TimeSeries timeseries_2, TimeSeries timeseries_3) {
		double x_cost = 0, y_cost = 0, z_cost = 0, cost = 0;
		TimePoint tp1, tp2, tp3;
		ArrayList<TimePoint> arrayList_1 = timeseries_1.getTimeseries();
		ArrayList<TimePoint> arrayList_2 = timeseries_2.getTimeseries();
		ArrayList<TimePoint> arrayList_3 = timeseries_3.getTimeseries();
		int len = timeseries_1.getLength();
		for (int i = 0; i < len; i++) {
			tp1 = arrayList_1.get(i);
			tp2 = arrayList_2.get(i);
			tp3 = arrayList_3.get(i);
			x_cost = tp1.getModify() - tp1.getValue();
			y_cost = tp2.getModify() - tp2.getValue();
			z_cost = tp3.getModify() - tp3.getValue();
			cost = cost + Math.sqrt(x_cost * x_cost + y_cost * y_cost + z_cost * z_cost);
		}
		cost /= len;
		return cost;
	}

	public  double CostNN(TimeSeriesN timeseries, int n) {
		double cost = 0;
		double delta = 0;
		double distance = 0;
		ArrayList<Double> modify = new ArrayList<Double>();
		ArrayList<Double> origin = new ArrayList<Double>();
		for (TimePointN tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			origin = tp.getOrgval();
			delta = 0;
			distance = 0;
			for(int i=0; i<n; i++){
				delta = modify.get(i) - origin.get(i);
				distance = distance + delta * delta;
			}
			distance = Math.sqrt(distance);
			cost += distance;
		}
		cost /= timeseries.getLength();
		return cost;
	}

	public int pointNum1(TimeSeries timeseries_1) {
		int num = 0;
		TimePoint tp1;
		ArrayList<TimePoint> arrayList_1 = timeseries_1.getTimeseries();
		int len = timeseries_1.getLength();
		for (int i = 0; i < len; i++) {
			tp1 = arrayList_1.get(i);
			if(tp1.getModify() != tp1.getValue()){
				num++;
			}
		}
		return num;
	}

	public int pointNum11(TimeSeries timeseries_1, TimeSeries timeseries_2) {
		int num = 0;
		TimePoint tp1, tp2;
		ArrayList<TimePoint> arrayList_1 = timeseries_1.getTimeseries();
		ArrayList<TimePoint> arrayList_2 = timeseries_2.getTimeseries();
		int len = timeseries_1.getLength();
		for (int i = 0; i < len; i++) {
			tp1 = arrayList_1.get(i);
			tp2 = arrayList_2.get(i);
			if(tp1.getModify() != tp1.getValue() || tp2.getModify() != tp2.getValue()){
				num++;
			}
		}
		return num;
	}

	public int pointNum111(TimeSeries timeseries_1, TimeSeries timeseries_2, TimeSeries timeseries_3) {
		int num = 0;
		TimePoint tp1, tp2, tp3;
		ArrayList<TimePoint> arrayList_1 = timeseries_1.getTimeseries();
		ArrayList<TimePoint> arrayList_2 = timeseries_2.getTimeseries();
		ArrayList<TimePoint> arrayList_3 = timeseries_3.getTimeseries();
		int len = timeseries_1.getLength();
		for (int i = 0; i < len; i++) {
			tp1 = arrayList_1.get(i);
			tp2 = arrayList_2.get(i);
			tp3 = arrayList_3.get(i);
			if(tp1.getModify() != tp1.getValue() || tp2.getModify() != tp2.getValue() || tp3.getModify() != tp3.getValue()){
				num++;
			}
		}
		return num;
	}

	public int pointNum22(TimeSeries2 timeseries) {
		int num = 0;
		double[] modify = new double[2];
		double[] error = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			error = tp.getOrgval();
			if(modify[0] != error[0] || modify[1] != error[1]){
				num++;
			}
		}
		return num;
	}

	public int pointNumN(TimeSeriesN timeseries, int n) {
		int num = 0;
		ArrayList<Double> modify = new ArrayList<Double>();
		ArrayList<Double> value = new ArrayList<Double>();
		for (TimePointN tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			value = tp.getOrgval();
			for(int i=0; i<n; i++){
				if(modify.get(i) != value.get(i)){
					num++;
					break;
				}
			}
		}
		return num;
	}

	/**
	 * RMS sqrt(|modify - truth|^2 / len)
	 * @param timeseries
	 * @return
	 */
	public double calcRMS(TimeSeries timeseries) {
		double cost = 0;
		double delta;
		for (TimePoint tp : timeseries.getTimeseries()) {
			// if (tp.isLabel()) {
			// 	continue;
			// }
			delta = tp.getModify() - tp.getTruth();
			cost += delta * delta;
			if (Double.isNaN(cost)) {
				System.out.println("NaN");
			} 
		}
		cost /= timeseries.getLength();

		return Math.sqrt(cost);
	}

	public double calcRMS_HTD(TimeSeries timeseries) {
		double cost = 0;
		double delta;
		for (TimePoint tp : timeseries.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			cost += delta * delta;
			if (Double.isNaN(cost)) {
				System.out.println("NaN");
			} 
		}
		int num = timeseries.getNum();
		cost = cost / (timeseries.getLength()-num);

		return Math.sqrt(cost);
	}

	public double calcRMS2(TimeSeries2 timeseries) {
		double x_cost = 0, y_cost = 0 , cost = 0;
		double[] delta = new double[2];
		double[] modify = new double[2];
		double[] truth = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			truth = tp.getTruth();
			delta[0] = modify[0] - truth[0];
			delta[1] = modify[1] - truth[1];
			x_cost += delta[0] * delta[0];
			y_cost += delta[1] * delta[1];
		}
		cost = x_cost + y_cost;
		cost /= timeseries.getLength();

		return Math.sqrt(cost);
	}

	public double RMS1(TimeSeries timeseries_1, TimeSeries timeseries_2) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double delta;
		for (TimePoint tp : timeseries_1.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			x_cost += delta * delta;
		}
		for (TimePoint tp : timeseries_2.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			y_cost += delta * delta;
		}
		cost = x_cost + y_cost;
		ArrayList<Integer> numList1 = timeseries_1.getNumList();
		ArrayList<Integer> numList2 = timeseries_2.getNumList();
		for (Integer num : numList2) {
            if (!numList1.contains(num)) {
                numList1.add(num);
            }
        }

		cost = cost / (timeseries_1.getLength()-numList1.size());
		return Math.sqrt(cost);
	}

	public double RMS1(TimeSeries timeseries_1, TimeSeries timeseries_2, TimeSeries timeseries_3) {
		double x_cost = 0, y_cost = 0, z_cost = 0,cost = 0;
		double delta;
		for (TimePoint tp : timeseries_1.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			x_cost += delta * delta;
		}
		for (TimePoint tp : timeseries_2.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			y_cost += delta * delta;
		}
		for (TimePoint tp : timeseries_3.getTimeseries()) {
			if (tp.isLabel()) {
				continue;
			}
			delta = tp.getModify() - tp.getTruth();
			z_cost += delta * delta;
		}
		cost = x_cost + y_cost + z_cost;
		ArrayList<Integer> numList1 = timeseries_1.getNumList();
		ArrayList<Integer> numList2 = timeseries_2.getNumList();
		ArrayList<Integer> numList3 = timeseries_3.getNumList();
		for (Integer num : numList2) {
            if (!numList1.contains(num)) {
                numList1.add(num);
            }
        }
		for (Integer num : numList3) {
            if (!numList1.contains(num)) {
                numList1.add(num);
            }
        }
		cost = cost / (timeseries_1.getLength()-numList1.size());
		return Math.sqrt(cost);
	}

	public double RMS2(TimeSeries2 timeseries) {
		double x_cost = 0, y_cost = 0, cost = 0;
		double[] delta = new double[2];
		double[] modify = new double[2];
		double[] truth = new double[2];
		for (TimePoint2 tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			truth = tp.getTruth();
			delta[0] = modify[0] - truth[0];
			delta[1] = modify[1] - truth[1];
			x_cost = delta[0] * delta[0];
			y_cost = delta[1] * delta[1];
			cost = cost + x_cost + y_cost;
		}
		cost /= timeseries.getLength();
		return Math.sqrt(cost);
	}

	public double RMSN(TimeSeriesN timeseries, int n) {
		double cost = 0;
		double delta = 0;
		ArrayList<Double> modify = new ArrayList<Double>();
		ArrayList<Double> truth = new ArrayList<Double>();
		for (TimePointN tp : timeseries.getTimeseries()) {
			modify = tp.getModify();
			truth = tp.getTruth();
			delta = 0;
			for(int i=0; i<n; i++){
				delta = modify.get(i) - truth.get(i);
				cost = cost + delta * delta;
			}
		}
		cost /= timeseries.getLength();
		return Math.sqrt(cost);
	}

	public double RMSN(ArrayList<TimeSeries> timeseries, int n) {
		double cost = 0;
		double delta = 0;
		for(int i=0; i<n; i++){
			TimeSeries tmptimeseries = timeseries.get(i);
			for (TimePoint tp : tmptimeseries.getTimeseries()) {
				if (tp.isLabel()) {
					continue;
				}
				delta = tp.getModify() - tp.getTruth();
				cost += delta * delta;
			}
		}
		double len = timeseries.get(0).getLength();
		
		cost /= len;
		return Math.sqrt(cost);
	}

	// get the Speed constraints of one-dimensional time series data
	public double[] getSpeed(TimeSeries timeSeries, double rate) {
		double[] speed = new double[2];
		double tempSpeed = 0;
		ArrayList<Double> SpeedList = new ArrayList<>();
		ArrayList<TimePoint> tempList = timeSeries.getTimeseries();
		TimePoint tp, tp1;
		int len = tempList.size();
		for (int i = 0; i < len-1; ++i) {
			tp = tempList.get(i);
			tp1 = tempList.get(i+1);
			tempSpeed = (tp1.getTruth() - tp.getTruth()) / (tp1.getTimestamp()-tp.getTimestamp());
			// tempSpeed = (tp1.getOrgval() - tp.getOrgval()) / (tp1.getTimestamp()-tp.getTimestamp());
			SpeedList.add(tempSpeed);
		}
		Collections.sort(SpeedList);
		rate = (1-rate) / 2;
		speed[0] = SpeedList.get((int) (len * rate));
		if(rate == 0.0){
			speed[1] = SpeedList.get(SpeedList.size()-1);
		}
		else{
			speed[1] = SpeedList.get((int) (len * (1-rate))-1);
		}
		return speed;
	}

	// get the Speed constraints of two-dimensional time series data
	public double getSpeed2(TimeSeries2 timeSeries, double rate) {
		double tempSpeed = 0;
		double[] truth1 = new double[2];
		double[] truth2 = new double[2];
		ArrayList<TimePoint2> tempList = timeSeries.getTimeseries();
		ArrayList<Double> speedList = new ArrayList<>();
		TimePoint2 tp1, tp2;
		int len = tempList.size();
		for (int i = 0; i < len-1; ++i) {
			tp1 = tempList.get(i);
			tp2 = tempList.get(i+1);
			truth1 = tp1.getTruth();
			truth2 = tp2.getTruth();
			// truth1 = tp1.getOrgval();
			// truth2 = tp2.getOrgval();
			// if(tp2.getTimestamp() <= tp1.getTimestamp()){
			// 	double a =0;
			// }
			tempSpeed = Math.sqrt((truth1[0]-truth2[0])*(truth1[0]-truth2[0]) + (truth1[1]-truth2[1])*(truth1[1]-truth2[1])) / (tp2.getTimestamp()-tp1.getTimestamp());
			speedList.add(tempSpeed);
		}
		len = speedList.size();
		int index = (int) (len * rate);
		Collections.sort(speedList);
		if(rate == 1.0){
			return speedList.get(speedList.size()-1);
		}
		else{
			return speedList.get(index-1);
		}
	}

	// get the Speed constraints of n-dimensional time series data
	public double getSpeedN(TimeSeriesN timeSeries, double rate, int n) {
		double tempSpeed = 0;
		double distance = 0;
		ArrayList<Double> truth1 = new ArrayList<Double>();
		ArrayList<Double> truth2 = new ArrayList<Double>();
		ArrayList<TimePointN> tempList = timeSeries.getTimeseries();
		ArrayList<Double> speedList = new ArrayList<>();
		TimePointN tp1, tp2;
		int len = tempList.size();
		for (int i = 0; i < len-1; ++i) {
			tp1 = tempList.get(i);
			tp2 = tempList.get(i+1);
			truth1 = tp1.getTruth();
			truth2 = tp2.getTruth();
			// truth1 = tp1.getOrgval();
			// truth2 = tp2.getOrgval();
			distance = 0;
			for(int j=0; j<n; j++){
				distance += Math.pow(truth1.get(j) - truth2.get(j), 2);
			}
			tempSpeed = Math.sqrt(distance) / (tp2.getTimestamp()-tp1.getTimestamp());
			speedList.add(tempSpeed);
		}
		len = speedList.size();
		int index = (int) (len * rate);
		Collections.sort(speedList);
		if(rate == 1.0){
			return speedList.get(speedList.size()-1);
		}
		else{
			return speedList.get(index-1);
		}
	}

	public  int errorNum(TimeSeries timeSeries) {
		int errorNum = 0;
		for(TimePoint tp1 : timeSeries.getTimeseries()) {
			if (tp1.getValue() != tp1.getTruth()) {
				errorNum++;
			}
		}
		return errorNum;
	}

	// get min and max for addNoise
	public  double[] getMinMax_1(ArrayList<TimePoint> orgList) {
		// min max min max
		double[] result = {Double.MAX_VALUE,Double.MIN_VALUE}; 
		double value = 0;
		for (int i = 0; i < orgList.size(); i++){
			value = orgList.get(i).getOrgval();
	  
			result[0] = result[0] < value ? result[0] : value;
			result[1] = result[1] > value ? result[1] : value;
		}
		return result;
	}

	// get min and max for addNoise
	public  double[] getMinMax(ArrayList<TimePoint2> orgList) {
		// min max min max
		double[] result = {Double.MAX_VALUE,Double.MIN_VALUE, Double.MAX_VALUE,Double.MIN_VALUE}; 
		double[] value = new double[2];
		for (int i = 0; i < orgList.size(); i++){
			value = orgList.get(i).getOrgval();
	  
			result[0] = result[0] < value[0] ? result[0] : value[0];
			result[1] = result[1] > value[0] ? result[1] : value[0];
			result[2] = result[2] < value[1] ? result[2] : value[1];
			result[3] = result[3] > value[1] ? result[3] : value[1];
		}
		return result;
	}

	// get min and max for addNoise
	public ArrayList<Double> getMinMax(ArrayList<TimePointN> orgList, int n) {
		// min max min max
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<Double> value = new ArrayList<Double>();
		for(int i=0; i<n; i++){
			result.add(Double.MAX_VALUE);
			result.add(Double.MIN_VALUE);
		}
		for (int i = 0; i < orgList.size(); i++){
			value = orgList.get(i).getOrgval();
			for(int j=0; j<n; j++){
				double min = result.get(2*j) < value.get(j) ? result.get(2*j) : value.get(j);
				double max = result.get(2*j+1) > value.get(j) ? result.get(2*j+1) : value.get(j);
				result.set(2*j, min);
				result.set(2*j+1, max);
			}
		}
		return result;
	}

	// add noise
	public TimeSeries addNoise(TimeSeries timeSeries, double drate, int seed) {
		ArrayList<TimePoint> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate);

		double[] noiseMinMax = new double[2];
		noiseMinMax = getMinMax_1(orgList);
		double noise, noiseLen;
		
		DecimalFormat df = new DecimalFormat("#.00");

		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		noiseLen = noiseMinMax[1] - noiseMinMax[0];
		for (int i = 0; i < errorNum; ++i) {
			index = random.nextInt(len);
			if (indexlist.contains(index) || index < 1) {
				i = i - 1;
				continue;
			}
			indexlist.add(index);
			
			// 全局最大最小
			noise = random.nextDouble(); // [0.0-1.0]
			if (noise == 0)
				noise = noiseMinMax[0];
			else {
				noise = noise * noiseLen + noiseMinMax[0];
			}

			noise = Double.parseDouble(df.format(noise));
			orgList.get(index).setOrgval(noise);
			orgList.get(index).setValue(noise);
			orgList.get(index).setModify(noise);
			orgList.get(index).setLabel(true);
		}
		

		return timeSeries;
	}

	public TimeSeries addNoise_stock(TimeSeries timeSeries, double drate, int seed) {
		ArrayList<TimePoint> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate);

		double[] noiseMinMax = new double[2];
		noiseMinMax = getMinMax_1(orgList);
		double noise, noiseLen;
		
		DecimalFormat df = new DecimalFormat("#.00");

		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		noiseLen = noiseMinMax[1] - noiseMinMax[0];
		for (int i = 0; i < errorNum; ++i) {
			index = random.nextInt(len);
			if (indexlist.contains(index) || index < 1 || (index ==(len-1))) {
				i = i - 1;
				continue;
			}
			indexlist.add(index);
			
			// 全局最大最小
			noise = random.nextDouble(); // [0.0-1.0]
			if (noise == 0)
				noise = noiseMinMax[0];
			else {
				noise = noise * noiseLen + noiseMinMax[0];
			}

			noise = Double.parseDouble(df.format(noise));
			orgList.get(index).setOrgval(noise);
			orgList.get(index).setValue(noise);
			orgList.get(index).setModify(noise);
			// orgList.get(index).setLabel(true);
		}
		return timeSeries;
	}

	// add noise to GPS, 附近+-2 或 +-20%
	public  TimeSeries2 addNoiseGPS_num(TimeSeries2 timeSeries, double drate, int seed) {
		ArrayList<TimePoint2> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate);

		double[] noiseMinMax = new double[4];
		noiseMinMax = getMinMax(orgList);
		double noise, noiseLen;
		
		DecimalFormat df = new DecimalFormat("#.00000");

		ArrayList<Integer> timelist = new ArrayList<Integer>();
		for (int i = 0; i < len-1; i++) {
			if (orgList.get(i+1).getTimestamp()-orgList.get(i).getTimestamp() < 0)
				timelist.add(i);
		}
		for (int j = 0; j < 2; j++) {
			ArrayList<Integer> indexlist = new ArrayList<Integer>();
			Random random = new Random(seed+j);
			int index = 0;
			noiseLen = noiseMinMax[1+j*2] - noiseMinMax[0+j*2];
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				
				// 附近+-2
				double[] t = new double[2];
				t = orgList.get(index).getTruth();
				if (j == 0) {
					noiseLen = t[0];
				}
				else if (j == 1) {
					noiseLen = t[1];
				}
				noise = random.nextDouble(); // [0.0-1.0]
				noise = noiseLen + 4 * (noise - 0.5);
				// 附近+-20%
				// noise = noiseLen * (1 + noise/2.5 - 0.2);

				noise = Double.parseDouble(df.format(noise));
				if (j == 0) {
					orgList.get(index).setX(noise);
				}
				else if (j == 1) {
					orgList.get(index).setY(noise);
				}
			}
		}

		return timeSeries;
	}

	// add noise , The maximum and minimum values of all data, 两个维度分开
	public TimeSeries2 addNoise2_maxmin_separate(TimeSeries2 timeSeries, double drate, int seed) {
		ArrayList<TimePoint2> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate / 2);

		double[] noiseMinMax = new double[4];
		noiseMinMax = getMinMax(orgList);
		double noise, noiseLen;
		
		DecimalFormat df = new DecimalFormat("#.00000");

		ArrayList<Integer> timelist = new ArrayList<Integer>();
		for (int i = 0; i < len-1; i++) {
			if (orgList.get(i+1).getTimestamp()-orgList.get(i).getTimestamp() < 0)
				timelist.add(i);
		}
		for (int j = 0; j < 2; j++) {
			ArrayList<Integer> indexlist = new ArrayList<Integer>();
			Random random = new Random(seed+j);
			int index = 0;
			noiseLen = noiseMinMax[1+j*2] - noiseMinMax[0+j*2];
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				
				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				if (noise == 0)
					noise = noiseMinMax[0+j*2];
				else {
					noise = noise * noiseLen + noiseMinMax[0+j*2];
				}

				noise = Double.parseDouble(df.format(noise));
				if (j == 0) {
					orgList.get(index).setX(noise);
				}
				else if (j == 1) {
					orgList.get(index).setY(noise);
				}
			}
		}

		return timeSeries;
	}

	// add noise , The maximum and minimum values of all data, 两个维度合起来
	public TimeSeries2 addNoise2_maxmin_together(TimeSeries2 timeSeries, double drate, int seed) {
		ArrayList<TimePoint2> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate);

		double[] noiseMinMax = new double[4];
		noiseMinMax = getMinMax(orgList);
		double noise, noiseLen_1, noiseLen_2;
		
		DecimalFormat df = new DecimalFormat("#.00000");

		ArrayList<Integer> timelist = new ArrayList<Integer>();
		for (int i = 0; i < len-1; i++) {
			if (orgList.get(i+1).getTimestamp()-orgList.get(i).getTimestamp() < 0)
				timelist.add(i);
		}

		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		noiseLen_1 = noiseMinMax[1] - noiseMinMax[0];
		noiseLen_2 = noiseMinMax[3] - noiseMinMax[2];
		for (int i = 0; i < errorNum; ++i) {
			index = random.nextInt(len);
			if (indexlist.contains(index) || index < 1) {
				i = i - 1;
				continue;
			}
			indexlist.add(index);
			
			// 第一维
			noise = random.nextDouble(); // [0.0-1.0]
			if (noise == 0){
				noise = noiseMinMax[0];
			}
			else {
				noise = noise * noiseLen_1 + noiseMinMax[0];
			}
			noise = Double.parseDouble(df.format(noise));
			orgList.get(index).setX(noise);

			// 第二维
			noise = random.nextDouble(); // [0.0-1.0]
			if (noise == 0){
				noise = noiseMinMax[2];
			}
			else {
				noise = noise * noiseLen_2 + noiseMinMax[2];
			}
			noise = Double.parseDouble(df.format(noise));
			orgList.get(index).setY(noise);
		}

		return timeSeries;
	}

	// add noise , The maximum and minimum values of all data, 维度分开
	public TimeSeriesN addNoiseN_maxmin_separate(TimeSeriesN timeSeries, double drate, int seed, int n) {
		ArrayList<TimePointN> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate / n);

		ArrayList<Double> noiseMinMax = getMinMax(orgList, n);
		double noise=0, noiseLen=0;
		
		DecimalFormat df = new DecimalFormat("#.00000");
		ArrayList<Integer> timelist = new ArrayList<Integer>();
		for (int i = 0; i < len-1; i++) {
			if (orgList.get(i+1).getTimestamp()-orgList.get(i).getTimestamp() < 0)
				timelist.add(i);
		}
		for (int j = 0; j < n; j++) {
			ArrayList<Integer> indexlist = new ArrayList<Integer>();
			Random random = new Random(seed+j);
			int index = 0;
			noiseLen = noiseMinMax.get(1+j*2) - noiseMinMax.get(j*2);
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				

				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				if (noise == 0)
					noise = noiseMinMax.get(j*2);
				else {
					noise = noise * noiseLen + noiseMinMax.get(j*2);
				}

				noise = Double.parseDouble(df.format(noise));
				orgList.get(index).setN(j, noise);

			}
		}

		return timeSeries;
	}

	// add noise , The maximum and minimum values of all data, 维度同时
	public TimeSeriesN addNoiseN_maxmin_together(TimeSeriesN timeSeries, double drate, int seed, int n) {
		ArrayList<TimePointN> orgList = timeSeries.getTimeseries();
		int len = orgList.size();
		int errorNum = (int) (len * drate);

		ArrayList<Double> noiseMinMax = getMinMax(orgList, n);
		double noise=0, noiseLen=0;
		
		DecimalFormat df = new DecimalFormat("#.00000");
		ArrayList<Integer> timelist = new ArrayList<Integer>();
		for (int i = 0; i < len-1; i++) {
			if (orgList.get(i+1).getTimestamp()-orgList.get(i).getTimestamp() < 0)
				timelist.add(i);
		}
		
		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		
		for (int i = 0; i < errorNum; ++i) {
			index = random.nextInt(len);
			if (indexlist.contains(index) || index < 1) {
				i = i - 1;
				continue;
			}
			indexlist.add(index);
			
			for(int j=0; j<n; j++){
				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				noiseLen = noiseMinMax.get(1+j*2) - noiseMinMax.get(j*2);
				if (noise == 0)
					noise = noiseMinMax.get(j*2);
				else {
					noise = noise * noiseLen + noiseMinMax.get(j*2);
				}

				noise = Double.parseDouble(df.format(noise));
				orgList.get(index).setN(j, noise);
			}
		}
		return timeSeries;
	}

	// get x or y from TimeSeries2
	public TimeSeries getXY(TimeSeries2 timeSeries, int type) {
		TimeSeries tempSeries = new TimeSeries();
		long timestamp;
		double[] value = new double[2];
		double[] truth = new double[2];
		// type: 0->x , 1->y
		for (TimePoint2 timePoint : timeSeries.getTimeseries()) {
			timestamp = timePoint.getTimestamp();
			value = timePoint.getOrgval();
			truth = timePoint.getTruth();
			TimePoint tp = new TimePoint(timestamp, value[type], truth[type]);
        	tempSeries.addPoint(tp);
		}
		return tempSeries;
	}

	public TimeSeries getN(TimeSeriesN timeSeries, int type) {
		TimeSeries tempSeries = new TimeSeries();
		long timestamp;
		ArrayList<Double> value = new ArrayList<Double>();
		ArrayList<Double> truth = new ArrayList<Double>();
		// type: 0->x , 1->y
		for (TimePointN timePoint : timeSeries.getTimeseries()) {
			timestamp = timePoint.getTimestamp();
			value = timePoint.getOrgval();
			truth = timePoint.getTruth();
			TimePoint tp = new TimePoint(timestamp, value.get(type), truth.get(type));
        	tempSeries.addPoint(tp);
		}
		return tempSeries;
	}

	// Save noise data
	public void saveNoiseData(String writefilename, TimeSeries2 timeSeries, double drate, int seed) {
		TimeSeries2 resultSeries = addNoise2_maxmin_together(timeSeries, drate, seed);
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			double[] orgval = new double[2];
        	double[] truth = new double[2];
	    	for(TimePoint2 tp1 : resultSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	orgval = tp1.getOrgval();
            	truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(orgval[0])+ ","
					+String.valueOf(orgval[1])+"," +String.valueOf(truth[0])+ ","
					+String.valueOf(truth[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  void saveDataFromTimeSeries2(String writefilename, TimeSeries2 timeSeries){
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			double[] modify = new double[2];
        	double[] truth = new double[2];
	    	for(TimePoint2 tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	modify = tp1.getModify();
            	truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(modify[0])+ ","
					+String.valueOf(modify[1])+"," +String.valueOf(truth[0])+ ","
					+String.valueOf(truth[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  void saveDataFromTimeSeries2_3(String writefilename, TimeSeries2 timeSeries){
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			double[] original = new double[2];
			double[] modify = new double[2];
        	double[] truth = new double[2];
	    	for(TimePoint2 tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
				original = tp1.getOrgval();
            	modify = tp1.getModify();
            	truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(original[0])+ ","
				+String.valueOf(original[1])+"," +String.valueOf(modify[0])+ ","
					+String.valueOf(modify[1])+"," +String.valueOf(truth[0])+ ","
					+String.valueOf(truth[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  void saveDataFromTimeSeries(String writefilename, TimeSeries timeSeries1, TimeSeries timeSeries2){
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			double[] tp1 = new double[2];
        	double[] tp2 = new double[2];
			TimePoint tp;
	    	for(int i=0; i< timeSeries1.getTimeseries().size(); i++) {
				tp = timeSeries1.getTimeseries().get(i);
				timestamp = tp.getTimestamp();
				tp1[0] = tp.getModify();
				tp1[1] = tp.getTruth();
				tp = timeSeries2.getTimeseries().get(i);
				tp2[0] = tp.getModify();
				tp2[1] = tp.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(tp1[0])+ ","
					+String.valueOf(tp2[0])+"," +String.valueOf(tp1[1])+ ","
					+String.valueOf(tp2[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromTimeSeries(String writefilename, TimeSeries timeSeries1){
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			double[] tp1 = new double[2];
			TimePoint tp;
	    	for(int i=0; i< timeSeries1.getTimeseries().size(); i++) {
				tp = timeSeries1.getTimeseries().get(i);
				timestamp = tp.getTimestamp();
				// tp1[0] = tp.getModify();
				// tp1[1] = tp.getTruth();
				tp1[0] = tp.getModify();
				tp1[1] = tp.getModify();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(tp1[0])+ "," +String.valueOf(tp1[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromNoise(String writefilename, TimeSeries timeSeries){
		try {
			FileWriter wr = new FileWriter("noise/" + writefilename);
			long timestamp = 0;
			double[] tp1 = new double[2];
			TimePoint tp;
			// wr.write("timestamp,value,label\n");
			wr.write("timestamp,value\n");
	    	for(int i=0; i< timeSeries.getTimeseries().size(); i++) {
				tp = timeSeries.getTimeseries().get(i);
				timestamp = tp.getTimestamp();
				tp1[0] = tp.getModify();
				tp1[1] = tp.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(tp1[0])+"\n");
				// tp1[0] = tp.getModify();
				// tp1[1] = tp.getModify();
				// String label = "";
				// if(tp.isLabel()){
				// 	label = "1";
				// }
				// else{
				// 	label = "0";
				// }
				// wr.write(String.valueOf(timestamp)+ "," +String.valueOf(tp1[0])+ ","+ label +"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromNoise(String writefilename, TimeSeries2 timeSeries){
		try {
			FileWriter wr = new FileWriter("noise/" + writefilename);
			long timestamp = 0;
			double[] modify = new double[2];
        	// double[] truth = new double[2];
	    	for(TimePoint2 tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	modify = tp1.getModify();
            	// truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(modify[0])+ ","
					+String.valueOf(modify[1])+",0,0\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromNoise_noLabel(String writefilename, TimeSeries2 timeSeries){
		try {
			FileWriter wr = new FileWriter("noise/" + writefilename);
			long timestamp = 0;
			double[] modify = new double[2];
        	// double[] truth = new double[2];
			wr.write("timestamp,tem,hum\n");
	    	for(TimePoint2 tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	modify = tp1.getModify();
            	// truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ "," +String.valueOf(modify[0])+ ","
					+String.valueOf(modify[1])+"\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromNoise(String writefilename, TimeSeriesN timeSeries, int n){
		try {
			FileWriter wr = new FileWriter("noise/" + writefilename);
			long timestamp = 0;
			ArrayList<Double> modify = new ArrayList<Double>();
        	// double[] truth = new double[2];
	    	for(TimePointN tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	modify = tp1.getModify();
            	// truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ ",");
				for(int j=0; j<n; j++){
					wr.write(String.valueOf(modify.get(j))+",");
				}
				wr.write("0,0,0\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromNoise_noLabel(String writefilename, TimeSeriesN timeSeries, int n){
		try {
			FileWriter wr = new FileWriter("noise/" + writefilename);
			long timestamp = 0;
			ArrayList<Double> modify = new ArrayList<Double>();


			wr.write("timestamp,");
			for(int i=0; i<n ;i++){
				wr.write("value"+Integer.toString(i));
				if(i!=(n-1)){
					wr.write(",");
				}
			}
			wr.write("\n");
	    	for(TimePointN tp1 : timeSeries.getTimeseries()) {
				timestamp = tp1.getTimestamp();
            	modify = tp1.getModify();
            	// truth = tp1.getTruth();
				wr.write(String.valueOf(timestamp)+ ",");
				for(int j=0; j<n; j++){
					wr.write(String.valueOf(modify.get(j)));
					if(j!=(n-1)){
						wr.write(",");
					}
				}
				wr.write("\n");
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataFromTimeSeriesN(String writefilename, TimeSeriesN timeSeries, int n){
		try {
			FileWriter wr = new FileWriter(PATH + writefilename);
			long timestamp = 0;
			ArrayList<Double> modify = new ArrayList<Double>();
			TimePointN tp;
	    	for(int i=0; i< timeSeries.getTimeseries().size(); i++) {
				tp = timeSeries.getTimeseries().get(i);
				timestamp = tp.getTimestamp();
				// tp1[0] = tp.getModify();
				// tp1[1] = tp.getTruth();
				modify = tp.getModify();
				wr.write(String.valueOf(timestamp)+ ",");
				for(int j=0; j<n; j++){
					wr.write(String.valueOf(modify.get(j)));
					if(j==(n-1)){
						wr.write("\n");
					}
					else{
						wr.write(",");
					}
				}
	    	}
			wr.close();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// change data to timestamp
	public  long dateToStamp(String s){
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = null;
		try {
	    	date = simpleDateFormat.parse(s);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
    	long ts = date.getTime();
		return ts;
	}
	
	public void appAddNoiseSave(String filePath, double drate, int seed, String dataName){
        ArrayList<Integer> labels = new ArrayList<>();
        int typeNum = 0;
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
		int minLabel = Integer.MAX_VALUE;
        
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                String label = parts[0];
                if(!labels.contains(Integer.parseInt(label))){
                    typeNum++;
					minLabel = Integer.parseInt(label) < minLabel ? Integer.parseInt(label): minLabel;
                }
                labels.add(Integer.parseInt(label));
                ArrayList<Double> rowData = new ArrayList<>();
                for(int i=1; i<parts.length; i++){
                    rowData.add(Double.parseDouble(parts[i]));
                }
                data.add(rowData);
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // 0:max, 1:min
        double[][] MaxMin = new double[typeNum][2];
        for(int i=0; i<typeNum; i++){
            MaxMin[i][0] = Double.MIN_VALUE;
            MaxMin[i][1] = Double.MAX_VALUE;
        }
        for(int i=0; i<data.size(); i++){
            int label = labels.get(i) - minLabel;
            // int label = labels.get(i)-1;
            ArrayList<Double> rowData = data.get(i);
            for(Double num : rowData){
                MaxMin[label][0] = MaxMin[label][0] > num ? MaxMin[label][0] : num;
                MaxMin[label][1] = MaxMin[label][1] < num ? MaxMin[label][1] : num;
            }
        }

		int len = data.get(0).size();
		int errorNum = (int) (len * drate);

		double noise, noiseLen;
		DecimalFormat df = new DecimalFormat("#.000000");
		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		
		for(int j=0; j<data.size(); j++){
			int label = labels.get(j) - minLabel;
			// int label = labels.get(j)-1;
			noiseLen = MaxMin[label][0] - MaxMin[label][1];
			indexlist = new ArrayList<Integer>();
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				
				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				if (noise == 0)
					noise = MaxMin[label][1];
				else {
					noise = noise * noiseLen + MaxMin[label][1];
				}
	
				noise = Double.parseDouble(df.format(noise));
				data.get(j).set(index, noise);
			}
		}
		
		// 将数据写入新的.tsv文件
		try {
			String outputFilePath = "data/UCR/"+dataName+"/"+String.valueOf(drate)+"_"+String.valueOf(seed)+".tsv";
			FileWriter writer = new FileWriter(outputFilePath);
			for (int i = 0; i < labels.size(); i++) {
				writer.write(String.valueOf(labels.get(i)));
				ArrayList<Double> rowData = data.get(i);
				for (Double value : rowData) {
					writer.write("\t" + String.valueOf(value));
				}
				writer.write("\n");
			}
			writer.close();
		}catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void saveTSVFromTimeSeriesList(ArrayList<TimeSeries> resultSeriesList, String dataName, ArrayList<Integer> labels){
		// 将数据写入新的.tsv文件
		try {
			String outputFilePath = "data/UCR/"+dataName;
			FileWriter writer = new FileWriter(outputFilePath);
			for (int i = 0; i < labels.size(); i++) {
				writer.write(String.valueOf(labels.get(i)));
				TimeSeries timeSeries = resultSeriesList.get(i);
				for (TimePoint tp : timeSeries.getTimeseries()) {
					writer.write("\t" + String.valueOf(tp.getModify()));
				}
				writer.write("\n");
			}
			writer.close();
		}catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public ArrayList<ArrayList<Double>> appAddNoise(String filePath, double drate, int seed){
        ArrayList<Integer> labels = new ArrayList<>();
        int typeNum = 0;
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        int minLabel = Integer.MAX_VALUE;
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                String label = parts[0];
                if(!labels.contains(Integer.parseInt(label))){
                    typeNum++;
					minLabel = Integer.parseInt(label) < minLabel ? Integer.parseInt(label): minLabel;
                }
                labels.add(Integer.parseInt(label));
                ArrayList<Double> rowData = new ArrayList<>();
                for(int i=1; i<parts.length; i++){
                    rowData.add(Double.parseDouble(parts[i]));
                }
                data.add(rowData);
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // 0:max, 1:min
        double[][] MaxMin = new double[typeNum][2];
        for(int i=0; i<typeNum; i++){
            MaxMin[i][0] = Double.MIN_VALUE;
            MaxMin[i][1] = Double.MAX_VALUE;
        }
        for(int i=0; i<data.size(); i++){
            int label = labels.get(i) - minLabel;
            // int label = labels.get(i)-1;
            ArrayList<Double> rowData = data.get(i);
            for(Double num : rowData){
                MaxMin[label][0] = MaxMin[label][0] > num ? MaxMin[label][0] : num;
                MaxMin[label][1] = MaxMin[label][1] < num ? MaxMin[label][1] : num;
            }
        }

		int len = data.get(0).size();
		int errorNum = (int) (len * drate);

		double noise, noiseLen;
		DecimalFormat df = new DecimalFormat("#.000000");
		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		
		for(int j=0; j<data.size(); j++){
			int label = labels.get(j) - minLabel;
			// int label = labels.get(j)-1;
			noiseLen = MaxMin[label][0] - MaxMin[label][1];
			indexlist = new ArrayList<Integer>();
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				
				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				if (noise == 0)
					noise = MaxMin[label][1];
				else {
					noise = noise * noiseLen + MaxMin[label][1];
				}
	
				noise = Double.parseDouble(df.format(noise));
				data.get(j).set(index, noise);
			}
		}
		return data;
	}

	public ArrayList<TimeSeries> appAddNoiseTimeSeires(String filePath, double drate, int seed){
        ArrayList<Integer> labels = new ArrayList<>();
        int typeNum = 0;
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        ArrayList<ArrayList<Double>> dirtydata = new ArrayList<>();
		ArrayList<TimeSeries> TimeSeriesList = new ArrayList<>();
		int minLabel = Integer.MAX_VALUE;
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                String label = parts[0];
                if(!labels.contains(Integer.parseInt(label))){
                    typeNum++;
					minLabel = Integer.parseInt(label) < minLabel ? Integer.parseInt(label): minLabel;
                }
                labels.add(Integer.parseInt(label));
                ArrayList<Double> rowData = new ArrayList<>();
				ArrayList<Double> rowData1 = new ArrayList<>();
                for(int i=1; i<parts.length; i++){
                    rowData.add(Double.parseDouble(parts[i]));
					rowData1.add(Double.parseDouble(parts[i]));
                }
                data.add(rowData);
				dirtydata.add(rowData1);
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // 0:max, 1:min
        double[][] MaxMin = new double[typeNum][2];
        for(int i=0; i<typeNum; i++){
            MaxMin[i][0] = Double.MIN_VALUE;
            MaxMin[i][1] = Double.MAX_VALUE;
        }
        for(int i=0; i<data.size(); i++){
            int label = labels.get(i) - minLabel;
            // int label = labels.get(i)-1;
            ArrayList<Double> rowData = data.get(i);
            for(Double num : rowData){
                MaxMin[label][0] = MaxMin[label][0] > num ? MaxMin[label][0] : num;
                MaxMin[label][1] = MaxMin[label][1] < num ? MaxMin[label][1] : num;
            }
        }

		int len = data.get(0).size();
		int errorNum = (int) (len * drate);

		double noise, noiseLen;
		DecimalFormat df = new DecimalFormat("#.000000");
		ArrayList<Integer> indexlist = new ArrayList<Integer>();
		Random random = new Random(seed);
		int index = 0;
		
		for(int j=0; j<dirtydata.size(); j++){
			int label = labels.get(j) - minLabel;
			// int label = labels.get(j)-1;
			noiseLen = MaxMin[label][0] - MaxMin[label][1];
			indexlist = new ArrayList<Integer>();
			// add noise
			for (int i = 0; i < errorNum; ++i) {
				index = random.nextInt(len);
				if (indexlist.contains(index) || index < 1) {
					i = i - 1;
					continue;
				}
				indexlist.add(index);
				
				// 全局最大最小
				noise = random.nextDouble(); // [0.0-1.0]
				if (noise == 0)
					noise = MaxMin[label][1];
				else {
					noise = noise * noiseLen + MaxMin[label][1];
				}
	
				noise = Double.parseDouble(df.format(noise));
				dirtydata.get(j).set(index, noise);
			}
			//generate TimeSeries
			TimeSeries timeSeries = new TimeSeries();
			for(int i=0; i<dirtydata.get(j).size(); i++){
				TimePoint tp = new TimePoint(i, dirtydata.get(j).get(i), data.get(j).get(i));
        		timeSeries.addPoint(tp);
			}
			TimeSeriesList.add(timeSeries);
		}

		return TimeSeriesList;
	}

	public ArrayList<Integer> appGetLabel(String filePath){
        ArrayList<Integer> labels = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                String label = parts[0];
                labels.add(Integer.parseInt(label));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
		return labels;
	}
}
