package gralog.algorithm;

import gralog.structure.*;
import org.junit.Test;

import java.util.*;

import static gralog.algorithm.ShortestPath.dijkstraShortestPath;
import static gralog.structure.StructureMatchers.equalsVertexSet;
import static org.junit.Assert.*;


public class SortestPathTest {

    @Test
    public void testShortestPath() {
        Structure<Vertex, Edge> structure = new DirectedGraph();

        Vertex v0 = structure.addVertex("v0");
        Vertex v1 = structure.addVertex("v1");
        Vertex v2 = structure.addVertex("v2");
        Vertex v3 = structure.addVertex("v3");
        Vertex v4 = structure.addVertex("v4");
        Vertex v5 = structure.addVertex("v5");
        Vertex v6 = structure.addVertex("v6");
        Vertex v7 = structure.addVertex("v7");

        structure.addEdge(v0, v7);
        structure.addEdge(v0, v1);
        structure.addEdge(v7, v6);
        structure.addEdge(v7, v5);
        structure.addEdge(v6, v5);
        structure.addEdge(v5, v4);
        structure.addEdge(v5, v3);

        HashMap<Vertex, Vertex> predecessor = new HashMap<>();
        HashMap<Vertex, Edge> edgeFromPredecessor = new HashMap<>();
        HashMap<Vertex, Double> distances = new HashMap<>();

        dijkstraShortestPath(structure, v0, null, predecessor, edgeFromPredecessor, distances);

        assertEquals(2, distances.get(v5), 0);
        assertEquals(0, distances.get(v0), 0);
        assertEquals(3, distances.get(v3), 0);
        assertEquals(1, distances.get(v1), 0);
        assertNull(distances.get(v2));

    }

}
