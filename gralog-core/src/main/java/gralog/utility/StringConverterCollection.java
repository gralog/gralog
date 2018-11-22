/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.utility;

import gralog.structure.Edge;
import gralog.structure.Vertex;

import java.util.function.Function;

public final class StringConverterCollection {

    private StringConverterCollection() { }

    public static final Function<Vertex, String> VERTEX_ID = v -> "" + v.getId();

    public static final Function<Edge, String> EDGE_ID = e -> "" + e.getId();
}
