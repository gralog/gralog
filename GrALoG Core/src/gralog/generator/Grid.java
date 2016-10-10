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
        
        ArrayList<Vertex> last = new ArrayList<>();
        for(int j = 0; j < n; j++)
        {
            Vertex temp = result.CreateVertex();
            last.add(temp);
            temp.Coordinates = new Vector2D(1d, 2d*j + 1d);
            if(j > 0)
                result.AddEdge(result.CreateEdge(last.get(j-1), temp));
            result.AddVertex(temp);
        }
        
        for(int i = 1; i < n; i++)
        {
            ArrayList<Vertex> next = new ArrayList<>();
            Vertex lasttemp = null;
            for(int j = 0; j < n; j++)
            {
                Vertex temp = result.CreateVertex();
                next.add(temp);
                temp.Coordinates = new Vector2D(2d*i + 1d, 2d*j + 1d);
                if(j > 0)
                    result.AddEdge(result.CreateEdge(lasttemp, temp));
                result.AddEdge(result.CreateEdge(last.get(j), temp));
                
                result.AddVertex(temp);
                lasttemp = temp;
            }
            
            last = next;
        }
        
        return result;
    }
}
