/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

import gralog.rendering.Vector2D;
import gralog.structure.*;
import java.util.ArrayList;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="Cylindrical Grid",
  text="Generates a Cylindrical Grid (the directed equivalent of an undirected grid)",
  url=""
)
public class CylindricalGrid extends Generator {
    
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
        
        ArrayList<Vertex> first = new ArrayList<>();
        for(int j = 0; j < n; j++) {
            Vertex temp = result.CreateVertex();
            first.add(temp);
            temp.Coordinates = new Vector2D(
                    Math.sin(0 * 2*Math.PI / n) * 2 * (j+2),
                    Math.cos(0 * 2*Math.PI / n) * 2 * (j+2)
            );
            if(j > 0)
                result.AddEdge(result.CreateEdge(first.get(j-1), temp));
            result.AddVertex(temp);
        }
        
        ArrayList<Vertex> last = first;
        for(int i = 1; i < n; i++)
        {
            ArrayList<Vertex> next = new ArrayList<>();
            Vertex lasttemp = null;
            for(int j = 0; j < n; j++) {
                Vertex temp = result.CreateVertex();
                next.add(temp);
                temp.Coordinates = new Vector2D(
                        Math.sin(i * 2*Math.PI / n) * 2 * (j+2),
                        Math.cos(i * 2*Math.PI / n) * 2 * (j+2)
                );
                if(lasttemp != null)
                    if(i % 2 == 0)
                        result.AddEdge(result.CreateEdge(lasttemp, temp));
                    else
                        result.AddEdge(result.CreateEdge(temp, lasttemp));
                
                result.AddEdge(result.CreateEdge(last.get(j), temp));
                
                result.AddVertex(temp);
                lasttemp = temp;
            }
            
            last = next;
        }
        
        for(int i = 0; i < n; i++)
            result.AddEdge(result.CreateEdge(last.get(i), first.get(i)));
        
        return result;
    }
}
