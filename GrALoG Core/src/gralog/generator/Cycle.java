/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.generator;

import gralog.rendering.Vector2D;
import gralog.structure.*;

/**
 *
 */
@GeneratorDescription(
  name="Cycle",
  text="Generates a Cyclic-Graph",
  url="https://en.wikipedia.org/wiki/Cycle_(graph_theory)"
)
public class Cycle extends Generator {
    
    @Override
    public GeneratorParameters getParameters() {
        return new StringGeneratorParameter("5");
    }
    
    @Override
    public Structure generate(GeneratorParameters p)
    {
        Integer n = Integer.parseInt(((StringGeneratorParameter)p).parameter);
        
        DirectedGraph result = new DirectedGraph();
        
        Vertex first = result.createVertex();
        first.coordinates = new Vector2D(
                Math.sin(0 * 2*Math.PI / n) * 3.5 + 3.5,
                Math.cos(0 * 2*Math.PI / n) * 3.5 + 3.5
        );
        result.addVertex(first);
        
        Vertex last = first;
        for(int i = 1; i < n; i++)
        {
            Vertex next = result.createVertex();
            next.coordinates = new Vector2D(
                    Math.sin(i * 2*Math.PI / n) * 3.5 + 3.5,
                    Math.cos(i * 2*Math.PI / n) * 3.5 + 3.5
            );
            result.addEdge(result.createEdge(last, next));
            
            last = next;
            result.addVertex(next);
        }
        result.addEdge(result.createEdge(last, first));
        
        return result;
    }
}
