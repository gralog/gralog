/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.events;

import gralog.structure.Vertex;

import java.util.EventObject;

/**
 *
 */
public class VertexEvent<V extends Vertex> extends EventObject {

    public VertexEvent(V source) {
        super(source);
    }
}
