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

import com.ryl.customControls.gauge.library.medusa.Needle;
import com.ryl.customControls.gauge.library.medusa.TickLabelLocation;
import com.ryl.customControls.gauge.library.medusa.Fonts;
import com.ryl.customControls.gauge.library.medusa.Gauge;
import com.ryl.customControls.gauge.library.medusa.Gauge.NeedleType;
import com.ryl.customControls.gauge.library.medusa.Gauge.LedType;
import com.ryl.customControls.gauge.library.medusa.Gauge.NeedleBehavior;
import com.ryl.customControls.gauge.library.medusa.Gauge.ScaleDirection;
import com.ryl.customControls.gauge.library.medusa.LcdDesign;
import com.ryl.customControls.gauge.library.medusa.Marker;
import com.ryl.customControls.gauge.library.medusa.Section;
import com.ryl.customControls.gauge.library.medusa.tools.AngleConicalGradient;
import com.ryl.customControls.gauge.library.medusa.tools.Helper;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

import static com.ryl.customControls.gauge.library.medusa.tools.Helper.formatNumber;


/**
 * Created by hansolo on 11.12.15.
 */
public class GaugeSkin extends GaugeSkinBase {
    private Map<Marker, Shape>         markerMap = new ConcurrentHashMap<>();
    private double                     oldValue;
    private double                     size;
    private Pane                       pane;
    private InnerShadow                backgroundInnerShadow;
    private Canvas                     tickMarkCanvas;
    private GraphicsContext            tickMarkCtx;
    private Canvas                     sectionsAndAreasCanvas;
    private GraphicsContext            sectionsAndAreasCtx;
    private double                     ledSize;
    private InnerShadow                ledOnShadow;
    private InnerShadow                ledOffShadow;
    private Paint                      ledFramePaint;
    private Paint                      ledOnPaint;
    private Paint                      ledOffPaint;
    private Paint                      ledHighlightPaint;
    private Canvas                     ledCanvas;
    private GraphicsContext            ledCtx;
    private Pane                       markerPane;
    private Path                       threshold;
    private Path                       average;
    private Rectangle                  lcd;
    private Path                       needle;
    private Rotate                     needleRotate;
    private Paint                      needlePaint;
    private Canvas                     knobCanvas;
    private GraphicsContext            knobCtx;
    private Group                      shadowGroup;
    private DropShadow                 dropShadow;
    private Text                       titleText;
    private Text                       subTitleText;
    private Text                       unitText;
    private Text                       valueText;
    private double                     angleStep;
    private double                     startAngle;
    private double                     angleRange;
    private EventHandler<MouseEvent>   mouseHandler;
    private ListChangeListener<Marker> markerListener;
    private InvalidationListener       currentValueListener;
    private Tooltip                    buttonTooltip;
    private Tooltip                    thresholdTooltip;
    private String                     formatString;
    private Locale                     locale;
    private double                     minValue;
    private double                     maxValue;
    private List<Section>              sections;
    private boolean                    highlightSections;
    private boolean                    sectionsVisible;
    private List<Section>              areas;
    private boolean                    highlightAreas;
    private boolean                    areasVisible;
    private TickLabelLocation          tickLabelLocation;
    private ScaleDirection             scaleDirection;
    private NeedleBehavior             needleBehavior;


    // ******************** Constructors **************************************
    public GaugeSkin(Gauge gauge) {
        super(gauge);
        if (gauge.isAutoScale()) gauge.calcAutoScale();
        startAngle           = gauge.getStartAngle();
        angleRange           = gauge.getAngleRange();
        angleStep            = gauge.getAngleStep();
        oldValue             = gauge.getValue();
        minValue             = gauge.getMinValue();
        maxValue             = gauge.getMaxValue();
        formatString         = new StringBuilder("%.").append(Integer.toString(gauge.getDecimals())).append("f").toString();
        locale               = gauge.getLocale();
        sections             = gauge.getSections();
        highlightSections    = gauge.isHighlightSections();
        sectionsVisible      = gauge.getSectionsVisible();
        areas                = gauge.getAreas();
        highlightAreas       = gauge.isHighlightAreas();
        areasVisible         = gauge.getAreasVisible();
        tickLabelLocation    = gauge.getTickLabelLocation();
        scaleDirection       = gauge.getScaleDirection();
        needleBehavior       = gauge.getNeedleBehavior();
        mouseHandler         = event -> handleMouseEvent(event);
        currentValueListener = o -> rotateNeedle(gauge.getCurrentValue());
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

        backgroundInnerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(10, 10, 10, 0.45), 8, 0.0, 8.0, 0.0);

        sectionsAndAreasCanvas = new Canvas();
        sectionsAndAreasCtx    = sectionsAndAreasCanvas.getGraphicsContext2D();

        tickMarkCanvas = new Canvas();
        tickMarkCtx    = tickMarkCanvas.getGraphicsContext2D();

        ledCanvas = new Canvas();
        ledCtx    = ledCanvas.getGraphicsContext2D();
        Helper.enableNode(ledCanvas, gauge.isLedVisible());

        thresholdTooltip = new Tooltip("Threshold\n(" + String.format(locale, formatString, gauge.getThreshold()) + ")");
        thresholdTooltip.setTextAlignment(TextAlignment.CENTER);

        threshold = new Path();
        Helper.enableNode(threshold, gauge.isThresholdVisible());
        Tooltip.install(threshold, thresholdTooltip);

        average = new Path();
        Helper.enableNode(average, gauge.isAverageVisible());

        markerPane = new Pane();

        lcd = new Rectangle(0.3 * PREFERRED_WIDTH, 0.014 * PREFERRED_HEIGHT);
        lcd.setArcWidth(0.0125 * PREFERRED_HEIGHT);
        lcd.setArcHeight(0.0125 * PREFERRED_HEIGHT);
        lcd.relocate((PREFERRED_WIDTH - lcd.getWidth()) * 0.5, 0.44 * PREFERRED_HEIGHT);
        Helper.enableNode(lcd, gauge.isLcdVisible() && gauge.isValueVisible());

        needleRotate = new Rotate(180 - startAngle);
        needleRotate.setAngle(needleRotate.getAngle() + (gauge.getValue() - oldValue - minValue) * angleStep);
        needle              = new Path();
        needle.setFillRule(FillRule.EVEN_ODD);
        needle.getTransforms().setAll(needleRotate);
        needle.setStrokeType(StrokeType.INSIDE);
        needle.setStroke(Color.TRANSPARENT);

        buttonTooltip    = new Tooltip();
        buttonTooltip.setTextAlignment(TextAlignment.CENTER);

        knobCanvas = new Canvas();
        knobCtx    = knobCanvas.getGraphicsContext2D();
        knobCanvas.setPickOnBounds(false);
        Helper.enableNode(knobCanvas, gauge.isKnobVisible());

        dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        dropShadow.setBlurType(BlurType.TWO_PASS_BOX);
        dropShadow.setRadius(0.015 * PREFERRED_WIDTH);
        dropShadow.setOffsetY(0.015 * PREFERRED_WIDTH);

