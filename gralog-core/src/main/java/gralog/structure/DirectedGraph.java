/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;

/**
 *
 */
@StructureDescription(
    name = "Directed Graph",
    text = "",
    url = "https://en.wikipedia.org/wiki/Directed_graph")
@XmlName(name = "digraph")
public class DirectedGraph extends Structure<Vertex, Edge> {

    @Override
    public Vertex createVertex() {
        return new Vertex();
    }

    @Override
    public Vertex createVertex(Configuration config) {
        return new Vertex(config);
    }

    @Override
    public Edge createEdge() {
        return new Edge();
    }
}
