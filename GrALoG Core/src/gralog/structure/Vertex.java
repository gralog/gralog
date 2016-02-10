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
    public Double Radius = 0.5; // cm
    public GralogColor FillColor = GralogColor.white;
    public Double StrokeWidth = 2.54/96; // cm
    public GralogColor StrokeColor = GralogColor.black;
    

    //public Vector<Double> Coordinates = new Vector<Double>();
    public VectorND Coordinates = new VectorND();
    Set<VertexListener> listeners = new HashSet<VertexListener>();
    

    public Double MaximumCoordinate(int dimension) {
        if(Coordinates.Dimensions() > dimension)
            return Coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }
    
    public Vector2D Intersection(Vector2D p1, Vector2D p2)
    {
        return p1.Minus(p2).Normalized().Multiply(this.Radius).Plus(p2);
    }
    
    
    public void Move(Vector<Double> offset) {
        while(Coordinates.Dimensions() < offset.size())
            Coordinates.add(0d);
        for(int i = 0; i < offset.size(); i++)
            Coordinates.set(i, Coordinates.get(i) + offset.get(i));
    }

    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        
        if(highlights != null && highlights.contains(this))
            gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius+0.07, GralogColor.red);

        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius, StrokeColor);
        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius-StrokeWidth, FillColor);
        gc.PutText(Coordinates.get(0), Coordinates.get(1), Label, FillColor.inverse());
    }
    
    public void SnapToGrid(Double GridSize)
    {
        for(int i = 0; i < Coordinates.Dimensions(); i++)
        {
            Double newCoord = (Coordinates.get(i) + GridSize/2);
            newCoord = newCoord - (newCoord%GridSize);
            Coordinates.set(i, newCoord);
        }
    }
    
    public boolean ContainsCoordinate(Double x, Double y) {
        Double tx = Coordinates.get(0);
        Double ty = Coordinates.get(1);
        if( (x-tx)*(x-tx) + (y-ty)*(y-ty) < Radius*Radius )
            return true;
        return false;
    }
    
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Coordinates.get(0).toString());
        vnode.setAttribute("y", Coordinates.get(1).toString());
        vnode.setAttribute("label", Label);
        vnode.setAttribute("radius", Radius.toString());
        vnode.setAttribute("fillcolor", FillColor.toHtmlString());
        vnode.setAttribute("strokewidth", StrokeWidth.toString());
        vnode.setAttribute("strokecolor", StrokeColor.toHtmlString());
        return vnode;
    }
    
    public String FromXml(Element vnode) {
        Coordinates.clear();
        Coordinates.add(Double.parseDouble(vnode.getAttribute("x")));
        Coordinates.add(Double.parseDouble(vnode.getAttribute("y")));
        if(vnode.hasAttribute("label"))
            Label = vnode.getAttribute("label");
        if(vnode.hasAttribute("radius"))
            Radius = Double.parseDouble(vnode.getAttribute("radius"));
        if(vnode.hasAttribute("fillcolor"))
            FillColor = GralogColor.parseColor(vnode.getAttribute("fillcolor"));
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
