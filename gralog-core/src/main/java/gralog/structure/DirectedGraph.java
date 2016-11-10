/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import gralog.plugins.XmlName;

/**
 *
 */
@StructureDescription(
        name = "Directed Graph",
        text = "",
        url = "https://en.wikipedia.org/wiki/Directed_graph"
)
@XmlName(name = "digraph")
public class DirectedGraph extends Structure<Vertex, Edge> {

    @Override
    public Vertex createVertex() {
        return new Vertex();
    }

    @Override
    public Edge createEdge() {
        return new Edge();
    }
}
