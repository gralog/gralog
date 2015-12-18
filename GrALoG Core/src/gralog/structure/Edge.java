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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author viktor
 */
@XmlName(name="edge")
public class Edge extends XmlMarshallable implements IMovable {
    
    Set<EdgeListener> listeners = new HashSet<EdgeListener>();
    
    public Vector<EdgeIntermediatePoint> intermediatePoints = new Vector<EdgeIntermediatePoint>();
    public Vertex source;
    public Vertex target;
    

    public Double MaximumCoordinate(int dimension) {
        Double result = Double.NEGATIVE_INFINITY;
        for(EdgeIntermediatePoint between : intermediatePoints)
            if(between.size() > dimension)
                result = Math.max(result, between.get(dimension));
        return result;
    }

    public void Move(Vector<Double> offset) {
        for(EdgeIntermediatePoint between : intermediatePoints)
            between.Move(offset);
    }

    
    public void Render(GralogGraphicsContext gc) {
        Double fromX = source.Coordinates.get(0);
        Double fromY = source.Coordinates.get(1);
        Double tempX = fromX;
        Double tempY = fromY;
        
        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = tempX;
            fromY = tempY;
            tempX = between.get(0);
            tempY = between.get(1);
            gc.Line(fromX, fromY, tempX, tempY);
        }

        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);
        gc.Line(tempX, tempY, toX, toY);
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
    }
    
    public boolean ContainsCoordinate(Double x, Double y) {
        Double fromX = source.Coordinates.get(0);
        Double fromY = source.Coordinates.get(1);
        Double nextfromX = fromX;
        Double nextfromY = fromY;

        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.get(0);
            nextfromY = between.get(1);
            if(DistancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY) < 0.3)
                return true;
        }
        
        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);
        return DistancePointToLine(x, y, nextfromX, nextfromY, toX, toY) < 0.3;
    }
    
    public boolean ContainsVertex(Vertex v) {
        return source == v || target == v;
    }
    
    public Double Length() {
        Vector2D from = new Vector2D(this.source.Coordinates);
        Vector2D to = new Vector2D(this.target.Coordinates);
        
        Double result = 0.0;
        for(EdgeIntermediatePoint between : this.intermediatePoints)
        {
            Vector2D betw = new Vector2D(between.get(0), between.get(1));
            result += betw.Minus(from).length();
            from = betw;
        }
        return result + to.Minus(from).length();
    }
    
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        
        for(EdgeIntermediatePoint p : intermediatePoints)
        {
            Element e = p.ToXml(doc);
            if(e != null)
                enode.appendChild(e);
        }
        
        return enode;
    }
    
    public void FromXml(Element enode, HashMap<String,Vertex> ids) throws Exception {
        source = ids.get(enode.getAttribute("source"));
        target = ids.get(enode.getAttribute("target"));
        
        NodeList children = enode.getChildNodes(); // load intermediate points
        for(int i = 0; i < children.getLength(); ++i)
        {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != childNode.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.InstantiateClass(child.getTagName());
            if(obj instanceof EdgeIntermediatePoint)
            {
                EdgeIntermediatePoint p = (EdgeIntermediatePoint)obj;
                p.FromXml(child);
                this.intermediatePoints.add(p);
            }
        }
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
