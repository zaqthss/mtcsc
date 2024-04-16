package cn.edu.thu.MTCSC.test.Application;

import cn.edu.thu.MTCSC.util.Assist;

public class ArrowHeadSave {
    public static void main(String[] args) {
        Assist assist = new Assist();
        String filePath = "data/UCR/TRAIN/ArrowHead_TRAIN.tsv";
        int seed = 1;
        double drate = 0.15;
        String dataName = "ArrowHead";
        assist.appAddNoiseSave(filePath, drate, seed, dataName);
    }
}
