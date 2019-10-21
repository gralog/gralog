/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;


import java.util.*;

/**
 * Manages highlights and annotations of vertices and edges.
 */
public class Highlights {

    private final LinkedHashSet<Object> selection = new LinkedHashSet<>();
    private final Map<Object, String> annotations = new HashMap<>();
    private Object lastAdded;

    public void select(Object o) {
        lastAdded = o;
        selection.add(o);
    }

    public Object lastAdded() {
        return lastAdded;
    }

    public void selectAll(Collection<?> elems) {
        selection.addAll(elems);
    }

    public void deselectAll(Collection<?> elems) {
        selection.removeAll(elems);
    }

    public void clearSelection() {
        selection.clear();
        lastAdded = null;
    }

    public void remove(Object o) {
        selection.remove(o);
    }

    public void removeAll(List o) {
        selection.removeAll(o);
    }

    /**
     * Removes all objects from the selection that have specified type.*/
    public void filterType(Class<?> t) {
        List<Object> l = new LinkedList<>();
        for (Object o : selection) {
            if (!(t.isInstance(o))) {
                l.add(o);
            }
        }
        selection.removeAll(l);
    }

    public ArrayList<Object> getFilteredByType(Class<?> t){
        ArrayList<Object> list = new ArrayList<>();
        for (Object o: selection){
            if (t.isInstance(o)){
                list.add(o);
            }
        }
        return list;
    }

    public LinkedHashSet<Object> getSelection() {
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
     * @param o          A vertex or an edge.
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
