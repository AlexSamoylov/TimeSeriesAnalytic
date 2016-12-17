package org.dnu.samoylov.controller.main;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dnu.samoylov.controller.sub.smoothing.ExponentialSmoothing;
import org.dnu.samoylov.controller.sub.smoothing.MedianSmoothing;
import org.dnu.samoylov.service.estimate.QuantilCalculator;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class SmoothXmlController extends MainController implements Initializable {

    public TableView<SmoothingDto> charSmoothingTable;
    public TableColumn smOrigValueColumn;
    public TableColumn smSmoothValue1Column;
    public TableColumn smDeviation1Column;
    public TableColumn smSmoothValue2Column;
    public TableColumn smDeviation2Column;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);


        smOrigValueColumn.setCellValueFactory(new PropertyValueFactory("original"));
        smSmoothValue1Column.setCellValueFactory(new PropertyValueFactory("smooth1"));
        smDeviation1Column.setCellValueFactory(new PropertyValueFactory("deviation1"));
        smSmoothValue2Column.setCellValueFactory(new PropertyValueFactory("smooth2"));
        smDeviation2Column.setCellValueFactory(new PropertyValueFactory("deviation2"));
    }

    @Override
    protected void twerkWithDataSet(List<Double> dataSet) {
        buildGist(dataSet);
        buildTable(dataSet);
        buildCorrelogramChart(dataSet);
        buildSmoothingTable(dataSet);
    }


    protected void buildSmoothingTable( List<Double> dataSet) {
        ExponentialSmoothing expBuilder = new ExponentialSmoothing(dataSet, QuantilCalculator.DEF_ALPHA);
        List<Double> expSmoothingDataSet = expBuilder.getSmoothing();

        MedianSmoothing medianBuilder = new MedianSmoothing(dataSet);
        List<Double> medSmoothingDataSet = medianBuilder.getSmoothing();

        int n = dataSet.size();

        List<SmoothingDto> characteristics = new ArrayList<>();

        double sumDifExp = 0;
        double sumDifMedian = 0;

        for (int i = 0; i < n; i++) {
            Double original = dataSet.get(i);
            Double exp = expSmoothingDataSet.get(i);
            Double median = medSmoothingDataSet.get(i);

            sumDifExp += Math.abs(original - exp);
            sumDifMedian += Math.abs(original - median);

            SmoothingDto dto = new SmoothingDto(original,
                    exp,
                    Math.abs(original - exp),
                    median,
                    Math.abs(original - median)
            );
            characteristics.add(dto);
        }


        characteristics.add(new SmoothingDto("Середня помилка",
                String.valueOf(sumDifExp / n), "1/N*sum(U[t]-U1[t])",
                String.valueOf(sumDifMedian / n), "1/N*sum(U[t]-U2[t])"));


        charSmoothingTable.setItems(FXCollections.observableList(characteristics));


        addNewLine(expSmoothingDataSet, "експоненційне вирівнювання");
        addNewLine(medSmoothingDataSet, "медіанне вирівнювання");
    }

    public static class SmoothingDto {
        private String original;
        private String smooth1;
        private String deviation1;
        private String smooth2;
        private String deviation2;

        public SmoothingDto() {
        }

        public SmoothingDto(String original, String smooth1, String deviation1, String smooth2, String deviation2) {
            this.original = original;
            this.smooth1 = smooth1;
            this.deviation1 = deviation1;
            this.smooth2 = smooth2;
            this.deviation2 = deviation2;
        }

        public SmoothingDto(Double original, Double exp, double abs, Double median, double abs1) {
            this.original = String.valueOf(original);
            this.smooth1 = String.valueOf(exp);
            this.deviation1 = String.valueOf(abs);
            this.smooth2 = String.valueOf(median);
            this.deviation2 = String.valueOf(abs1);
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getSmooth1() {
            return smooth1;
        }

        public void setSmooth1(String smooth1) {
            this.smooth1 = smooth1;
        }

        public String getDeviation1() {
            return deviation1;
        }

        public void setDeviation1(String deviation1) {
            this.deviation1 = deviation1;
        }

        public String getSmooth2() {
            return smooth2;
        }

        public void setSmooth2(String smooth2) {
            this.smooth2 = smooth2;
        }

        public String getDeviation2() {
            return deviation2;
        }

        public void setDeviation2(String deviation2) {
            this.deviation2 = deviation2;
        }
    }

}
