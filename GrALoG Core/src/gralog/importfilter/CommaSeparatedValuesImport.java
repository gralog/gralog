/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import gralog.rendering.Vector2D;
import gralog.structure.*;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 *
 */
@ImportFilterDescription(
        name = "Comma Separated Values",
        text = "",
        url = "https://en.wikipedia.org/wiki/Trivial_Graph_Format",
        fileExtension = "csv"
)
public class CommaSeparatedValuesImport extends ImportFilter {

    String cellSeparator = ",";
    // char RecordSeparator = '\n'; // cannot be changed in BufferedReader

    @Override
    public Structure importGraph(InputStream stream,
            ImportFilterParameters params) throws Exception {
        DirectedGraph result = new DirectedGraph();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String s = br.readLine();

            HashMap<String, Vertex> NodeIndex = new HashMap<>();

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

                StringTokenizer tokenizer = new StringTokenizer(s, cellSeparator);
                String sfrom = tokenizer.nextToken();
                String sto = tokenizer.nextToken();

                for (String vertstring : new String[]{sfrom, sto}) {
                    if (!NodeIndex.containsKey(vertstring)) {
                        Vertex newnode = result.createVertex();
                        NodeIndex.put(vertstring, newnode);
                    }
                }

                Vertex vfrom = NodeIndex.get(sfrom);
                Vertex vto = NodeIndex.get(sto);
                result.addEdge(result.createEdge(vfrom, vto));

                s = br.readLine();
            }

            for (Vertex newnode : NodeIndex.values()) {
                newnode.coordinates = new Vector2D(
                        Math.random() * 3d * NodeIndex.size(),
                        Math.random() * 3d * NodeIndex.size());
                result.addVertex(newnode);
            }
        }
        return result;
    }
}
