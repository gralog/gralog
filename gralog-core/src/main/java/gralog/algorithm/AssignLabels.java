/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.preferences.Preferences;
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
        return new StringAlgorithmParameter(
            "Prefix",
            Preferences.getString(this.getClass(), "prefix", ""),
            "Vertices will be labeled prefix0, prefix1, etc.");
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter stringparam = (StringAlgorithmParameter) p;
        Preferences.setString(this.getClass(), "prefix", stringparam.parameter);

        int i = 0;
        Set<Vertex> vertices = s.getVertices();
        for (Vertex a : vertices) {
            a.label = stringparam.parameter + i;
            ++i;
        }

        return null;
    }
}
