/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.algorithm;

/**
 *
 */
import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.rendering.GralogColor;
import javafx.scene.control.Alert;

import java.util.*;

@AlgorithmDescription(
    name = "Majority colorings",
    text = "Finds a coloring with 3 colors (if possible) in a digraph such that at most half of direct successors of a vertex have ot's color.",
    url = "https://arxiv.org/abs/1608.03040")
public class MajorityColoring extends Algorithm {

    /*
    * Converts an integer into the base-3 representation and fills leading places with zeros until arity nVertices.
     */
    public static StringBuilder convertToBase3(long n, int nVertices) {
        StringBuilder ans = new StringBuilder();
        while (n > 0) {
            int rem = (int) (n % 3);
            n = n / 3;
            String temp = Integer.toString(rem);
            ans.append(temp);
        }
        ans = ans.reverse();
        while (ans.length() < nVertices) {
            ans.append('0');
        }
        return ans;
    }

    boolean check(StringBuilder str, DirectedGraph s) {
        Collection<Vertex> vertices = s.getVertices();
        Set<Edge> edges = s.getEdges();
        for (Vertex vertex : vertices) {
            int x = Integer.parseInt(vertex.label);
            int cnt = 0;
            int size = 0;
            for (Vertex w : vertices) {
                for (Edge edge : edges) {
                    if (edge.getSource() == vertex && edge.getTarget() == w) {
                        int y = Integer.parseInt(w.label);
                        if (str.charAt(x) == str.charAt(y)) {

                            cnt = cnt + 1;

                        }
                        size = size + 1;

                    }
                }

            }
            if (cnt > (size / 2)) {

                return false;
            }
        }

        return true;
    }

    public Object run(DirectedGraph s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        Collection<Vertex> vertices = s.getVertices();
        int nVertices = vertices.size();
        int k = 0;
        // save labels
        HashMap<Vertex, String> label= new HashMap<>();
        for (Vertex vertex : vertices)
            label.put(vertex, vertex.label);

        // misuse labels
        for (Vertex v : vertices) {
            v.label = Integer.toString(k);
            k++;
        }

        // iterate over all 3-subsets of the vertex set
        for (long i = 1; i <= Math.pow(3, nVertices); i++) {
            StringBuilder str = convertToBase3(i, nVertices);
            if (check(str, s)) {
                int j = 0;
                for (Vertex v : vertices) {
                    String x = Character.toString(str.charAt(j));
                    switch (x) {
                        case "0":
                            v.fillColor = GralogColor.BLUE;
                            break;
                        case "1":
                            v.fillColor = GralogColor.GREEN;
                            break;
                        default:
                            v.fillColor = GralogColor.RED;
                            break;
                    }
                    j = j + 1;

                }
                if (onprogress != null) {
                    onprogress.onProgress(s);
                }

                // reset all labels to previous values
                for (Vertex vertex : vertices)
                    vertex.setLabel(label.get(vertex));

                return "Arrangement possible";
            }
        }
        if (onprogress != null) {
            onprogress.onProgress(s);
        }
        // reset all labels to previous values
        for (Vertex vertex : vertices)
            vertex.label = label.get(vertex);

        return "Contradiction found!!";
    }
}
