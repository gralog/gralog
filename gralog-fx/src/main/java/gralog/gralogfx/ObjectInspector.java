/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx;

import gralog.gralogfx.views.View;
import gralog.gralogfx.views.ViewManager;

import java.util.function.Consumer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

/**
 *
 */
public class ObjectInspector extends AnchorPane {

    private View view;

    public ObjectInspector() {
        this.setMinWidth(200.0d);
    }

    public void setObject(Object obj, StructurePane structurePane) throws Exception {
        setObject(obj, structurePane, (b) -> {
        });
    }

    public void setObject(Object obj, StructurePane structurePane,
        Consumer<Boolean> submitPossible) throws Exception {
        this.getChildren().clear();
        if (obj == null)
            return;

        view = ViewManager.instantiateView(obj.getClass());
        if (view == null)
            return;
        if (!(view instanceof Node))
            throw new Exception("Class " + view.getClass().getName() + " is not derived from javafx.scene.Node");

        view.setStructurePane(structurePane);
        view.setObject(obj, submitPossible);
        this.getChildren().add((Node) view);

        AnchorPane.setTopAnchor((Node) view, 3.0);
        AnchorPane.setRightAnchor((Node) view, 3.0);
        AnchorPane.setBottomAnchor((Node) view, 3.0);
        AnchorPane.setLeftAnchor((Node) view, 3.0);
    }

    /**
     * This event handler is called when the stage is about to be closed.
     */
    public final void onClose() {
        if (view != null)
            view.onClose();
    }
}
