# mtcsc
Source code and supplementary materials for "Multivariate Time Series Cleaning under Speed Constraints".

To enable reproductivity, we open source all datasets, algorithms and codes introduced in the paper, and this document produces a guideline of reproduction. 

## **Note !!!**
* **Use the IDE to open the MTCSC folder instead of the mtcsc-E4CC folder, which does not contain README.md.**

## Requirement

- java : 1.7/1.8

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
    * OLS.java 、BaseAnomaly.java and HTD.java : Corresponding HTD baseline.
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
* Section 5.4.1 GPS Trajectory with Human Walking
  * gps.java, results in Result/GPS/
* Section 5.4.3 Adaptive Speed with Different Transportation : 
  * Path/DynamicSpeed.java, results in Result/DynamicSpeed/
  * Path/DynamicSpeed_bucket.java, results in Result/DynamicSpeed_Bucket/
  * Path/DynamicSpeed_threshold.java, results in Result/DynamicSpeed_Threshold/
* Section 5.5 Applications : repair results in Data/UCR, The final classification and clustering experiments were completed using Python
  * Path/app/ArrowHead.java
  * Path/app/Car.java
  * Path/app/DiatomSizeReduction.java
  * Path/app/Meat.java
  