        shadowGroup = new Group(needle, knobCanvas);
        shadowGroup.setEffect(gauge.isShadowsEnabled() ? dropShadow : null);

        titleText = new Text(gauge.getTitle());
        titleText.setTextOrigin(VPos.CENTER);
        titleText.setMouseTransparent(true);
        Helper.enableNode(titleText, !gauge.getTitle().isEmpty());

        subTitleText = new Text(gauge.getSubTitle());
        subTitleText.setTextOrigin(VPos.CENTER);
        subTitleText.setMouseTransparent(true);
        Helper.enableNode(subTitleText, !gauge.getSubTitle().isEmpty());

        unitText = new Text(gauge.getUnit());
        unitText.setMouseTransparent(true);
        unitText.setTextOrigin(VPos.CENTER);
        unitText.setMouseTransparent(true);
        Helper.enableNode(unitText, !gauge.getUnit().isEmpty());

        valueText = new Text(formatNumber(gauge.getLocale(), gauge.getFormatString(), gauge.getDecimals(), gauge.getCurrentValue()));
        valueText.setMouseTransparent(true);
        valueText.setTextOrigin(VPos.CENTER);
        valueText.setMouseTransparent(true);
        Helper.enableNode(valueText, gauge.isValueVisible());

        // Set initial value
        double targetAngle = 180 - startAngle + (gauge.getCurrentValue() - minValue) * angleStep;
        targetAngle        = Helper.clamp(180 - startAngle, 180 - startAngle + angleRange, targetAngle);
        needleRotate.setAngle(targetAngle);

        rotateNeedle(gauge.getCurrentValue());

        // Add all nodes
        pane = new Pane(sectionsAndAreasCanvas,
                        tickMarkCanvas,
                        markerPane,
                        ledCanvas,
                        lcd,
                        titleText,
                        subTitleText,
                        unitText,
                        valueText,
                        shadowGroup);
        pane.setBorder(new Border(new BorderStroke(gauge.getBorderPaint(), BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(gauge.getBorderWidth()))));
        pane.setBackground(new Background(new BackgroundFill(gauge.getBackgroundPaint(), new CornerRadii(1024), Insets.EMPTY)));

