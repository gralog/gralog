/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.GridParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.generator.Cycle;
import gralog.generator.CylindricalGrid;
import gralog.plugins.PluginManager;
import gralog.rendering.GralogColor;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gralog.structure.StructureMatchers.equalsStructure;
import static org.junit.Assert.assertThat;

public class StructureTest {

    @BeforeClass
    public static void setUpClass() {
        try {
            // We need to register these classes or reading from the XML won't work.
            PluginManager.registerClass(EdgeIntermediatePoint.class);
            PluginManager.registerClass(Vertex.class);
            PluginManager.registerClass(Edge.class);
            PluginManager.registerClass(UndirectedGraph.class);
            PluginManager.registerClass(DirectedGraph.class);
        } catch (Exception ex) {
            Logger.getLogger("StructureTest").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Tests if writing the given structure to XML and then reading it again
     * from the XML produces an equivalent structure.
     *
     * @param structure The structure to test.
     */
    private void testWriteRead(Structure structure) {
        try {
            StringWriter writer = new StringWriter();
            structure.writeToStream(new StreamResult(writer));
            Structure structureRead = Structure.loadFromStream(
                    new ByteArrayInputStream(writer.toString().getBytes("UTF-8")));
            assertThat(structureRead, equalsStructure(structure));
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    @Test
    public void testSimpleDirectedGraph() {
        Structure<Vertex, Edge> structure = new DirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);

        testWriteRead(structure);
    }

    @Test
    public void testSimpleUndirectedGraph() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);

        testWriteRead(structure);
    }

    @Test
    public void testSimpleUndirectedGraphLoop() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        structure.addEdge(v, v);

        testWriteRead(structure);
    }

    @Test
    public void testCycleReading() {
        Structure<Vertex, Edge> structure = (new Cycle()).generate(new StringAlgorithmParameter("", "5"));
        int i = 0;
        for (Vertex v : structure.getVertices())
            v.label = "" + i++;
        testWriteRead(structure);
    }

    @Test
    public void testCylindricalGridReading() {
        List<String> parameters = Arrays.asList("6","5");
        Structure<Vertex, Edge> structure = (new CylindricalGrid()).generate(new GridParameters(parameters));
        int i = 0;
        for (Vertex v : structure.getVertices())
            v.label = "" + i++;
        testWriteRead(structure);
    }

    /**
     * Tests vertices with all their attributes.
     */
    @Test
    public void testComplexVertexReading() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);

        int i = 0;
        for (Vertex u : structure.getVertices()) {
            u.setCoordinates((double) i, (double) i);
            u.radius = i;
            u.strokeWidth = (double) i;
            u.textHeight = (double) i;
            u.fillColor = new GralogColor(i, i, i);
            u.strokeColor = new GralogColor(i, i, i);
        }
        testWriteRead(structure);
    }
}
