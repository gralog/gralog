/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages highlights and annotations of vertices and edges.
 */
public class Highlights {

    private final Set<Object> selection = new HashSet<>();
    private final Map<Object, String> annotations = new HashMap<>();

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

    /**
     * Annotates the given vertex or edge with the given string. Overrides the
     * old annotation for this vertex/edge if present.
     *
     * @param o A vertex or an edge.
     * @param annotation The annotation.
     */
    public void annotate(Object o, String annotation) {
        annotations.put(o, annotation);
    }

    /**
     * Removes all annotations from all vertices and all edges.
     */
    public void clearAnnotations() {
        annotations.clear();
    }

    /**
     * Returns the string annotation for the given vertex or edge.
     *
     * @param o A vertex or an edge.
     * @return The string annotation for the given vertex or edge.
     */
    public String getAnnotation(Object o) {
        return annotations.get(o);
    }
}
