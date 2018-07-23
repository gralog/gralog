package gralog.structure;

import gralog.rendering.Vector2D;
import gralog.structure.controlpoints.ControlPoint;
import org.junit.Test;
import static org.junit.Assert.*;

public class EdgeTest {

    @Test
    public void controlPointCreation(){

        Structure s = new DirectedGraph();

        Vertex first = s.createVertex();
        Vertex secnd = s.createVertex();

        ControlPoint ctrl1, ctrl2, ctrl3;

        first.coordinates = new Vector2D(0, 0);
        secnd.coordinates = new Vector2D(1, 0);

        s.addVertex(first);
        s.addVertex(secnd);

        Edge e = s.addEdge(first, secnd);
        e.edgeType = Edge.EdgeType.SHARP;

        ctrl1 = e.addControlPoint(new Vector2D(0,1), new Vector2D(0.5, 0));
        ctrl2 = e.addControlPoint(new Vector2D(1,1), new Vector2D(0.5, 0.5));

        /*
        current state
        o - - - o
        |       |
        |       |
        |       |
        c - - - c
        */

        ctrl3 = e.addControlPoint(new Vector2D(2,0), new Vector2D(1, 0.5));
        assertEquals(2, e.controlPoints.indexOf(ctrl3));

        /*
        v1 ---- v2 -- c3
        |           /
        |          /
        |         /
        c1 ---- c2
        */

        ctrl3 = e.addControlPoint(new Vector2D(0, 2), new Vector2D(0.5, 1));
        assertEquals(1, e.controlPoints.indexOf(ctrl3));
        assertEquals(2, e.controlPoints.indexOf(ctrl2));

        /*
        v1 ---- v2 -- c3
        |           /
        |          /
        |         /
        c1      c2
        |      /
        |     /
        |    /
        |   /
        c3'
        */
    }

    @Test
    public void edgeTypeSwitchDeletion(){
        Structure s = new DirectedGraph();

        Vertex first = s.createVertex();
        Vertex secnd = s.createVertex();

        first.coordinates = new Vector2D(0, 0);
        secnd.coordinates = new Vector2D(1, 0);

        s.addVertex(first);
        s.addVertex(secnd);

        Edge e = s.addEdge(first, secnd);
        e.edgeType = Edge.EdgeType.SHARP;

        e.addControlPoint(new Vector2D(0,1), new Vector2D(0.5, 0));
        e.addControlPoint(new Vector2D(1,1), new Vector2D(0.5, 0.5));
        e.addControlPoint(new Vector2D(0.5,1), new Vector2D(0.5, 1));

        e.setEdgeType(Edge.EdgeType.BEZIER);

        assertEquals(2, e.controlPoints.size());

        assertEquals(0.25d, e.controlPoints.get(0).getPosition().getX(), 0.001);
        assertEquals(1.00d, e.controlPoints.get(0).getPosition().getY(), 0.001);

        assertEquals(0.75d, e.controlPoints.get(1).getPosition().getX(), 0.001);
        assertEquals(1.00d, e.controlPoints.get(1).getPosition().getY(), 0.001);
    }

    @Test
    public void edgeIdTest(){
        Structure s = new DirectedGraph();


        //Simple test case
        Vertex first = s.addVertex("");
        Vertex secnd = s.addVertex("");

        s.addVertex(first);
        s.addVertex(secnd);

        Edge one = s.addEdge(first, secnd);
        Edge five = s.addEdge(first, secnd, 2);
        Edge two = s.addEdge(first, secnd);
        Edge three = s.addEdge(first, secnd, 1024);

        assertEquals(0, one.getId());
        assertEquals(1, two.getId());
        assertEquals(2, five.getId());
        assertEquals(1024, three.getId());

        //advanced test case
        Vertex third = s.addVertex("");

        Edge a = s.addEdge(first, third);
        Edge b = s.addEdge(first, third);
        Edge c = s.addEdge(first, third, 200);
        Edge d = s.addEdge(first, first);

        assertEquals(3, a.getId());
        assertEquals(4, b.getId());
        assertEquals(200, c.getId());
        assertEquals(5, d.getId());

    }
}
