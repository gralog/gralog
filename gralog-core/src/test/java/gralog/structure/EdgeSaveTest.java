package gralog.structure;

import gralog.rendering.Vector2D;
import gralog.structure.controlpoints.ControlPoint;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EdgeSaveTest {


    @Test
    public void controlPointCreation() {

        Structure s = new DirectedGraph();

        Vertex first = s.addVertex();
        Vertex secnd = s.addVertex();

        ControlPoint ctrl1, ctrl2, ctrl3;

        first.setCoordinates(0, 0);
        secnd.setCoordinates(1, 0);


        Edge e = s.addEdge(first, secnd, 0, null);
        e.edgeType = Edge.EdgeType.BEZIER;

        ctrl1 = e.addControlPoint(new Vector2D(0, 1), new Vector2D(0.5, 0));
        ctrl2 = e.addControlPoint(new Vector2D(1, 1), new Vector2D(0.5, 0.5));

        e.intermediatePoints.add(new EdgeIntermediatePoint(new Vector2D(0.3, 0.3)));


        HashMap<Vertex, String> ids = new HashMap<>();
        for (Vertex v : (Collection<Vertex>) s.getVertices())
            ids.put(v,Integer.toString(v.getId()));
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = e.toXml(doc,ids);
            assertEquals("0.3",element.getFirstChild().getAttributes().getNamedItem("x").getTextContent());
            assertEquals("1.0",element.getLastChild().getAttributes().getNamedItem("x").getTextContent());
        }catch (Exception exc){
            System.err.println(exc);
        }

    }

}
