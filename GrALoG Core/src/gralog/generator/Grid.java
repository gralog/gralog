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
  name="Grid",
  text="Generates a Grid-Graph",
  url="https://en.wikipedia.org/wiki/Lattice_graph"
)
public class Grid extends Generator {
    
    // null means it has no parameters
    @Override
    public GeneratorParameters GetParameters() {
        return new StringGeneratorParameter("5");
    }
    
    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        Integer n = Integer.parseInt(((StringGeneratorParameter)p).parameter);
        
        UndirectedGraph result = new UndirectedGraph();
        
        Vector<Vertex> last = new Vector<Vertex>();
        for(int j = 0; j < n; j++)
        {
            Vertex temp = result.CreateVertex();
            last.add(temp);
            temp.Coordinates.add(1d);
            temp.Coordinates.add(2d*j + 1d);
            if(j > 0)
                result.AddEdge(result.CreateEdge(last.elementAt(j-1), temp));
            result.AddVertex(temp);
        }
        
        for(int i = 1; i < n; i++)
        {
            Vector<Vertex> next = new Vector<Vertex>();
            Vertex lasttemp = null;
            for(int j = 0; j < n; j++)
            {
                Vertex temp = result.CreateVertex();
                next.add(temp);
                temp.Coordinates.add(2d*i + 1d);
                temp.Coordinates.add(2d*j + 1d);
                if(j > 0)
                    result.AddEdge(result.CreateEdge(lasttemp, temp));
                result.AddEdge(result.CreateEdge(last.elementAt(j), temp));
                
                result.AddVertex(temp);
                lasttemp = temp;
            }
            
            last = next;
        }
        
        return result;
    }
}
