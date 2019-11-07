/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.structure.*;
import org.junit.Test;

import java.util.*;

import static gralog.structure.StructureMatchers.equalsVertexSet;
import static org.junit.Assert.*;

/**
 *
 */
public class StronglyConnectedComponentsTest {

    @Test
    public void testTarjanStrongComponentsAcyclic() {
        Structure<Vertex, Edge> structure = new DirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);

        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        HashSet<HashSet<Vertex>> verticesInComponent = new HashSet<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure,
                componentOfVertex,
                verticesInComponent,
                new HashSet<>());
        assertSame("Number of components", verticesInComponent.size(), 2);
        Iterator<HashSet<Vertex>> iterator = verticesInComponent.iterator();
        HashSet<Vertex> first = iterator.next();
        HashSet<Vertex> sec = iterator.next();
        assertEquals(1, first.size());
        assertEquals(1, sec.size());
        assert first.contains(v) || sec.contains(v);
        assert first.contains(w) || sec.contains(w);
        assertNotEquals("Components of v and w",
                componentOfVertex.get(v),
                componentOfVertex.get(w));
    }

    @Test
    public void testTarjanStrongComponentsUndirected() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);

        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        HashSet<HashSet<Vertex>> verticesInComponent = new HashSet<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure,
                componentOfVertex,
                verticesInComponent,
                new HashSet<>());
        assertSame("Number of components", verticesInComponent.size(), 1);
        Iterator<HashSet<Vertex>> iterator = verticesInComponent.iterator();
        HashSet<Vertex> first = iterator.next();
        assertEquals(2, first.size());
        assert first.contains(v);
        assert first.contains(w);
        assertEquals("Components of v and w", componentOfVertex.get(v), componentOfVertex.get(w));
        assertEquals("Components of v", componentOfVertex.get(v), Integer.valueOf(0));
    }

    @Test
    public void testTarjanStrongComponentsUndirectedDisconnected() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");

        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        HashSet<HashSet<Vertex>> verticesInComponent = new HashSet<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure,
                componentOfVertex,
                verticesInComponent,
                new HashSet<>());
        assertSame("Number of components", verticesInComponent.size(), 2);
        Iterator<HashSet<Vertex>> iterator = verticesInComponent.iterator();
        HashSet<Vertex> first = iterator.next();
        HashSet<Vertex> sec = iterator.next();
        assertEquals(1, first.size());
        assertEquals(1, sec.size());
        assert first.contains(v) || sec.contains(v);
        assert first.contains(w) || sec.contains(w);
        assertNotEquals("Components of v and w",
                componentOfVertex.get(v),
                componentOfVertex.get(w));
    }

    @Test
    public void testTarjanStrongComponentsCycle() {
        Structure<Vertex, Edge> structure = new DirectedGraph();

        Vertex u = structure.addVertex("u");
        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);
        structure.addEdge(w, u);
        structure.addEdge(u, v);

        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        HashSet<HashSet<Vertex>> verticesInComponent = new HashSet<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure,
                componentOfVertex,
                verticesInComponent,
                new HashSet<>());
        assertSame("Number of components", verticesInComponent.size(), 1);
        assertThat("First component",
                verticesInComponent.toArray()[0],
                equalsVertexSet(structure.getVertices()));
    }
}
