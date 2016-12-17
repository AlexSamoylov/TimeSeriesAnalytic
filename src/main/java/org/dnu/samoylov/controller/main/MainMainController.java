package org.dnu.samoylov.controller.main;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.dnu.samoylov.controller.sub.CorrelogramMethod;
import org.dnu.samoylov.controller.sub.RecordMethod;
import org.dnu.samoylov.controller.sub.Result;
import org.dnu.samoylov.controller.sub.SignMethod;
import org.dnu.samoylov.util.onetimerun.OneTimeRunMethod;
import org.dnu.samoylov.util.onetimerun.RunLambda;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class MainMainController extends AbstractFxmlController implements Initializable {


    public TableView<ExistDto> charTable;

    public TableColumn charNameColumn;
    public TableColumn charStatisticColumn;
    public TableColumn charQuantilColumn;
    public TableColumn charResultColumn;



    @FXML
    private LineChart<Number, Number> empFunctionChart;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empFunctionChart.setCreateSymbols(false);
        empFunctionChart.setAnimated(false);


        charNameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        charStatisticColumn.setCellValueFactory(new PropertyValueFactory("statistic"));
        charQuantilColumn.setCellValueFactory(new PropertyValueFactory("quantil"));
        charResultColumn.setCellValueFactory(new PropertyValueFactory("result"));
    }



    protected void buildGist( List<Double> dataSet) {
        empFunctionChart.getData().clear();
        empFunctionChart.layout();
        addNewLine(dataSet, "початковий");
    }

    protected void addNewLine(List<Double> dataSet, String name) {
        addNewLine(dataSet, name, 0);
    }

    protected void addNewLine(List<Double> dataSet, String name, int startPosition) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < dataSet.size(); i++) {
            series.getData().add( new XYChart.Data<>(i + startPosition, dataSet.get(i)) );
        }

        series.setName(name);
        empFunctionChart.getData().add(series);
    }

    protected void buildTable( List<Double> dataSet) {
        List<ExistDto> characteristics = new ArrayList<>();
        Result signResult = SignMethod.invoke(dataSet);
        List<Result> recordResult = RecordMethod.invoke(dataSet);


        characteristics.add(toDto("Метод знаків", signResult));
        characteristics.add(toDto("Метод рекордних значень", recordResult.get(0)));
        characteristics.add(toDto("Метод рекордних значень", recordResult.get(1)));

        charTable.setItems(FXCollections.observableList(characteristics));
    }

    protected ExistDto toDto(String name, Result signResult) {
        return new ExistDto(name, signResult.getValue(),
                signResult.getQantil(), signResult.getResult());
    }



    public static class ExistDto {
        private String name;
        private Float statistic;
        private Float quantil;
        private String result;

        public ExistDto() {
        }

        public ExistDto(String name, Float statistic, Float quantil, String result) {
            this.name = name;
            this.statistic = statistic;
            this.quantil = quantil;
            this.result = result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Float getStatistic() {
            return statistic;
        }

        public void setStatistic(Float statistic) {
            this.statistic = statistic;
        }

        public Float getQuantil() {
            return quantil;
        }

        public void setQuantil(Float quantil) {
            this.quantil = quantil;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
