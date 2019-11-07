/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.rendering.GralogColor;

import java.lang.reflect.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import gralog.algorithm.AlgorithmParameters;

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
import gralog.annotations.DataField;


import javax.sound.sampled.Line;

/**
 *
 */
@ViewDescription(forClass = AlgorithmParameters.class)
public class MultipleParameterView extends GridPaneView<Object> {
    private static final double LABEL_WIDTH = 200;
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
                    

                    String name = f.getName();
                    Label nameLabel = new Label(name);
                    nameLabel.setMaxWidth(LABEL_WIDTH);
                    nameLabel.setPrefWidth(LABEL_WIDTH);

                    Object value = f.get(displayObject);
                    Control valueControl = null;
                    Class<?> type = f.getType();


                    if (type.equals(Double.class) || type.equals(double.class)) {
                        valueControl = createDoubleValueField((double)value, f, displayObject);
                    } else if (type.equals(Integer.class) || type.equals(int.class)) {
                        valueControl = createIntValueField(value, f, displayObject);
                    } else if (type.equals(GralogColor.class)) {
                        valueControl = createColorPicker(value, f, displayObject);
                    } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                        valueControl = createBooleanValueField(value, f, displayObject);
                    } else if (type.isAssignableFrom(String.class)) {
                        valueControl = createStringValueField(value, f, displayObject);
                    } else if (type.isAssignableFrom(GralogGraphicsContext.LineType.class)) {
                        valueControl = createLineTypeValueField(value, f, displayObject);
                    } else if (type.isAssignableFrom(Edge.EdgeType.class)) {
                        valueControl = createEdgeTypeValueField(value, f, displayObject);
                    } else if (type.isAssignableFrom(RenderingShape.class)){
                        addRenderingShapeControls(value, f, displayObject, i);
                        i+= 5;
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

    void addSeparator(int i){
        Separator s = new Separator();
        setConstraints(s, 0, i);
        getChildren().add(s);

        s = new Separator();
        setConstraints(s, 1, i);
        getChildren().add(s);

    }

    private Control createDoubleValueField(double value, Field f, Object displayObject){
        String valueString = String.format("%.4f", value);
        TextField valueField = new TextField(valueString);

        valueField.textProperty().addListener(e -> {
            try {
                f.set(displayObject,
                        Double.parseDouble(valueField.getText()));
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {

            }
        });
   
        return valueField;
    }

    private Control createIntValueField(Object value, Field f, Object displayObject){
        String valueString = value.toString();
        TextField valueField = new TextField(valueString);
       
        valueField.textProperty().addListener(e -> {
            try {
                f.set(displayObject, Integer.parseInt(valueField.getText()));
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });
 
        return valueField;
    }

    private Control createColorPicker(Object value, Field f, Object displayObject){
        String valueString = ((GralogColor) value).toHtmlString();

        ColorPicker colorPicker = new ColorPicker(Color.web(valueString));
        colorPicker.setOnAction(e -> {
            try {
                f.set(displayObject, GralogColor.parseColorAlpha(colorPicker.getValue().toString()));
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });
        return colorPicker;
    }

    private Control createBooleanValueField(Object value, Field f, Object displayObject){
        CheckBox valueField = new CheckBox();
        if ((Boolean) value){
            valueField.setSelected(true);
        }
 
        valueField.selectedProperty().addListener(e -> {
            try {
                f.set(displayObject, valueField.isSelected());
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });

        return valueField;
    }

    private Control createStringValueField(Object value, Field f, Object displayObject){
        String valueString = value.toString();
        TextField valueField = new TextField(valueString);
        valueField.textProperty().addListener(e -> {
            try {
                f.set(displayObject, valueField.getText());
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });
        return valueField;
    }

    private Control createLineTypeValueField(Object value, Field f, Object displayObject){
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
        return choiceBox;
    }

    private Control createEdgeTypeValueField(Object value, Field f, Object displayObject){
        ChoiceBox<Edge.EdgeType> choiceBox =
                new ChoiceBox<>(FXCollections.observableArrayList(Edge.EdgeType.values()));
        choiceBox.getSelectionModel().select((Edge.EdgeType) value);
    
        choiceBox.getSelectionModel().selectedIndexProperty().addListener( (obs, old, d) ->{
            ((Edge)displayObject).setEdgeType(Edge.EdgeType.values()[d.intValue()]);
            requestRedraw();
        });
        
        return choiceBox;
    }

    private void addRenderingShapeControls(Object value, Field f, Object displayObject, int i){
        RenderingShape shape = (RenderingShape)value;

        ChoiceBox<Class<? extends RenderingShape>> choiceBox =
                new ChoiceBox<>(FXCollections.observableArrayList(RenderingShape.renderingShapeClasses));

        choiceBox.getSelectionModel().select(RenderingShape.renderingShapeClasses.indexOf(value.getClass()));
        choiceBox.setConverter(new RenderingShape.ShapeConverter());
    
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            try{
                ///here!
                Constructor cs = RenderingShape.renderingShapeClasses.get(newValue.intValue()).getConstructors()[0];
                f.set(displayObject, cs.newInstance(shape.sizeBox));
                requestRedraw();
            }catch(IllegalAccessException | IllegalArgumentException ex) {
            }catch(InstantiationException | InvocationTargetException ex){
                ex.printStackTrace();
            }
        });
       

        String widthText = String.format("%.3f", shape.sizeBox.width);
        String heightText = String.format("%.3f", shape.sizeBox.height);
        TextField widthField = new TextField(widthText);
        TextField heightField = new TextField(heightText);

        widthField.textProperty().addListener(e -> {
            try {
                RenderingShape localShape = (RenderingShape) f.get(displayObject);
                localShape.setWidth(Double.parseDouble(widthField.getText()));
                f.set(displayObject, localShape);
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });
      

        heightField.textProperty().addListener(e -> {
            try {
                RenderingShape localShape = (RenderingShape) f.get(displayObject);
                localShape.setHeight(Double.parseDouble(heightField.getText()));
                f.set(displayObject, localShape);
                requestRedraw();
            } catch (IllegalAccessException | IllegalArgumentException ex) {
            }
        });

        addSeparator(i);
        i++;
        addPair("shape", choiceBox, i);
        addPair("width", widthField, i + 1);
        addPair("height", heightField, i + 2);

        addSeparator(i + 3);
    }

    void addPair(String label, Control b, int i){
        Label a = new Label(label);
        a.setPrefWidth(LABEL_WIDTH);
        b.setMaxWidth(MAX_FIELD_WIDTH);
        add(a, 0, i);
        add(b, 1, i);
    }
}
