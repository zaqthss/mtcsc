package cn.edu.thu.MTCSC.util;

import java.io.BufferedReader;
// import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
// import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
// import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
// import java.util.Random;

import cn.edu.thu.MTCSC.entity.TimeSeries;
import cn.edu.thu.MTCSC.entity.TimePoint;

public class FileHandler {
	public static String PATH = "data/";

	/**
	 * Two basic attributes: timestamp, value
	 * 
	 * @param filename
	 * @return
	 */
	public TimeSeries readData(String filename) {
		TimeSeries timeSeries = new TimeSeries();

		try {
			FileReader fr = new FileReader(PATH + filename);
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			long timestamp;
			double value;
			double truth;
			TimePoint tp = null;

			// line = br.readLine(); // total len

			while ((line = br.readLine()) != null) {
				String[] vals = line.split(",");
				timestamp = Long.parseLong(vals[0]);
				// System.out.println("vals.length is " + vals.length);
				value = Double.parseDouble(vals[1]);
				if (vals.length == 3) { 
					truth = Double.parseDouble(vals[2]);
				} else if(vals.length == 2) {
					truth = Double.parseDouble(vals[1]);
				} else {
					break;
				}

				tp = new TimePoint(timestamp, value, truth);
				timeSeries.addPoint(tp);
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeSeries;
	}

	/**
	 * Three attribute: timestamp, value, status
	 * 
	 * @param filename
	 * @return
	 */
	// public TimeSeries readHeuristicData(String filename) {
	// 	TimeSeries timeSeries = new TimeSeries();

	// 	try {
	// 		FileReader fr = new FileReader(PATH + filename);
	// 		BufferedReader br = new BufferedReader(fr);

	// 		String line = null;
	// 		long timestamp;
	// 		double value;
	// 		int status;
	// 		TimePoint tp = null;

	// 		while ((line = br.readLine()) != null) {
	// 			String[] vals = line.split(",");
	// 			timestamp = Long.parseLong(vals[0]);
	// 			value = Double.parseDouble(vals[1]);
	// 			status = Integer.parseInt(vals[2]);
	// 			tp = new TimePoint(timestamp, value);
	// 			tp.setStatus(status);
	// 			if (status == 1)
	// 				tp.setModify(value);
	// 			timeSeries.addPoint(tp);
	// 		}

	// 		br.close();
	// 		fr.close();
	// 	} catch (FileNotFoundException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}

	// 	return timeSeries;
	// }

	/**
	 * change the timestamp
	 */
	public void transData(String filename) {
		try {
			FileReader fr = new FileReader(PATH + filename + ".csv");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter(PATH + filename + ".data");
			PrintWriter pw = new PrintWriter(fw);

			String line = null;
			double val1, val2;

			line = br.readLine();
			line = br.readLine();

			long stamp = 0;
			while ((line = br.readLine()) != null) {
				stamp++;
				String[] vals = line.split(",");
				// timestamp = Long.parseLong(vals[0]);
				val1 = Double.parseDouble(vals[1]);
				val2 = Double.parseDouble(vals[2]);

				pw.print(stamp);
				pw.print(',');
				pw.print(val1);
				pw.print(',');
				pw.println(val2);
			}

			br.close();
			fr.close();
			pw.close();
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * write to binary file .dat
	 * 
	 * @param filename
	 */
	public void transDatatoBinary(String filename) {
		try {
			FileReader fr = new FileReader(PATH + filename + ".data");
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fos = new FileOutputStream(PATH + filename
					+ ".dat");
			DataOutputStream out = new DataOutputStream(fos);

			int num = Integer.parseInt(br.readLine()); // line num
			out.writeInt(num);

			String line = null;
			double val1;
			double val2;
			long timestamp;

			while ((line = br.readLine()) != null) {
				String[] vals = line.split(",");
				timestamp = Long.parseLong(vals[0]);
				out.writeLong(timestamp);

				val1 = Double.parseDouble(vals[1]);
				out.writeDouble(val1);

				if (vals.length == 3) {
					val2 = Double.parseDouble(vals[2]);
					out.writeDouble(val2);
				}

				// System.out.println(timestamp + "," + val2);
			}

			br.close();
			fr.close();
			out.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Two basic attributes: timestamp, value
	 * 
	 * @param filename
	 * @param isECG
	 * @return
	 */
	// public TimeSeries readDataBinary(String filename, boolean isECG) {
	// 	TimeSeries timeSeries = new TimeSeries();

	// 	try {
	// 		FileInputStream fis = new FileInputStream(PATH + filename);
	// 		DataInputStream in = new DataInputStream(fis);

	// 		int num = in.readInt();

	// 		long timestamp;
	// 		double val2;
	// 		TimePoint tp = null;

	// 		for (int i = 0; i < num; ++i) {

	// 			timestamp = in.readLong();
	// 			if (isECG) {
	// 				val2 = in.readDouble();
	// 			} else {
	// 				val2 = in.readDouble();
	// 			}

	// 			// System.out.println(timestamp + "," + val2);

	// 			tp = new TimePoint(timestamp, val2);
	// 			timeSeries.addPoint(tp);
	// 		}

	// 		in.close();
	// 		fis.close();
	// 	} catch (FileNotFoundException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}

	// 	return timeSeries;
	// }

	/**
	 * trans standard time to timestamp
	 * 
	 * @param filename
	 */
	public void transOil(String filename) {
		ArrayList<String> timeList = new ArrayList<String>();
		ArrayList<Double> valList = new ArrayList<Double>();

		try {
			FileReader fr = new FileReader(PATH + filename + ".csv");
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter(PATH + filename + ".data");
			PrintWriter pw = new PrintWriter(fw);

			String line = null;
			long timestamp;
			double value;

			while ((line = br.readLine()) != null) {
				String[] vals = line.split(",");
				value = Double.parseDouble(vals[1]);
				timeList.add(vals[0]);
				valList.add(value);
			}

			br.close();
			fr.close();

			pw.println(valList.size());

			long basetime = transTimetoSecond(timeList.get(0));
			for (int i = 0; i < timeList.size(); ++i) {
				timestamp = transTimetoSecond(timeList.get(i)) - basetime + 1;
				// tp = new TimePoint(timestamp, valList.get(i));
				// timeSeries.addPoint(tp);
				pw.println(timestamp + "," + valList.get(i));
			}

			pw.close();
			fw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return timeSeries;
	}

	/**
	 * Using Calendar to 2012/01/01 00:00:00
	 * 
	 * @param time
	 * @return
	 */
	public long transTimetoSecond(String time) {
		long secDiff = 0;

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		try {
			Date basicdate = format.parse("2012/01/01 00:00:00");
			Calendar basicCalendar = Calendar.getInstance();
			basicCalendar.setTime(basicdate);

			Date date = format.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			secDiff = (cal.getTimeInMillis() - basicCalendar.getTimeInMillis()) / (1000);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return secDiff;
	}

	/**
	 * trans standard time to timestamp
	 * 
	 * @param filename
	 */
	// public void transStock(String filename) {
	// 	ArrayList<TimePoint> tpList = new ArrayList<TimePoint>();

	// 	try {
	// 		FileReader fr = new FileReader(PATH + filename + ".csv");
	// 		BufferedReader br = new BufferedReader(fr);
	// 		FileWriter fw = new FileWriter(PATH + filename + ".data");
	// 		PrintWriter pw = new PrintWriter(fw);

	// 		String line = null;
	// 		TimePoint tp = null;
	// 		long timestamp = 1;
	// 		double value;

	// 		line = br.readLine(); // title

	// 		while ((line = br.readLine()) != null) {
	// 			String[] vals = line.split(",");

	// 			value = Double.parseDouble(vals[3]); // open
	// 			tp = new TimePoint(timestamp, value);
	// 			tpList.add(tp);
	// 			timestamp++;

	// 			value = Double.parseDouble(vals[6]); // close
	// 			tp = new TimePoint(timestamp, value);
	// 			tpList.add(tp);
	// 			timestamp++;
	// 		}

	// 		br.close();
	// 		fr.close();

	// 		pw.println(tpList.size());

	// 		for (TimePoint temptp : tpList) {
	// 			pw.println(temptp.getTimestamp() + "," + temptp.getValue());
	// 		}

	// 		pw.close();
	// 		fw.close();

	// 	} catch (FileNotFoundException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}

	// 	// return timeSeries;
	// }

	// public void pickSubData(String filename, double rate) {
	// 	System.out.println("");
	// 	TimeSeries timeseries = readDataBinary(filename + ".dat", true);
	// 	ArrayList<TimePoint> orgList = timeseries.getTimeseries();
	// 	long seed = 19920827;
	// 	Random random = new Random(seed);
	// 	ArrayList<Integer> indexList = new ArrayList<Integer>();

	// 	int size = timeseries.getLength();
	// 	int missnum = (int) (size * rate);
	// 	int index;

	// 	for (int i = 0; i < missnum; ++i) {
	// 		index = random.nextInt(size);
	// 		if (indexList.contains(index)) {
	// 			i = i - 1;
	// 			continue;
	// 		}
	// 		indexList.add(index);
	// 	}
	// 	System.out.println("choose over !");

	// 	Collections.sort(indexList);
	// 	System.out.println("sort over !");

	// 	for (int i = missnum - 1; i > -1; --i) {
	// 		orgList.remove(indexList.get(i).intValue());
	// 	}
	// 	System.out.println("remove over !");

	// 	writeDatatoBinary(timeseries, filename + "-" + rate + ".dat");
	// }

	public void writeDatatoBinary(TimeSeries timeseries, String filename) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(PATH + filename);
			DataOutputStream out = new DataOutputStream(fos);

			int num = timeseries.getLength();
			out.writeInt(num);

			for (TimePoint tp : timeseries.getTimeseries()) {
				out.writeLong(tp.getTimestamp());
				out.writeDouble(tp.getValue());
			}

			out.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void mergeEcg() {
		String path1 = "C:/Users/Stoke/Desktop/temp/ecg/"; // 1,5
		String path2 = "result/ecg/"; // 0,2,3,4
		String outpath = "D:/study/final/gnuplot/bin/ecg/";

		File dir1 = new File(path1);
		String[] names1 = dir1.list();
		File dir2 = new File(path2);
		String[] names2 = dir2.list();

		int num1 = names1.length;
		int num2 = names2.length;
		if (num1 != num2) {
			System.out.println("Wrong num");
			return;
		}

		BufferedReader br1, br2;
		FileWriter fw = null;
		PrintWriter pw = null;
		String templine = null;
		StringBuilder sb = new StringBuilder();
		ArrayList<String> strs1 = new ArrayList<String>();
		ArrayList<String> strs2 = new ArrayList<String>();

		try {
			for (int i = 0; i < num1; ++i) {
				sb.setLength(0);
				strs1.clear();
				strs2.clear();

				String name1 = names1[i];
				String name2 = names2[i];

				br1 = new BufferedReader(new FileReader(path1 + name1));
				while ((templine = br1.readLine()) != null) {
					strs1.add(templine);
				}
				br1.close();

				br2 = new BufferedReader(new FileReader(path2 + name2));
				while ((templine = br2.readLine()) != null) {
					strs2.add(templine);
				}
				br2.close();

				int len = strs1.size();
				fw = new FileWriter(outpath + name1);
				pw = new PrintWriter(fw);

				for (int j = 0; j < len; ++j) {
					String[] vals1 = strs1.get(j).split("\t");
					String[] vals2 = strs2.get(j).split("\t");
					sb.setLength(0);

					sb.append(vals2[0] + "\t");
					sb.append(vals1[1] + "\t");
					sb.append(vals2[2] + "\t");
					sb.append(vals2[3] + "\t");
					sb.append(vals2[4] + "\t");
					sb.append(vals1[5] + "\t");

					pw.println(sb.toString());
				}

				pw.close();
				fw.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getSubExp(int totalnum) {
		String path = "result/exp/";
		String infileName = "exp-oil-sample.dat";
		String outfileName = "exp-oil-sample.dat";

		BufferedReader br = null;
		FileWriter fw = null;
		PrintWriter pw = null;

		try {
			br = new BufferedReader(new FileReader(path + infileName));
			ArrayList<String> strList = new ArrayList<String>();

			String templine = null;
			while ((templine = br.readLine()) != null) {
				strList.add(templine);
			}
			br.close();

			int totalsize = strList.size();
			fw = new FileWriter(path + outfileName);
			pw = new PrintWriter(fw);

			int num = totalsize > totalnum ? totalnum : totalsize;
			for (int i = 0; i < num; ++i) {
				pw.println(strList.get(i));
			}

			pw.close();
			fw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void findMostDate(String filename) {
		HashMap<String, Integer> dateNumMap = new HashMap<String, Integer>();

		try {
			FileReader fr = new FileReader(PATH + filename + ".csv");
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int num = 0;

			while ((line = br.readLine()) != null) {
				String[] vals = line.split(",");
				String date = vals[0].split(" ")[0];
				
				if (dateNumMap.containsKey(date)) {
					dateNumMap.put(date, dateNumMap.get(date) + 1);
				} else {
					dateNumMap.put(date, 1);
				}
			}

			br.close();
			fr.close();

			String goalDate = null;
			Iterator<Entry<String, Integer>> iter = dateNumMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter
						.next();
				int size = entry.getValue();
				if (size > num) {
					num = size;
					goalDate = entry.getKey();
				}
			}

			System.out.println(goalDate + ", num = " + num);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// 	System.out.println("file handler...");
	// 	FileHandler fh = new FileHandler();

	// 	// String filename = "AIP";
	// 	// double rate = 0.2;
	// 	// fh.pickSubData(filename, rate);

	// 	// String filename = "AIP";
	// 	// fh.transStock(filename);
	// 	// fh.transDatatoBinary(filename);

	// 	// fh.mergeEcg();

	// 	// int totalnum = 1000;
	// 	// fh.getSubExp(totalnum);

	// 	fh.findMostDate("oil1");
	// 	String basetime = "2012/9/25  7:21:23";
	// 	String time = "2012/11/24  7:56:27";
	// 	long basestamp = fh.transTimetoSecond(basetime);
	// 	long timestamp = fh.transTimetoSecond(time);
	// 	long realstamp = timestamp - basestamp + 1;
		
	// 	TimeSeries timeseries = fh.readDataBinary("oil1.dat", false);
	// 	int index = timeseries.findRealPos(realstamp);
	// 	System.out.println(realstamp + "," + index);

	// 	System.out.println("end");
	// }
}
