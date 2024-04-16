package MTCSC.util;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Constants {
	// latitude_1 and latitute_gps_xy_2_e4
	// public static double S1 = 0.48;
	// public static double S2 = -0.51;
	// longitude_1 and longitude_true and longitude_gps_xy_2_e4
	// public static double S1 = 0.51;
	// public static double S2 = -0.59;
	// latitude_2
	// public static double S1 = 1.85;
	// public static double S2 = -2.04;
	// longitude_2
	// public static double S1 = 0.92;
	// public static double S2 = -1.14;
	// temperature
	// public static double S1 = 0.0327;
	// public static double S2 = -0.0229;
	// temperature_x5
	// public static double S1 = 0.1633;
	// public static double S2 = -0.1143;
	// humidity
	// public static double S1 = 0.1999;
	// public static double S2 = -0.1176;
	// humidity_x100
	// public static double S1 = 19.99;
	// public static double S2 = -11.76;
	// humidity_x10
	// public static double S1 = 1.999;
	// public static double S2 = -1.176;
	// humidity_x2
	// public static double S1 = 0.4;
	// public static double S2 = -0.2353;
	// humidity_x9
	public static double S1 = 1.799;
	public static double S2 = -1.059;
	// voltage
	// public static double S1 = 0.0043;
	// public static double S2 = -0.0085;
	// voltage_x10
	// public static double S1 = 0.043;
	// public static double S2 = -0.085;


	public static double EPSILON = 1.0e-5;
  public static double MINVAL = Double.MAX_VALUE;
  public static double MAXVAL = -Double.MAX_VALUE;
  public static long SEED = 19941011;
  public static long T = 20;
  public static double EARTH_RADIUS = 6378.137;
  // public static double EARTH_RADIUS = 6371.004;

  public static double NOISE_MIN = 0;
  public static double NOISE_MAX = 1;

  // lng&lat
  public static long THRESHOLD = 200000;
  public static DecimalFormat DF = new DecimalFormat("#.#");

  // SCREEN
  public static enum POS {
    XL, XR, XM, BL, BR, XO
  };

  public static enum ERROR_GPS {
    LONGITUDE, LATITUDE, BOTH
  };

  public static enum GEN_ERROR {
    UNIFORM, GAUSSIAN, AR
  };
  
  // Three situations, rising, falling, protruding in the middle, and including the initial error：level shift
  public static enum OUTLIER_TYPE {
    UP, DOWN, MID, SHIFT
  };

  // for Yu Zheng
  public static enum MODE_TYPE {
    ZERO, WALK, NON_WALK
  };

  public static enum CLASS_ID {
    WALK, BIKE, BUS, CAR, SUBWAY
  };

  // for SCREEN
  public static long GPS_TIME_INTERVAL = 200000;
  public static double GPS_LATITUDE_CONSTRAINT = 4;

  public static double STOCK_CONSTRAINT = 6;

  public static double ILD_CONSTRAINT_S1 = 0.4;
  public static double ILD_CONSTRAINT_S2 = -0.4;

  public static double CMA_CONSTRAINT_S1 = 6;
  public static double CMA_CONSTRAINT_S2 = -6;

  public static double TAXI_CONSTRAINT_S1 = 0.0001;
  public static double TAXI_CONSTRAINT_S2 = -0.0001;

  // for COLLA
  public static HashMap<Double, Double> LAMBDA = new HashMap<>();

  public static double RES = 0.05;
  public static double RESL = 0.01;
  public static int PARAM = 20;
  public static int PARAML = 100;
  public static double RESE = 20;
  public static double ZEROP = -10000; // instead of zero probability

  public static double[] SPEEDPAT;
  public static double[] SPEEDOUT;

  public static double MINV = -36.5;
  public static double MAXV = 36.5;
  public static double INTERV = 1;

  
}
