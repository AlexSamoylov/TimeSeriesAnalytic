package org.dnu.samoylov.controller.sub.lab3;


import java.util.ArrayList;
import java.util.List;

public class ExponentialMultiplyModel {
    List<Double> dataSet;
    double beta1, beta2, beta3;
    int k;
    int t;
    public int l;
    public List<Double> a1 = new ArrayList<>();
    public List<Double> r = new ArrayList<>();
    public List<Double> f = new ArrayList<>();
    public List<Double> U = new ArrayList<>();
    public List<Double> e = new ArrayList<>();

    public ExponentialMultiplyModel(List<Double> dataSet, double beta1, double beta2, double beta3, int k, int l, int t) {
        this.dataSet = dataSet;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.beta3 = beta3;
        this.k = k;
        this.t = t;
        this.l = l;
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
        }
        if (k > 1) {
            for (int j = 0; j < k; j++) {
                a1.add(0d);
                for (int i = 0; i <= j; i++) {
                    a1.set(j, a1.get(j) + dataSet.get(i));
                }
                a1.set(j, a1.get(j) / (j + 1));
            }
        }
    }

    public void CalculateModel() {
        double aver = CalculateAvaregeOfSelection();
        for (int i = 0; i < l; i++) {
            f.add(dataSet.get(i) / aver);
        }
        for (int i = 0; i < l; i++) {
            U.add(dataSet.get(i));
            e.add(0d);
        }
        r.add(dataSet.get(1) / dataSet.get(0)); //remove r
        InitializeA1A2(l);
        for (int i = 1; i < a1.size(); i++) {
            r.add(beta2 * a1.get(i) / a1.get(i - 1) + (1 - beta2) * r.get(i - 1));
        }
        for (int i = l; i < dataSet.size(); i++) {
            a1.add(beta1 * dataSet.get(i) / f.get(i - l) + (1 - beta1) * (a1.get(i - 1) * r.get(i - 1)));
            r.add(beta2 * a1.get(i) / a1.get(i - 1) + (1 - beta2) * r.get(i - 1));
            f.add(beta3 * dataSet.get(i) / a1.get(i) + (1 - beta3) * f.get(i - l));
            U.add(a1.get(i) * r.get(i) * f.get(i - l));
            e.add(dataSet.get(i) - U.get(i));
        }
        int count = f.size();
        for (int i = 0; i < t; i++) {
            f.add(f.get(count - l + i % l));
            U.add(BoxDjenkinsModel.last(a1) * Math.pow(BoxDjenkinsModel.last(r), i + 1) * f.get((count + i) - l));

        }

        //U.add((a1.Last() + a2.Last()) * f.get(f.size() - l));
    }
}