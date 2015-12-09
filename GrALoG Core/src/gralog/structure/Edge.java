/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="edge")
public class Edge extends XmlMarshallable {
    
    Set<EdgeListener> listeners = new HashSet<EdgeListener>();
    
    public Vector<Vector<Double>> Coordinates = new Vector<Vector<Double>>();
    public Vertex source;
    public Vertex target;
    

    public Double MaximumCoordinate(int dimension) {
        Double result = Double.NEGATIVE_INFINITY;
        for(Vector<Double> between : Coordinates)
            if(between.size() > dimension)
                result = Math.max(result, between.get(dimension));
        return result;
    }

    public void Move(Vector<Double> offset) {
        for(Vector<Double> between : Coordinates)
            while(between.size() < offset.size())
                between.add(0d);
        for(Vector<Double> between : Coordinates)
            for(int i = 0; i < offset.size(); i++)
                between.set(i, between.get(i) + offset.get(i));
    }

    
    public void Render(GralogGraphicsContext gc) {
        Vector<Double> from = source.Coordinates;
        Vector<Double> temp = from;

        for(Vector<Double> between : Coordinates)
        {
            from = temp;
            temp = between;
            gc.Line(from.get(0), from.get(1), temp.get(0), temp.get(1));
        }

        Vector<Double> to = target.Coordinates;
        gc.Line(temp.get(0), temp.get(1), to.get(0), to.get(1));
    }
    
    
    
    public Vector2D ClosestPointOnLine(Double px, Double py, Double l1x, Double l1y, Double l2x, Double l2y)
    {
        Vector2D p = new Vector2D(px,py);
        Vector2D l1 = new Vector2D(l1x,l1y);
        Vector2D l2 = new Vector2D(l2x,l2y);
        Vector2D l = l2.Minus(l1);
        
        // normal-vector
        Vector2D n = l.Orthogonal();
        
        // lotfuß-punkt
        Double k = Math.abs(l1.Minus(p).Multiply(n))
                   /
                   (n.Multiply(n));
        Vector2D q = p.Plus(n.Multiply(k));
        
        return q;
    }
    
    protected Double DistancePointToLine(Double px, Double py, Double l1x, Double l1y, Double l2x, Double l2y)
    {
        Vector2D p = new Vector2D(px,py);
        Vector2D l1 = new Vector2D(l1x,l1y);
        Vector2D l2 = new Vector2D(l2x,l2y);
        Vector2D l = l2.Minus(l1);

        if(l.getX() == 0 && l.getY() == 0) // (*)
            return l1.Minus(p).length(); // l1==l2 so "the line" is actually just the point l1

        
        Vector2D perpendicular = ClosestPointOnLine(px,py,l1x,l1y,l2x,l2y);
        Double lScaleToPerpendicular = 0.0;
        if(l.getX() != 0)
            lScaleToPerpendicular = (perpendicular.getX() - l1.getX())/l.getX();
        else // if(l.getY() != 0) // true, because of (*)
            lScaleToPerpendicular = (perpendicular.getY() - l1.getY())/l.getY();
        
        
        if(lScaleToPerpendicular < 0)
            return l1.Minus(p).length();
        if(lScaleToPerpendicular > 1)
            return l2.Minus(p).length();
        return perpendicular.Minus(p).length();
        
        
        /*
        // normal-vector
        Vector2D n = l.Orthogonal();
        
        // perpendicular
        Double k = Math.abs(l1.Minus(p).Multiply(n))
                   /
                   (n.Multiply(n));
        Vector2D q = p.Plus(n.Multiply(k));
        
        return
            Math.abs(l1.Minus(p).Multiply(n))
            /
            n.length();
        */
    }
    
    public boolean ContainsCoordinate(Double x, Double y) {
        Vector<Double> from = source.Coordinates;
        Vector<Double> nextfrom = from;

        for(Vector<Double> between : Coordinates)
        {
            from = nextfrom;
            nextfrom = between;
            if(DistancePointToLine(x, y, from.get(0), from.get(1), nextfrom.get(0), nextfrom.get(1)) < 0.3)
                return true;
        }
        
        Vector<Double> to = target.Coordinates;
        return DistancePointToLine(x, y, nextfrom.get(0), nextfrom.get(1), to.get(0), to.get(1)) < 0.3;
    }
    
    public Double Length() {
        Vector2D from = new Vector2D(this.source.Coordinates);
        Vector2D to = new Vector2D(this.target.Coordinates);
        
        Double result = 0.0;
        for(Vector<Double> between : this.Coordinates)
        {
            Vector2D betw = new Vector2D(between);
            result += betw.Minus(from).length();
            from = betw;
        }
        return result + to.Minus(from).length();
    }
    
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        return enode;
    }
    
    public void FromXml(Element enode, HashMap<String,Vertex> ids) {
        source = ids.get(enode.getAttribute("source"));
        target = ids.get(enode.getAttribute("target"));
    }
    
    
    protected void notifyEdgeListeners() {
        for(EdgeListener listener : listeners)
            listener.EdgeChanged(new EdgeEvent(this));
    }
    public void addEdgeListener(EdgeListener listener) {
        listeners.add(listener);
    }
    public void removeEdgeListener(EdgeListener listener) {
        listeners.remove(listener);
    }
}
