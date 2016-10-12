/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx;

import gralog.gralogfx.views.View;
import gralog.gralogfx.views.ViewManager;

import java.util.Set;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

/**
 *
 */
public class ObjectInspector extends AnchorPane {

    public ObjectInspector() {
        this.setMinWidth(200.0d);
        try {
            setObject(null, null);
        }
        catch (Exception ex) {
            // does not happen with null parameter
        }
    }

    public void setObjects(Set<Object> objects, StructurePane structurePane) throws Exception {
        setObject(null, null);
        if (objects != null && objects.size() == 1)
            for (Object o : objects)
                setObject(o, structurePane);
    }

    public void setObject(Object obj, StructurePane structurePane) throws Exception {
        this.getChildren().clear();
        if (obj == null)
            return;

        View view = ViewManager.instantiateView(obj.getClass());
        if (view == null)
            return;
        if (!(view instanceof Node))
            throw new Exception("Class " + view.getClass().getName() + " is not derived from javafx.scene.Node");

        view.setStructurePane(structurePane);
        view.update(obj);
        this.getChildren().add((Node) view);

        AnchorPane.setTopAnchor((Node) view, 3.0);
        AnchorPane.setRightAnchor((Node) view, 3.0);
        AnchorPane.setBottomAnchor((Node) view, 3.0);
        AnchorPane.setLeftAnchor((Node) view, 3.0);
    }
}
