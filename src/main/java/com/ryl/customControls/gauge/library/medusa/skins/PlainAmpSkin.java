/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ryl.customControls.gauge.library.medusa.skins;

import com.ryl.customControls.gauge.library.medusa.Fonts;
import com.ryl.customControls.gauge.library.medusa.Gauge;
import com.ryl.customControls.gauge.library.medusa.LcdDesign;
import com.ryl.customControls.gauge.library.medusa.Marker;
import com.ryl.customControls.gauge.library.medusa.Section;
import com.ryl.customControls.gauge.library.medusa.TickLabelOrientation;
import com.ryl.customControls.gauge.library.medusa.tools.Helper;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ryl.customControls.gauge.library.medusa.tools.Helper.enableNode;


public class PlainAmpSkin extends GaugeSkinBase {
    protected static final double                      PREFERRED_WIDTH  = 300;
    protected static final double                      PREFERRED_HEIGHT = 150;
    protected static final double                      MINIMUM_WIDTH    = 30;
    protected static final double                      MINIMUM_HEIGHT   = 15;
    protected static final double                      MAXIMUM_WIDTH    = 1024;
    protected static final double                      MAXIMUM_HEIGHT   = 858;
    private static final   double                      ASPECT_RATIO     = 0.5;
    private static final   double                      START_ANGLE      = 225;
    private static final   double                      ANGLE_RANGE      = 90;
    private                Map<Marker, Shape>          markerMap        = new ConcurrentHashMap<>();
    private                double                      oldValue;
    private                double                      width;
    private                double                      height;
    private                Pane                        pane;
    private                Canvas                      ticksAndSectionsCanvas;
    private                GraphicsContext             ticksAndSections;
    private                Pane                        markerPane;
    private                Path                        threshold;
    private                Path                        average;
    private                double                      ledSize;
    private                InnerShadow                 ledOnShadow;
    private                InnerShadow                 ledOffShadow;
    private                LinearGradient              frameGradient;
    private                LinearGradient              ledOnGradient;
    private                LinearGradient              ledOffGradient;
    private                RadialGradient              highlightGradient;
    private                Canvas                      ledCanvas;
    private                GraphicsContext             led;
    private                Path                        needle;
    private                MoveTo                      needleMoveTo1;
    private                CubicCurveTo                needleCubicCurveTo2;
    private                CubicCurveTo                needleCubicCurveTo3;
    private                CubicCurveTo                needleCubicCurveTo4;
    private                LineTo                      needleLineTo5;
    private                CubicCurveTo                needleCubicCurveTo6;
    private                ClosePath                   needleClosePath7;
    private                Rotate                      needleRotate;
    private                Group                       shadowGroup;
    private                DropShadow                  dropShadow;
    private                Text                        unitText;
    private                Rectangle                   lcd;
    private                Label                       lcdText;
    private                double                      angleStep;
    private                Tooltip                     thresholdTooltip;
    private                String                      formatString;
    private                Locale                      locale;
    private                ListChangeListener<Section> sectionListener;
    private                InvalidationListener        currentValueListener;
    private                InvalidationListener        needleRotateListener;
    private                ListChangeListener<Marker>  markerListener;


