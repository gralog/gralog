/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import gralog.structure.*;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.StringTokenizer;


/**
 *
 * @author viktor
 */
@ImportFilterDescription(
  name="Comma Separated Values",
  text="",
  url="https://en.wikipedia.org/wiki/Trivial_Graph_Format",
  fileextension="csv"
)
public class CommaSeparatedValuesImport extends ImportFilter {

    String CellSeparator = ",";
    // char RecordSeparator = '\n'; // cannot be changed in BufferedReader
    
    @Override
    public Structure Import(InputStream stream, ImportFilterParameters params) throws Exception {
        DirectedGraph result = new DirectedGraph();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String s = br.readLine();

        HashMap<String, Vertex> NodeIndex = new HashMap<String, Vertex>();
        
        while (s != null) {

            s = s.trim();

            // ignore blank lines.
            if (s.equals("")) {
                s = br.readLine();
                continue;
            }

            // ignore lines that start with a comment marker ("%").
            if (s.substring(0, 1).equals("%")) {
                s = br.readLine();
                continue;
            }

            StringTokenizer tokenizer = new StringTokenizer(s, CellSeparator);
            String sfrom = tokenizer.nextToken();
            String sto = tokenizer.nextToken();

            for(String vertstring : new String[]{sfrom, sto})
            {
                if(!NodeIndex.containsKey(vertstring))
                {
                    Vertex newnode = result.CreateVertex();
                    NodeIndex.put(vertstring, newnode);
                }
            }

            Vertex vfrom = NodeIndex.get(sfrom);
            Vertex vto = NodeIndex.get(sto);
            result.AddEdge(result.CreateEdge(vfrom, vto));
            
            s = br.readLine();
        }
        
        for(Vertex newnode : NodeIndex.values())
        {
            newnode.Coordinates.add(Math.random()*3d*NodeIndex.size());
            newnode.Coordinates.add(Math.random()*3d*NodeIndex.size());
            result.AddVertex(newnode);
        }

        br.close();
        return result;

    }
    
}
