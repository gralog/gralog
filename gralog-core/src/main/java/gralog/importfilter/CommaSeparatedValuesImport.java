/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.importfilter;

import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 */
@ImportFilterDescription(
        name = "Comma Separated Values",
        text = "",
        url = "https://en.wikipedia.org/wiki/Trivial_Graph_Format",
        fileExtension = "csv")
public class CommaSeparatedValuesImport extends ImportFilter {

    String cellSeparator = ",";
    // char RecordSeparator = '\n'; // cannot be changed in BufferedReader

    @Override
    public Structure importGraph(InputStream stream,
                                 ImportFilterParameters params) throws Exception {
        DirectedGraph result = new DirectedGraph();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String s = br.readLine();

            HashMap<String, Vertex> nodeIndex = new HashMap<>();

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

                for (String vertstring : new String[] {sfrom, sto}) {
                    if (!nodeIndex.containsKey(vertstring)) {
                        Vertex newnode = result.createVertex();
                        nodeIndex.put(vertstring, newnode);
                    }
                }

                Vertex vfrom = nodeIndex.get(sfrom);
                Vertex vto = nodeIndex.get(sto);
                result.addEdge(result.createEdge(vfrom, vto));

                s = br.readLine();
            }

            for (Vertex newnode : nodeIndex.values()) {
                newnode.setCoordinates(
                        Math.random() * 3d * nodeIndex.size(),
                        Math.random() * 3d * nodeIndex.size());
                result.addVertex(newnode);
            }
        }
        return result;
    }
}
