package com.alextim.SFI.frontend.widget;

import de.gsi.chart.axes.Axis;
import de.gsi.chart.plugins.AbstractSingleValueIndicator;
import de.gsi.chart.plugins.ValueIndicator;
import de.gsi.dataset.event.EventSource;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;

public class MyXValueIndicatorPlugin extends AbstractSingleValueIndicator implements EventSource, ValueIndicator {
    public MyXValueIndicatorPlugin(final Axis axis, final double value, final String text) {
        super(axis, value, text);
        triangle.getPoints().setAll(0.0, 0.0, -8.0, -8.0, 8.0, -8.0);
        setLabelPosition(0.04);

        triangle.setOpacity(0);
        triangle.setOnMouseDragged(null);
        triangle.setOnMouseEntered(null);
        triangle.setOnMouseReleased(null);

        pickLine.setOnMouseDragged(null);
        pickLine.setOnMouseEntered(null);
        pickLine.setOnMouseReleased(null);

        label.setOnMouseDragged(null);
        label.setOnMouseEntered(null);
        label.setOnMouseReleased(null);

        layoutChildren();
    }

    @Override
    public void layoutChildren() {
        if (getChart() == null) {
            return;
        }

        final Bounds plotAreaBounds = getChart().getCanvas().getBoundsInLocal();
        final double minX = plotAreaBounds.getMinX();
        final double maxX = plotAreaBounds.getMaxX();
        final double minY = plotAreaBounds.getMinY();
        final double maxY = plotAreaBounds.getMaxY();
        final double xPos = minX + getChart().getFirstAxis(Orientation.HORIZONTAL).getDisplayPosition(getValue());

        if (xPos < minX || xPos > maxX) {
            getChartChildren().clear();
        } else {
            layoutLine(xPos, minY, xPos, maxY);
            layoutMarker(xPos, minY + 1.5 * AbstractSingleValueIndicator.triangleHalfWidth, xPos, maxY);
            layoutLabel(new BoundingBox(xPos, minY, 0, maxY - minY), AbstractSingleValueIndicator.MIDDLE_POSITION,
                    getLabelPosition());
        }
    }

    @Override
    public void updateStyleClass() {
        setStyleClasses(label, "x-", AbstractSingleValueIndicator.STYLE_CLASS_LABEL);
        setStyleClasses(line, "x-", AbstractSingleValueIndicator.STYLE_CLASS_LINE);
        setStyleClasses(triangle, "x-", AbstractSingleValueIndicator.STYLE_CLASS_MARKER);
    }
}