        getChildren().setAll(pane);
    }

    @Override protected void registerListeners() {
        super.registerListeners();

        gauge.getMarkers().addListener(markerListener);
        gauge.currentValueProperty().addListener(currentValueListener);

        handleEvents("INTERACTIVITY");
        handleEvents("VISIBILITY");
    }


    // ******************** Methods *******************************************
    @Override protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);
        if ("FINISHED".equals(EVENT_TYPE)) {
            double currentValue = gauge.getCurrentValue();
            // com.ryl.customControls.gauge.library.medusa.Check sections for value and fire section events
            if (gauge.getCheckSectionsForValue()) {
                int listSize = sections.size();
                for (int i = 0 ; i < listSize ; i++) { sections.get(i).checkForValue(currentValue); }
            }

            // com.ryl.customControls.gauge.library.medusa.Check areas for value and fire section events
            if (gauge.getCheckAreasForValue()) {
                int listSize = areas.size();
                for (int i = 0 ; i < listSize ; i++) { areas.get(i).checkForValue(currentValue); }
            }

            // Highlight Sections and/or Areas if enabled
            if (highlightSections | highlightAreas) {
                sectionsAndAreasCtx.clearRect(0, 0, size, size);
                drawAreasAndSections(sectionsAndAreasCtx);
            }
        } else if ("VISIBILITY".equals(EVENT_TYPE)) {
            Helper.enableNode(ledCanvas, gauge.isLedVisible());
            Helper.enableNode(titleText, !gauge.getTitle().isEmpty());
            Helper.enableNode(subTitleText, !gauge.getSubTitle().isEmpty());
            Helper.enableNode(unitText, !gauge.getUnit().isEmpty());
            Helper.enableNode(valueText, gauge.isValueVisible());
            Helper.enableNode(lcd, gauge.isLcdVisible() && gauge.isValueVisible());
            Helper.enableNode(knobCanvas, gauge.isKnobVisible());
            Helper.enableNode(threshold, gauge.isThresholdVisible());
            Helper.enableNode(average, gauge.isAverageVisible());
            sectionsVisible = gauge.getSectionsVisible();
            areasVisible    = gauge.getAreasVisible();
            boolean markersVisible = gauge.getMarkersVisible();
            for (Shape shape : markerMap.values()) { Helper.enableNode(shape, markersVisible); }
            resize();
            redraw();
        } else if ("LED".equals(EVENT_TYPE)) {
            if (gauge.isLedVisible()) { drawLed(); }
        } else if ("LCD".equals(EVENT_TYPE)) {
            if (gauge.isLcdVisible()) redraw();
        } else if ("RECALC".equals(EVENT_TYPE)) {
            startAngle = gauge.getStartAngle();
            angleRange = gauge.getAngleRange();
            angleStep  = gauge.getAngleStep();
            minValue   = gauge.getMinValue();
            maxValue   = gauge.getMaxValue();
            if (gauge.getValue() < minValue) { oldValue = minValue; }
            if (gauge.getValue() > maxValue) { oldValue = maxValue; }
            resize();
            redraw();
            rotateNeedle(gauge.getCurrentValue());
        } else if ("SECTION".equals(EVENT_TYPE)) {
            sections          = gauge.getSections();
            highlightSections = gauge.isHighlightSections();
            sectionsVisible   = gauge.getSectionsVisible();
            areas             = gauge.getAreas();
            highlightAreas    = gauge.isHighlightAreas();
            areasVisible      = gauge.getAreasVisible();
            resize();
            redraw();
        } else if ("INTERACTIVITY".equals(EVENT_TYPE)) {
            if (gauge.isInteractive()) {
                knobCanvas.setOnMousePressed(mouseHandler);
                knobCanvas.setOnMouseReleased(mouseHandler);
                if (!gauge.getButtonTooltipText().isEmpty()) {
                    buttonTooltip.setText(gauge.getButtonTooltipText());
                    Tooltip.install(knobCanvas, buttonTooltip);
                }
            } else {
                knobCanvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                knobCanvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
                Tooltip.uninstall(knobCanvas, buttonTooltip);
            }
        }
    }

    public void handleMouseEvent(final MouseEvent EVENT) {
        if (gauge.isDisabled()) return;
        final EventType TYPE = EVENT.getEventType();
        if (MouseEvent.MOUSE_PRESSED == TYPE) {
            gauge.fireEvent(gauge.BTN_PRESSED_EVENT);
            drawKnob(true);
        } else if (MouseEvent.MOUSE_RELEASED == TYPE) {
            gauge.fireEvent(gauge.BTN_RELEASED_EVENT);
            drawKnob(false);
        }
    }

    @Override public void dispose() {
        gauge.getMarkers().removeListener(markerListener);
        gauge.currentValueProperty().removeListener(currentValueListener);
        if (gauge.isInteractive()) {
            knobCanvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            knobCanvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
        }
        super.dispose();
    }


    // ******************** Private Methods ***********************************
    private void rotateNeedle(double value) {
        double startOffsetAngle = 180 - startAngle;
        double targetAngle;
        if (NeedleBehavior.STANDARD == needleBehavior) {
            if (ScaleDirection.CLOCKWISE == gauge.getScaleDirection()) {
                targetAngle = startOffsetAngle + (value - minValue) * angleStep;
                targetAngle = Helper.clamp(startOffsetAngle, startOffsetAngle + angleRange, targetAngle);
            } else {
                targetAngle = startOffsetAngle - (value - minValue) * angleStep;
                targetAngle = Helper.clamp(startOffsetAngle - angleRange, startOffsetAngle, targetAngle);
            }
        } else {
            if (value < minValue) value = maxValue - minValue + value;
            if (value > maxValue) value = value - maxValue + minValue;
            if (ScaleDirection.CLOCKWISE == gauge.getScaleDirection()) {
                targetAngle = startOffsetAngle + (value - minValue) * angleStep;
                targetAngle = Helper.clamp(startOffsetAngle, startOffsetAngle + angleRange, targetAngle);
            } else {
                targetAngle = startOffsetAngle - (value - minValue) * angleStep;
                targetAngle = Helper.clamp(startOffsetAngle - angleRange, startOffsetAngle, targetAngle);
            }
        }

        needleRotate.setAngle(targetAngle);
        valueText.setText(formatNumber(gauge.getLocale(), gauge.getFormatString(), gauge.getDecimals(), value));
        if (gauge.isLcdVisible()) {
            valueText.setTranslateX((0.691 * size - valueText.getLayoutBounds().getWidth()));
        } else {
            valueText.setTranslateX((size - valueText.getLayoutBounds().getWidth()) * 0.5);
        }

        if (gauge.isAverageVisible()) {
            drawAverage();
        }
    }

    private void drawGradientBar() {
        double     xy     = TickLabelLocation.OUTSIDE == tickLabelLocation ? 0.115 * size : 0.0515 * size;
        double     wh     = TickLabelLocation.OUTSIDE == tickLabelLocation ? size * 0.77 : size * 0.897;
        double     offset = 90 - startAngle;
        List<Stop> stops  = gauge.getGradientBarStops();
        Map<Double, Color> stopAngleMap     = new HashMap<>(stops.size());
        for (Stop stop : stops) { stopAngleMap.put(stop.getOffset() * angleRange, stop.getColor()); }
        double                  offsetFactor        = ScaleDirection.CLOCKWISE == scaleDirection ? (startAngle - 90) : (startAngle + 180);
        AngleConicalGradient    gradient            = new AngleConicalGradient(size * 0.5, size * 0.5, offsetFactor, stopAngleMap, gauge.getScaleDirection());

        double barStartAngle  = ScaleDirection.CLOCKWISE == scaleDirection ? -minValue * angleStep : minValue * angleStep;
        double barAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? gauge.getRange() * angleStep : -gauge.getRange() * angleStep;
        tickMarkCtx.save();
        tickMarkCtx.setStroke(gradient.getImagePattern(new Rectangle(xy - 0.026 * size, xy - 0.026 * size, wh + 0.052 * size, wh + 0.052 * size)));
        tickMarkCtx.setLineWidth(size * 0.052);
        tickMarkCtx.setLineCap(StrokeLineCap.BUTT);
        tickMarkCtx.strokeArc(xy, xy, wh, wh, -(offset + barStartAngle), -barAngleExtend, ArcType.OPEN);
        tickMarkCtx.restore();
    }

    private void drawAreasAndSections(final GraphicsContext CTX) {
        if (areas.isEmpty() && sections.isEmpty()) return;
        double value  = gauge.getCurrentValue();
        double offset = 90 - startAngle;
        double xy;
        double wh;
        int    listSize;

        // Draw Areas
        if (areasVisible && !areas.isEmpty()) {
            xy       = TickLabelLocation.OUTSIDE == tickLabelLocation ? 0.0895 * size : 0.025 * size;
            wh       = TickLabelLocation.OUTSIDE == tickLabelLocation ? size * 0.821 : size * 0.95;
            listSize = areas.size();
            for (int i = 0; i < listSize ; i++) {
                Section area = areas.get(i);
                double areaStartAngle;
                if (Double.compare(area.getStart(), maxValue) <= 0 && Double.compare(area.getStop(), minValue) >= 0) {
                    if (area.getStart() < minValue && area.getStop() < maxValue) {
                        areaStartAngle = 0;
                    } else {
                        areaStartAngle = ScaleDirection.CLOCKWISE == scaleDirection ? (area.getStart() - minValue) * angleStep : -(area.getStart() - minValue) * angleStep;
                    }
                    double areaAngleExtend;
                    if (area.getStop() > maxValue) {
                        areaAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (maxValue - area.getStart()) * angleStep : -(maxValue - area.getStart()) * angleStep;
                    } else if (Double.compare(area.getStart(), minValue) < 0) {
                        areaAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (area.getStop() - minValue) * angleStep : -(area.getStop() - minValue) * angleStep;
                    } else {
                        areaAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (area.getStop() - area.getStart()) * angleStep : -(area.getStop() - area.getStart()) * angleStep;
                    }
                    CTX.save();
                    if (highlightAreas) {
                        CTX.setFill(area.contains(value) ? area.getHighlightColor() : area.getColor());
                    } else {
                        CTX.setFill(area.getColor());
                    }
                    CTX.fillArc(xy, xy, wh, wh, -(offset + areaStartAngle), - areaAngleExtend, ArcType.ROUND);
                    CTX.restore();
                }
            }
        }

        // Draw Sections
        if (sectionsVisible && !sections.isEmpty()) {
            xy       = TickLabelLocation.OUTSIDE == tickLabelLocation ? 0.115 * size : 0.0515 * size;
            wh       = TickLabelLocation.OUTSIDE == tickLabelLocation ? size * 0.77 : size * 0.897;
            listSize = sections.size();
            CTX.setLineWidth(size * 0.052);
            CTX.setLineCap(StrokeLineCap.BUTT);
            for (int i = 0; i < listSize; i++) {
                Section section = sections.get(i);
                double  sectionStartAngle;
                if (Double.compare(section.getStart(), maxValue) <= 0 && Double.compare(section.getStop(), minValue) >= 0) {
                    if (Double.compare(section.getStart(), minValue) < 0 && Double.compare(section.getStop(), maxValue) < 0) {
                        sectionStartAngle = 0;
                    } else {
                        sectionStartAngle = ScaleDirection.CLOCKWISE == scaleDirection ? (section.getStart() - minValue) * angleStep : -(section.getStart() - minValue) * angleStep;
                    }
                    double sectionAngleExtend;
                    if (Double.compare(section.getStop(), maxValue) > 0) {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (maxValue - section.getStart()) * angleStep : -(maxValue - section.getStart()) * angleStep;
                    } else if (Double.compare(section.getStart(), minValue) < 0) {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (section.getStop() - minValue) * angleStep : -(section.getStop() - minValue) * angleStep;
                    } else {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == scaleDirection ? (section.getStop() - section.getStart()) * angleStep : -(section.getStop() - section.getStart()) * angleStep;
                    }
                    CTX.save();
                    if (highlightSections) {
                        CTX.setStroke(section.contains(value) ? section.getHighlightColor() : section.getColor());
                    } else {
                        CTX.setStroke(section.getColor());
                    }
                    CTX.strokeArc(xy, xy, wh, wh, -(offset + sectionStartAngle), -sectionAngleExtend, ArcType.OPEN);
                    CTX.restore();
                }
            }
        }
    }

    private void drawLed() {
        ledCtx.clearRect(0, 0, ledSize, ledSize);

        boolean isFlatLed = LedType.FLAT == gauge.getLedType();

        if (!isFlatLed) {
            ledCtx.setFill(ledFramePaint);
            ledCtx.fillOval(0, 0, ledSize, ledSize);
        } else {
            double lineWidth = 0.0037037 * size;
            ledCtx.setStroke(ledFramePaint);
            ledCtx.setLineWidth(lineWidth);
            ledCtx.strokeOval(lineWidth, lineWidth, ledSize - 2 * lineWidth, ledSize - 2 * lineWidth);
        }

        ledCtx.save();
        if (gauge.isLedOn()) {
            ledCtx.setEffect(ledOnShadow);
            ledCtx.setFill(ledOnPaint);
        } else {
            ledCtx.setEffect(ledOffShadow);
            ledCtx.setFill(ledOffPaint);
        }
        if (isFlatLed) {
            ledCtx.fillOval(0.2 * ledSize, 0.2 * ledSize, 0.6 * ledSize, 0.6 * ledSize);
        } else {
            ledCtx.fillOval(0.14 * ledSize, 0.14 * ledSize, 0.72 * ledSize, 0.72 * ledSize);
        }
        ledCtx.restore();

        ledCtx.setFill(ledHighlightPaint);
        ledCtx.fillOval(0.21 * ledSize, 0.21 * ledSize, 0.58 * ledSize, 0.58 * ledSize);
    }

    private void drawMarkers() {
        markerPane.getChildren().setAll(markerMap.values());
        markerPane.getChildren().addAll(average, threshold);
        TickLabelLocation tickLabelLocation = gauge.getTickLabelLocation();
        double         markerSize     = TickLabelLocation.OUTSIDE == tickLabelLocation ? 0.0125 * size : 0.015 * size;
        double         pathHalf       = markerSize * 0.3;
        double         centerX        = size * 0.5;
        double         centerY        = size * 0.5;
        ScaleDirection scaleDirection = gauge.getScaleDirection();
        if (gauge.getMarkersVisible()) {

            for (Map.Entry<Marker, Shape> entry : markerMap.entrySet()) {
                Marker marker = entry.getKey();
                Shape  shape  = entry.getValue();
                double valueAngle;
                if (ScaleDirection.CLOCKWISE == scaleDirection) {
                    valueAngle = startAngle - (marker.getValue() - minValue) * angleStep;
                } else {
                    valueAngle = startAngle + (marker.getValue() - minValue) * angleStep;
                }
                double sinValue = Math.sin(Math.toRadians(valueAngle));
                double cosValue = Math.cos(Math.toRadians(valueAngle));
                switch (marker.getMarkerType()) {
                    case TRIANGLE:
                        Path triangle = (Path) shape;
                        triangle.getElements().clear();
                        switch (tickLabelLocation) {
                            case OUTSIDE:
                                triangle.getElements().add(new MoveTo(centerX + size * 0.38 * sinValue, centerY + size * 0.38 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                                triangle.getElements().add(new LineTo(centerX + size * 0.4075 * sinValue, centerY + size * 0.4075 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                                triangle.getElements().add(new LineTo(centerX + size * 0.4075 * sinValue, centerY + size * 0.4075 * cosValue));
                                triangle.getElements().add(new ClosePath());
                                break;
                            case INSIDE:
                            default:
                                triangle.getElements().add(new MoveTo(centerX + size * 0.465 * sinValue, centerY + size * 0.465 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                                triangle.getElements().add(new LineTo(centerX + size * 0.436 * sinValue, centerY + size * 0.436 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                                triangle.getElements().add(new LineTo(centerX + size * 0.436 * sinValue, centerY + size * 0.436 * cosValue));
                                triangle.getElements().add(new ClosePath());
                                break;
                        }
                        break;
                    case DOT:
                        Circle dot = (Circle) shape;
                        dot.setRadius(markerSize);
                        switch (tickLabelLocation) {
                            case OUTSIDE:
                                dot.setCenterX(centerX + size * 0.3945 * sinValue);
                                dot.setCenterY(centerY + size * 0.3945 * cosValue);
                                break;
                            default:
                                dot.setCenterX(centerX + size * 0.449 * sinValue);
                                dot.setCenterY(centerY + size * 0.449 * cosValue);
                                break;
                        }
                        break;
                    case STANDARD:
                    default:
                        Path standard = (Path) shape;
                        standard.getElements().clear();
                        switch (tickLabelLocation) {
                            case OUTSIDE:
                                standard.getElements().add(new MoveTo(centerX + size * 0.38 * sinValue, centerY + size * 0.38 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                                standard.getElements().add(new LineTo(centerX + size * 0.4075 * sinValue, centerY + size * 0.4075 * cosValue));
                                standard.getElements().add(new LineTo(centerX + size * 0.4575 * sinValue, centerY + size * 0.4575 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                                standard.getElements().add(new LineTo(centerX + size * 0.4575 * sinValue, centerY + size * 0.4575 * cosValue));
                                standard.getElements().add(new LineTo(centerX + size * 0.4075 * sinValue, centerY + size * 0.4075 * cosValue));
                                standard.getElements().add(new ClosePath());
                                break;
                            case INSIDE:
                            default:
                                standard.getElements().add(new MoveTo(centerX + size * 0.465 * sinValue, centerY + size * 0.465 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle - pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle - pathHalf));
                                standard.getElements().add(new LineTo(centerX + size * 0.436 * sinValue, centerY + size * 0.436 * cosValue));
                                standard.getElements().add(new LineTo(centerX + size * 0.386 * sinValue, centerY + size * 0.386 * cosValue));
                                sinValue = Math.sin(Math.toRadians(valueAngle + pathHalf));
                                cosValue = Math.cos(Math.toRadians(valueAngle + pathHalf));
                                standard.getElements().add(new LineTo(centerX + size * 0.386 * sinValue, centerY + size * 0.386 * cosValue));
                                standard.getElements().add(new LineTo(centerX + size * 0.436 * sinValue, centerY + size * 0.436 * cosValue));
                                standard.getElements().add(new ClosePath());
                                break;
                        }
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
            double thresholdAngle;
            if (ScaleDirection.CLOCKWISE == scaleDirection) {
                thresholdAngle = startAngle - (gauge.getThreshold() - minValue) * angleStep;
            } else {
                thresholdAngle = startAngle + (gauge.getThreshold() - minValue) * angleStep;
            }
            double thresholdSize = Helper.clamp(3.0, 3.5, 0.01 * size);
            double sinValue      = Math.sin(Math.toRadians(thresholdAngle));
            double cosValue      = Math.cos(Math.toRadians(thresholdAngle));
            switch (tickLabelLocation) {
                case OUTSIDE:
                    threshold.getElements().add(new MoveTo(centerX + size * 0.38 * sinValue, centerY + size * 0.38 * cosValue));
                    sinValue = Math.sin(Math.toRadians(thresholdAngle - thresholdSize));
                    cosValue = Math.cos(Math.toRadians(thresholdAngle - thresholdSize));
                    threshold.getElements().add(new LineTo(centerX + size * 0.34 * sinValue, centerY + size * 0.34 * cosValue));
                    sinValue = Math.sin(Math.toRadians(thresholdAngle + thresholdSize));
                    cosValue = Math.cos(Math.toRadians(thresholdAngle + thresholdSize));
                    threshold.getElements().add(new LineTo(centerX + size * 0.34 * sinValue, centerY + size * 0.34 * cosValue));
                    threshold.getElements().add(new ClosePath());
                    break;
                case INSIDE:
                default:
                    threshold.getElements().add(new MoveTo(centerX + size * 0.465 * sinValue, centerY + size * 0.465 * cosValue));
                    sinValue = Math.sin(Math.toRadians(thresholdAngle - thresholdSize));
                    cosValue = Math.cos(Math.toRadians(thresholdAngle - thresholdSize));
                    threshold.getElements().add(new LineTo(centerX + size * 0.425 * sinValue, centerY + size * 0.425 * cosValue));
                    sinValue = Math.sin(Math.toRadians(thresholdAngle + thresholdSize));
                    cosValue = Math.cos(Math.toRadians(thresholdAngle + thresholdSize));
                    threshold.getElements().add(new LineTo(centerX + size * 0.425 * sinValue, centerY + size * 0.425 * cosValue));
                    threshold.getElements().add(new ClosePath());
                    break;
            }
            threshold.setFill(gauge.getThresholdColor());
            threshold.setStroke(gauge.getTickMarkColor());
        }
    }

    private void drawAverage() {
        double centerX = size * 0.5;
        double centerY = size * 0.5;
        // Draw average
        average.getElements().clear();
        double averageAngle;
        if (ScaleDirection.CLOCKWISE == scaleDirection) {
            averageAngle = startAngle - (gauge.getAverage() - minValue) * angleStep;
        } else {
            averageAngle = startAngle + (gauge.getAverage() - minValue) * angleStep;
        }
        double averageSize = Helper.clamp(3.0, 3.5, 0.01 * size);
        double sinValue      = Math.sin(Math.toRadians(averageAngle));
        double cosValue      = Math.cos(Math.toRadians(averageAngle));
        switch (tickLabelLocation) {
            case OUTSIDE:
                average.getElements().add(new MoveTo(centerX + size * 0.38 * sinValue, centerY + size * 0.38 * cosValue));
                sinValue = Math.sin(Math.toRadians(averageAngle - averageSize));
                cosValue = Math.cos(Math.toRadians(averageAngle - averageSize));
                average.getElements().add(new LineTo(centerX + size * 0.34 * sinValue, centerY + size * 0.34 * cosValue));
                sinValue = Math.sin(Math.toRadians(averageAngle + averageSize));
                cosValue = Math.cos(Math.toRadians(averageAngle + averageSize));
                average.getElements().add(new LineTo(centerX + size * 0.34 * sinValue, centerY + size * 0.34 * cosValue));
                average.getElements().add(new ClosePath());
                break;
            case INSIDE:
            default:
                average.getElements().add(new MoveTo(centerX + size * 0.465 * sinValue, centerY + size * 0.465 * cosValue));
                sinValue = Math.sin(Math.toRadians(averageAngle - averageSize));
                cosValue = Math.cos(Math.toRadians(averageAngle - averageSize));
                average.getElements().add(new LineTo(centerX + size * 0.425 * sinValue, centerY + size * 0.425 * cosValue));
                sinValue = Math.sin(Math.toRadians(averageAngle + averageSize));
                cosValue = Math.cos(Math.toRadians(averageAngle + averageSize));
                average.getElements().add(new LineTo(centerX + size * 0.425 * sinValue, centerY + size * 0.425 * cosValue));
                average.getElements().add(new ClosePath());
                break;
        }
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

    private void drawKnob(final boolean PRESSED) {
        knobCanvas.setCache(false);
        double w = knobCanvas.getWidth();
        double h = knobCanvas.getHeight();
        knobCtx.clearRect(0, 0, w, h);

        Color  knobColor = gauge.getKnobColor();
        double hue       = knobColor.getHue();
        double sat       = knobColor.getSaturation();
        double alp       = knobColor.getOpacity();
        double brg       = Color.BLACK.equals(knobColor) ? 0.2 : knobColor.getBrightness();
        double gradTop;
        double gradBot;

        switch (gauge.getKnobType()) {
            case PLAIN:
                knobCtx.setFill(new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(180,180,180)),
                                                   new Stop(0.46, Color.rgb(63,63,63)),
                                                   new Stop(1.0, Color.rgb(40,40,40))));
                knobCtx.fillOval(0, 0, w, h);

                knobCtx.setFill(new LinearGradient(0, 0.11764706 * h, 0, 0.76470588 * h, false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.hsb(hue, sat, PRESSED ? brg * 0.9 : brg * 1.0, alp)),
                                                   new Stop(0.01, Color.hsb(hue, sat, PRESSED ? brg * 0.75 : brg * 0.85, alp)),
                                                   new Stop(0.5, Color.hsb(hue, sat, PRESSED ? brg * 0.4 : brg * 0.5, alp)),
                                                   new Stop(0.51, Color.hsb(hue, sat, PRESSED ? brg * 0.35 : brg * 0.45, alp)),
                                                   new Stop(1.0, Color.hsb(hue, sat, PRESSED ? brg * 0.7 : brg * 0.8, alp))));
                knobCtx.fillOval(w * 0.11764706, h * 0.11764706, w - w * 0.23529412, h - h * 0.23529412);

                knobCtx.setFill(new RadialGradient(0, 0, 0.5 * w, 0.47 * h, w * 0.38, false, CycleMethod.NO_CYCLE,
                                                   new Stop(0, Color.TRANSPARENT),
                                                   new Stop(0.76, Color.TRANSPARENT),
                                                   new Stop(1.0, Color.rgb(0, 0, 0, PRESSED ? 0.5 : 0.2))));
                knobCtx.fillOval(w * 0.11764706, h * 0.11764706, w - w * 0.23529412, h - h * 0.23529412);
                break;
            case METAL:
                knobCtx.setFill(new LinearGradient(0, 0, 0, h,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(92,95,101)),
                                                   new Stop(0.47, Color.rgb(46,49,53)),
                                                   new Stop(1.0, Color.rgb(22,23,26))));
                knobCtx.fillOval(0, 0, w, h);

                knobCtx.setFill(new LinearGradient(0, 0.058823529411764705 * h, 0, 0.9411764705882353 * h,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.hsb(hue, sat, PRESSED ? brg * 0.7 : brg * 0.9, alp)),
                                                   new Stop(0.0, Color.hsb(hue, sat, PRESSED ? brg * 0.3 : brg * 0.5, alp))));
                knobCtx.fillOval(0.05882353 * w, 0.05882353 * h, w * 0.88235294, h * 0.88235294);

                knobCtx.beginPath();
                knobCtx.moveTo(0.17647058823529413 * w, 0.8235294117647058 * h);
                knobCtx.bezierCurveTo(0.29411764705882354 * w, 0.8823529411764706 * h, 0.35294117647058826 * w, 0.9411764705882353 * h, 0.5294117647058824 * w, 0.9411764705882353 * h);
                knobCtx.bezierCurveTo(0.6470588235294118 * w, 0.9411764705882353 * h, 0.7058823529411765 * w, 0.8823529411764706 * h, 0.8235294117647058 * w, 0.8235294117647058 * h);
                knobCtx.bezierCurveTo(0.7647058823529411 * w, 0.7058823529411765 * h, 0.6470588235294118 * w, 0.5882352941176471 * h, 0.5294117647058824 * w, 0.5882352941176471 * h);
                knobCtx.bezierCurveTo(0.35294117647058826 * w, 0.5882352941176471 * h, 0.23529411764705882 * w, 0.7058823529411765 * h, 0.17647058823529413 * w, 0.8235294117647058 * h);
                knobCtx.closePath();
                knobCtx.setFill(new RadialGradient(0, 0,
                                                0.47058823529411764 * w, 0.8823529411764706 * h,
                                                0.3235294117647059 * w,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(255, 255, 255, PRESSED ? 0.3 : 0.6)),
                                                   new Stop(1.0, Color.TRANSPARENT)));
                knobCtx.fill();

                knobCtx.beginPath();
                knobCtx.moveTo(0.058823529411764705 * w, 0.29411764705882354 * h);
                knobCtx.bezierCurveTo(0.17647058823529413 * w, 0.35294117647058826 * h, 0.35294117647058826 * w, 0.35294117647058826 * h, 0.5294117647058824 * w, 0.35294117647058826 * h);
                knobCtx.bezierCurveTo(0.6470588235294118 * w, 0.35294117647058826 * h, 0.8235294117647058 * w, 0.35294117647058826 * h, 0.9411764705882353 * w, 0.29411764705882354 * h);
                knobCtx.bezierCurveTo(0.8823529411764706 * w, 0.11764705882352941 * h, 0.7058823529411765 * w, 0.0 * h, 0.5294117647058824 * w, 0.0 * h);
                knobCtx.bezierCurveTo(0.29411764705882354 * w, 0.0 * h, 0.11764705882352941 * w, 0.11764705882352941 * h, 0.058823529411764705 * w, 0.29411764705882354 * h);
                knobCtx.closePath();
                knobCtx.setFill(new RadialGradient(0, 0,
                                                0.47058823529411764 * w, 0.0,
                                                0.4411764705882353 * w,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(255, 255, 255, PRESSED ? 0.45 : 0.75)),
                                                   new Stop(1.0, Color.TRANSPARENT)));
                knobCtx.fill();

                knobCtx.setFill(new LinearGradient(0.5294117647058824 * w, 0.23529411764705882 * h,
                                                0.5294117647058824 * w, 0.7647058823529411 * h,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.BLACK),
                                                   new Stop(1.0, Color.rgb(204, 204, 204))));
                knobCtx.fillOval(0.23529412 * w, 0.23529412 * h, 0.52941176 * w, 0.52941176 * h);

                knobCtx.setFill(new LinearGradient(0.5294117647058824 * w, 0.29411764705882354 * h,
                                                0.5294117647058824 * w, 0.7058823529411765 * h,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(1,6,11)),
                                                   new Stop(1.0, Color.rgb(50,52,56))));
                knobCtx.fillOval(0.29411765 * w, 0.29411765 * h, 0.41176471 * w, 0.41176471 * h);
                break;
            case FLAT:
                double lineWidth = 0.00740741 * size;
                double knobSize  = w - 2 * lineWidth;
                knobCtx.setFill(PRESSED ? knobColor.darker() : knobColor);
                knobCtx.setStroke(PRESSED ? Color.WHITE.darker() : Color.WHITE);
                knobCtx.setLineWidth(lineWidth);
                knobCtx.fillOval(lineWidth, lineWidth, knobSize, knobSize);
                knobCtx.strokeOval(lineWidth, lineWidth, knobSize, knobSize);
                break;
            case STANDARD:
            default:
                knobCtx.setFill(new LinearGradient(0, 0, 0, h,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.rgb(133, 133, 133).brighter().brighter()),
                                                   new Stop(0.52, Color.rgb(133, 133, 133)),
                                                   new Stop(1.0, Color.rgb(133, 133, 133).darker().darker())));
                knobCtx.fillOval(0, 0, w, h);
                gradTop = PRESSED ? h - size * 0.01 : size * 0.005;
                gradBot = PRESSED ? size * 0.005 : h - size * 0.01;
                knobCtx.setFill(new LinearGradient(0, gradTop, 0, gradBot,
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.hsb(hue, sat, brg * 0.85, alp)),
                                                   new Stop(0.45, Color.hsb(hue, sat, brg * 0.65, alp)),
                                                   new Stop(1.0, Color.hsb(hue, sat, brg * 0.4, alp))));
                knobCtx.fillOval(size * 0.005, size * 0.005, w - size * 0.01, h - size * 0.01);
                break;
        }
        knobCanvas.setCache(true);
        knobCanvas.setCacheHint(CacheHint.QUALITY);
    }

    private void drawNeedle() {
        double center = size * 0.5;
        double needleWidth;
        double needleHeight;
        needle.setCache(false);
        needle.getElements().clear();
        switch(gauge.getNeedleType()) {
            case BIG:
                needleWidth  = 0.06 * size;
                needleHeight = TickLabelLocation.INSIDE == tickLabelLocation ? 0.4975 * size : 0.415 * size;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.BIG, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, center - needle.getLayoutBounds().getHeight() * (TickLabelLocation.INSIDE == tickLabelLocation ? 0.93969849 : 0.92771084));
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight() * (TickLabelLocation.INSIDE == tickLabelLocation ? 0.93969849 : 0.92771084));
                break;
            case FAT:
                needleWidth  = 0.3 * size;
                needleHeight = 0.505 * size;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.FAT, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, size * 0.145);
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight() * 0.7029703);
                break;
            case SCIENTIFIC:
                needleWidth  = 0.1 * size;
                needleHeight = TickLabelLocation.INSIDE == tickLabelLocation ? 0.645 * size : 0.5625 * size;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.SCIENTIFIC, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, TickLabelLocation.INSIDE == tickLabelLocation ? size * 0.0325 : size * 0.115);
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight() * (TickLabelLocation.INSIDE == tickLabelLocation ? 0.7248062 : 0.68444444));
                break;
            case AVIONIC:
                needleWidth  = 0.06 * size;
                needleHeight = TickLabelLocation.INSIDE == tickLabelLocation ? 0.5975 * size : 0.515 * size;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.AVIONIC, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, center - needle.getLayoutBounds().getHeight() * (TickLabelLocation.INSIDE == tickLabelLocation ? 0.78242678 : 0.74757282));
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight() * (TickLabelLocation.INSIDE == tickLabelLocation ? 0.78242678 : 0.74757282));
                break;
            case VARIOMETER:
                needleWidth  = size * gauge.getNeedleSize().FACTOR;
                needleHeight = TickLabelLocation.INSIDE == tickLabelLocation ? size * 0.4675 : size * 0.385;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.VARIOMETER, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, center - needle.getLayoutBounds().getHeight());
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight());
                break;
            case STANDARD:
            default      :
                needleWidth  = size * gauge.getNeedleSize().FACTOR;
                needleHeight = TickLabelLocation.INSIDE == tickLabelLocation ? size * 0.455 : size * 0.3965;
                Needle.INSTANCE.getPath(needle, needleWidth, needleHeight, NeedleType.STANDARD, tickLabelLocation);
                needle.relocate(center - needle.getLayoutBounds().getWidth() * 0.5, center - needle.getLayoutBounds().getHeight());
                needleRotate.setPivotX(needle.getLayoutBounds().getWidth() * 0.5);
                needleRotate.setPivotY(needle.getLayoutBounds().getHeight());
                break;
        }
        needle.setCache(true);
        needle.setCacheHint(CacheHint.ROTATE);
    }

    private void resizeText() {
        double maxWidth = 0.4 * size;
        double fontSize = 0.06 * size;

        titleText.setFont(Fonts.robotoMedium(fontSize));
        titleText.setText(gauge.getTitle());
        if (titleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(titleText, maxWidth, fontSize); }
        titleText.relocate((size - titleText.getLayoutBounds().getWidth()) * 0.5, size * 0.26);

        fontSize = 0.05 * size;
        unitText.setFont(Fonts.robotoRegular(fontSize));
        unitText.setText(gauge.getUnit());
        if (unitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(unitText, maxWidth, fontSize); }
        unitText.relocate((size - unitText.getLayoutBounds().getWidth()) * 0.5, size * 0.35);

        maxWidth = 0.25 * size;
        subTitleText.setFont(Fonts.robotoRegular(fontSize));
        subTitleText.setText(gauge.getSubTitle());
        if (subTitleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(subTitleText, maxWidth, fontSize); }
        subTitleText.relocate((size - subTitleText.getLayoutBounds().getWidth()) * 0.5, size * 0.76);
    }

    @Override protected void resize() {
        double width  = gauge.getWidth() - gauge.getInsets().getLeft() - gauge.getInsets().getRight();
        double height = gauge.getHeight() - gauge.getInsets().getTop() - gauge.getInsets().getBottom();
        size          = width < height ? width : height;

        if (size > 0) {
            double center = size * 0.5;

            pane.setMaxSize(size, size);
            pane.relocate((gauge.getWidth() - size) * 0.5, (gauge.getHeight() - size) * 0.5);

            dropShadow.setRadius(0.008 * size);
            dropShadow.setOffsetY(0.008 * size);

            backgroundInnerShadow.setOffsetX(0);
            backgroundInnerShadow.setOffsetY(size * 0.03);
            backgroundInnerShadow.setRadius(size * 0.04);

            pane.setEffect(gauge.isInnerShadowEnabled() ? backgroundInnerShadow : null);

            sectionsAndAreasCanvas.setWidth(size);
            sectionsAndAreasCanvas.setHeight(size);

            tickMarkCanvas.setWidth(size);
            tickMarkCanvas.setHeight(size);

            markerPane.setPrefSize(size, size);

            boolean isFlatLed = LedType.FLAT == gauge.getLedType();
            ledSize = 0.07 * size;
            ledCanvas.setWidth(ledSize);
            ledCanvas.setHeight(ledSize);
            ledCanvas.relocate(0.3 * size, 0.47 * size);
            ledOffShadow = isFlatLed ? null : new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * ledSize, 0, 0, 0);
            ledOnShadow  = isFlatLed ? null : new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * ledSize, 0, 0, 0);
            if (!isFlatLed) {
                ledOnShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, gauge.getLedColor(), 0.36 * ledSize, 0, 0, 0));
            }

            resizeText();

            if (gauge.isLcdVisible()) {
                lcd.setWidth(0.4 * size);
                lcd.setHeight(0.114 * size);
                lcd.setArcWidth(0.0125 * size);
                lcd.setArcHeight(0.0125 * size);
                lcd.relocate((size - lcd.getWidth()) * 0.5, 0.583 * size);

                switch(gauge.getLcdFont()) {
                    case LCD:
                        valueText.setFont(Fonts.digital(0.108 * size));
                        valueText.setTranslateY(0.64 * size);
                        break;
                    case DIGITAL:
                        valueText.setFont(Fonts.digitalReadout(0.105 * size));
                        valueText.setTranslateY(0.65 * size);
                        break;
                    case DIGITAL_BOLD:
                        valueText.setFont(Fonts.digitalReadoutBold(0.105 * size));
                        valueText.setTranslateY(0.65 * size);
                        break;
                    case ELEKTRA:
                        valueText.setFont(Fonts.elektra(0.1116 * size));
                        valueText.setTranslateY(0.645 * size);
                        break;
                    case STANDARD:
                    default:
                        valueText.setFont(Fonts.robotoMedium(0.09 * size));
                        valueText.setTranslateY(0.64 * size);
                        break;
                }
                valueText.setTranslateX((0.691 * size - valueText.getLayoutBounds().getWidth()));
            } else {
                valueText.setFont(Fonts.robotoMedium(size * 0.1));
                valueText.setTranslateX((size - valueText.getLayoutBounds().getWidth()) * 0.5);
                valueText.setTranslateY(size * 0.65);
            }

            drawNeedle();

            knobCanvas.setWidth(size * 0.1);
            knobCanvas.setHeight(size * 0.1);
            knobCanvas.relocate(center - size * 0.05, center - size * 0.05);

            buttonTooltip.setText(gauge.getButtonTooltipText());
        }
    }

    @Override protected void redraw() {
        locale       = gauge.getLocale();
        formatString = new StringBuilder("%.").append(Integer.toString(gauge.getDecimals())).append("f").toString();
        shadowGroup.setEffect(gauge.isShadowsEnabled() ? dropShadow : null);

        // Background stroke and fill
        pane.setBorder(new Border(new BorderStroke(gauge.getBorderPaint(), BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(gauge.getBorderWidth() / PREFERRED_WIDTH * size))));
        pane.setBackground(new Background(new BackgroundFill(gauge.getBackgroundPaint(), new CornerRadii(1024), Insets.EMPTY)));

        // Areas, Sections and Tick Marks
        tickLabelLocation = gauge.getTickLabelLocation();
        scaleDirection    = gauge.getScaleDirection();
        areasVisible      = gauge.getAreasVisible();
        sectionsVisible   = gauge.getSectionsVisible();
        sectionsAndAreasCtx.clearRect(0, 0, size, size);
        drawAreasAndSections(sectionsAndAreasCtx);

        tickMarkCanvas.setCache(false);
        tickMarkCtx.clearRect(0, 0, size, size);
        if (gauge.isGradientBarEnabled() && gauge.getGradientLookup() != null) {
            drawGradientBar();
        }
        Helper.drawRadialTickMarks(gauge, tickMarkCtx, minValue, maxValue, startAngle, angleRange, angleStep, size * 0.5, size * 0.5, size);
        tickMarkCanvas.setCache(true);
        tickMarkCanvas.setCacheHint(CacheHint.QUALITY);

        // LED
        if (gauge.isLedVisible()) {
            final Color LED_COLOR = gauge.getLedColor();
            switch(gauge.getLedType()) {
                case FLAT:
                    ledFramePaint = Color.WHITE;
                    ledOnPaint = new LinearGradient(0, 0.25 * ledSize,
                                                    0, 0.74 * ledSize,
                                                    false, CycleMethod.NO_CYCLE,
                                                    new Stop(0.0, LED_COLOR),
                                                    new Stop(1.0, LED_COLOR.deriveColor(0.0, 1.0, 0.5, 1.0)));
                    ledOffPaint = new LinearGradient(0, 0.25 * ledSize,
                                                     0, 0.74 * ledSize,
                                                     false, CycleMethod.NO_CYCLE,
                                                     new Stop(0.0, LED_COLOR.deriveColor(0.0, 1.0, 0.5, 1.0)),
                                                     new Stop(1.0, LED_COLOR.deriveColor(0.0, 1.0, 0.13, 1.0)));
                    ledHighlightPaint = Color.TRANSPARENT;
                    break;
                case STANDARD:
                default:
                    ledFramePaint = new LinearGradient(0.14 * ledSize, 0.14 * ledSize,
                                                       0.84 * ledSize, 0.84 * ledSize,
                                                       false, CycleMethod.NO_CYCLE,
                                                       new Stop(0.0, Color.rgb(20, 20, 20, 0.65)),
                                                       new Stop(0.15, Color.rgb(20, 20, 20, 0.65)),
                                                       new Stop(0.26, Color.rgb(41, 41, 41, 0.65)),
                                                       new Stop(0.26, Color.rgb(41, 41, 41, 0.64)),
                                                       new Stop(0.85, Color.rgb(200, 200, 200, 0.41)),
                                                       new Stop(1.0, Color.rgb(200, 200, 200, 0.35)));
                    ledOnPaint = new LinearGradient(0.25 * ledSize, 0.25 * ledSize,
                                                       0.74 * ledSize, 0.74 * ledSize,
                                                    false, CycleMethod.NO_CYCLE,
                                                    new Stop(0.0, LED_COLOR.deriveColor(0.0, 1.0, 0.77, 1.0)),
                                                    new Stop(0.49, LED_COLOR.deriveColor(0.0, 1.0, 0.5, 1.0)),
                                                    new Stop(1.0, LED_COLOR));
                    ledOffPaint = new LinearGradient(0.25 * ledSize, 0.25 * ledSize,
                                                        0.74 * ledSize, 0.74 * ledSize,
                                                     false, CycleMethod.NO_CYCLE,
                                                     new Stop(0.0, LED_COLOR.deriveColor(0.0, 1.0, 0.20, 1.0)),
                                                     new Stop(0.49, LED_COLOR.deriveColor(0.0, 1.0, 0.13, 1.0)),
                                                     new Stop(1.0, LED_COLOR.deriveColor(0.0, 1.0, 0.2, 1.0)));
                    ledHighlightPaint = new RadialGradient(0, 0,
                                                           0.3 * ledSize, 0.3 * ledSize,
                                                           0.29 * ledSize,
                                                           false, CycleMethod.NO_CYCLE,
                                                           new Stop(0.0, Color.WHITE),
                                                           new Stop(1.0, Color.TRANSPARENT));
                    break;
            }
            drawLed();
        }

        // LCD
        LcdDesign lcdDesign = gauge.getLcdDesign();
        Color[]   lcdColors = lcdDesign.getColors();
        if (gauge.isLcdVisible() && gauge.isValueVisible()) {
            LinearGradient lcdGradient = new LinearGradient(0, 1, 0, lcd.getHeight() - 1,
                                                            false, CycleMethod.NO_CYCLE,
                                                            new Stop(0, lcdColors[0]),
                                                            new Stop(0.03, lcdColors[1]),
                                                            new Stop(0.5, lcdColors[2]),
                                                            new Stop(0.5, lcdColors[3]),
                                                            new Stop(1.0, lcdColors[4]));
            Paint lcdFramePaint;
            if (LcdDesign.FLAT_CUSTOM == lcdDesign) {
                lcdFramePaint = lcdDesign.lcdForegroundColor;
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
        }

        // Text
        titleText.setFill(gauge.getTitleColor());
        unitText.setFill(gauge.getUnitColor());
        subTitleText.setFill(gauge.getSubTitleColor());
        valueText.setFill(gauge.isLcdVisible() ? lcdColors[5] : gauge.getValueColor());
        resizeText();

        // Needle
        Color needleColor = gauge.getNeedleColor();
        switch(gauge.getNeedleShape()) {
            case ROUND:
                needlePaint = new LinearGradient(needle.getLayoutBounds().getMinX(), 0,
                                                 needle.getLayoutBounds().getMaxX(), 0,
                                                 false, CycleMethod.NO_CYCLE,
                                                 new Stop(0.0, needleColor.darker()),
                                                 new Stop(0.5, needleColor.brighter().brighter()),
                                                 new Stop(1.0, needleColor.darker()));
                needle.setStrokeWidth(0);
                break;
            case FLAT:
                needlePaint = needleColor;
                needle.setStrokeWidth(0.0037037 * size);
                break;
            case ANGLED:
            default:
                needlePaint = new LinearGradient(needle.getLayoutBounds().getMinX(), 0,
                                                 needle.getLayoutBounds().getMaxX(), 0,
                                                 false, CycleMethod.NO_CYCLE,
                                                 new Stop(0.0, needleColor.darker()),
                                                 new Stop(0.5, needleColor.darker()),
                                                 new Stop(0.5, needleColor.brighter()),
                                                 new Stop(1.0, needleColor.brighter()));
                needle.setStrokeWidth(0);
                break;
        }
        if (NeedleType.AVIONIC == gauge.getNeedleType()) {
            needlePaint = new LinearGradient(0, needle.getLayoutBounds().getMinY(),
                                             0, needle.getLayoutBounds().getMaxY(),
                                             false, CycleMethod.NO_CYCLE,
                                             new Stop(0.0, needleColor),
                                             new Stop(0.3, needleColor),
                                             new Stop(0.3, Color.BLACK),
                                             new Stop(1.0, Color.BLACK));
        }
        needle.setFill(needlePaint);
        needle.setStroke(gauge.getNeedleBorderColor());

        // Knob
        drawKnob(false);

        // Markers
        drawMarkers();
        thresholdTooltip.setText("Threshold\n(" + String.format(locale, formatString, gauge.getThreshold()) + ")");
    }
}
