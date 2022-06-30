package com.ryl.custom.restyleCss;
import javafx.scene.control.CheckBox;

/**
 * @author Gerrit Grunwald
 */
public class Switch extends CheckBox {
    /**
     * 构造器
     * @param text switch(checkBox)的文本
     * description:主要是为了新建一个css类switch来覆盖checkBox默认的css样式
     */
    public Switch(final String text) {
        super(text);
        getStyleClass().setAll("switch");
    }
}
