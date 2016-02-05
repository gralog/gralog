/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.gralogfx.views.View;
import gralog.gralogfx.views.ReflectedView;

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
    
    
    HashMap<Class, Class> SkinFactory = new HashMap<Class,Class>();
    
    
    public ObjectInspector() {
        this.setMinWidth(200.0d);
        try {
            SetObject(null, null);
        } catch (Exception ex) {
            // does not happen with null parameter
        }
        AddSkin(Object.class, ReflectedView.class);
    }
    
    public void AddSkin(Class forClass, Class skinClass) {
        SkinFactory.put(forClass, skinClass);
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
        
        Class skinClass = null;
        for(Class sup = obj.getClass(); sup != null && skinClass == null; sup = sup.getSuperclass())
            if(SkinFactory.containsKey(sup))
                skinClass = SkinFactory.get(sup);
        if(skinClass == null)
            return;
        
        Constructor ctor = skinClass.getConstructor(new Class<?>[]{});
        if(ctor == null)
            throw new Exception("Class " + skinClass.getName() + " has no constructor with empty signature");
        Object view = ctor.newInstance(new Object[]{});
        if(!(view instanceof Node))
            throw new Exception("Class " + skinClass.getName() + " is not derived from javafx.scene.Node");
        if(!(view instanceof View))
            throw new Exception("Class " + skinClass.getName() + " does not implement gralog.gralogfx.views.View");
        
        View v = (View)view;
        v.Update(obj);
        v.setStructurePane(structurePane);
        this.getChildren().add((Node)view);
    }
    
}
