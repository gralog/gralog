/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

import java.lang.reflect.*;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
                    String valueString = value.toString();
                    TextField valueField = new TextField(valueString);
                    valueField.textProperty().addListener(e -> {
                        try {
                            f.set(displayObject, valueField.getText());
                        } catch(Exception ex) {
                            // dont know how to reset
                        }
                    });
                    
                    add(nameLabel, 0, i);
                    add(valueField, 1, i);
                    i++;
                }
            }
        } catch(Exception ex) {
            getChildren().clear();
        }
        
    }
}
