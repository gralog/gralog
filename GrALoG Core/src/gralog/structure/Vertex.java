/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Set;
import java.util.HashSet;

/**
 * A vertex with a circle shape.
 */
@XmlName(name = "node")
public class Vertex extends XmlMarshallable implements IMovable {

    public String label = "";
    public double radius = 0.5; // cm
    public GralogColor fillColor = GralogColor.WHITE;
    public double strokeWidth = 2.54 / 96; // cm
    public double textHeight = 0.4d; // cm
    public GralogColor strokeColor = GralogColor.BLACK;

    public Vector2D coordinates = new Vector2D(0.0, 0.0);
    private final Set<VertexListener> listeners = new HashSet<>();

    private final Set<Edge> connectedEdges = new HashSet<>();

    void connectEdge(Edge e) {
        this.connectedEdges.add(e);
    }

    void disconnectEdge(Edge e) {
        this.connectedEdges.remove(e);
    }

    public Set<Edge> getConnectedEdges() {
        return connectedEdges;
    }

    public double maximumCoordinate(int dimension) {
        if (coordinates.dimensions() > dimension)
            return coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }

    public Vector2D intersection(Vector2D p1, Vector2D p2) {
        return p1.minus(p2).normalized().multiply(this.radius).plus(p2);
    }

    @Override
    public void move(Vector2D offset) {
        coordinates = coordinates.plus(offset);
    }

    public void render(GralogGraphicsContext gc, Set<Object> highlights) {
        if (highlights != null && highlights.contains(this))
            gc.circle(coordinates, radius + 0.07, GralogColor.RED);

        gc.circle(coordinates, radius, strokeColor);
        gc.circle(coordinates, radius - strokeWidth, fillColor);
        gc.putText(coordinates, label, textHeight, fillColor.inverse());
    }

    public void snapToGrid(double GridSize) {
        coordinates = coordinates.snapToGrid(GridSize);
    }

    /**
     * @return True if the given coordinates are inside the circular shape of
     * this vertex.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public boolean containsCoordinate(double x, double y) {
        double tx = coordinates.getX();
        double ty = coordinates.getY();
        return (x - tx) * (x - tx) + (y - ty) * (y - ty) < radius * radius;
    }

    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Double.toString(coordinates.getX()));
        vnode.setAttribute("y", Double.toString(coordinates.getY()));
        vnode.setAttribute("label", label);
        vnode.setAttribute("radius", Double.toString(radius));
        vnode.setAttribute("fillcolor", fillColor.toHtmlString());
        vnode.setAttribute("textheight", Double.toString(textHeight));
        vnode.setAttribute("strokewidth", Double.toString(strokeWidth));
        vnode.setAttribute("strokecolor", strokeColor.toHtmlString());
        return vnode;
    }

    public String fromXml(Element vnode) {
        coordinates = new Vector2D(
                Double.parseDouble(vnode.getAttribute("x")),
                Double.parseDouble(vnode.getAttribute("y"))
        );
        if (vnode.hasAttribute("label"))
            label = vnode.getAttribute("label");
        if (vnode.hasAttribute("radius"))
            radius = Double.parseDouble(vnode.getAttribute("radius"));
        if (vnode.hasAttribute("fillcolor"))
            fillColor = GralogColor.parseColor(vnode.getAttribute("fillcolor"));
        if (vnode.hasAttribute("textheight"))
            textHeight = Double.parseDouble(vnode.getAttribute("textheight"));
        if (vnode.hasAttribute("strokewidth"))
            strokeWidth = Double.parseDouble(vnode.getAttribute("strokewidth"));
        if (vnode.hasAttribute("strokecolor"))
            strokeColor = GralogColor.parseColor(vnode.getAttribute("strokecolor"));

        return vnode.getAttribute("id");
    }

    protected void notifyVertexListeners() {
        for (VertexListener listener : listeners)
            listener.VertexChanged(new VertexEvent(this));
    }

    public void addVertexListener(VertexListener listener) {
        listeners.add(listener);
    }

    public void removeVertexListener(VertexListener listener) {
        listeners.remove(listener);
    }
}
