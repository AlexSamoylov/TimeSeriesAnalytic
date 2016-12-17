package org.dnu.samoylov.service.estimate;


import java.util.Collections;
import java.util.List;

public class EstimateCalculator {

    public static float calculateArithmeticMean(List<Float> data) {
        float arithmeticMean = 0;
        for(Float f: data) {
            arithmeticMean += f;
        }
        return arithmeticMean / data.size();
    }

    public static float calculateMedian( List<Float> data) {
        Collections.sort(data);
        if(data.size() % 2 == 0) {
            return (data.get(data.size()/2) + data.get(data.size()/2+1)) / 2;
        } else {
            return (data.get(data.size()/2));
        }
    }

    public static float calculateMeanDeviation(List<Float> data) {
        float deviationMean = 0;
        float arithmeticMean = calculateArithmeticMean(data);
        for(Float f: data) {
            deviationMean += Math.pow(f - arithmeticMean, 2);
        }
        return deviationMean / (data.size() -1) ;
    }

    public static float calculateMeanDeviation_(List<Float> data) {
        int N = data.size();
        float sum = 0;
        float x_ = calculateArithmeticMean(data);
        for(float f : data) {
            sum += Math.pow(f - x_, 2);
        }
        return (float)(1.0/N) * sum;
    }


    public static float calculateAsimCooef_(List<Float> data) {
        int N = data.size();
        float sum = 0;
        float x_ = calculateArithmeticMean(data);
        for(float f : data) {
            sum += Math.pow(f - x_, 3);
        }
        float A_ = (float) (1 / (N * Math.pow(Math.sqrt(calculateMeanDeviation_(data)), 3))) * sum;
        return A_;
    }

    public static float calculateAsimCooef(List<Float> data) {
        int N = data.size();
        float A = (float)((Math.sqrt(N * (N - 1))) / (N - 2)) * calculateAsimCooef_(data);
        return A;
    }

    public static float calculateExcessCoef_(List<Float> data) {
        int N = data.size();
        float sum = 0;
        float x_ = calculateArithmeticMean(data);
        for(float f : data) {
            sum += Math.pow(f - x_, 4);
        }
        float E_ = (float)(1/(N * Math.pow(Math.sqrt(calculateMeanDeviation_(data)), 4))) * sum;
        return E_;
    }

    public static float calculateExcessCoef(List<Float> data) {
        int N = data.size();
        float E = (float)(  (Math.pow(N, 2) - 1) / (((N - 2) * (N - 3))) ) * ((calculateExcessCoef_(data) - 3) + 6.0f/(N + 1) );
        return E;
    }

    public static float calculateContrExcessCoef(List<Float> data) {
        return  1.0f / ((float)Math.sqrt(calculateExcessCoef_(data)));
    }

    public static float calculateVariationCoef(List<Float> data) {
        float x_ = calculateArithmeticMean(data);
        return (float)Math.sqrt(calculateMeanDeviation(data))/x_;
    }












    public static Double calculateArithmeticMeanD(List<Double> data) {
        Double arithmeticMean = 0D;
        for(Double f: data) {
            arithmeticMean += f;
        }
        return arithmeticMean / data.size();
    }

    public static Double calculateMedianD( List<Double> data) {
        Collections.sort(data);
        if(data.size() % 2 == 0) {
            return (data.get(data.size()/2) + data.get(data.size()/2+1)) / 2;
        } else {
            return (data.get(data.size()/2));
        }
    }

    public static Double calculateMeanDeviationD(List<Double> data) {
        Double deviationMean = 0D;
        Double arithmeticMean = calculateArithmeticMeanD(data);
        for(Double f: data) {
            deviationMean += Math.pow(f - arithmeticMean, 2);
        }
        return deviationMean / (data.size() -1) ;
    }

    public static Double calculateMeanDeviation_D(List<Double> data) {
        int N = data.size();
        Double sum = 0D;
        Double x_ = calculateArithmeticMeanD(data);
        for(Double f : data) {
            sum += Math.pow(f - x_, 2);
        }
        return (Double)(1.0/N) * sum;
    }


    public static Double calculateAsimCooef_D(List<Double> data) {
        int N = data.size();
        Double sum = 0D;
        Double x_ = calculateArithmeticMeanD(data);
        for(Double f : data) {
            sum += Math.pow(f - x_, 3);
        }
        Double A_ = (Double) (1 / (N * Math.pow(Math.sqrt(calculateMeanDeviation_D(data)), 3))) * sum;
        return A_;
    }

    public static Double calculateAsimCooefD(List<Double> data) {
        int N = data.size();
        Double A = (Double)((Math.sqrt(N * (N - 1))) / (N - 2)) * calculateAsimCooef_D(data);
        return A;
    }

    public static Double calculateExcessCoef_D(List<Double> data) {
        int N = data.size();
        Double sum = 0D;
        Double x_ = calculateArithmeticMeanD(data);
        for(Double f : data) {
            sum += Math.pow(f - x_, 4);
        }
        Double E_ = (Double)(1/(N * Math.pow(Math.sqrt(calculateMeanDeviation_D(data)), 4))) * sum;
        return E_;
    }

    public static Double calculateExcessCoefD(List<Double> data) {
        int N = data.size();
        Double E = (Math.pow(N, 2) - 1) / (((N - 2) * (N - 3))) * ((calculateExcessCoef_D(data) - 3) + 6.0f/(N + 1) );
        return E;
    }

    public static Double calculateContrExcessCoefD(List<Double> data) {
        return  1.0f / Math.sqrt(calculateExcessCoef_D(data));
    }

    public static Double calculateVariationCoefD(List<Double> data) {
        Double x_ = calculateArithmeticMeanD(data);
        return (Double)Math.sqrt(calculateMeanDeviationD(data))/x_;
    }

}
