/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.XmlMarshallable;
import gralog.plugins.XmlName;
import gralog.rendering.VectorND;

import java.util.HashMap;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="intermediatepoint")
public class EdgeIntermediatePoint extends XmlMarshallable implements IMovable {
    
    VectorND Coordinates = new VectorND();
    
    public EdgeIntermediatePoint()
    {
    }
    
    public EdgeIntermediatePoint(Double x, Double y)
    {
        this.Coordinates.add(x);
        this.Coordinates.add(y);
    }
    
    public EdgeIntermediatePoint(Vector<Double> coords)
    {
        for(Double d : coords)
            this.Coordinates.add(d);
    }
    
    public boolean ContainsCoordinate(Double x, Double y)
    {
        return  (get(0)-x)*(get(0)-x)
              + (get(1)-y)*(get(1)-y)
              < 0.15*0.15;
    }
    
    public void Move(Vector<Double> offsets)
    {
        int n = Math.min(offsets.size(), Coordinates.Dimensions());
        
        for(int i = 0; i < n; i++)
            Coordinates.set(i, Coordinates.get(i) + offsets.get(i));
        
        // if offsets has more elements, treat coordinates as if it had
        // (infinitely many) zeroes behind
        for(int i = n; i < offsets.size(); i++)
            Coordinates.add(offsets.get(i));
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
    
    public int size() {
        return Coordinates.Dimensions();
    }
    
    public Double get(int dimension) {
        if(dimension < Coordinates.Dimensions())
            return Coordinates.get(dimension);
        return 0.0d;
    }
    
    public Element ToXml(Document doc) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("x", this.get(0).toString());
        enode.setAttribute("y", this.get(1).toString());
        return enode;
    }
    
    public void FromXml(Element enode) {
        Coordinates.clear();
        Coordinates.add(Double.parseDouble(enode.getAttribute("x")));
        Coordinates.add(Double.parseDouble(enode.getAttribute("y")));
    }
    
    @Override
    public String toString() {
        return Coordinates.toString();
    }
}
