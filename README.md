# mtcsc
Source code and supplementary materials for "Multivariate Time Series Cleaning under Speed Constraints".

To enable reproductivity, we open source all datasets, algorithms and codes introduced in the paper, and this document produces a guideline of reproduction. 

## Requirement

- java:1.7

## Code Structure

* Section 4.2 Adaptive Speed
  * DynamicSpeed_beta_ws
* Section 5.2.1 Comparison among our proposals
  * stock
* Section 5.2.2  Varying Error Rate
  * oneDimension_errorRate
* Section 5.2.3 Varying Data Size
  * oneDimension_dataSize
* Section 5.3.1 Varying Error Rate
  * ILD_errorRate_separate
  * TAO_errorRate_separate
  * ECG_errorRate_together
  * TAO_errorRate_together
* Section 5.3.2 Varying Data Size
  * ILD_dataSize_separate
  * ILD_dataSize_together
  * TAO_dataSize_separate
  * TAO_dataSize_together
* Section 5.3.3 Varying Data Size
  * In 5.3.1 and 5.3.2
* Section 5.4.1 GPS Trajectory with Human Walking
  * gps
* Section 5.4.3 Adaptive Speed with Different Transportation
  * DynamicSpeed
  * DynamicSpeed_bucket
  * DynamicSpeed_threshold
* Section 5.5
  * app/ArrowHead
  * app/Car
  * app/DiatomSizeReduction
  * app/Meat
  