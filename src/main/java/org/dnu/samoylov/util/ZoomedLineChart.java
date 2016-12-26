package org.dnu.samoylov.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Comparator;

public class ZoomedLineChart extends LineChart<Number, Number> {

    final Rectangle zoomRect = new Rectangle();

    public ZoomedLineChart() {
        this(createAxis(), createAxis());
    }

    public ZoomedLineChart(Axis<Number> numberAxis, Axis<Number> numberAxis2) {
        this(numberAxis, numberAxis2, FXCollections.observableArrayList());
    }

    public ZoomedLineChart(Axis<Number> numberAxis, Axis<Number> numberAxis2, ObservableList<Series<Number, Number>> data) {
        super(numberAxis, numberAxis2, data);
        init();
    }

    private static NumberAxis createAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        return xAxis;
    }

    public void init() {
        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        this.getChartChildren().add(zoomRect);
        setUpZooming(zoomRect);
    }



    private void setUpZooming(final Rectangle rect) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAnchor.set(new Point2D(event.getX() - ZoomedLineChart.this.getLayoutX(), event.getY() - ZoomedLineChart.this.getLayoutY()));
                mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                rect.setWidth(0);
                rect.setHeight(0);
            }

        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                rect.setX(Math.min(x, mouseAnchor.get().getX()));
                rect.setY(Math.min(y, mouseAnchor.get().getY()));
                rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));

            }
        });

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.MIDDLE) {
                    doZoom();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    resetZoom();
                }
            }
        });
    }



    public void resetZoom() {
        double maxX = this.getData()
                .stream()
                .flatMap(x -> x.getData().stream())
                .max(Comparator.comparing(x -> x.getXValue().doubleValue()))
                .get().getXValue().doubleValue();
        double maxY = this.getData()
                .stream()
                .flatMap(x -> x.getData().stream())
                .max(Comparator.comparing(x -> x.getYValue().doubleValue()))
                .get().getYValue().doubleValue();

        double minX = this.getData()
                .stream()
                .flatMap(x -> x.getData().stream())
                .min(Comparator.comparing(x -> x.getXValue().doubleValue()))
                .get().getXValue().doubleValue();
        double minY = this.getData()
                .stream()
                .flatMap(x -> x.getData().stream())
                .min(Comparator.comparing(x -> x.getYValue().doubleValue()))
                .get().getYValue().doubleValue();

        final NumberAxis xAxis = (NumberAxis)this.getXAxis();
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);

        final NumberAxis yAxis = (NumberAxis)this.getYAxis();
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);

        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }

    private void doZoom() {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());

        final NumberAxis yAxis = (NumberAxis) this.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);

        final NumberAxis xAxis = (NumberAxis) this.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);

        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX() + this.getLayoutX();
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY() + this.getLayoutY();

        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();

        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);

        System.out.println("y= " + yAxis.getLowerBound() + " " + yAxis.getUpperBound());
        System.out.println("x= " + xAxis.getLowerBound() + " " + xAxis.getUpperBound());

        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
}
