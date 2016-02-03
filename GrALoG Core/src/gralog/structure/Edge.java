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
    public String Label;
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

    public IMovable FindObject(Double x, Double y) {
        for(EdgeIntermediatePoint p : intermediatePoints)
            if(p.ContainsCoordinate(x,y))
                return p;
        
        if(this.ContainsCoordinate(x, y))
            return this;
        
        return null;
    }
    
    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        Double fromX = source.Coordinates.get(0);
        Double fromY = source.Coordinates.get(1);
        Double tempX = fromX;
        Double tempY = fromY;

        GralogColor color = new GralogColor( highlights != null && highlights.contains(this) ? 0xFF0000 : 0x000000 );
        
        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = tempX;
            fromY = tempY;
            tempX = between.get(0);
            tempY = between.get(1);
            gc.Line(fromX, fromY, tempX, tempY, color);
        }

        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);
        gc.Line(tempX, tempY, toX, toY, color);
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
            if(Vector2D.DistancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY) < 0.3)
                return true;
        }
        
        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);
        return Vector2D.DistancePointToLine(x, y, nextfromX, nextfromY, toX, toY) < 0.3;
    }

    public void addIntermediatePoint(Double x, Double y)
    {
        Double fromX = source.Coordinates.get(0);
        Double fromY = source.Coordinates.get(1);
        Double nextfromX = fromX;
        Double nextfromY = fromY;
        
        int i = 0, insertionIndex = 0;
        Double MinDistance = 0d;

        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.get(0);
            nextfromY = between.get(1);
            
            Double distanceTemp = Vector2D.DistancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY);
            if( distanceTemp < MinDistance )
            {
                insertionIndex = i;
                MinDistance = distanceTemp;
            }
            i++;
        }
        
        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);

        Double distanceTemp = Vector2D.DistancePointToLine(x, y, nextfromX, nextfromY, toX, toY);
        if( distanceTemp < MinDistance )
        {
            insertionIndex = i;
            MinDistance = distanceTemp;
        }
        
        intermediatePoints.insertElementAt(new EdgeIntermediatePoint(x,y), i);
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
            result += betw.Minus(from).Length();
            from = betw;
        }
        return result + to.Minus(from).Length();
    }
    
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        enode.setAttribute("label", Label);
        
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
        Label = enode.getAttribute("label");
        
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
