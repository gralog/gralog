package gralog.utility;

import gralog.structure.Edge;
import gralog.structure.Vertex;

import java.util.function.Function;

public class StringConverterCollection {

    public static final Function<Vertex, String> VERTEX_ID = v -> "" + v.getId();

    public static final Function<Edge, String> EDGE_ID = e -> "" + e.getId();
}
