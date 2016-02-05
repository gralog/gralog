/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;

import gralog.rendering.GralogColor;

import java.lang.reflect.*;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Control;

/**
 *
 * @author viktor
 */
public class ReflectedView extends GridPane implements View {
    
    Object displayObject = null;
    StructurePane structurePane = null;
    
    public void setStructurePane(StructurePane structurePane) {
        this.structurePane = structurePane;
    }

    public void Update(Object newObject) {
        this.displayObject = newObject;
        Update();
    }
    
    public void Update() {
        this.getChildren().clear();
        int i = 0;
        
        try {
            if(displayObject != null)
            {
                Class<?> c = displayObject.getClass();
                for(Field f : c.getFields())
                {
                    String name = f.getName();
                    Label nameLabel = new Label(name);
                    Object value = f.get(displayObject);
                    Control valueControl = null;
                    Class<?> type = f.getType();
                    
                    if(type.equals(Double.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, Double.parseDouble(valueField.getText()));
                                 if(structurePane != null)structurePane.RequestRedraw();} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.equals(Integer.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, Integer.parseInt(valueField.getText()));
                                 if(structurePane != null)structurePane.RequestRedraw();} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.equals(GralogColor.class)) {
                        String valueString = ((GralogColor)value).toHtmlString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, GralogColor.parseColor(valueField.getText()));
                                 if(structurePane != null)structurePane.RequestRedraw();} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.equals(Boolean.class)) {
                        CheckBox valueField = new CheckBox();
                        if((Boolean)value)
                            valueField.setSelected(true);
                        valueField.selectedProperty().addListener(e -> {
                            try {f.set(displayObject, valueField.isSelected());
                                 if(structurePane != null)structurePane.RequestRedraw();} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.isAssignableFrom(String.class)) {
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, valueField.getText());
                                 if(structurePane != null)structurePane.RequestRedraw();} catch(Exception ex) {} });
                        valueControl = valueField;
                    } 

                    if(valueControl != null) 
                    {
                        add(nameLabel, 0, i);
                        add(valueControl, 1, i);
                        i++;
                    }
                }
            }
        } catch(Exception ex) {
            getChildren().clear();
        }
        
    }
}
