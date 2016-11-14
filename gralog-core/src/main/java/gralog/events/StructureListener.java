/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.events;

import gralog.structure.*;
import java.util.EventListener;

/**
 *
 */
public interface StructureListener<V extends Vertex, E extends Edge> extends EventListener {

    void structureChanged(StructureEvent<V, E> e);

    void vertexChanged(VertexEvent<V> e);

    void edgeChanged(EdgeEvent<E> e);
}
