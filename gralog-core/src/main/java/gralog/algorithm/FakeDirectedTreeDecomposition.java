/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import gralog.treedecomposition.Bag;
import gralog.treedecomposition.TreeDecomposition;

import javax.swing.text.Highlighter;
import java.util.Collection;
import java.util.Set;

@AlgorithmDescription(
        name = "Fake Directed Tree Decomposition",
        text = "Creates a (fake) directed tree decomposition",
        url = "http://dx.doi.org/10.1006/jctb.2000.2031"
)
public class FakeDirectedTreeDecomposition extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return null; // new DirectedTreeDecompositionParameters();
    }

    protected Bag fakeTreeDecomposition(Structure s, int depth, int width,
                                        int children) {
        Bag result = new Bag();
        Collection<Vertex> setV = s.getVertices();

        for (int i = 0; i < width; i++) {
            int idx = (int) (Math.random() * ((Collection) setV).size());
            for (Vertex v : setV)
                if (--idx < 0) {
                    result.nodes.add(v);
                    break;
                }
        }

        if (depth > 0)
            for (int i = 0; i < children; i++)
                result.childBags.add(fakeTreeDecomposition(s, depth - 1, width, children));

        return result;
    }

    public Object run(Structure s, AlgorithmParameters ap, Set<Object> selection,
                      ProgressHandler onprogress) throws Exception {
        // DirectedTreeDecompositionParameters p = (DirectedTreeDecompositionParameters)ap;

        TreeDecomposition dtdecomp = new TreeDecomposition();

        dtdecomp.rootBag = fakeTreeDecomposition(s, 5, 6, 2);

        return dtdecomp;
    }

}