    // ******************** Constructors **************************************
    public PlainAmpSkin(Gauge gauge) {
        super(gauge);
        if (gauge.isAutoScale()) gauge.calcAutoScale();
        angleStep            = gauge.getAngleRange() / gauge.getRange();
        oldValue             = gauge.getValue();
        formatString         = new StringBuilder("%.").append(Integer.toString(gauge.getDecimals())).append("f").toString();
        locale               = gauge.getLocale();
        sectionListener      = c -> redraw();
        currentValueListener = o -> rotateNeedle();
        needleRotateListener = o -> handleEvents("ANGLE");
        markerListener       = c -> {
            updateMarkers();
            redraw();
        };

        updateMarkers();

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        // Set initial size
        if (Double.compare(gauge.getPrefWidth(), 0.0) <= 0 || Double.compare(gauge.getPrefHeight(), 0.0) <= 0 ||
            Double.compare(gauge.getWidth(), 0.0) <= 0 || Double.compare(gauge.getHeight(), 0.0) <= 0) {
            if (gauge.getPrefWidth() > 0 && gauge.getPrefHeight() > 0) {
                gauge.setPrefSize(gauge.getPrefWidth(), gauge.getPrefHeight());
            } else {
                gauge.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        ticksAndSectionsCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ticksAndSections       = ticksAndSectionsCanvas.getGraphicsContext2D();

        ledCanvas = new Canvas();
        led       = ledCanvas.getGraphicsContext2D();

        thresholdTooltip = new Tooltip("Threshold\n(" + String.format(locale, formatString, gauge.getThreshold()) + ")");
        thresholdTooltip.setTextAlignment(TextAlignment.CENTER);

        threshold = new Path();
        Helper.enableNode(threshold, gauge.isThresholdVisible());
        Tooltip.install(threshold, thresholdTooltip);

        average = new Path();
        Helper.enableNode(average, gauge.isAverageVisible());

        markerPane = new Pane();

        needleRotate = new Rotate(180 - START_ANGLE);
        needleRotate.setAngle(needleRotate.getAngle() + (gauge.getValue() - oldValue - gauge.getMinValue()) * angleStep);

        needleMoveTo1       = new MoveTo();
        needleCubicCurveTo2 = new CubicCurveTo();
        needleCubicCurveTo3 = new CubicCurveTo();
        needleCubicCurveTo4 = new CubicCurveTo();
        needleLineTo5       = new LineTo();
        needleCubicCurveTo6 = new CubicCurveTo();
        needleClosePath7    = new ClosePath();
        needle              = new Path(needleMoveTo1, needleCubicCurveTo2, needleCubicCurveTo3, needleCubicCurveTo4, needleLineTo5, needleCubicCurveTo6, needleClosePath7);
        needle.setFillRule(FillRule.EVEN_ODD);
        needle.getTransforms().setAll(needleRotate);

        dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        dropShadow.setBlurType(BlurType.TWO_PASS_BOX);
        dropShadow.setRadius(0.015 * PREFERRED_WIDTH);
        dropShadow.setOffsetY(0.015 * PREFERRED_WIDTH);

        shadowGroup = new Group(needle);
        shadowGroup.setEffect(gauge.isShadowsEnabled() ? dropShadow : null);

        unitText = new Text(gauge.getUnit());
        unitText.setMouseTransparent(true);
        unitText.setTextOrigin(VPos.CENTER);

        lcd = new Rectangle(0.3 * PREFERRED_WIDTH, 0.1 * PREFERRED_HEIGHT);
        lcd.setArcWidth(0.0125 * PREFERRED_HEIGHT);
        lcd.setArcHeight(0.0125 * PREFERRED_HEIGHT);
        lcd.relocate((PREFERRED_WIDTH - lcd.getWidth()) * 0.5, 0.66 * PREFERRED_HEIGHT);
        Helper.enableNode(lcd, gauge.isLcdVisible() && gauge.isValueVisible());

        lcdText = new Label(String.format(locale, "%." + gauge.getDecimals() + "f", gauge.getValue()));
        lcdText.setAlignment(Pos.CENTER_RIGHT);
        lcdText.setVisible(gauge.isValueVisible());

        // Set initial value
        angleStep          = ANGLE_RANGE / gauge.getRange();
        double targetAngle = 180 - START_ANGLE + (gauge.getValue() - gauge.getMinValue()) * angleStep;
        targetAngle        = clamp(180 - START_ANGLE, 180 - START_ANGLE + ANGLE_RANGE, targetAngle);
        needleRotate.setAngle(targetAngle);

        // Add all nodes
        pane = new Pane();
        pane.getChildren().setAll(ticksAndSectionsCanvas,
                                  markerPane,
                                  ledCanvas,
                                  unitText,
                                  lcd,
                                  lcdText,
                                  shadowGroup);

        getChildren().setAll(pane);
    }

    @Override protected void registerListeners() {
        super.registerListeners();

        gauge.getMarkers().addListener(markerListener);
        gauge.getSections().addListener(sectionListener);
        gauge.currentValueProperty().addListener(currentValueListener);
        needleRotate.angleProperty().addListener(needleRotateListener);
    }


    // ******************** Methods *******************************************
    @Override
    protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);
        if ("FINISHED".equals(EVENT_TYPE)) {
            if ( gauge.isHighlightSections() ) {
                redraw();
            }
        } else if ("ANGLE".equals(EVENT_TYPE)) {
            double currentValue = (needleRotate.getAngle() + START_ANGLE - 180) / angleStep + gauge.getMinValue();
            lcdText.setText((String.format(locale, formatString, currentValue)));
            if (gauge.isLcdVisible()) {
                lcdText.setAlignment(Pos.CENTER_RIGHT);
                lcdText.setTranslateX((width - lcdText.getPrefWidth()) * 0.5);
            } else {
                lcdText.setAlignment(Pos.CENTER);
                lcdText.setTranslateX((width - lcdText.getLayoutBounds().getWidth()) * 0.5);
            }
        } else if ("VISIBILITY".equals(EVENT_TYPE)) {
            enableNode(ledCanvas, gauge.isLedVisible());
            enableNode(unitText, !gauge.getUnit().isEmpty());
            enableNode(lcd,gauge.isLcdVisible());
            enableNode(lcdText,gauge.isValueVisible());
            enableNode(threshold, gauge.isThresholdVisible());
            enableNode(average, gauge.isAverageVisible());
            boolean markersVisible = gauge.getMarkersVisible();
            for (Shape shape : markerMap.values()) { Helper.enableNode(shape, markersVisible); }
            resize();
            redraw();
        } else if ("LED".equals(EVENT_TYPE)) {
            if (gauge.isLedVisible()) { drawLed(led); }
        } else if ("LCD".equals(EVENT_TYPE)) {
            if (gauge.isLcdVisible()) redraw();
        } else if ("RECALC".equals(EVENT_TYPE)) {
            angleStep = gauge.getAngleStep();
            if (gauge.getValue() < gauge.getMinValue()) {
                oldValue = gauge.getMinValue();
            }
            if (gauge.getValue() > gauge.getMaxValue()) {
                oldValue = gauge.getMaxValue();
            }
            redraw();
            rotateNeedle();
        }
    }


