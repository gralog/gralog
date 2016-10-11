/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
        name = "Assign Labels",
        text = "Assigns consecutive labels to the vertices",
        url = ""
)
public class AssignLabels extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter("");
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
            ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter stringparam = (StringAlgorithmParameter) p;
        int i = 0;
        Set<Vertex> vertices = s.getVertices();
        for (Vertex a : vertices) {
            a.label = stringparam.parameter + i;
            ++i;
        }

        return null;
    }
}
