package org.dnu.samoylov.controller.sub.lab3;


import java.util.ArrayList;
import java.util.List;

public class LinearAdaptiveModel {
    List<Double> dataSet;
    double beta1, beta2, beta3;
    int k;
    public int l;
    int t;
    public List<Double> a1 = new ArrayList<>();
    public List<Double> a2 = new ArrayList<>();
    public List<Double> g = new ArrayList<>();
    public List<Double> U = new ArrayList<>();
    public List<Double> e = new ArrayList<>();

    public LinearAdaptiveModel(List<Double> dataSet, double beta1, double beta2, double beta3, int k, int l, int t) {
        this.dataSet = dataSet;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.beta3 = beta3;
        this.k = k;
        this.l = l;
        this.t = t;
    }

    private double CalculateAvaregeOfSelection() {
        double sum = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            sum += dataSet.get(i);
        }
        sum /= dataSet.size();
        return sum;
    }

    private void InitializeA1A2(int k) {
        if (k == 1) {
            a1.add(dataSet.get(0));
            a2.add(dataSet.get(1) - dataSet.get(0));
        }
        if (k > 1) {
            for (int j = 0; j < k; j++) {
                a1.add(0d);
                a2.add(0d);
                for (int i = 0; i <= j; i++) {
                    a1.set(j, a1.get(j) + dataSet.get(i));
                }
                a1.set(j, a1.get(j) / (j + 1));
                int index = j;
                if (j == 0) {
                    index = 1;
                }
                a2.set(j, (dataSet.get(j) - dataSet.get(0)) / (index));
            }
        }
    }

    public void CalculateModel() {
        double aver = CalculateAvaregeOfSelection();
        for (int i = 0; i < l; i++) {
            g.add(dataSet.get(i) - aver);
        }
        for (int i = 0; i < l; i++) {
            U.add(dataSet.get(i));
            e.add(0d);
        }
        InitializeA1A2(l);
        for (int i = l; i < dataSet.size(); i++) {
            a1.add(beta1 * (dataSet.get(i) - g.get(i - l)) + (1 - beta1) * (a1.get(i - 1) - a2.get(i - 1)));
            a2.add(beta2 * (a1.get(i) - a1.get(i - 1)) + (1 - beta2) * a2.get(i - 1));
            g.add(beta3 * (dataSet.get(i) - a1.get(i)) + (1 - beta3) * g.get(i - l));
            U.add(a1.get(i) + a2.get(i) + g.get(i - l));
            e.add(dataSet.get(i) - U.get(i));
        }
        int count = g.size();
        for (int i = 0; i < t; i++) {
            g.add(g.get(count - l + i % l));
            U.add(BoxDjenkinsModel.last(a1) + BoxDjenkinsModel.last(a2) * (i + 1) + g.get((count + i) - l));
        }

    }
}