    // ******************** Private Methods ***********************************
    private void rotateNeedle() {
        angleStep          = ANGLE_RANGE / (gauge.getRange());
        double targetAngle = 180 - START_ANGLE + (gauge.getCurrentValue() - gauge.getMinValue()) * angleStep;
        targetAngle        = clamp(180 - START_ANGLE, 180 - START_ANGLE + ANGLE_RANGE, targetAngle);
        needleRotate.setAngle(targetAngle);
        if (gauge.isAverageVisible()) drawAverage();
    }

    private void drawMarkers() {
        markerPane.getChildren().setAll(markerMap.values());
        markerPane.getChildren().addAll(average, threshold);
        double minValue    = gauge.getMinValue();
        double markerSize  = 0.0125 * width;
        double pathHalf    = markerSize * 0.3;
        double scaledWidth = width * 1.106;
        double centerX     = width * 0.5;
        double centerY     = height * 1.4;
        if (gauge.getMarkersVisible()) {
            for (Map.Entry<Marker, Shape> entry : markerMap.entrySet()) {
                Marker marker     = entry.getKey();
                Shape  shape      = entry.getValue();
                double valueAngle = START_ANGLE - (marker.getValue() - minValue) * angleStep;
                double sinValue   = Math.sin(Math.toRadians(valueAngle));
                double cosValue   = Math.cos(Math.toRadians(valueAngle));
                switch (marker.getMarkerType()) {
                    case TRIANGLE:
                        Path triangle = (Path) shape;
                        triangle.getElements().clear();
                        triangle.getElements().add(new MoveTo(centerX + scaledWidth * 0.38 * sinValue, centerY + scaledWidth * 0.38 * cosValue));
                        sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                        cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                        triangle.getElements().add(new LineTo(centerX + scaledWidth * 0.4075 * sinValue, centerY + scaledWidth * 0.4075 * cosValue));
                        sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                        cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                        triangle.getElements().add(new LineTo(centerX + scaledWidth * 0.4075 * sinValue, centerY + scaledWidth * 0.4075 * cosValue));
                        triangle.getElements().add(new ClosePath());
                        break;
                    case DOT:
                        Circle dot = (Circle) shape;
                        dot.setRadius(markerSize);
                        dot.setCenterX(centerX + scaledWidth * 0.3945 * sinValue);
                        dot.setCenterY(centerY + scaledWidth * 0.3945 * cosValue);
                        break;
                    case STANDARD:
                    default:
                        Path standard = (Path) shape;
                        standard.getElements().clear();
                        standard.getElements().add(new MoveTo(centerX + scaledWidth * 0.38 * sinValue, centerY + scaledWidth * 0.38 * cosValue));
                        sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                        cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                        standard.getElements().add(new LineTo(centerX + scaledWidth * 0.4075 * sinValue, centerY + scaledWidth * 0.4075 * cosValue));
                        standard.getElements().add(new LineTo(centerX + scaledWidth * 0.43   * sinValue, centerY + scaledWidth * 0.43   * cosValue));
                        sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                        cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                        standard.getElements().add(new LineTo(centerX + scaledWidth * 0.43   * sinValue, centerY + scaledWidth * 0.43   * cosValue));
                        standard.getElements().add(new LineTo(centerX + scaledWidth * 0.4075 * sinValue, centerY + scaledWidth * 0.4075 * cosValue));
                        standard.getElements().add(new ClosePath());
                        break;
                }
                Color markerColor = marker.getColor();
                shape.setFill(markerColor);
                shape.setStroke(markerColor.darker());
                shape.setPickOnBounds(false);
                Tooltip markerTooltip;
                if (marker.getText().isEmpty()) {
                    markerTooltip = new Tooltip(Double.toString(marker.getValue()));
                } else {
                    markerTooltip = new Tooltip(new StringBuilder(marker.getText()).append("\n(").append(Double.toString(marker.getValue())).append(")").toString());
                }
                markerTooltip.setTextAlignment(TextAlignment.CENTER);
                Tooltip.install(shape, markerTooltip);
                shape.setOnMousePressed(e -> marker.fireMarkerEvent(marker.MARKER_PRESSED_EVENT));
                shape.setOnMouseReleased(e -> marker.fireMarkerEvent(marker.MARKER_RELEASED_EVENT));
            }
        }

        if (gauge.isThresholdVisible()) {
            // Draw threshold
            threshold.getElements().clear();
            double thresholdAngle = START_ANGLE - (gauge.getThreshold() - minValue) * angleStep;
            double thresholdSize  = Helper.clamp(2.0, 2.5, 0.01 * scaledWidth);
            double sinValue       = Math.sin(Math.toRadians(thresholdAngle));
            double cosValue       = Math.cos(Math.toRadians(thresholdAngle));
            threshold.getElements().add(new MoveTo(centerX + scaledWidth * 0.38 * sinValue, centerY + scaledWidth * 0.38 * cosValue));
            sinValue = Math.sin(Math.toRadians(thresholdAngle - thresholdSize));
            cosValue = Math.cos(Math.toRadians(thresholdAngle - thresholdSize));
            threshold.getElements().add(new LineTo(centerX + scaledWidth * 0.35 * sinValue, centerY + scaledWidth * 0.35 * cosValue));
            sinValue = Math.sin(Math.toRadians(thresholdAngle + thresholdSize));
            cosValue = Math.cos(Math.toRadians(thresholdAngle + thresholdSize));
            threshold.getElements().add(new LineTo(centerX + scaledWidth * 0.35 * sinValue, centerY + scaledWidth * 0.35 * cosValue));
            threshold.getElements().add(new ClosePath());
            threshold.setFill(gauge.getThresholdColor());
            threshold.setStroke(gauge.getTickMarkColor());
        }
    }

