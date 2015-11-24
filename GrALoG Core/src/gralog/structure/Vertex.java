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
public class Vertex extends XmlMarshallable {
 
    
    public Vector<Double> Coordinates = new Vector<Double>();
    Set<VertexListener> listeners = new HashSet<VertexListener>();
    

    public Double MaximumCoordinate(int dimension) {
        if(Coordinates.size() > dimension)
            return Coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }
    
    public void Move(Vector<Double> offset) {
        while(Coordinates.size() < offset.size())
            Coordinates.add(0d);
        for(int i = 0; i < offset.size(); i++)
            Coordinates.set(i, Coordinates.get(i) + offset.get(i));
    }
    
    protected final double diameter = 5d;
    public void Render(GralogGraphicsContext gc) {
        gc.Circle(Coordinates.get(0), Coordinates.get(1), diameter);
    }
    
    public boolean ContainsCoordinate(Double x, Double y) {
        Double tx = Coordinates.get(0);
        Double ty = Coordinates.get(1);
        if( (x-tx)*(x-tx) + (y-ty)*(y-ty) < diameter*diameter )
            return true;
        return false;
    }
    
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Coordinates.get(0).toString());
        vnode.setAttribute("y", Coordinates.get(1).toString());
        return vnode;
    }
    
    public String FromXml(Element vnode) {
        Coordinates.clear();
        Coordinates.add(Double.parseDouble(vnode.getAttribute("x")));
        Coordinates.add(Double.parseDouble(vnode.getAttribute("y")));
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
    
    
    @Override
    public String toString() {
        return "";
    }
}
