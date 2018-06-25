/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.preferences.Configuration;
import gralog.rendering.*;
import gralog.rendering.shapes.Ellipse;
import gralog.rendering.shapes.RenderingShape;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import gralog.core.annotations.DataField;


import java.lang.reflect.InvocationTargetException;
import java.util.*;



/**
 * A vertex with a circle shape.
 */
@XmlName(name = "node")
public class Vertex extends XmlMarshallable implements IMovable {

    @DataField(display=true,readOnly=true)
    public int id;
    @DataField(display=true)
    public String label = "";
    @DataField(display=false)
    public double radius = 0.7;     // cm

    //the position of the loop center on the circle
    ///note: -90 is on top because the coordinate system is flipped horizontally
    @DataField(display=false)
    public Double loopAnchor = -90d;  // degrees
    //the position of the endpoints of a loop
    @DataField(display=false)
    public double loopAngle = 20;   // degrees

    public double strokeWidth = 2.54 / 96; // cm
    public double textHeight = 0.4d; // cm

    @DataField(display=true)
    public GralogColor fillColor = GralogColor.WHITE;
    @DataField(display=true)
    public GralogColor strokeColor = GralogColor.BLACK;

    @DataField(display=true)
    public RenderingShape shape = Ellipse.create(1.4, 1.4);

    public Vector2D coordinates = new Vector2D(0.0, 0.0);

    Set<VertexListener> listeners;
    Set<Edge> outgoingEdges;
    Set<Edge> incomingEdges;
    Set<Edge> incidentEdges;


    public Vertex() {
        listeners = new HashSet<>();
        outgoingEdges = new HashSet<>();
        incidentEdges = new HashSet<>();
        incomingEdges = new HashSet<>();
    }



    public Vertex(Configuration config){
        this();
        if(config != null){
            initWithConfig(config);
        }
    }

    /**
     * Initializes lots of variables from a given configuration
     * @param config
     */
    protected void initWithConfig(Configuration config){
        //TODO: complete
        strokeColor = config.getValue("Vertex_strokeColor", GralogColor::parseColor, GralogColor.BLACK);
        fillColor = config.getValue("Vertex_fillColor", GralogColor::parseColor, GralogColor.WHITE);
        shape.setWidth(config.getValue("Vertex_width", Double::parseDouble, 1.0));
        shape.setHeight(config.getValue("Vertex_height", Double::parseDouble, 1.0));
    }

    /**
     * Copies a vertex information from a given vertex object. Not the ID.
     *
     */
    public <V extends Vertex> void copy(V v){
        //this.id = v.id;
        this.radius = v.radius;
        this.loopAngle = v.loopAngle;
        this.loopAnchor = v.loopAnchor;
        this.strokeWidth = v.strokeWidth;
        try {
            this.shape = (RenderingShape) v.shape.getClass().getConstructors()[0].newInstance(v.shape.sizeBox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.strokeColor = new GralogColor(v.strokeColor);
        this.fillColor = new GralogColor(v.fillColor);

        this.coordinates = new Vector2D(v.coordinates);
        this.listeners = new HashSet<>(v.listeners);

        this.incidentEdges = new HashSet<>(v.incidentEdges);
        this.outgoingEdges = new HashSet<>(v.outgoingEdges);
        this.incomingEdges = new HashSet<>(v.incomingEdges);

    }

    @Override
    public String toString() {
        return "Vertex{" + "label=" + label + ", radius=" + radius +
                ", fillColor=" + fillColor +
                ", strokeWidth=" + strokeWidth +
                ", textHeight=" + textHeight +
                ", strokeColor=" + strokeColor +
                ", coordinates=" + coordinates + '}';
    }

    public void setLabel(String label){
        this.label = label;
    }

    void connectEdge(Edge e) {
        System.out.println("connecty boi");
        if(e.getSource() == this){
            //deprecated local id Vergabe. 
            // if(e.getId() == -1 && incidentEdges.isEmpty()){
            //     e.setId(0);
            // }
            // if(e.getId() == -1){
            //     int[] allIndices = new int[incidentEdges.size()];
            //     int k = 0;
                
            //     for(Edge edge : incidentEdges){
            //         allIndices[k] = edge.getId();
            //         k++;
            //     }
            //     System.out.println("i got indicex array: ");
            //     for (int x : allIndices){
            //         System.out.println("bla: " + x);
            //     }
            //     Arrays.sort(allIndices);

            //     boolean changedOnce = false;
            //     for(int i = 0; i < allIndices.length; i++){
            //         if(i < allIndices[i]){
            //             e.setId(i);
            //             changedOnce = true;
            //             break;
            //         }
            //     }
            //     if(!changedOnce){
            //         e.setId(allIndices.length); //fallback
            //     }
            // }
            outgoingEdges.add(e);
        }
        if (e.getTarget() == this){
            this.incomingEdges.add(e);
        }
        this.incidentEdges.add(e);
    }

    void connectEdgeLocal(Edge e) {
        if(e.getSource() == this){
            //if id has not been set already, set it
            if(e.getId() == -1 && incidentEdges.isEmpty()){
                e.setId(0);
            }
            if(e.getId() == -1){
                int[] allIndices = new int[incidentEdges.size()];
                int k = 0;
                
                for(Edge edge : incidentEdges){
                    allIndices[k] = edge.getId();
                    k++;
                }
                
                Arrays.sort(allIndices);

                boolean changedOnce = false;
                for(int i = 0; i < allIndices.length; i++){
                    if(i < allIndices[i]){
                        e.setId(i);
                        changedOnce = true;
                        break;
                    }
                }
                if(!changedOnce){
                    e.setId(allIndices.length); //fallback
                }
            }
            outgoingEdges.add(e);
        }
        if (e.getTarget() == this){
            this.incomingEdges.add(e);
        }
        this.incidentEdges.add(e);
    }

    void disconnectEdge(Edge e) {
        if(e.getSource() == this){
            outgoingEdges.remove(e);
        }
        if (e.getTarget() == this){
            incomingEdges.remove(e);
        }
        this.incidentEdges.remove(e);
    }

    public Set<Edge> getIncidentEdges() {
        return incidentEdges;
    }
    public int getDegree (){return incidentEdges.size();}

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Set<Edge> getOutgoingEdges(){
        return outgoingEdges;
    }
    public int getOutDegree (){return outgoingEdges.size();}

    public Set<Edge> getIncomingEdges(){
        return this.incomingEdges;
    }
    public int getInDegree (){return incomingEdges.size();}
//##########START depricated!!!! use getNeighbours instead#########
    /**
     * @return The set of adjacent vertices.
     */
    public Set<Vertex> getAdjacentVertices() {
        Set<Vertex> result = new HashSet<>();
        for (Edge e : incidentEdges) {
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
        GralogColor c = highlights.isSelected(this) ? GralogColor.RED : strokeColor;
        shape.render(gc, coordinates, label, c, fillColor);

        String annotation = highlights.getAnnotation(this);
        if (annotation != null) {
            gc.putText(coordinates.plus(new Vector2D(0, 1)),
                annotation, textHeight, GralogColor.RED);
        }
    }


    public Set<Vertex> getNeighbours(){
        Set<Vertex> neighbours = new HashSet<Vertex>();
        for (Edge e : this.getIncidentEdges()){
            if (e.getTarget() != this){
                neighbours.add(e.getTarget());
            }else{
                neighbours.add(e.getSource());
            }
        }
        return neighbours;
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