    private void drawAverage() {
        double scaledWidth = width * 1.106;
        double centerX     = width * 0.5;
        double centerY     = height * 1.4;
        double minValue    = gauge.getMinValue();
        // Draw average
        average.getElements().clear();
        double averageAngle = START_ANGLE - (gauge.getAverage() - minValue) * angleStep;
        double averageSize  = Helper.clamp(2.0, 2.5, 0.01 * scaledWidth);
        double sinValue     = Math.sin(Math.toRadians(averageAngle));
        double cosValue     = Math.cos(Math.toRadians(averageAngle));
        average.getElements().add(new MoveTo(centerX + scaledWidth * 0.38 * sinValue, centerY + scaledWidth * 0.38 * cosValue));
        sinValue = Math.sin(Math.toRadians(averageAngle - averageSize));
        cosValue = Math.cos(Math.toRadians(averageAngle - averageSize));
        average.getElements().add(new LineTo(centerX + scaledWidth * 0.35 * sinValue, centerY + scaledWidth * 0.35 * cosValue));
        sinValue = Math.sin(Math.toRadians(averageAngle + averageSize));
        cosValue = Math.cos(Math.toRadians(averageAngle + averageSize));
        average.getElements().add(new LineTo(centerX + scaledWidth * 0.35 * sinValue, centerY + scaledWidth * 0.35 * cosValue));
        average.getElements().add(new ClosePath());
        average.setFill(gauge.getAverageColor());
        average.setStroke(gauge.getTickMarkColor());
    }

    private void updateMarkers() {
        markerMap.clear();
        for (Marker marker : gauge.getMarkers()) {
            switch(marker.getMarkerType()) {
                case TRIANGLE: markerMap.put(marker, new Path()); break;
                case DOT     : markerMap.put(marker, new Circle()); break;
                case STANDARD:
                default:       markerMap.put(marker, new Path()); break;
            }
        }
    }

