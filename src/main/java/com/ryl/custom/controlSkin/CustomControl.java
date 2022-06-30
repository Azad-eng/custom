package com.ryl.custom.controlSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * @author Gerrit Grunwald
 * description：全新自定义的控件——实现功能：开关切换
 */
public class CustomControl extends Control {
    public enum SkinType{ LED, SWITCH }

    private static final StyleablePropertyFactory<CustomControl> FACTORY
            = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    /** css样式属性 **/
    private static final CssMetaData<CustomControl, Color> COLOR
            = FACTORY.createColorCssMetaData("-color", s -> s.color, Color.RED, false);

    /** css伪类 **/
    private static final PseudoClass ON_PSEUDO_CLASS = PseudoClass.getPseudoClass("on");


    /** Properties **/
    private SkinType skinType;
    private BooleanProperty state;
    private final StyleableProperty<Color> color;

    /** 用户代理样式表 **/
    private static String defaultUserAgentStyleSheet;
    private static String switchUserAgentStyleSheet;

    /**
     * 构造器重载
     */
    public CustomControl() {
        this(SkinType.LED);
    }

    public CustomControl(final SkinType skinType){
        getStyleClass().add("custom-control");
        this.skinType = skinType;
        this.state = new BooleanPropertyBase(false){
            @Override
            protected void invalidated(){
                pseudoClassStateChanged(ON_PSEUDO_CLASS,get());
            }
            @Override
            public Object getBean() {
                return this;
            }
            @Override
            public String getName() {
                return "state";
            }
        };
        this.color = new SimpleStyleableObjectProperty<>(COLOR,this,"color");
    }

    /**
     * state
     */
    public boolean getState(){
        return state.get();
    }
    public void setState(final boolean state) {
        this.state.set(state);
    }
    public BooleanProperty stateProperty() {
        return state;
    }

    /**
     * color
     */
    public Color getColor() {
        return color.getValue();
    }
    public void setColor(final Color color){
        this.color.setValue(color);
    }
    public ObjectProperty<Color> colorProperty(){
        return (ObjectProperty<Color>) color;
    }

    /**
     * style related
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        switch (skinType){
            case SWITCH: return new SwitchSkin(CustomControl.this);
            case LED:
            default: return new LedSkin(CustomControl.this);
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        switch(skinType) {
            case SWITCH:
                if (null == switchUserAgentStyleSheet) {
                    switchUserAgentStyleSheet = CustomControl.class.getResource("/controlSkin/switch.css").toExternalForm();
                }
                return switchUserAgentStyleSheet;
            case LED   :
            default    :
                if (null == defaultUserAgentStyleSheet) {
                    defaultUserAgentStyleSheet = CustomControl.class.getResource("/controlSkin/custom-control.css").toExternalForm();
                }
                return defaultUserAgentStyleSheet;
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
