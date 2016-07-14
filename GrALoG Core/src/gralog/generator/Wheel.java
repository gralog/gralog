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
  name="Wheel",
  text="Generates a Wheel-Graph",
  url="https://en.wikipedia.org/wiki/Wheel_graph"
)
public class Wheel extends Generator {
    
    @Override
    public GeneratorParameters GetParameters() {
        return new StringGeneratorParameter("5");
    }
    
    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        Integer n = Integer.parseInt(((StringGeneratorParameter)p).parameter);
        
        UndirectedGraph result = new UndirectedGraph();

        Vertex center = result.CreateVertex();
        center.Coordinates.add(3.5);
        center.Coordinates.add(3.5);
        result.AddVertex(center);
        
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
            result.AddEdge(result.CreateEdge(last, center));
            
            last = next;
            result.AddVertex(next);
        }
        result.AddEdge(result.CreateEdge(last, first));
        result.AddEdge(result.CreateEdge(last, center));
        
        return result;
    }
}
