/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.rendering.GralogColor;

import java.lang.reflect.*;
import java.util.function.Consumer;

import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.shapes.RenderingShape;
import gralog.rendering.shapes.SizeBox;
import gralog.structure.Edge;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.css.Size;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.lang.annotation.Annotation;
import gralog.core.annotations.DataField;


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

        setVgap(5);
        //this.setPrefWidth(280);
        int i = 0;

        try {
            if (displayObject != null) {
                Class<?> c = displayObject.getClass();

                
                for (Field f : c.getDeclaredFields()) {
                    f.setAccessible(true);
                    boolean display = false;
                    boolean readOnly = false;
                    Annotation[] annotations = f.getDeclaredAnnotations();
                    for(Annotation annotation : annotations){
                        if(annotation instanceof DataField){
                            DataField dataField = (DataField)annotation;
                            display = dataField.display();
                            readOnly = dataField.readOnly();
                            break;
                        }
                    }


                    String name = f.getName();
                    Label nameLabel = new Label(name);
                    nameLabel.setPrefWidth(LABEL_WIDTH);

                    Object value = f.get(displayObject);
                    Control valueControl = null;
                    Class<?> type = f.getType();
                   
                    if (display){
                        if (type.equals(Double.class) || type.equals(double.class)) {

                            String valueString = value.toString();
                            TextField valueField = new TextField(valueString);
                            if (!readOnly){
                                valueField.textProperty().addListener(e -> {
                                    try {
                                        f.set(displayObject, Double.parseDouble(valueField.getText()));
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {

                                    }
                                });
                            }else{
                                valueField.setDisable(true);
                            }
                            valueControl = valueField;
                        } else if (type.equals(Integer.class) || type.equals(int.class)) {
                            String valueString = value.toString();
                            TextField valueField = new TextField(valueString);
                            if (!readOnly){
                                valueField.textProperty().addListener(e -> {
                                    try {
                                        f.set(displayObject, Integer.parseInt(valueField.getText()));
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                                    }
                                });
                            }else{
                                valueField.setDisable(true);
                            }
                            valueControl = valueField;
                        } else if (type.equals(GralogColor.class)) {
                            String valueString = ((GralogColor) value).toHtmlString();

                            ColorPicker colorPicker = new ColorPicker(Color.web(valueString));
                            if (!readOnly){
                                colorPicker.setOnAction(e -> {
                                    try {
                                        f.set(displayObject, GralogColor.parseColorAlpha(colorPicker.getValue().toString()));
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                                    }
                                });
                            }else{
                                colorPicker.setDisable(true);
                            }
                            valueControl = colorPicker;
                        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                            CheckBox valueField = new CheckBox();
                            if ((Boolean) value){
                                valueField.setSelected(true);
                            }
                            if (!readOnly){

                                valueField.selectedProperty().addListener(e -> {
                                    System.out.println("halpppp they're changing meeeeee");
                                    try {
                                        f.set(displayObject, valueField.isSelected());
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                                    }
                                });
                                
                            }else{
                                valueField.setDisable(true);
                            }
                            valueControl = valueField;
                        } else if (type.isAssignableFrom(String.class)) {
                            String valueString = value.toString();
                            TextField valueField = new TextField(valueString);
                            if (!readOnly){
                                valueField.textProperty().addListener(e -> {
                                    try {
                                        f.set(displayObject, valueField.getText());
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                                    }
                                });
                            }else{
                                valueField.setDisable(true);
                            }
                            valueControl = valueField;
                        } else if (type.isAssignableFrom(GralogGraphicsContext.LineType.class)) {
                            ChoiceBox<GralogGraphicsContext.LineType> choiceBox =
                                    new ChoiceBox<>(FXCollections.observableArrayList(GralogGraphicsContext.LineType.values()));
                            choiceBox.getSelectionModel().select((GralogGraphicsContext.LineType)value);
                            if (!readOnly){
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
                            }else{
                                choiceBox.setDisable(true);
                            }
                            valueControl = choiceBox;
                        } else if (type.isAssignableFrom(Edge.EdgeType.class)) {
                            ChoiceBox<Edge.EdgeType> choiceBox =
                                    new ChoiceBox<>(FXCollections.observableArrayList(Edge.EdgeType.values()));
                            choiceBox.getSelectionModel().select((Edge.EdgeType) value);
                            if (!readOnly){
                                choiceBox.getSelectionModel().selectedIndexProperty().addListener( (obs, old, d) ->{
                                    ((Edge)displayObject).setEdgeType(Edge.EdgeType.values()[d.intValue()]);
                                    requestRedraw();
                                });
                            }else{
                                choiceBox.setDisable(true);
                            }
                            valueControl = choiceBox;
                        } else if (type.isAssignableFrom(RenderingShape.class)){

                            RenderingShape shape = (RenderingShape)value;

                            ChoiceBox<Class<? extends RenderingShape>> choiceBox =
                                    new ChoiceBox<>(FXCollections.observableArrayList(RenderingShape.renderingShapeClasses));

                            choiceBox.getSelectionModel().select(RenderingShape.renderingShapeClasses.indexOf(value.getClass()));
                            choiceBox.setConverter(new RenderingShape.ShapeConverter());
                            if (!readOnly){
                                choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                        try{
                                            ///here!
                                            Constructor cs = RenderingShape.renderingShapeClasses.get(newValue.intValue()).getConstructors()[0];
                                            f.set(displayObject, cs.newInstance(shape.sizeBox));
                                            requestRedraw();
                                        }catch(IllegalAccessException | IllegalArgumentException ex) {
                                        }catch(InstantiationException | InvocationTargetException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                            }else{
                                choiceBox.setDisable(true);
                            }

                            TextField widthField = new TextField(shape.sizeBox.width.toString());
                            TextField heightField = new TextField(shape.sizeBox.height.toString());
                            if (!readOnly){
                                widthField.textProperty().addListener(e -> {
                                    try {
                                        RenderingShape localShape = (RenderingShape) f.get(displayObject);
                                        localShape.setWidth(Double.parseDouble(widthField.getText()));
                                        f.set(displayObject, localShape);
                                        requestRedraw();
                                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                                    }
                                });
                            }else{
                                widthField.setDisable(true);
                            }
                            if (!readOnly){
                                heightField.textProperty().addListener(e -> {
                                   try {
                                       RenderingShape localShape = (RenderingShape) f.get(displayObject);
                                       localShape.setHeight(Double.parseDouble(heightField.getText()));
                                       f.set(displayObject, localShape);
                                       requestRedraw();
                                   } catch (IllegalAccessException | IllegalArgumentException ex) {
                                   }
                               });
                            }else{
                                heightField.setDisable(true);
                            }

                            addSeparator(i);
                            i++;
                            addPair("shape", choiceBox, i);
                            addPair("width", widthField, i + 1);
                            addPair("height", heightField, i + 2);

                            addSeparator(i + 3);

                            i+=4;
                        }

                        if (valueControl != null) {
                            valueControl.setMaxWidth(MAX_FIELD_WIDTH);
                            add(nameLabel, 0, i);
                            add(valueControl, 1, i);
                            i++;
                        }
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            getChildren().clear();
        }
    }

    void addSeparator(int i){
        Separator s = new Separator();
        setConstraints(s, 0, i);
        getChildren().add(s);

        s = new Separator();
        setConstraints(s, 1, i);
        getChildren().add(s);

    }

    void addPair(String label, Control b, int i){
        Label a = new Label(label);
        a.setPrefWidth(LABEL_WIDTH);
        b.setMaxWidth(MAX_FIELD_WIDTH);
        add(a, 0, i);
        add(b, 1, i);
    }
}
