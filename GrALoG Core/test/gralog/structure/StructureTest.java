/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import gralog.generator.Cycle;
import gralog.generator.CylindricalGrid;
import gralog.generator.StringGeneratorParameter;
import gralog.plugins.PluginManager;
import gralog.rendering.GralogColor;
import gralog.rendering.Vector2D;
import static gralog.structure.StructureMatchers.equalsStructure;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamResult;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

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
        }
        catch (Exception ex) {
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
            Structure structureRead = Structure.loadFromStream(new ByteArrayInputStream(writer.toString().getBytes()));
            assertThat(structureRead, equalsStructure(structure));
        }
        catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private static Vertex addVertex(Structure structure, String label) {
        Vertex v = structure.createVertex();
        v.label = label;
        structure.addVertex(v);
        return v;
    }

    @Test
    public void testSimpleDirectedGraph() {
        Structure<Vertex, Edge> structure = new DirectedGraph();

        Vertex v = addVertex(structure, "v");
        Vertex w = addVertex(structure, "w");
        structure.addEdge(structure.createEdge(v, w));

        testWriteRead(structure);
    }

    @Test
    public void testSimpleUndirectedGraph() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = addVertex(structure, "v");
        Vertex w = addVertex(structure, "w");
        structure.addEdge(structure.createEdge(v, w));

        testWriteRead(structure);
    }

    @Test
    public void testSimpleUndirectedGraphLoop() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = addVertex(structure, "v");
        structure.addEdge(structure.createEdge(v, v));

        testWriteRead(structure);
    }

    @Test
    public void testCycleReading() {
        Structure<Vertex, Edge> structure = (new Cycle()).generate(new StringGeneratorParameter("5"));
        int i = 0;
        for (Vertex v : structure.getVertices())
            v.label = "" + i++;
        testWriteRead(structure);
    }

    @Test
    public void testCylindricalGridReading() {
        Structure<Vertex, Edge> structure = (new CylindricalGrid()).generate(new StringGeneratorParameter("5"));
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

        Vertex v = addVertex(structure, "v");
        Vertex w = addVertex(structure, "w");
        structure.addEdge(structure.createEdge(v, w));

        int i = 0;
        for (Vertex u : structure.getVertices()) {
            u.coordinates = new Vector2D((double) i, (double) i);
            u.radius = i;
            u.strokeWidth = i;
            u.textHeight = i;
            u.fillColor = new GralogColor(i, i, i);
            u.strokeColor = new GralogColor(i, i, i);
        }
        testWriteRead(structure);
    }
}