    private void drawTickMarks(final GraphicsContext CTX) {
        double  sinValue;
        double  cosValue;
        double  orthText            = TickLabelOrientation.ORTHOGONAL == gauge.getTickLabelOrientation() ? 0.61 : 0.62;
        Point2D center              = new Point2D(width * 0.5, height * 1.4);
        double  minorTickSpace      = gauge.getMinorTickSpace();
        double  minValue            = gauge.getMinValue();
        //double     maxValue         = gauge.getMaxValue();
        double     tmpAngleStep     = angleStep * minorTickSpace;
        int        decimals         = gauge.getTickLabelDecimals();
        BigDecimal minorTickSpaceBD = BigDecimal.valueOf(minorTickSpace);
        BigDecimal majorTickSpaceBD = BigDecimal.valueOf(gauge.getMajorTickSpace());
        BigDecimal mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpace);
        BigDecimal mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpace);
        BigDecimal counterBD        = BigDecimal.valueOf(minValue);
        double     counter          = minValue;

        Font tickLabelFont = Fonts.robotoCondensedRegular((decimals == 0 ? 0.07 : 0.068) * height);
        CTX.setFont(tickLabelFont);

        for (double angle = 0 ; Double.compare(-ANGLE_RANGE - tmpAngleStep, angle) < 0 ; angle -= tmpAngleStep) {
            sinValue = Math.sin(Math.toRadians(angle + START_ANGLE));
            cosValue = Math.cos(Math.toRadians(angle + START_ANGLE));

            Point2D innerPoint       = new Point2D(center.getX() + width * 0.51987097 * sinValue, center.getY() + width * 0.51987097 * cosValue);
            Point2D outerMinorPoint  = new Point2D(center.getX() + width * 0.55387097 * sinValue, center.getY() + width * 0.55387097 * cosValue);
            Point2D outerMediumPoint = new Point2D(center.getX() + width * 0.56387097 * sinValue, center.getY() + width * 0.56387097 * cosValue);
            Point2D outerPoint       = new Point2D(center.getX() + width * 0.58387097 * sinValue, center.getY() + width * 0.58387097 * cosValue);
            Point2D textPoint        = new Point2D(center.getX() + width * orthText * sinValue, center.getY() + width * orthText * cosValue);

            CTX.setStroke(gauge.getTickMarkColor());
            if (Double.compare(counterBD.remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw major tickmark
                CTX.setLineWidth(height * 0.0055);
                CTX.strokeLine(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());

                // Draw text
                CTX.save();
                CTX.translate(textPoint.getX(), textPoint.getY());
                switch(gauge.getTickLabelOrientation()) {
                    case ORTHOGONAL:
                        if ((360 - START_ANGLE - angle) % 360 > 90 && (360 - START_ANGLE - angle) % 360 < 270) {
                            CTX.rotate((180 - START_ANGLE - angle) % 360);
                        } else {
                            CTX.rotate((360 - START_ANGLE - angle) % 360);
                        }
                        break;
                    case TANGENT:
                        if ((360 - START_ANGLE - angle - 90) % 360 > 90 && (360 - START_ANGLE - angle - 90) % 360 < 270) {
                            CTX.rotate((90 - START_ANGLE - angle) % 360);
                        } else {
                            CTX.rotate((270 - START_ANGLE - angle) % 360);
                        }
                        break;
                    case HORIZONTAL:
                    default:
                        break;
                }
                CTX.setTextAlign(TextAlignment.CENTER);
                CTX.setTextBaseline(VPos.CENTER);
                CTX.setFill(gauge.getTickLabelColor());
                CTX.fillText(String.format(locale, "%." + decimals + "f", counter), 0, 0);
                CTX.restore();
            } else if (gauge.getMediumTickMarksVisible() &&
                       Double.compare(minorTickSpaceBD.remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                       Double.compare(counterBD.remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                CTX.setLineWidth(height * 0.0035);
                CTX.strokeLine(innerPoint.getX(), innerPoint.getY(), outerMediumPoint.getX(), outerMediumPoint.getY());
            } else if (gauge.getMinorTickMarksVisible() && Double.compare(counterBD.remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                CTX.setLineWidth(height * 0.00225);
                CTX.strokeLine(innerPoint.getX(), innerPoint.getY(), outerMinorPoint.getX(), outerMinorPoint.getY());
            }
            counterBD = counterBD.add(minorTickSpaceBD);
            counter   = counterBD.doubleValue();
        }
    }

    private void drawSections(final GraphicsContext CTX) {
        final double                  x                 = -width * 0.03;
        final double                  y                 = height * 0.345;
        final double                  w                 = width * 1.06;
        final double                  h                 = height * 2.085;
        final double                  MIN_VALUE         = gauge.getMinValue();
        final double                  MAX_VALUE         = gauge.getMaxValue();
        final double                  OFFSET            = 90 - START_ANGLE;
        final ObservableList<Section> sections          = gauge.getSections();
        final boolean                 highlightSections = gauge.isHighlightSections();

        double value    = gauge.getCurrentValue();
        int    listSize = sections.size();
        for (int i = 0 ; i < listSize ; i++) {
            final Section SECTION = sections.get(i);
            final double  SECTION_START_ANGLE;
            if (Double.compare(SECTION.getStart(), MAX_VALUE) <= 0 && Double.compare(SECTION.getStop(), MIN_VALUE) >= 0) {
                if (SECTION.getStart() < MIN_VALUE && SECTION.getStop() < MAX_VALUE) {
                    SECTION_START_ANGLE = 0;
                } else {
                    SECTION_START_ANGLE = (SECTION.getStart() - MIN_VALUE) * angleStep;
                }
                final double SECTION_ANGLE_EXTEND;
                if (SECTION.getStop() > MAX_VALUE) {
                    SECTION_ANGLE_EXTEND = (MAX_VALUE - SECTION.getStart()) * angleStep;
                } else {
                    SECTION_ANGLE_EXTEND = (SECTION.getStop() - SECTION.getStart()) * angleStep;
                }

                CTX.save();
                if (highlightSections) {
                    CTX.setStroke(SECTION.contains(value) ? SECTION.getHighlightColor() : SECTION.getColor());
                } else {
                    CTX.setStroke(SECTION.getColor());
                }
                CTX.setLineWidth(height * 0.0415);
                CTX.setLineCap(StrokeLineCap.BUTT);
                CTX.strokeArc(x, y, w, h, -(OFFSET + SECTION_START_ANGLE), -SECTION_ANGLE_EXTEND, ArcType.OPEN);
                CTX.restore();
            }
        }
    }

    private void drawLed(final GraphicsContext CTX) {
        CTX.clearRect(0, 0, ledSize, ledSize);
        CTX.setFill(frameGradient);
        CTX.fillOval(0, 0, ledSize, ledSize);

        CTX.save();
        if (gauge.isLedOn()) {
            CTX.setEffect(ledOnShadow);
            CTX.setFill(ledOnGradient);
        } else {
            CTX.setEffect(ledOffShadow);
            CTX.setFill(ledOffGradient);
        }
        CTX.fillOval(0.14 * ledSize, 0.14 * ledSize, 0.72 * ledSize, 0.72 * ledSize);
        CTX.restore();

        CTX.setFill(highlightGradient);
        CTX.fillOval(0.21 * ledSize, 0.21 * ledSize, 0.58 * ledSize, 0.58 * ledSize);
    }

    private double clamp(final double MIN_VALUE, final double MAX_VALUE, final double VALUE) {
        if (VALUE < MIN_VALUE) return MIN_VALUE;
        if (VALUE > MAX_VALUE) return MAX_VALUE;
        return VALUE;
    }

    private void resizeStaticText() {
        double maxWidth = width * 0.3;
        double fontSize = height * 0.15;

        unitText.setFont(Fonts.robotoMedium(fontSize));
        unitText.setText(gauge.getUnit());
        if ( unitText.getLayoutBounds().getWidth() > maxWidth ) {
            Helper.adjustTextSize(unitText, maxWidth, fontSize);
        }
        unitText.setTranslateX((width - unitText.getLayoutBounds().getWidth()) * 0.5);
        unitText.setTranslateY(height * 0.55);
    }

    private void resizeText() {
        resizeStaticText();

        if (gauge.isLcdVisible()) {

            lcdText.setPadding(new Insets(0, 0.005 * width, 0, 0.005 * width));

            switch(gauge.getLcdFont()) {
                case LCD:
                    lcdText.setFont(Fonts.digital(0.216 * height));
                    lcdText.setTranslateY(0.715 * height);
                    break;
                case DIGITAL:
                    lcdText.setFont(Fonts.digitalReadout(0.210 * height));
                    lcdText.setTranslateY(0.7 * height);
                    break;
                case DIGITAL_BOLD:
                    lcdText.setFont(Fonts.digitalReadoutBold(0.210 * height));
                    lcdText.setTranslateY(0.695 * height);
                    break;
                case ELEKTRA:
                    lcdText.setFont(Fonts.elektra(0.2232 * height));
                    lcdText.setTranslateY(0.685 * height);
                    break;
                case STANDARD:
                default:
                    lcdText.setFont(Fonts.robotoMedium(0.18 * height));
                    lcdText.setTranslateY(0.675 * height);
                    break;
            }
            lcdText.setAlignment(Pos.CENTER_RIGHT);
            lcdText.setPrefSize(0.3 * width, 0.014 * height);
            lcdText.setTranslateX((width - lcdText.getPrefWidth()) * 0.5);

        } else {
            lcdText.setAlignment(Pos.CENTER);
            lcdText.setFont(Fonts.robotoMedium(height * 0.1));
            lcdText.setPrefSize(0.3 * width, 0.014 * height);
            lcdText.setTranslateY(0.59 * height);
            lcdText.setTranslateX((width - lcdText.getLayoutBounds().getWidth()) * 0.5);
        }
    }

    @Override public void dispose() {
        gauge.getMarkers().removeListener(markerListener);
        gauge.getSections().removeListener(sectionListener);
        gauge.currentValueProperty().removeListener(currentValueListener);
        needleRotate.angleProperty().removeListener(needleRotateListener);
        super.dispose();
    }

    @Override protected void resize() {
        width  = gauge.getWidth() - gauge.getInsets().getLeft() - gauge.getInsets().getRight();
        height = gauge.getHeight() - gauge.getInsets().getTop() - gauge.getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.relocate((gauge.getWidth() - width) * 0.5, (gauge.getHeight() - height) * 0.5);

            dropShadow.setRadius(0.01 * height);
            dropShadow.setOffsetY(0.01 * height);

            ticksAndSectionsCanvas.setWidth(width);
            ticksAndSectionsCanvas.setHeight(height);

            markerPane.setPrefSize(width, width);

            lcd.setWidth(0.3 * width);
            lcd.setHeight(0.2 * height);
            lcd.setArcWidth(0.0125 * height);
            lcd.setArcHeight(0.0125 * height);
            lcd.relocate((width - lcd.getWidth()) * 0.5, 0.7 * height);

            ledSize = 0.1 * height;
            final Color LED_COLOR = gauge.getLedColor();
            frameGradient = new LinearGradient(0.14 * ledSize, 0.14 * ledSize,
                                               0.84 * ledSize, 0.84 * ledSize,
                                               false, CycleMethod.NO_CYCLE,
                                               new Stop(0.0, Color.rgb(20, 20, 20, 0.65)),
                                               new Stop(0.15, Color.rgb(20, 20, 20, 0.65)),
                                               new Stop(0.26, Color.rgb(41, 41, 41, 0.65)),
                                               new Stop(0.26, Color.rgb(41, 41, 41, 0.64)),
                                               new Stop(0.85, Color.rgb(200, 200, 200, 0.41)),
                                               new Stop(1.0, Color.rgb(200, 200, 200, 0.35)));

            ledOnGradient = new LinearGradient(0.25 * ledSize, 0.25 * ledSize,
                                               0.74 * ledSize, 0.74 * ledSize,
                                               false, CycleMethod.NO_CYCLE,
                                               new Stop(0.0, LED_COLOR.deriveColor(0.0, 1.0, 1.2, 1.0)),
                                               new Stop(0.49, LED_COLOR.deriveColor(0.0, 1.0, 0.5, 1.0)),
                                               new Stop(1.0, LED_COLOR));

            ledOffGradient = new LinearGradient(0.25 * ledSize, 0.25 * ledSize,
                                                0.74 * ledSize, 0.74 * ledSize,
                                                false, CycleMethod.NO_CYCLE,
                                                new Stop(0.0, LED_COLOR.deriveColor(0.0, 1.0, 0.20, 1.0)),
                                                new Stop(0.49, LED_COLOR.deriveColor(0.0, 1.0, 0.13, 1.0)),
                                                new Stop(1.0, LED_COLOR.deriveColor(0.0, 1.0, 0.2, 1.0)));

            highlightGradient = new RadialGradient(0, 0,
                                                   0.3 * ledSize, 0.3 * ledSize,
                                                   0.29 * ledSize,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.WHITE),
                                                   new Stop(1.0, Color.TRANSPARENT));

            ledCanvas.setWidth(ledSize);
            ledCanvas.setHeight(ledSize);
            ledCanvas.relocate(0.05 * width, 0.10 * height);
            ledOffShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * ledSize, 0, 0, 0);
            ledOnShadow  = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * ledSize, 0, 0, 0);
            ledOnShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, gauge.getLedColor(), 0.36 * ledSize, 0, 0, 0));

            double needleWidth  = height * 0.02;
            double needleHeight = height * 1.175;

            needle.setCache(false);

            needleMoveTo1.setX(0.25 * needleWidth); needleMoveTo1.setY(0.025423728813559324 * needleHeight);

            needleCubicCurveTo2.setControlX1(0.25 * needleWidth); needleCubicCurveTo2.setControlY1(0.00847457627118644 * needleHeight);
            needleCubicCurveTo2.setControlX2(0.375 * needleWidth); needleCubicCurveTo2.setControlY2(0);
            needleCubicCurveTo2.setX(0.5 * needleWidth); needleCubicCurveTo2.setY(0);

            needleCubicCurveTo3.setControlX1(0.625 * needleWidth); needleCubicCurveTo3.setControlY1(0);
            needleCubicCurveTo3.setControlX2(0.75 * needleWidth); needleCubicCurveTo3.setControlY2(0.00847457627118644 * needleHeight);
            needleCubicCurveTo3.setX(0.75 * needleWidth); needleCubicCurveTo3.setY(0.025423728813559324 * needleHeight);

            needleCubicCurveTo4.setControlX1(0.75 * needleWidth); needleCubicCurveTo4.setControlY1(0.025423728813559324 * needleHeight);
            needleCubicCurveTo4.setControlX2(needleWidth); needleCubicCurveTo4.setControlY2(needleHeight);
            needleCubicCurveTo4.setX(needleWidth); needleCubicCurveTo4.setY(needleHeight);

            needleLineTo5.setX(0); needleLineTo5.setY(needleHeight);

            needleCubicCurveTo6.setControlX1(0); needleCubicCurveTo6.setControlY1(needleHeight);
            needleCubicCurveTo6.setControlX2(0.25 * needleWidth); needleCubicCurveTo6.setControlY2(0.025423728813559324 * needleHeight);
            needleCubicCurveTo6.setX(0.25 * needleWidth); needleCubicCurveTo6.setY(0.025423728813559324 * needleHeight);

            needle.setCache(true);
            needle.setCacheHint(CacheHint.ROTATE);

            LinearGradient needleGradient = new LinearGradient(needle.getLayoutBounds().getMinX(), 0,
                                                               needle.getLayoutBounds().getMaxX(), 0,
                                                               false, CycleMethod.NO_CYCLE,
                                                               new Stop(0.0, gauge.getNeedleColor()),
                                                               new Stop(0.5, gauge.getNeedleColor()),
                                                               new Stop(0.5, gauge.getNeedleColor().brighter().brighter()),
                                                               new Stop(1.0, gauge.getNeedleColor().brighter().brighter()));

            needle.setFill(needleGradient);
            needle.setStrokeWidth(0);
            needle.setStroke(Color.TRANSPARENT);

            needle.relocate((width - needle.getLayoutBounds().getWidth()) * 0.5, height * 1.4 - needle.getLayoutBounds().getHeight());
            needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
            needleRotate.setPivotY(needle.getLayoutBounds().getHeight());

            shadowGroup.setClip(new Rectangle(width, height));

            resizeText();
        }
    }

    @Override protected void redraw() {
        locale       = gauge.getLocale();
        formatString = new StringBuilder("%.").append(Integer.toString(gauge.getDecimals())).append("f").toString();

        Color backgroundColor = gauge.getBackgroundPaint() instanceof Color ? (Color) gauge.getBackgroundPaint() : Color.WHITE;
        ticksAndSectionsCanvas.setCache(false);
        ticksAndSections.clearRect(0, 0, width, height);
        ticksAndSections.setFill(backgroundColor);

        ticksAndSections.fillRect(0, 0, width, height);
        if (gauge.getSectionsVisible()) drawSections(ticksAndSections);
        drawTickMarks(ticksAndSections);
        ticksAndSectionsCanvas.setCache(true);
        ticksAndSectionsCanvas.setCacheHint(CacheHint.QUALITY);

        unitText.setFill(gauge.getUnitColor());
        unitText.setText(gauge.getUnit());

        if (gauge.isLcdVisible()) {
            LcdDesign lcdDesign = gauge.getLcdDesign();
            Color[]   lcdColors = lcdDesign.getColors();
            LinearGradient lcdGradient = new LinearGradient(0, 1, 0, lcd.getHeight() - 1,
                                                            false, CycleMethod.NO_CYCLE,
                                                            new Stop(0, lcdColors[0]),
                                                            new Stop(0.03, lcdColors[1]),
                                                            new Stop(0.5, lcdColors[2]),
                                                            new Stop(0.5, lcdColors[3]),
                                                            new Stop(1.0, lcdColors[4]));
            Paint lcdFramePaint;
            if (lcdDesign.name().startsWith("FLAT")) {
                lcdFramePaint = Color.WHITE;
            } else {
                lcdFramePaint = new LinearGradient(0, 0, 0, lcd.getHeight(),
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(26, 26, 26)),
                                                   new Stop(0.01, Color.rgb(77, 77, 77)),
                                                   new Stop(0.99, Color.rgb(77, 77, 77)),
                                                   new Stop(1.0, Color.rgb(221, 221, 221)));
            }
            lcd.setFill(lcdGradient);
            lcd.setStroke(lcdFramePaint);

            lcdText.setTextFill(lcdColors[5]);
        }

        if (gauge.isLedVisible()) drawLed(led);

        shadowGroup.setEffect(gauge.isShadowsEnabled() ? dropShadow : null);

        // Markers
        drawMarkers();
        thresholdTooltip.setText("Threshold\n(" + String.format(locale, formatString, gauge.getThreshold()) + ")");
    }
}
