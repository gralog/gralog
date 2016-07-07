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
  name="Trivial Graph Format",
  text="",
  url="https://en.wikipedia.org/wiki/Trivial_Graph_Format",
  fileextension="tgf"
)
public class TrivialGraphFormatImport extends ImportFilter {

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

            // ignore lines that start with a comment marker ("#").
            if (s.substring(0, 1).equals("#")) {
                s = br.readLine();
                continue;
            }

            StringTokenizer tokenizer = new StringTokenizer(s, " ");
            String from = tokenizer.nextToken();
            String to = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;

            if(to == null)
            {
                Vertex newnode = result.CreateVertex();
                newnode.Label = from;
                if(NodeIndex.containsKey(from))
                    throw new Exception("Vertex-identifier \"" + from + "\" multiply defined");
                NodeIndex.put(from, newnode);
            }
            else
            {
                Vertex nodeA = NodeIndex.containsKey(from) ? NodeIndex.get(from) : null;
                Vertex nodeB = NodeIndex.containsKey(to) ? NodeIndex.get(to) : null;

                if(nodeA == null)
                    throw new Exception("Edge containing undefined Vertex-identifier \"" + from + "\"");
                if(nodeB == null)
                    throw new Exception("Edge containing undefined Vertex-identifier \"" + to + "\"");

                Edge e = result.CreateEdge(nodeA, nodeB);
                result.AddEdge(e);
            }
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
