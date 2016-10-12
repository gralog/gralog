/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;
import javafx.scene.layout.GridPane;

/**
 *
 */
public abstract class GridPaneView extends GridPane implements View {

    protected StructurePane structurePane = null;
    protected Object displayObject = null;

    @Override
    public void update(Object newObject) {
        this.displayObject = newObject;
        update();
    }

    @Override
    public void setStructurePane(StructurePane structurePane) {
        this.structurePane = structurePane;
    }

    public void requestRedraw() {
        StructurePane sp = structurePane;
        if (sp != null)
            sp.requestRedraw();
    }
}
