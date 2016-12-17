package org.dnu.samoylov.service.estimate;

import java.util.List;

public class MeanCalculator {

    public static float calculateMeanArithmetic(List<Float> data) {
        return (float)Math.sqrt(EstimateCalculator.calculateMeanDeviation(data))/(float)Math.sqrt(data.size());
    }

    public static float calculateMeanOfMeanDeviation(List<Float> data) {
        return (float)Math.sqrt(EstimateCalculator.calculateMeanDeviation(data))/(float)(Math.sqrt(2*data.size()));
    }

    public static float calculateMeanAsimCoef(List<Float> data) {
        int N = data.size();
        return (float)Math.sqrt( (6.0f/N) * (1 - (12.0f/(2.0f*N + 7))));
    }

    public static float calculateMeanExcessCoef(List<Float> data) {
        int N = data.size();
        return (float)Math.sqrt( (24.0f/N) * (1 - (225.0f/(15*N + 124))) );
    }

    public static float calculateMeanContrExcessCoef(List<Float> data) {
        float E_ = EstimateCalculator.calculateExcessCoef_(data);
        int N = data.size();
        return (float)Math.sqrt(Math.abs(E_)/(29*N)) * nthRootCalc((float)Math.pow(Math.abs(Math.pow(E_, 2) - 1), 3), 4);
    }

    public static float calculateMeanVariationCoef(List<Float> data) {
        float W = EstimateCalculator.calculateVariationCoef(data);
        return (float) (W * Math.sqrt((1 + 2 * Math.pow(W, 2)) / (2*data.size())));
    }

    private static float nthRootCalc(float num, int root) {
        return (float)Math.pow(num, 1.0/(float)root);
    }
}
