/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.Description;
import gralog.plugins.XmlName;

/**
 *
 * @author viktor
 */
@Description(
  name="Directed Graph",
  text="",
  url="https://en.wikipedia.org/wiki/Directed_graph"
)
@XmlName(name="digraph")
public class DirectedGraph extends Structure<Vertex,Edge> {

    @Override
    public Vertex CreateVertex() {
        return new Vertex();
    }

    @Override
    public Edge CreateEdge() {
        return new Edge();
    }

}
