/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.gralogfx.views.View;
import gralog.gralogfx.views.ReflectedView;
import gralog.gralogfx.views.ViewManager;

import java.util.HashMap;
import java.util.Set;
import java.lang.reflect.Constructor;
import javafx.scene.layout.Pane;
import javafx.scene.Node;

/**
 *
 * @author viktor
 */
public class ObjectInspector extends Pane {
    
    
    public ObjectInspector() {
        this.setMinWidth(200.0d);
        try {
            SetObject(null, null);
        } catch (Exception ex) {
            // does not happen with null parameter
        }
    }
    
    
    public void SetObjects(Set<Object> objects, StructurePane structurePane) throws Exception {
        SetObject(null, null);
        if(objects != null && objects.size() == 1)
            for(Object o : objects)
                SetObject(o, structurePane);
    }
    
    
    public void SetObject(Object obj, StructurePane structurePane) throws Exception {
        this.getChildren().clear();
        if(obj == null)
            return;

        View view = ViewManager.InstantiateView(obj.getClass());
        if(view == null)
            return;
        if(!(view instanceof Node))
            throw new Exception("Class " + view.getClass().getName() + " is not derived from javafx.scene.Node");
        
        view.setStructurePane(structurePane);
        view.Update(obj);
        this.getChildren().add((Node)view);
    }
    
}
