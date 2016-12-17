package org.dnu.samoylov.controller.sub.smoothing;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MedianSmoothing {
    List<Double> s;
    List<Double> dataSet;

    public MedianSmoothing(List<Double> dataSet) {
        this.dataSet = dataSet;
        this.s = new ArrayList<>();
    }



    public List<Double> getSmoothing() {
        int n = dataSet.size();
        for (int i = 0; i < dataSet.size(); i++) {
            if (i == 0) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i + 1));
                m.add(3 * dataSet.get(i + 1) - 2 * dataSet.get(i + 2));
                s.add(calculateMedian(m));
            }
            if (i == 1) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i - 1));
                m.add(dataSet.get(i + 1));
                m.add(dataSet.get(i + 2));
                m.add(dataSet.get(i + 3));
                s.add(calculateMedian(m));
            }
            if (i == 2) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i - 1));
                m.add(dataSet.get(i - 2));
                m.add(dataSet.get(i + 1));
                m.add(dataSet.get(i + 2));
                m.add(dataSet.get(i + 3));
                s.add(calculateMedian(m));
            }
            if (i == n - 3) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i - 1));
                m.add(dataSet.get(i - 2));
                m.add(dataSet.get(i - 3));
                m.add(dataSet.get(i + 1));
                m.add(dataSet.get(i + 2));
                s.add(calculateMedian(m));
            }
            if (i == n - 2) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i - 1));
                m.add(dataSet.get(i - 2));
                m.add(dataSet.get(i - 3));
                m.add(dataSet.get(i + 1));
                s.add(calculateMedian(m));
            }
            if (i == n - 1) {
                List<Double> m = new ArrayList<>();
                m.add(dataSet.get(i));
                m.add(dataSet.get(i - 1));
                m.add(3 * dataSet.get(i - 1) - 2 * dataSet.get(i - 2));
                s.add(calculateMedian(m));
            }
            if ((i > 2) && (i < n - 3)) {
                s.add(calculateS(i));
            }

        }
        return s;
    }


    /**
     * @param select will be sorted!
     * @return median
     */
    private double calculateMedian(List<Double> select) {
        double m;
        select.sort(Comparator.<Double>naturalOrder());
        if (select.size() % 2 == 0) {
            m = select.get(select.size() / 2);
        } else {
            m = (select.get(select.size() / 2) + select.get(select.size() / 2 + 1)) / 2;
        }

        return m;
    }

    private double calculateS(int k) {
        double s;
        List<Double> m = new ArrayList<>();

        for (int i = k - 3; i <= k + 3; i++) {
            m.add(dataSet.get(i));
        }
        s = calculateMedian(m);

        return s;
    }
}
