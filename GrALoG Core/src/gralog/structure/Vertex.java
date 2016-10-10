/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author viktor
 */
@XmlName(name="node")
public class Vertex extends XmlMarshallable implements IMovable {
 
    
    public String Label = "";
    public double Radius = 0.5; // cm
    public GralogColor FillColor = GralogColor.white;
    public double StrokeWidth = 2.54/96; // cm
    public double TextHeight = 0.4d; // cm
    public GralogColor StrokeColor = GralogColor.black;
    

    public Vector2D Coordinates = new Vector2D(0.0, 0.0);
    private final Set<VertexListener> listeners = new HashSet<>();
    
    
    private final Set<Edge> connectedEdges = new HashSet<>();
    void connectEdge(Edge e)
    {
        this.connectedEdges.add(e);
    }
    void disconnectEdge(Edge e)
    {
        this.connectedEdges.remove(e);
    }
    public Set<Edge> getConnectedEdges()
    {
        return connectedEdges;
    }
    
    

    public double MaximumCoordinate(int dimension) {
        if(Coordinates.Dimensions() > dimension)
            return Coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }
    
    public Vector2D Intersection(Vector2D p1, Vector2D p2)
    {
        return p1.Minus(p2).Normalized().Multiply(this.Radius).Plus(p2);
    }
    
    
    @Override
    public void Move(Vector2D offset) {
        Coordinates = Coordinates.Plus(offset);
    }

    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        
        if(highlights != null && highlights.contains(this))
            gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius+0.07, GralogColor.red);

        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius, StrokeColor);
        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius-StrokeWidth, FillColor);
        gc.PutText(Coordinates.get(0), Coordinates.get(1), Label, TextHeight, FillColor.inverse());
    }
    
    public void SnapToGrid(double GridSize)
    {
        Coordinates = Coordinates.SnapToGrid(GridSize);
    }
    
    public boolean ContainsCoordinate(double x, double y) {
        double tx = Coordinates.get(0);
        double ty = Coordinates.get(1);
        return (x-tx)*(x-tx) + (y-ty)*(y-ty) < Radius*Radius;
    }
    
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Double.toString(Coordinates.getX()));
        vnode.setAttribute("y", Double.toString(Coordinates.getY()));
        vnode.setAttribute("label", Label);
        vnode.setAttribute("radius", Double.toString(Radius));
        vnode.setAttribute("fillcolor", FillColor.toHtmlString());
        vnode.setAttribute("textheight", Double.toString(TextHeight));
        vnode.setAttribute("strokewidth", Double.toString(StrokeWidth));
        vnode.setAttribute("strokecolor", StrokeColor.toHtmlString());
        return vnode;
    }
    
    public String FromXml(Element vnode) {
        Coordinates = new Vector2D(
                Double.parseDouble(vnode.getAttribute("x")),
                Double.parseDouble(vnode.getAttribute("y"))
        );
        if(vnode.hasAttribute("label"))
            Label = vnode.getAttribute("label");
        if(vnode.hasAttribute("radius"))
            Radius = Double.parseDouble(vnode.getAttribute("radius"));
        if(vnode.hasAttribute("fillcolor"))
            FillColor = GralogColor.parseColor(vnode.getAttribute("fillcolor"));
        if(vnode.hasAttribute("textheight"))
            TextHeight = Double.parseDouble(vnode.getAttribute("textheight"));
        if(vnode.hasAttribute("strokewidth"))
            StrokeWidth = Double.parseDouble(vnode.getAttribute("strokewidth"));
        if(vnode.hasAttribute("strokecolor"))
            StrokeColor = GralogColor.parseColor(vnode.getAttribute("strokecolor"));

        return vnode.getAttribute("id");
    }
    

    
    protected void notifyVertexListeners() {
        for(VertexListener listener : listeners)
            listener.VertexChanged(new VertexEvent(this));
    }
    public void addVertexListener(VertexListener listener) {
        listeners.add(listener);
    }
    public void removeVertexListener(VertexListener listener) {
        listeners.remove(listener);
    }

}
