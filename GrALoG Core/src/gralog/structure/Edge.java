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
    
    public String Label = "";
    
    public Boolean isDirected = true;
    
    public Double ArrowHeadLength = 0.4d; // cm
    public Double ArrowHeadAngle = 40d; // degrees
    public Double Width = 2.54/96; // cm
    public GralogColor Color = GralogColor.black;
    
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

        GralogColor color = this.Color;
        if(highlights != null && highlights.contains(this))
            color = GralogColor.red;
        
        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = tempX;
            fromY = tempY;
            tempX = between.get(0);
            tempY = between.get(1);
            gc.Line(fromX, fromY, tempX, tempY, color, Width);
            
            if(highlights != null && highlights.contains(this))
                gc.Rectangle(tempX-0.1, tempY-0.1, tempX+0.1, tempY+0.1, color, 2.54/96);
        }

        Double toX = target.Coordinates.get(0);
        Double toY = target.Coordinates.get(1);
        if(isDirected)
        {
            Vector2D intersection = target.Intersection(new Vector2D(tempX,tempY), new Vector2D(toX,toY));
            gc.Arrow(tempX, tempY, intersection.getX(), intersection.getY(), ArrowHeadAngle, ArrowHeadLength, color, Width);
        }
        else
            gc.Line(tempX, tempY, toX, toY, color, Width);
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

    public EdgeIntermediatePoint addIntermediatePoint(Double x, Double y)
    {
        Double fromX = source.Coordinates.get(0);
        Double fromY = source.Coordinates.get(1);
        Double nextfromX = fromX;
        Double nextfromY = fromY;
        
        int i = 0, insertionIndex = 0;
        Double MinDistance = Double.MAX_VALUE;

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
            insertionIndex = intermediatePoints.size();
            MinDistance = distanceTemp;
        }
        
        EdgeIntermediatePoint result = new EdgeIntermediatePoint(x,y);
        intermediatePoints.insertElementAt(result, insertionIndex);
        return result;
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
        enode.setAttribute("isdirected", isDirected ? "true" : "false");
        enode.setAttribute("label", Label);
        enode.setAttribute("width", Width.toString());
        enode.setAttribute("arrowheadlength", ArrowHeadLength.toString());
        enode.setAttribute("arrowheadangle", ArrowHeadAngle.toString());
        enode.setAttribute("color", Color.toHtmlString());
        
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

        if(enode.hasAttribute("isdirected"))
            isDirected = enode.getAttribute("isdirected").equals("true");
        Label = enode.getAttribute("label");
        if(enode.hasAttribute("width"))
            Width = Double.parseDouble(enode.getAttribute("width"));

        if(enode.hasAttribute("arrowheadlength"))
            ArrowHeadLength = Double.parseDouble(enode.getAttribute("arrowheadlength"));
        if(enode.hasAttribute("arrowheadangle"))
            ArrowHeadAngle = Double.parseDouble(enode.getAttribute("arrowheadangle"));
        if(enode.hasAttribute("color"))
            Color = GralogColor.parseColor(enode.getAttribute("color"));
        
        
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
