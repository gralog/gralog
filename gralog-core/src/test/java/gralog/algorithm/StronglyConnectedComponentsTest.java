/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.structure.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure, componentOfVertex, verticesInComponent);
        assertSame("Number of components", verticesInComponent.size(), 2);
        assertThat("First component",
                verticesInComponent.get(componentOfVertex.get(w)),
                equalsVertexSet(Arrays.asList(w)));
        assertThat("Second component",
                verticesInComponent.get(componentOfVertex.get(v)),
                equalsVertexSet(Arrays.asList(v)));
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
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure, componentOfVertex, verticesInComponent);
        assertSame("Number of components", verticesInComponent.size(), 1);
        assertThat("First component",
                verticesInComponent.get(0),
                equalsVertexSet(structure.getVertices()));
        assertEquals("Components of v and w", componentOfVertex.get(v), componentOfVertex.get(w));
        assertEquals("Components of v", componentOfVertex.get(v), Integer.valueOf(0));
    }

    @Test
    public void testTarjanStrongComponentsUndirectedDisconnected() {
        Structure<Vertex, Edge> structure = new UndirectedGraph();

        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");

        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure, componentOfVertex, verticesInComponent);
        assertSame("Number of components", verticesInComponent.size(), 2);
        assertThat("First component",
                verticesInComponent.get(componentOfVertex.get(w)),
                equalsVertexSet(Arrays.asList(w)));
        assertThat("Second component",
                verticesInComponent.get(componentOfVertex.get(v)),
                equalsVertexSet(Arrays.asList(v)));
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
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();
        StronglyConnectedComponents.tarjanStrongComponents(structure, componentOfVertex, verticesInComponent);
        assertSame("Number of components", verticesInComponent.size(), 1);
        assertThat("First component",
                verticesInComponent.get(0),
                equalsVertexSet(structure.getVertices()));
    }
}
