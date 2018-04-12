/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
@XmlName(name = "edge")
public class Edge extends XmlMarshallable implements IMovable {

    Set<EdgeListener> listeners = new HashSet<>();

    public String label = "";
    public double cost = 1.0d;

    public Boolean isDirected = true;

    public double arrowHeadLength = 0.4d; // cm
    public double arrowHeadAngle = 40d; // degrees
    public double width = 2.54 / 96; // cm
    public GralogColor color = GralogColor.BLACK;

    public ArrayList<EdgeIntermediatePoint> intermediatePoints = new ArrayList<>();

    private Vertex source = null;
    private Vertex target = null;

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        if (this.source != null)
            this.source.disconnectEdge(this);
        this.source = source;
        if (source != null)
            this.source.connectEdge(this);
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        if (this.target != null)
            this.target.disconnectEdge(this);
        this.target = target;
        if (target != null)
            this.target.connectEdge(this);
    }

    public double maximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        for (EdgeIntermediatePoint between : intermediatePoints)
            result = Math.max(result, between.get(dimension));
        return result;
    }

    @Override
    public void move(Vector2D offset) {
        for (EdgeIntermediatePoint between : intermediatePoints)
            between.move(offset);
    }

    public void snapToGrid(double gridSize) {
        for (EdgeIntermediatePoint between : intermediatePoints)
            between.snapToGrid(gridSize);
    }

    public IMovable findObject(double x, double y) {
        for (EdgeIntermediatePoint p : intermediatePoints)
            if (p.containsCoordinate(x, y))
                return p;

        if (this.containsCoordinate(x, y))
            return this;

        return null;
    }

    protected double binomialCoefficients(int n, int k) {
        double result = 1.0;
        for (int i = 1; i <= k; i++)
            result = result * (n + 1 - i) / i;
        return result;
    }

    protected Vector2D bezierCurve(double t) {
        int n = intermediatePoints.size() + 1;

        Vector2D result = this.source.coordinates.multiply(Math.pow(1 - t, n));
        result = result.plus(this.target.coordinates.multiply(Math.pow(t, n)));

        int i = 1;
        for (EdgeIntermediatePoint between : intermediatePoints) {
            result = result.plus(between.coordinates.multiply(binomialCoefficients(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i)
            ));
            i++;
        }

        return result;
    }

    public void render(GralogGraphicsContext gc, Highlights highlights) {
        double fromX = source.coordinates.getX();
        double fromY = source.coordinates.getY();
        double toX = target.coordinates.getX();
        double toY = target.coordinates.getY();

        /*
        if(intermediatePoints.size() > 0)
        {
            double steps = 1/40d;

            for(double t = steps; t < 1; t += steps) {
                VectorND next = BezierCurve(t);
                double tempX = next.get(0);
                double tempY = next.get(1);

                gc.Line(fromX, fromY, tempX, tempY, this.Color, this.Width);
                fromX = tempX;
                fromY = tempY;
            }
            gc.Line(fromX, fromY, toX, toY, this.Color, this.Width);

            //if(highlights != null && highlights.contains(this))
                for(EdgeIntermediatePoint between : intermediatePoints)
                    gc.Rectangle(between.get(0)-0.1, between.get(1)-0.1, between.get(0)+0.1, between.get(1)+0.1, this.Color, 2.54/96);
        } else {
            gc.Line(fromX, fromY, toX, toY, this.Color, this.Width);
        }
         */
        double tempX = fromX;
        double tempY = fromY;

        GralogColor edgeColor = this.color;
        if (highlights.isSelected(this))
            edgeColor = GralogColor.RED;

        for (EdgeIntermediatePoint between : intermediatePoints) {
            fromX = tempX;
            fromY = tempY;
            tempX = between.getX();
            tempY = between.getY();
            gc.line(fromX, fromY, tempX, tempY, edgeColor, width);

            if (highlights.isSelected(this))
                gc.rectangle(tempX - 0.1, tempY - 0.1, tempX + 0.1, tempY + 0.1, edgeColor, 2.54 / 96);
        }

        if (isDirected) {
            Vector2D intersection = target.intersection(new Vector2D(tempX, tempY), new Vector2D(toX, toY));
            gc.arrow(new Vector2D(tempX, tempY), intersection, arrowHeadAngle, arrowHeadLength, edgeColor, width);
        } else {
            gc.line(tempX, tempY, toX, toY, edgeColor, width);
        }
    }

    public boolean containsCoordinate(double x, double y) {
        double fromX = source.coordinates.getX();
        double fromY = source.coordinates.getY();
        double nextfromX = fromX;
        double nextfromY = fromY;

        for (EdgeIntermediatePoint between : intermediatePoints) {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.getX();
            nextfromY = between.getY();
            if (Vector2D.distancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY) < 0.3)
                return true;
        }

        double toX = target.coordinates.getX();
        double toY = target.coordinates.getY();
        return Vector2D.distancePointToLine(x, y, nextfromX, nextfromY, toX, toY) < 0.3;
    }

    public EdgeIntermediatePoint addIntermediatePoint(double x, double y) {
        double fromX = source.coordinates.getX();
        double fromY = source.coordinates.getY();
        double nextfromX = fromX;
        double nextfromY = fromY;

        int i = 0, insertionIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (EdgeIntermediatePoint between : intermediatePoints) {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.getX();
            nextfromY = between.getY();

            double distanceTemp = Vector2D.distancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY);
            if (distanceTemp < minDistance) {
                insertionIndex = i;
                minDistance = distanceTemp;
            }
            i++;
        }

        double toX = target.coordinates.getX();
        double toY = target.coordinates.getY();

        double distanceTemp = Vector2D.distancePointToLine(x, y, nextfromX, nextfromY, toX, toY);
        if (distanceTemp < minDistance) {
            insertionIndex = intermediatePoints.size();
        }

        EdgeIntermediatePoint result = new EdgeIntermediatePoint(x, y);
        intermediatePoints.add(insertionIndex, result);
        return result;
    }

    public boolean containsVertex(Vertex v) {
        return source == v || target == v;
    }

    public double length() {
        Vector2D from = this.source.coordinates;
        Vector2D to = this.target.coordinates;

        double result = 0.0;
        for (EdgeIntermediatePoint between : this.intermediatePoints) {
            result += between.coordinates.minus(from).length();
            from = between.coordinates;
        }
        return result + to.minus(from).length();
    }

    public Element toXml(Document doc, HashMap<Vertex, String> ids) throws Exception {
        Element enode = super.toXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        enode.setAttribute("isdirected", isDirected ? "true" : "false");
        enode.setAttribute("label", label);
        enode.setAttribute("cost", Double.toString(cost));
        enode.setAttribute("width", Double.toString(width));
        enode.setAttribute("arrowheadlength", Double.toString(arrowHeadLength));
        enode.setAttribute("arrowheadangle", Double.toString(arrowHeadAngle));
        enode.setAttribute("color", color.toHtmlString());

        for (EdgeIntermediatePoint p : intermediatePoints) {
            Element e = p.toXml(doc);
            if (e != null)
                enode.appendChild(e);
        }

        return enode;
    }

    public void fromXml(Element enode, HashMap<String, Vertex> ids) throws Exception {
        setSource(ids.get(enode.getAttribute("source")));
        setTarget(ids.get(enode.getAttribute("target")));

        if (enode.hasAttribute("isdirected"))
            isDirected = enode.getAttribute("isdirected").equals("true");
        label = enode.getAttribute("label");
        if (enode.hasAttribute("cost"))
            cost = Double.parseDouble(enode.getAttribute("cost"));

        if (enode.hasAttribute("width"))
            width = Double.parseDouble(enode.getAttribute("width"));

        if (enode.hasAttribute("arrowheadlength"))
            arrowHeadLength = Double.parseDouble(enode.getAttribute("arrowheadlength"));
        if (enode.hasAttribute("arrowheadangle"))
            arrowHeadAngle = Double.parseDouble(enode.getAttribute("arrowheadangle"));
        if (enode.hasAttribute("color"))
            color = GralogColor.parseColor(enode.getAttribute("color"));

        NodeList children = enode.getChildNodes(); // load intermediate points
        for (int i = 0; i < children.getLength(); ++i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.instantiateClass(child.getTagName());
            if (obj instanceof EdgeIntermediatePoint) {
                EdgeIntermediatePoint p = (EdgeIntermediatePoint) obj;
                p.fromXml(child);
                this.intermediatePoints.add(p);
            }
        }
    }

    protected void notifyEdgeListeners() {
        for (EdgeListener listener : listeners)
            listener.edgeChanged(new EdgeEvent(this));
    }

    public void addEdgeListener(EdgeListener listener) {
        listeners.add(listener);
    }

    public void removeEdgeListener(EdgeListener listener) {
        listeners.remove(listener);
    }
}
