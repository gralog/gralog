/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.*;

import static org.junit.Assert.*;

public final class StructureMatchers {

    private StructureMatchers() {
    }

    /**
     * @param v The expected vertex.
     * @return A matcher that compares an actual vertex with the expected
     * vertex. Two vertices are considered equal if all their fields are equal.
     */
    @Factory
    public static Matcher equalsVertex(Vertex v) {
        return new TypeSafeMatcher<Vertex>() {
            @Override
            protected boolean matchesSafely(Vertex w) {
                return v.label.equals(w.label)
                        && Double.doubleToLongBits(v.radius) == Double.doubleToLongBits(w.radius)
                        && v.fillColor.equals(w.fillColor)
                        && v.strokeWidth == w.strokeWidth
                        && v.textHeight == w.textHeight
                        && v.strokeColor.equals(w.strokeColor)
                        && v.getCoordinates().equals(w.getCoordinates());
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("Vertex equality");
            }
        };
    }

    /**
     * @param expectedVertices The expected set of vertices.
     * @return A matcher that compares an actual set of vertices with the
     * expected set of vertices. Two vertex sets are considered equal if they
     * compare equivalent vertices (as defined by the equalsVertex matcher).
     * Requires unique labels (it does not check for unique labels). The running
     * time is O(n log n).
     */
    @Factory
    public static Matcher equalsVertexSet(Collection<Vertex> expectedVertices) {
        return new TypeSafeMatcher<Collection<Vertex>>() {
            @Override
            protected boolean matchesSafely(Collection<Vertex> actualVertices) {
                assertSame("Vertex set size", expectedVertices.size(), actualVertices.size());

                List<Vertex> expectedVerticesSorted = new ArrayList<>(expectedVertices);
                List<Vertex> actualVerticesSorted = new ArrayList<>(actualVertices);
                expectedVerticesSorted.sort((v, w) -> v.label.compareTo(w.label));
                actualVerticesSorted.sort((v, w) -> v.label.compareTo(w.label));
                for (int i = 0; i < expectedVerticesSorted.size(); ++i) {
                    Vertex v = expectedVerticesSorted.get(i);
                    Vertex w = actualVerticesSorted.get(i);
                    assertThat("Vertex", v, equalsVertex(w));
                }
                return true;
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("Vertex set equality");
            }
        };
    }

    /**
     * @param s The expected structure.
     * @return A matcher that compares an actual structure with the expected
     * structure by identifying vertices via their labels.
     */
    @Factory
    public static Matcher equalsStructure(Structure s) {
        return new TypeSafeMatcher<Structure>() {
            private Map<String, Vertex> getVertexLabelMap(Collection<Vertex> vertices) {
                Map<String, Vertex> vertexMap = new HashMap<>();
                for (Vertex v : vertices) {
                    if (v.label.isEmpty())
                        throw new AssertionError("Cannot compare structures: Missing vertex labels");
                    vertexMap.put(v.label, v);
                }
                return vertexMap;
            }

            @Override
            protected boolean matchesSafely(Structure t) {
                assertSame("Structure type", s.getClass(), t.getClass());

                final Collection<Vertex> sV = s.getVertices();
                final Collection<Vertex> tV = t.getVertices();
                assertSame("Number of vertices", sV.size(), tV.size());
                assertSame("Number of edges", s.getEdges().size(), t.getEdges().size());

                // Deduce the isomorphism from the vertex labels and check adjacencies.
                Map<String, Vertex> sVertexMap = getVertexLabelMap(sV);
                Map<String, Vertex> tVertexMap = getVertexLabelMap(tV);
                assertEquals("Vertex labels", sVertexMap.keySet(), tVertexMap.keySet());
                if (sVertexMap.size() != sV.size())
                    throw new AssertionError("Cannot compare structures: Non-unique vertex labels");

                assertThat("Vertex sets", sV, equalsVertexSet(tV));

                for (Vertex v : sV) {
                    final Vertex w = tVertexMap.get(v.label);
                    assertThat("Adjacency sets",
                            v.getNeighbours(),
                            equalsVertexSet(w.getNeighbours()));
                }

                return true;
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("Structure equality");
            }
        };
    }
}
