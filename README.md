# mtcsc
Source code and supplementary materials for "Multivariate Time Series Cleaning under Speed Constraints".

To enable reproductivity, we open source all datasets, algorithms and codes introduced in the paper, and this document produces a guideline of reproduction. 

## Requirement

- java:1.7
- python: 3.7

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
  

## Example Invocation

Take the code of drawing Table 4 as an example:

```java
// Set parameters
String[] vars = {"yahoo","twitter"};
boolean[] willOperate = {true, true, true};

String[] algNames = {"NETS", "Stare","SHESD"};
String[] metricNames = {"precision", "recall", "fmeasure"};

...
    
// NETS
algIndex++;
if (willOperate[algIndex]) {
    System.out.println(algNames[algIndex] + " begin");
    if (!seriesMulMap.containsKey(dsName)) {
        timeSeriesMulDim = fh.readMulDataWithLabel(rawPath);
        seriesMulMap.put(dsName, timeSeriesMulDim);
        TreeMap<Long, TimePointMulDim> realAnomaly =
            DataHandler.findAnomalyPoint(timeSeriesMulDim);
        realAnomalyMulMap.put(dsName, realAnomaly);
    } else {
        timeSeriesMulDim = seriesMulMap.get(dsName);
    }
        algtime[algIndex][0] = System.currentTimeMillis();
        nets = new NETS();
        Map<String, Object> netsParams = meta.getDataAlgParam().get(dsName).get(algNames[algIndex]);
        nets.init(netsParams, timeSeriesMulDim);
        nets.run();
        algtime[algIndex][1] = System.currentTimeMillis();
        DataHandler.evaluate(
            timeSeriesMulDim, realAnomalyMulMap.get(dsName), metrics[index][algIndex]);
}

...
   
//Write results
fh.writeResults("acc", "uni-point", vars, algNames, metricNames, totaltime, metrics, 1);
```

Result:

<table>     <tr>         <th rowspan="2">Dataset</th><th colspan="3">NETS</th><th colspan="3">Stare</th><th colspan="3">SHSED</th>      </tr> 
    <tr>        <th>Precison</th><th>Recall</th><th>Fmeasure</th>    <th>Precison</th><th>Recall</th><th>Fmeasure</th> <th>Precison</th><th>Recall</th><th>Fmeasure</th>  </tr> 
    <tr>    <th> Yahoo</th>    <th>0.727</th><th>1</th><th>0.842</th>    <th>0.429</th><th>0.375</th><th>0.400</th> <th>`1</th><th>0.625</th><th>0.769</th>  </tr> 
    <tr>    <th> Twitter</th>    <th>0.739</th><th>0.878</th><th>0.802</th>    <th>0.203</th><th>0.959</th><th>0.335</th> <th>`0.260</th><th>0.176</th><th>0.210</th>  </tr> 
</table>



