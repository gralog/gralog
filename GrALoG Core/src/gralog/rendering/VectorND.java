/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.rendering;

import java.util.Vector;

/**
 *
 * @author viktor
 */
public class VectorND {
    
    protected Vector<Double> vector;
    
    public VectorND()
    {
        this.vector = new Vector<Double>();
    }
    public VectorND(Vector<Double> vect)
    {
        this.vector = (Vector<Double>)vect.clone();
    }
    
    public Vector<Double> toVector() {
        return this.vector;
    }
    
    public void add(Double d) {
        vector.add(d);
    }
    
    public VectorND Plus(VectorND other)
    {
        VectorND result = new VectorND();
        
        int n = Math.max(vector.size(), other.vector.size());
        for(int i = 0; i < n; i++)
            result.vector.add(  (vector.size() > i ? vector.elementAt(i) : 0d) 
                  + (other.vector.size() > i ? other.vector.elementAt(i) : 0d));
        
        return result;
    }
    
    public VectorND Minus(VectorND other)
    {
        VectorND result = new VectorND();
        
        int n = Math.max(vector.size(), other.vector.size());
        for(int i = 0; i < n; i++)
            result.vector.add(  (vector.size() > i ? vector.elementAt(i) : 0d) 
                  - (other.vector.size() > i ? other.vector.elementAt(i) : 0d));
        
        return result;
    }
    
    public Double Multiply(VectorND other)
    {
        Double result = 0d;
        int n = Math.min(vector.size(), other.vector.size());
        for(int i = 0; i < n; i++)
            result += vector.elementAt(i) * other.vector.elementAt(i);
        return result;
    }
    
    public VectorND Multiply(Double c)
    {
        VectorND result = new VectorND();
        
        for(int i = 0; i < vector.size(); i++)
            result.vector.add( vector.elementAt(i) * c );
        
        return result;
    }
    
    public Double Length()
    {
        Double result = 0d;
        for(int i = 0; i < vector.size(); i++)
        {
            Double temp = vector.elementAt(i);
            result += temp * temp;
        }
        return Math.sqrt(result);
    }
    
    public VectorND Normalized()
    {
        return this.Multiply(1d / this.Length());
    }
        
}
