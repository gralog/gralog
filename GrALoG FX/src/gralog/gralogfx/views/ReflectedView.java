/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

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
                    System.out.print(type.getName());
                    
                    if(type.equals(Double.class)) {
                        System.out.println("double " + name);
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, Double.parseDouble(valueField.getText()));} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.equals(Integer.class)) {
                        System.out.println("int " + name);
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, Integer.parseInt(valueField.getText()));} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.equals(Boolean.class)) {
                        System.out.println("bool " + name);
                        CheckBox valueField = new CheckBox();
                        valueField.selectedProperty().addListener(e -> {
                            try {f.set(displayObject, valueField.isSelected());} catch(Exception ex) {} });
                        valueControl = valueField;
                    } else if(type.isAssignableFrom(String.class)) {
                        System.out.println("string " + name);
                        String valueString = value.toString();
                        TextField valueField = new TextField(valueString);
                        valueField.textProperty().addListener(e -> {
                            try {f.set(displayObject, valueField.getText());} catch(Exception ex) {} });
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
