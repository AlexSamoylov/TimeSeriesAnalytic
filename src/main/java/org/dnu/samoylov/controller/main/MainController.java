package org.dnu.samoylov.controller.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import org.dnu.samoylov.controller.sub.CorrelogramMethod;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class MainController extends MainMainController implements Initializable {

    @FXML private BarChart<String, Number> classesChart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        classesChart.setAnimated(false);
    }



    public void loadData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/data/"));
        List<Double> dataSet
                = loadDataSet(fileChooser.showOpenDialog(getView().getScene().getWindow()).toPath());
        twerkWithDataSet(dataSet);
    }

    protected void twerkWithDataSet(List<Double> dataSet) {
        buildGist(dataSet);
        buildTable(dataSet);
        buildCorrelogramChart(dataSet);
    }

    private List<Double> loadDataSet(final Path filePath) {
        try {
            List<Double> values;
            try (Stream<String> stream = Files.lines(filePath, Charset.defaultCharset())) {
                values = stream.collect(Collectors.toList()).stream().map(Double::valueOf).collect(Collectors.toList());
            }

            return values;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





    protected void buildCorrelogramChart(List<Double> dataSet) {
        classesChart.getData().clear();
        classesChart.layout();
        final XYChart.Series<String, Number> series = new XYChart.Series<>();

        CorrelogramMethod.Result result = CorrelogramMethod.invoke(dataSet);
        List<Double> rKList = result.rKList;

        for (int i = 0; i < rKList.size(); i++) {
            series.getData().add(
                    new XYChart.Data<>(String.valueOf(i), rKList.get(i)));

        }

        series.setName(result.isExistCorrelation? "кореляція наявна" : "кореляції немає");

        classesChart.getData().add(series);
    }
}
