/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.rendering.GralogColor;

import java.lang.reflect.*;
import java.util.function.Consumer;

import gralog.rendering.GralogGraphicsContext;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import javax.sound.sampled.Line;

/**
 *
 */
@ViewDescription(forClass = Object.class)
public class ReflectedView extends GridPaneView<Object> {
    private static final double LABEL_WIDTH = 100;
    private static final double MAX_FIELD_WIDTH = 150;
    @Override
    public void setObject(Object displayObject, Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        //this.setPrefWidth(280);
        int i = 0;

        try {
            if (displayObject != null) {
                Class<?> c = displayObject.getClass();
                for (Field f : c.getFields()) {
                    String name = f.getName();
                    Label nameLabel = new Label(name);
                    nameLabel.setPrefWidth(LABEL_WIDTH);

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
                    } else if (type.isAssignableFrom(GralogGraphicsContext.LineType.class)) {
                        ChoiceBox<GralogGraphicsContext.LineType> choiceBox =
                                new ChoiceBox<>(FXCollections.observableArrayList(GralogGraphicsContext.LineType.values()));
                        choiceBox.getSelectionModel().select((GralogGraphicsContext.LineType)value);
                        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                try{
                                    f.set(displayObject, GralogGraphicsContext.LineType.values()[newValue.intValue()]);
                                    requestRedraw();
                                }catch(IllegalAccessException | IllegalArgumentException ex) {
                                }
                            }
                        });
                        valueControl = choiceBox;
                    }

                    if (valueControl != null) {
                        valueControl.setMaxWidth(MAX_FIELD_WIDTH);
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
