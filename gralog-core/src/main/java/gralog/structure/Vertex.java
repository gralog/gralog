/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * A vertex with a circle shape.
 */
@XmlName(name = "node")
public class Vertex extends XmlMarshallable implements IMovable {

    public int id;
    public String label = "";
    public double radius = 0.7;     // cm

    //the position of the loop center on the circle
    ///note: -90 is on top because the coordinate system is flipped horizontally
    public double loopAnchor = -90;  // degrees
    //the position of the endpoints of a loop
    public double loopAngle = 20;   // degrees

    public double strokeWidth = 2.54 / 96; // cm
    public double textHeight = 0.4d; // cm
    public GralogColor fillColor = GralogColor.WHITE;
    public GralogColor strokeColor = GralogColor.BLACK;

    public Vector2D coordinates = new Vector2D(0.0, 0.0);

    Set<VertexListener> listeners;
    Set<Edge> outgoingEdges;
    Set<Edge> incomingEdges;
    Set<Edge> connectedEdges;

    public Vertex() {
        listeners = new HashSet<>();
        outgoingEdges = new HashSet<>();
        connectedEdges = new HashSet<>();
        incomingEdges = new HashSet<>();
    }

    /**
     * Copies a vertex information from a given vertex object.
     *
     */
    public <V extends Vertex> void copy(V v){
        this.id = v.id;
        this.radius = v.radius;
        this.loopAngle = v.loopAngle;
        this.loopAnchor = v.loopAnchor;
        this.strokeWidth = v.strokeWidth;
        this.strokeColor = new GralogColor(v.strokeColor);
        this.coordinates = new Vector2D(v.coordinates);
        this.listeners = new HashSet<>(v.listeners);

        this.connectedEdges = new HashSet<>(v.connectedEdges);
        this.outgoingEdges = new HashSet<>(v.outgoingEdges);
        this.incomingEdges = new HashSet<>(v.incomingEdges);
    }

    @Override
    public String toString() {
        return "Vertex{" + "label=" + label + ", radius=" + radius + ", fillColor=" + fillColor + ", strokeWidth=" + strokeWidth + ", textHeight=" + textHeight + ", strokeColor=" + strokeColor + ", coordinates=" + coordinates + '}';
    }

    void connectEdge(Edge e) {
        if(e.getSource() == this){
            outgoingEdges.add(e);
        }
        if (e.getTarget() == this){
            this.incomingEdges.add(e);
        }
        this.connectedEdges.add(e);
    }

    void disconnectEdge(Edge e) {
        if(e.getSource() == this){
            outgoingEdges.remove(e);
        }
        if (e.getTarget() == this){
            incomingEdges.remove(e);
        }
        this.connectedEdges.remove(e);
    }

    public Set<Edge> getConnectedEdges() {
        return connectedEdges;
    }

    public int getId(){
        return this.id;
    }

    public Set<Edge> getOutgoingEdges(){
        return outgoingEdges;
    }

    public Set<Edge> getIncomingEdges(){
        return this.incomingEdges;
    }

//##########START depricated!!!! use getConnectedNeighbours instead#########
    /**
     * @return The set of adjacent vertices.
     */
    public Set<Vertex> getAdjacentVertices() {
        Set<Vertex> result = new HashSet<>();
        for (Edge e : connectedEdges) {
            Vertex v = e.getSource();
            if (v == this)
                v = e.getTarget();
            result.add(v);
        }
        return result;
    }

//##########END#########

    public double maximumCoordinate(int dimension) {
        if (coordinates.dimensions() > dimension)
            return coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }

    public Vector2D intersection(Vector2D p1, Vector2D p2) {
        return p1.minus(p2).normalized().multiply(this.radius).plus(p2);
    }
    public Vector2D intersectionAdjusted(Vector2D p1, Vector2D p2, double adjust){
        return p1.minus(p2).normalized().multiply(this.radius - adjust).plus(p2);
    }
    @Override
    public void move(Vector2D offset) {
        coordinates = coordinates.plus(offset);
    }

    public void render(GralogGraphicsContext gc, Highlights highlights) {
        if (highlights.isSelected(this))
            gc.circle(coordinates, radius + 0.07, GralogColor.RED);

        gc.circle(coordinates, radius, strokeColor);
        gc.circle(coordinates, radius - strokeWidth, fillColor);
        gc.putText(coordinates, label, textHeight, fillColor.inverse());

        String annotation = highlights.getAnnotation(this);
        if (annotation != null) {
            gc.putText(coordinates.plus(new Vector2D(0, 1)),
                annotation, textHeight, GralogColor.RED);
        }
    }


    public Set<Vertex> getConnectedNeighbours(){
        Set<Vertex> connectedNeighbours = new HashSet<Vertex>();
        for (Edge e : this.getConnectedEdges()){
            if (e.getTarget() != this){
                connectedNeighbours.add(e.getTarget());
            }else{
                connectedNeighbours.add(e.getSource());
            }
        }
        return connectedNeighbours;
    }

    public Set<Vertex> getOutgoingNeighbours(){
        Set<Vertex> outgoingNeighbours = new HashSet<Vertex>();
        for (Edge e : this.getOutgoingEdges()){
            outgoingNeighbours.add(e.getTarget());
        }
        return outgoingNeighbours;
    }


    public Set<Vertex> getIncomingNeighbours(){
        Set<Vertex> incomingNeighbours = new HashSet<Vertex>();
        for (Edge e : this.getIncomingEdges()){
            incomingNeighbours.add(e.getSource());
        }
        return incomingNeighbours;
    }

    public void snapToGrid(double gridSize) {
        coordinates = coordinates.snapToGrid(gridSize);
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
            listener.vertexChanged(new VertexEvent(this));
    }

    public void addVertexListener(VertexListener listener) {
        listeners.add(listener);
    }

    public void removeVertexListener(VertexListener listener) {
        listeners.remove(listener);
    }

}
