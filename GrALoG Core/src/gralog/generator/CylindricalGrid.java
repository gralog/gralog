/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

import gralog.structure.*;
import java.util.Vector;

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
        
        Vector<Vertex> first = new Vector<Vertex>();
        for(int j = 0; j < n; j++) {
            Vertex temp = result.CreateVertex();
            first.add(temp);
            temp.Coordinates.add(Math.sin(0 * 2*Math.PI / n) * 2 * (j+2));
            temp.Coordinates.add(Math.cos(0 * 2*Math.PI / n) * 2 * (j+2));
            if(j > 0)
                result.AddEdge(result.CreateEdge(first.elementAt(j-1), temp));
            result.AddVertex(temp);
        }
        
        Vector<Vertex> last = first;
        for(int i = 1; i < n; i++)
        {
            Vector<Vertex> next = new Vector<Vertex>();
            Vertex lasttemp = null;
            for(int j = 0; j < n; j++) {
                Vertex temp = result.CreateVertex();
                next.add(temp);
                temp.Coordinates.add(Math.sin(i * 2*Math.PI / n) * 2 * (j+2));
                temp.Coordinates.add(Math.cos(i * 2*Math.PI / n) * 2 * (j+2));
                if(lasttemp != null)
                    if(i % 2 == 0)
                        result.AddEdge(result.CreateEdge(lasttemp, temp));
                    else
                        result.AddEdge(result.CreateEdge(temp, lasttemp));
                
                result.AddEdge(result.CreateEdge(last.elementAt(j), temp));
                
                result.AddVertex(temp);
                lasttemp = temp;
            }
            
            last = next;
        }
        
        for(int i = 0; i < n; i++)
            result.AddEdge(result.CreateEdge(last.elementAt(i), first.elementAt(i)));
        
        return result;
    }
}
