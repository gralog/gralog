/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;
import java.util.function.Consumer;

/**
 *
 * @param <T> The object to display.
 */
public interface View<T> {

    void setObject(T obj, Consumer<Boolean> submitPossible);

    void setStructurePane(StructurePane structurePane);

    void onClose();
}
