/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

import gralog.structure.*;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="Cycle",
  text="Generates a Cyclic-Graph",
  url="https://en.wikipedia.org/wiki/Cycle_(graph_theory)"
)
public class Cycle extends Generator {
    
    // null means it has no parameters
    @Override
    public GeneratorParameters GetParameters() {
        return new StringGeneratorParameter("5");
    }
    
    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        Integer n = Integer.parseInt(((StringGeneratorParameter)p).parameter);
        
        DirectedGraph result = new DirectedGraph();
        
        Vertex first = result.CreateVertex();
        first.Coordinates.add(Math.sin(0 * 2*Math.PI / n) * 3.5 + 3.5);
        first.Coordinates.add(Math.cos(0 * 2*Math.PI / n) * 3.5 + 3.5);
        result.AddVertex(first);
        
        Vertex last = first;
        for(int i = 1; i < n; i++)
        {
            Vertex next = result.CreateVertex();
            next.Coordinates.add(Math.sin(i * 2*Math.PI / n) * 3.5 + 3.5);
            next.Coordinates.add(Math.cos(i * 2*Math.PI / n) * 3.5 + 3.5);
            result.AddEdge(result.CreateEdge(last, next));
            
            last = next;
            result.AddVertex(next);
        }
        result.AddEdge(result.CreateEdge(last, first));
        
        return result;
    }
}
