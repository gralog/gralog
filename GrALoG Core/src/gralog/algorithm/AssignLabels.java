/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
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
        return new StringAlgorithmParameter("Starting number", "");
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
