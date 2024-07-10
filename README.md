# mtcsc
Source code and supplementary materials for "Multivariate Time Series Cleaning under Speed Constraints".

To enable reproductivity, we open source all datasets, algorithms and codes introduced in the paper, and this document produces a guideline of reproduction. 

We use publicly available code sources for the machine learning baseline and deep leaning baselines: 
* Holoclean: https://github.com/HoloClean/holoclean
* CAE-M: https://github.com/imperial-qore/TranAD
* TranAD: https://github.com/imperial-qore/TranAD

## **Note !!!**
* **Use the IDE to open the MTCSC folder instead of the mtcsc-E4CC folder, which does not contain README.md.**
* **The folder named MTCSC_python is about 5.3.3 statistical testing and 5.5 application experiments. If you need to reproduce the results, please open it separately using the Python IDE. Details can be found in the "Experiments"**

## Requirement

* java
  * java : 1.7/1.8
  * IDE : IntelliJ IDEA or Visual Studio Code (Recommended for use, confirm that it can run)
* python (IDE : pycharm)
  * python==3.6
  * torch==1.3.0
  * sklearn==0.24.2
  * pandas==1.1.5
  * numpy==1.19.2
  * matplotlib==3.3.4
  * scipy==1.5.4
  * networkx==2.5.1

## Code Structure
* MTCSC/
  * data/ : This directory stores all the datasets used in this paper. 
    * example/ : 
      * example1.data corresponds to Example 1.1 in this paper. 
      * example2.data corresponds to Example 2.4/2.5/2.6/2.7 in this paper. 
      * example3.data corresponds to Example 3.3 in this paper. 
  * lib/ : This directory stores all the .jar used in this code. 
  * result/ : This directory stores all the result of experiments in this paper, and it can be reproduced according to the following "Experiments". 
  * src/MTCSC/ :
    * entity/ : Defined the required classes.
    * experiment/ : The directory stores all the Java files corresponding to the experiments in this paper. Details can be found in the following "Experiments". 
    * test/ : The directory stores test files and the Example files in this paper.
      * example1.java corresponds to Example 1.1 in this paper. 
      * example2.java corresponds to Example 2.4/2.5/2.6/2.7 in this paper. 
      * example3.java corresponds to Example 3.3 in this paper. 
    * util/ : Defined the required utils.
    * OLS.java, BaseAnomaly.java and HTD.java : Corresponding HTD baseline.
    * EWMA.java : Corresponding EWMA baseline.
    * Global_1.java : Corresponding the one-dimensional version of MTCSC-G.
    * Global_2.java : Corresponding the two-dimensional version of MTCSC-G.
    * Global_N.java : Corresponding the N-dimensional version of MTCSC-G.
    * Local_1.java : Corresponding the one-dimensional version of MTCSC-L.
    * Local_N.java : Corresponding the N-dimensional version of MTCSC-L.
    * Lsgreedy.java : Corresponding LsGreedy baseline.
    * MTCSC_2.java : Corresponding the two-dimensional version of MTCSC-C.
    * MTCSC_AS.java : Corresponding the adaptive speed version of MTCSC-C.
    * MTCSC_N.java : Corresponding the N-dimensional version of MTCSC-C.
    * MTCSC_QCQP.java : Corresponding MTCSC-C based on the minimum change principle.
    * MTCSC_Uni.java : Corresponding the one-dimensional version of MTCSC-C.
    * OneMILP.java : One dimensional global solution based on minimum fix principle for gurobi, corresponds to section 2.2.1 in this paper.
    * QCQP.java : Two dimensional global solution based on minimum change principle for gurobi.
    * RCSWS.java : Corresponding RCSWS baseline.
    * SCREEN.java : Corresponding SCREEN baseline.
    * SpeedAcc.java : Corresponding SpeedAcc baseline.
    * TwoMILP.java : Two dimensional global solution based on minimum fix principle for gurobi.

## Experiments
If you want to reproduce the experimental results in this paper, simply run a separate Java file. The correspondence between the Java file and the experimental section is as follows.

**Path = MTCSC/src/MTCSC/experiment/**

**Data = MTCSC/data/**

**Result = MTCSC/result/**

* Section 4.2 Adaptive Speed
  * Path/DynamicSpeed_beta_ws.java, results in Result/DynamicSpeed_Beta/
* Section 5.2.1 Comparison among our proposals
  * Path/stock.java, results in Result/One/stock/
* Section 5.2.2  Varying Error Rate
  * Path/oneDimension_errorRate.java, results in Result/One/tem/errorRate
* Section 5.2.3 Varying Data Size
  * Path/oneDimension_dataSize.java, results in Result/One/tem/dataSize
* Section 5.3.1 Varying Error Rate
  * Path/ILD_errorRate_separate.java, results in Result/Two/TemHum/errorRate/separate/
  * Path/TAO_errorRate_separate.java, results in Result/MultiDimension/TAO/errorRate/separate/
  * Path/ECG_errorRate_together.java, results in Result/MultiDimension/ECG/errorRate/together/
  * Path/TAO_errorRate_together.java, results in Result/MultiDimension/TAO/errorRate/together/
* Section 5.3.2 Varying Data Size
  * Path/ILD_dataSize_separate.java, results in Result/Two/TemHum/dataSize/separate/
  * Path/TAO_dataSize_separate.java, results in Result/MultiDimension/TAO/dataSize/separate/
  * Path/ILD_dataSize_together.java, results in Result/Two/TemHum/dataSize/separate/
  * Path/TAO_dataSize_together.java, results in Result/MultiDimension/TAO/dataSize/together/
* Section 5.3.3 Varying Data Size
  * In 5.3.1 and 5.3.2
  * Critical (statistical tests) : Open the MTCSC_python folder using a Python IDE and run /StatisticalTest/sig.py, the result is in /StatisticalTest/fig/
* Section 5.3.4 Varying Dimension
  * Path/Varying_dimensions.java, results in Result/VaringDimension/
* Section 5.4.1 GPS Trajectory with Human Walking
  * gps.java, results in Result/GPS/
* Section 5.4.3 Adaptive Speed with Different Transportation : 
  * Path/DynamicSpeed.java, results in Result/DynamicSpeed/
  * Path/DynamicSpeed_bucket.java, results in Result/DynamicSpeed_Bucket/
  * Path/DynamicSpeed_threshold.java, results in Result/DynamicSpeed_Threshold/
* Section 5.5 Applications : The final classification and clustering experiments were completed using Python. First, you need to run the corresponding Java files below to generate repair results for various algorithms, and then copy the results to the folder corresponding to MTCSC_python/Application/ (already created)
  * java
    * Path/app/ArrowHead.java, repair results in Data/UCR/ArrowHead
    * Path/app/Car.java, repair results in Data/UCR/Car
    * Path/app/DSR.java, repair results in Data/UCR/DSR
    * Path/app/Meat.java, repair results in Data/UCR/Meat
    * Path/app/AtrialFib.java, repair results in Data/UEA/AtrialFibrillation
    * Path/app/SWJ.java, repair results in Data/UEA/StandWalkJump
  * python (Open the MTCSC_python folder using a Python IDE)
    * MTCSC_python/multi-classification.py -> AtrialFib
    * MTCSC_python/multi-cluster.py -> SWJ
    * MTCSC_python/uni-classification.py -> ArrowHead and Car (Removed in revision version)
    * MTCSC_python/uni-cluster.py -> DSR and Meat (Removed in revision version)
  