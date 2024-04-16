package MTCSC.test;

import MTCSC.util.Assist;

public class adjustGPS {
    public static void main(String[] args) {
        Assist assist = new Assist();
        // String readFileName = "gps_xy_2.data";
        // String writeFileName = "gps_xy_2_e4.data";
        // String readFileName = "route2_final.data";
        // String writeFileName = "route2_final_e4.data";
        // String readFileName = "GPS/people_1_gps.data";
        // String writeFileName = "GPS/people_1_gps_e4.data";
        // String readFileName = "GPS/artificial_gps.data";
        // String writeFileName = "GPS/artificial_gps_e4.data";
        // String readFileName = "GPS/trgps-high-final.data";
        // String writeFileName = "GPS/trgps-high-final-e4.data";
        // String readFileName = "GPS/gps-high-final_xy.csv";
        // String writeFileName = "GPS/gps-high-final_xy-e4.data";
        // String readFileName = "GPS/all_walk_final_xy.csv";
        // String writeFileName = "GPS/all_walk_final_xy-e4.data";
        String readFileName = "GPS/gps_final_xy.csv";
        String writeFileName = "GPS/gps_final_xy-e4.data";
        assist.adjustAccuracy(readFileName, ",", writeFileName);
        System.out.println("finish");
    }
}
