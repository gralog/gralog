/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.events;

import gralog.structure.*;
import java.util.EventListener;

/**
 *
 */
public interface VertexListener<V extends Vertex> extends EventListener {

    void vertexChanged(VertexEvent<V> e);
}
