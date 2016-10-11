/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.XmlMarshallable;
import gralog.plugins.XmlName;
import gralog.rendering.Vector2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="intermediatepoint")
public class EdgeIntermediatePoint extends XmlMarshallable implements IMovable {
    
    public Vector2D Coordinates;
    
    public EdgeIntermediatePoint()
    {
        Coordinates = new Vector2D(0d, 0d);
    }
    
    public EdgeIntermediatePoint(Double x, Double y)
    {
        Coordinates = new Vector2D(x, y);
    }
    
    public EdgeIntermediatePoint(Vector2D coords)
    {
        Coordinates = coords;
    }
    
    public boolean ContainsCoordinate(double x, double y)
    {
        return (getX() - x) * (getX() - x) +
               (getY() - y) * (getY() - y) < 0.15*0.15;
    }
    
    @Override
    public void Move(Vector2D offset)
    {
        Coordinates = Coordinates.Plus(offset);
    }

    public void SnapToGrid(double GridSize)
    {
        Coordinates = Coordinates.SnapToGrid(GridSize);
    }
    
    public double get(int dimension) {
        if(dimension < Coordinates.Dimensions())
            return Coordinates.get(dimension);
        return 0.0d;
    }
    public double getX() {
        return Coordinates.getX();
    }
    public double getY() {
        return Coordinates.getY();
    }
    
    @Override
    public Element ToXml(Document doc) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("x", Double.toString(getX()));
        enode.setAttribute("y", Double.toString(getY()));
        return enode;
    }
    
    public void FromXml(Element enode) {
        double x = Double.parseDouble(enode.getAttribute("x"));
        double y = Double.parseDouble(enode.getAttribute("y"));
        Coordinates = new Vector2D(x, y);
    }
    
    @Override
    public String toString() {
        return Coordinates.toString();
    }
}
