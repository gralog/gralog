/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.junit.Assert.*;

public class StructureMatchers {

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
                       && v.coordinates.equals(w.coordinates);
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("Vertex equality");
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
            private Map<String, Vertex> getVertexLabelMap(Set<Vertex> vertices) {
                Map<String, Vertex> vertexMap = new HashMap<>();
                for (Vertex v : vertices) {
                    if (v.label.isEmpty())
                        throw new AssertionError("Cannot compare structures: Missing vertex labels");
                    vertexMap.put(v.label, v);
                }
                return vertexMap;
            }

            private void equalVertexSets(String description,
                    Set<Vertex> sV, Set<Vertex> tV,
                    Map<String, Vertex> sVertexMap,
                    Map<String, Vertex> tVertexMap) {
                for (Vertex v : sV) {
                    Vertex w = tVertexMap.get(v.label);
                    assertThat(description + ", missing vertex", v, equalsVertex(w));
                }
                for (Vertex v : tV) {
                    Vertex w = sVertexMap.get(v.label);
                    assertThat(description + ", additional vertex", v, equalsVertex(w));
                }
            }

            @Override
            protected boolean matchesSafely(Structure t) {
                assertSame("Structure type", s.getClass(), t.getClass());

                final Set<Vertex> sV = s.getVertices();
                final Set<Vertex> tV = t.getVertices();
                assertSame("Number of vertices", sV.size(), tV.size());
                assertSame("Number of edges", s.getEdges().size(), t.getEdges().size());

                // Deduce the isomorphism from the vertex labels and check adjacencies.
                Map<String, Vertex> sVertexMap = getVertexLabelMap(sV);
                Map<String, Vertex> tVertexMap = getVertexLabelMap(tV);
                assertEquals("Vertex labels", sVertexMap.keySet(), tVertexMap.keySet());
                if (sVertexMap.size() != sV.size())
                    throw new AssertionError("Cannot compare structures: Non-unique vertex labels");

                equalVertexSets("Vertex sets", sV, tV, sVertexMap, tVertexMap);

                for (Vertex v : sV) {
                    final Vertex w = tVertexMap.get(v.label);
                    equalVertexSets("Adjacency sets",
                                    v.getAdjacentVertices(), w.getAdjacentVertices(),
                                    sVertexMap, tVertexMap);
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
