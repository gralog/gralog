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
        name = "Undirected Graph",
        text = "",
        url = "https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)#Undirected_graph"
)
@XmlName(name = "graph")
public class UndirectedGraph extends Structure<Vertex, Edge> {

    @Override
    public Vertex createVertex() {
        return new Vertex();
    }

    @Override
    public Edge createEdge() {
        Edge result = new Edge();
        result.isDirected = false;
        return result;
    }
}
