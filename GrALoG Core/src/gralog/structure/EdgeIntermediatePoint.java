/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import gralog.plugins.XmlMarshallable;
import gralog.plugins.XmlName;

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
    
    Vector<Double> coordinates = new Vector<Double>();
    
    public EdgeIntermediatePoint()
    {
    }
    
    public EdgeIntermediatePoint(Double x, Double y)
    {
        this.coordinates.add(x);
        this.coordinates.add(y);
    }
    
    public EdgeIntermediatePoint(Vector<Double> coords)
    {
        for(Double d : coords)
            this.coordinates.add(d);
    }
    
    public boolean ContainsCoordinate(Double x, Double y)
    {
        return  (get(0)-x)*(get(0)-x)
              + (get(1)-y)*(get(1)-y)
              < 0.3;
    }
    
    public void Move(Vector<Double> offsets)
    {
        int n = Math.min(offsets.size(), coordinates.size());
        
        for(int i = 0; i < n; i++)
            coordinates.set(i, coordinates.get(i) + offsets.get(i));
        
        // if offsets has more elements, treat coordinates as if it had
        // (infinitely many) zeroes behind
        for(int i = n; i < offsets.size(); i++)
            coordinates.add(offsets.get(i));
    }
    
    public int size() {
        return coordinates.size();
    }
    
    public Double get(int dimension) {
        if(dimension < coordinates.size())
            return coordinates.get(dimension);
        return 0.0d;
    }
    
    public Element ToXml(Document doc) throws Exception {
        Element enode = super.ToXml(doc);
        enode.setAttribute("x", this.get(0).toString());
        enode.setAttribute("y", this.get(1).toString());
        return enode;
    }
    
    public void FromXml(Element enode) {
        coordinates.clear();
        coordinates.add(Double.parseDouble(enode.getAttribute("x")));
        coordinates.add(Double.parseDouble(enode.getAttribute("y")));
    }
    
    @Override
    public String toString() {
        return coordinates.toString();
    }
}
