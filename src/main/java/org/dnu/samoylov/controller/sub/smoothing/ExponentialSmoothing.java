package org.dnu.samoylov.controller.sub.smoothing;


import java.util.ArrayList;
import java.util.List;

public class ExponentialSmoothing {
    private double alpha;
    private double beta;
    List<Double> s;

    List<Double> dataSet;

    public ExponentialSmoothing(List<Double> dataSet, double alpha) {
        this.alpha = alpha;
        this.beta = 1 - alpha;
        this.dataSet = dataSet;
        this.s = new ArrayList<>();
    }

    public List<Double> getSmoothing() {
        s.clear();
        s.add(calculateAverage(dataSet));
        for (int i = 0; i < dataSet.size(); i++) {
            s.add(calculateS(i));
        }
        return s;
    }


    private double calculateS(int k) {
        return alpha * dataSet.get(k) + beta * s.get(k);
    }

    private double calculateAverage(List<Double> dataSet) {
        double sum = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            sum += dataSet.get(i);
        }
        sum /= dataSet.size();
        return sum;
    }


}
