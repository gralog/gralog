/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.GridParameters;
import gralog.algorithm.RandomGraphParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.StringAlgorithmParametersList;
import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the generators generate the expected structure type and the
 * expected number of vertices/edges.
 */
public class GeneratorTest {

    @Test
    public void testCycle() throws Exception {
        Structure result = (new Cycle()).generate(new StringAlgorithmParameter("", "5"));
        assertTrue(result instanceof DirectedGraph);
        assertEquals(result.getVertices().size(), 5);
        assertEquals(result.getEdges().size(), 5);
    }

    @Test
    public void testCylindricalGrid() throws Exception {

        int length = 8, width = 7;
        Structure result = (new CylindricalGrid()).generate(new GridParameters(Arrays.asList(Integer.toString(length),
                Integer.toString(width))));
        assertTrue(result instanceof DirectedGraph);
        assertEquals(length*width,result.getVertices().size());
        assertEquals(length*width+(length-1)*width,result.getEdges().size());
    }

    @Test
    public void testRandomgraph() throws Exception {

        int n = 8;
        double p = 0.2;
        boolean directed = true;
        Structure result = (new RandomGraph()).generate(new RandomGraphParameters(Arrays.asList(Integer.toString(n),
                Double.toString(p), Boolean.toString(directed))));
        assertTrue(result instanceof DirectedGraph);
        assertEquals(n,result.getVertices().size());
    }


    @Test
    public void testGrid() throws Exception {
        int length = 8, width = 7;
        Structure result = (new Grid()).generate(new GridParameters(Arrays.asList(Integer.toString(length),
                Integer.toString(width))));
        assertTrue(result instanceof UndirectedGraph);
        assertEquals(length*width,result.getVertices().size());
        assertEquals((length-1)*width + (width-1)*length,result.getEdges().size());
    }

    @Test
    public void testWheel() throws Exception {
        Structure result = (new Wheel()).generate(new StringAlgorithmParameter("", "5"));
        assertTrue(result instanceof UndirectedGraph);
        assertEquals(result.getVertices().size(), 6);
        assertEquals(result.getEdges().size(), 10);
    }
}
