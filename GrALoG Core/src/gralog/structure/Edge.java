/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
    
    Set<EdgeListener> listeners = new HashSet<>();
    
    public String Label = "";
    public double Cost = 1.0d;
    
    public Boolean isDirected = true;
    
    public double ArrowHeadLength = 0.4d; // cm
    public double ArrowHeadAngle = 40d; // degrees
    public double Width = 2.54/96; // cm
    public GralogColor Color = GralogColor.black;
    
    public ArrayList<EdgeIntermediatePoint> intermediatePoints = new ArrayList<>();
    
    private Vertex source = null;
    private Vertex target = null;
    public Vertex getSource()
    {
        return source;
    }
    public void setSource(Vertex source)
    {
        if(this.source != null)
            this.source.disconnectEdge(this);
        this.source = source;
        if(source != null)
            this.source.connectEdge(this);
    }
    public Vertex getTarget()
    {
        return target;
    }
    public void setTarget(Vertex target)
    {
        if(this.target != null)
            this.target.disconnectEdge(this);
        this.target = target;
        if(target != null)
            this.target.connectEdge(this);
    }
    
    

    public double MaximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        for(EdgeIntermediatePoint between : intermediatePoints)
            result = Math.max(result, between.get(dimension));
        return result;
    }

    @Override
    public void Move(Vector2D offset) {
        for(EdgeIntermediatePoint between : intermediatePoints)
            between.Move(offset);
    }

    public void SnapToGrid(double GridSize) {
        for(EdgeIntermediatePoint between : intermediatePoints)
            between.SnapToGrid(GridSize);
    }
    
    public IMovable FindObject(double x, double y) {
        for(EdgeIntermediatePoint p : intermediatePoints)
            if(p.ContainsCoordinate(x,y))
                return p;
        
        if(this.ContainsCoordinate(x, y))
            return this;
        
        return null;
    }
    
    
    protected double BinomialCoefficients(int n, int k)
    {
        double result = 1.0;
        for (int i = 1; i <= k; i++)
            result = result * (n + 1 - i) / i;
        return result;
    }
    
    protected Vector2D BezierCurve(double t) {
        int n = intermediatePoints.size() + 1;
        
        Vector2D result = this.source.Coordinates.Multiply( Math.pow(1-t, n) );
        result = result.Plus(this.target.Coordinates.Multiply( Math.pow(t, n) ));

        int i = 1;
        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            result = result.Plus(between.Coordinates.Multiply(
                BinomialCoefficients(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i)
            ));
            i++;
        }

        return result;
    }
    
    
    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        
        double fromX = source.Coordinates.get(0);
        double fromY = source.Coordinates.get(1);
        double toX = target.Coordinates.get(0);
        double toY = target.Coordinates.get(1);

        /*
        if(intermediatePoints.size() > 0)
        {
            double steps = 1/40d;

            for(double t = steps; t < 1; t += steps) {
                VectorND next = BezierCurve(t);
                double tempX = next.get(0);
                double tempY = next.get(1);

                gc.Line(fromX, fromY, tempX, tempY, this.Color, this.Width);
                fromX = tempX;
                fromY = tempY;
            }
            gc.Line(fromX, fromY, toX, toY, this.Color, this.Width);

            //if(highlights != null && highlights.contains(this))
                for(EdgeIntermediatePoint between : intermediatePoints)
                    gc.Rectangle(between.get(0)-0.1, between.get(1)-0.1, between.get(0)+0.1, between.get(1)+0.1, this.Color, 2.54/96);
                
        } else {
            gc.Line(fromX, fromY, toX, toY, this.Color, this.Width);
        }
        */
        
        double tempX = fromX;
        double tempY = fromY;

        GralogColor color = this.Color;
        if(highlights != null && highlights.contains(this))
            color = GralogColor.red;
        
        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = tempX;
            fromY = tempY;
            tempX = between.getX();
            tempY = between.getY();
            gc.Line(fromX, fromY, tempX, tempY, color, Width);
            
            if(highlights != null && highlights.contains(this))
                gc.Rectangle(tempX-0.1, tempY-0.1, tempX+0.1, tempY+0.1, color, 2.54/96);
        }

        if(isDirected)
        {
            Vector2D intersection = target.Intersection(new Vector2D(tempX,tempY), new Vector2D(toX,toY));
            gc.Arrow(tempX, tempY, intersection.getX(), intersection.getY(), ArrowHeadAngle, ArrowHeadLength, color, Width);
        }
        else
            gc.Line(tempX, tempY, toX, toY, color, Width);
    }
    
    public boolean ContainsCoordinate(double x, double y) {
        double fromX = source.Coordinates.get(0);
        double fromY = source.Coordinates.get(1);
        double nextfromX = fromX;
        double nextfromY = fromY;

        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.getX();
            nextfromY = between.getY();
            if(Vector2D.DistancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY) < 0.3)
                return true;
        }
        
        double toX = target.Coordinates.get(0);
        double toY = target.Coordinates.get(1);
        return Vector2D.DistancePointToLine(x, y, nextfromX, nextfromY, toX, toY) < 0.3;
    }

    public EdgeIntermediatePoint addIntermediatePoint(double x, double y)
    {
        double fromX = source.Coordinates.get(0);
        double fromY = source.Coordinates.get(1);
        double nextfromX = fromX;
        double nextfromY = fromY;
        
        int i = 0, insertionIndex = 0;
        double MinDistance = Double.MAX_VALUE;

        for(EdgeIntermediatePoint between : intermediatePoints)
        {
            fromX = nextfromX;
            fromY = nextfromY;
            nextfromX = between.getX();
            nextfromY = between.getY();
            
            double distanceTemp = Vector2D.DistancePointToLine(x, y, fromX, fromY, nextfromX, nextfromY);
            if( distanceTemp < MinDistance )
            {
                insertionIndex = i;
                MinDistance = distanceTemp;
            }
            i++;
        }
        
        double toX = target.Coordinates.get(0);
        double toY = target.Coordinates.get(1);

        double distanceTemp = Vector2D.DistancePointToLine(x, y, nextfromX, nextfromY, toX, toY);
        if( distanceTemp < MinDistance )
        {
            insertionIndex = intermediatePoints.size();
        }
        
        EdgeIntermediatePoint result = new EdgeIntermediatePoint(x,y);
        intermediatePoints.add(insertionIndex, result);
        return result;
    }    
    
    public boolean ContainsVertex(Vertex v) {
        return source == v || target == v;
    }
    
    public double Length() {
        Vector2D from = this.source.Coordinates;
        Vector2D to = this.target.Coordinates;
        
        double result = 0.0;
        for(EdgeIntermediatePoint between : this.intermediatePoints)
        {
            result += between.Coordinates.Minus(from).Length();
            from = between.Coordinates;
        }
        return result + to.Minus(from).Length();
    }
    
    
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        enode.setAttribute("isdirected", isDirected ? "true" : "false");
        enode.setAttribute("label", Label);
        enode.setAttribute("cost", Double.toString(Cost));
        enode.setAttribute("width", Double.toString(Width));
        enode.setAttribute("arrowheadlength", Double.toString(ArrowHeadLength));
        enode.setAttribute("arrowheadangle", Double.toString(ArrowHeadAngle));
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
        if(enode.hasAttribute("cost"))
            Cost = Double.parseDouble(enode.getAttribute("cost"));

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
            if(childNode.getNodeType() != Node.ELEMENT_NODE)
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
