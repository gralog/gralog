/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;

/**
 *
 */
public interface View {
    void update();
    void update(Object obj);
    void setStructurePane(StructurePane structurePane);
}
