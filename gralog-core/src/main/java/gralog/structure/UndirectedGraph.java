/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;

import java.io.Serializable;

/**
 *
 */
@StructureDescription(
        name = "Undirected Graph",
        text = "",
        url = "https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)#Undirected_graph")
@XmlName(name = "graph")
public class UndirectedGraph extends Structure<Vertex, Edge> implements Serializable {

    @Override
    public Vertex createVertex() {
        return new Vertex();
    }

    @Override
    public Vertex createVertex(Configuration config) {
        return new Vertex(config);
    }

    @Override
    public Edge createEdge(Configuration config) {
        Edge result = new Edge(config);
        result.setDirectedness(false);
        return result;
    }
}
