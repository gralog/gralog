/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.rendering.GralogColor;

import java.lang.reflect.*;
import java.util.function.Consumer;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;

/**
 *
 */
@ViewDescription(forClass = Object.class)
public class ReflectedView extends GridPaneView<Object> {

    @Override
    public void setObject(Object displayObject, Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        int i = 0;

        try {
            if (displayObject != null) {
                Class<?> c = displayObject.getClass();
                for (Field f : c.getFields()) {
                    String name = f.getName();
                    Label nameLabel = new Label(name);
                    Object value = f.get(displayObject);
                    Control valueControl = null;
                    Class<?> type = f.getType();

                    if (type.equals(Double.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {
                                f.set(displayObject, Double.parseDouble(valueField.getText()));
                                requestRedraw();
                            } catch (IllegalAccessException | IllegalArgumentException ex) {
                            }
                        });
                        valueControl = valueField;
                    } else if (type.equals(Integer.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {
                                f.set(displayObject, Integer.parseInt(valueField.getText()));
                                requestRedraw();
                            } catch (IllegalAccessException | IllegalArgumentException ex) {
                            }
                        });
                        valueControl = valueField;
                    } else if (type.equals(GralogColor.class)) {
                        String valueString = ((GralogColor) value).toHtmlString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {
                                f.set(displayObject, GralogColor.parseColor(valueField.getText()));
                                requestRedraw();
                            } catch (IllegalAccessException | IllegalArgumentException ex) {
                            }
                        });
                        valueControl = valueField;
                    } else if (type.equals(Boolean.class)) {
                        CheckBox valueField = new CheckBox();
                        if ((Boolean) value)
                            valueField.setSelected(true);
                        valueField.selectedProperty().addListener(e -> {
                            try {
                                f.set(displayObject, valueField.isSelected());
                                requestRedraw();
                            } catch (IllegalAccessException | IllegalArgumentException ex) {
                            }
                        });
                        valueControl = valueField;
                    } else if (type.isAssignableFrom(String.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {
                                f.set(displayObject, valueField.getText());
                                requestRedraw();
                            } catch (IllegalAccessException | IllegalArgumentException ex) {
                            }
                        });
                        valueControl = valueField;
                    }

                    if (valueControl != null) {
                        add(nameLabel, 0, i);
                        add(valueControl, 1, i);
                        i++;
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            getChildren().clear();
        }
    }
}
