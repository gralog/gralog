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
import java.util.*;

@AlgorithmDescription(
    name = "3-colorability",
    text = "",
    url = "https://en.wikipedia.org/wiki/Graph_coloring")
public class ThreeColorability extends Algorithm {

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
        Collection<Vertex> ver = s.getVertices();
        Set<Edge> edge = s.getEdges();
        for (Vertex i : ver) {
            int x = Integer.parseInt(i.label);
            int cnt = 0;
            int size = 0;
            for (Vertex j : ver) {
                for (Edge e : edge) {
                    if (e.getSource() == i && e.getTarget() == j) {
                        int y = Integer.parseInt(j.label);
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
        for (Vertex v : vertices) {
            v.label = Integer.toString(k);
            k++;
        }

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
                return "Arrangement possible";
            }
        }
        if (onprogress != null) {
            onprogress.onProgress(s);
        }
        return "Contradiction found!!";
    }
}
