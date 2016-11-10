/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.events;

import gralog.structure.*;

import java.util.EventObject;

/**
 *
 */
public class StructureEvent<V extends Vertex, E extends Edge> extends EventObject {
    public StructureEvent(Structure<V,E> source) {
        super(source);
    }
}
