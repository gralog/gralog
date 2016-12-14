/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages highlights and annotations of vertices and edges.
 */
public class Highlights {

    private final Set<Object> selection = new HashSet<>();

    public void select(Object o) {
        selection.add(o);
    }

    public void selectAll(Collection elems) {
        for (Object o : elems)
            selection.add(o);
    }

    public void clearSelection() {
        selection.clear();
    }

    public Set<Object> getSelection() {
        return selection;
    }

    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }

    /**
     * Returns true if the given vertex or edge is currently selected. There may
     * be more than one currently selected vertex or edge.
     *
     * @param o A vertex or an edge.
     * @return True if the vertex or edge is currently selected.
     */
    public boolean isSelected(Object o) {
        return selection.contains(o);
    }
}
