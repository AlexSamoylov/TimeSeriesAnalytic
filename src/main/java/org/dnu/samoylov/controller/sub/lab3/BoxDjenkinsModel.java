package org.dnu.samoylov.controller.sub.lab3;

import java.util.ArrayList;
import java.util.List;

public class BoxDjenkinsModel {
    List<Double> dataSet;
    double beta1, beta2, beta3;
    int k;
    int t;
    public List<Double> a1 = new ArrayList<>();
    public List<Double> a2 = new ArrayList<>();
    public List<Double> U = new ArrayList<>();
    public List<Double> e = new ArrayList<>();

    public BoxDjenkinsModel(List<Double> dataSet, double beta1, double beta2, double beta3, int k, int t) {
        this.dataSet = dataSet;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.beta3 = beta3;
        this.k = k;
        this.t = t;
    }

    public void CalculateModel() {
        for (int i = 0; i < k - 1; i++) {
            U.add(dataSet.get(i));
            e.add(0d);
            if (i == 0) {
                a2.add(dataSet.get(i + 1) - dataSet.get(i));
            } else {
                a2.add((dataSet.get(i) - dataSet.get(0)) / i);
            }

            double sum = 0;
            for (int j = 0; j < i + 1; j++) {
                sum += dataSet.get(j);
            }
            a1.add(sum / (i + 1));
        }
        for (int i = k - 1; i < dataSet.size(); i++) {
            if (i == k - 1) {
                U.add(dataSet.get(i));
                e.add(0d);
            } else {
                U.add(last(a1) + last(a2));
                e.add(dataSet.get(i) - last(U));
            }

            a1.add(beta1 * dataSet.get(i) + (1 - beta1) * (last(a1) + last(a2)) + beta3 * (e.get(i) - e.get(i - 1)));

            a2.add(beta2 * (a1.get(i) - a1.get(i - 1)) + (1 - beta2) * last(a2));
        }
        for (int i = 0; i < t; i++) {
            U.add(last(a1) + last(a2) * (i + 1));
        }

    }

    public static Double last(List<Double> a1) {
        return a1.get(a1.size()-1);
    }
}