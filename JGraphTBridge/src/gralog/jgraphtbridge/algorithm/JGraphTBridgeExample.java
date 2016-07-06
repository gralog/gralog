/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.jgraphtbridge.algorithm;

import gralog.algorithm.AlgorithmDescription;
import gralog.structure.Vertex;
import gralog.algorithm.AlgorithmParameters;
import gralog.progresshandler.ProgressHandler;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="JGraphT Bridge Example",
  text="Test of the Bridge to JGraphT",
  url="http://jgrapht.org/"
)

public class JGraphTBridgeExample extends JGraphTAlgorithm {
    
    @Override
    public Object JGraphTRun(Graph<Vertex,DefaultEdge> g, AlgorithmParameters ap, ProgressHandler onprogress) throws Exception
    {
        int i = 0;
        for(Vertex v : g.vertexSet())
            i++;
        return ""+i+" nodes (computed through JGraphTBridge)";
    }
    
}
