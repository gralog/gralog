/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Spring Embedder 3D",
  text="Assigns coordinates to the vertices using the spring-embedding techinque",
  url="https://en.wikipedia.org/wiki/Force-directed_graph_drawing"
)
public class SpringEmbedder3D extends SpringEmbedder {
 
    public SpringEmbedder3D() {
        super();
        this.dimension_limits.add(20d);
    }
    
}
