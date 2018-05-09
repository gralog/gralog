/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.gralogfx.views.View;
import gralog.gralogfx.views.ViewManager;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 */
public class ObjectInspector extends AnchorPane {

    private View view;

    public ObjectInspector (){
        //this.setPrefWidth(310.0d);
    }
    public void setObject(Object obj, StructurePane structurePane) throws Exception {
        setObject(obj, structurePane, (b) -> {
        });
    }

    public void setObject(Object obj, StructurePane structurePane,
        Consumer<Boolean> submitPossible) throws Exception {
        this.getChildren().clear();


        ScrollPane sp = new ScrollPane();

        sp.setStyle("-fx-background-color:transparent;");


        AnchorPane.setTopAnchor(sp, 4.0);
        AnchorPane.setRightAnchor(sp, 4.0);
        AnchorPane.setBottomAnchor(sp, 4.0);
        AnchorPane.setLeftAnchor(sp, 4.0);

        if (obj == null && structurePane != null) {
            this.getChildren().add(sp);
            return;
        } else if (structurePane == null) {
            return;
        }

        view = ViewManager.instantiateView(obj.getClass());

        if (view == null)
            return;
        if (!(view instanceof Node))
            throw new Exception("Class " + view.getClass().getName() + " is not derived from javafx.scene.Node");

        view.setStructurePane(structurePane);
        view.setObject(obj, submitPossible);

        Node viewNode = (Node) view;

        sp.setContent(viewNode);

        this.getChildren().add(sp);

    }
    public Node getNode(){
        return null;
    }
    /**
     * This event handler is called when the stage is about to be closed.
     */
    public final void onClose() {
        if (view != null)
            view.onClose();
    }
}
