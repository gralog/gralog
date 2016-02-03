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
public class Vector2D {
    
    protected Double x;
    protected Double y;
    public Vector2D(Double x, Double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Double getX()
    {
        return x;
    }
    public Double getY()
    {
        return y;
    }
    
    public Vector2D(Vector<Double> vect)
    {
        this.x = vect.size() > 0 ? vect.get(0) : 0.0;
        this.y = vect.size() > 1 ? vect.get(1) : 0.0;
    }
    

    public Vector2D Orthogonal()
    {
        return new Vector2D(-this.y, this.x);
    }
    
    public Vector2D Plus(Vector2D other)
    {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    
    public Vector2D Minus(Vector2D other)
    {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    
    public Double Multiply(Vector2D other)
    {
        return this.x * other.x + this.y * other.y;
    }
    
    public Vector2D Normalized()
    {
        return this.Multiply(1d / this.Length());
    }
    
    public Vector2D Multiply(Double c)
    {
        return new Vector2D(this.x * c, this.y * c);
    }
    
    public Double Length()
    {
        return Math.sqrt(x*x + y*y);
    }
 
    public Double Theta()
    {
        Double alpha = Math.asin( this.Normalized().y );
        alpha *= 180d / Math.PI; // radian to degrees

        if(x > 0)
        {
            if(y > 0)
                return alpha;
            else
                return 360d + alpha; // alpha is negative
        }
        else
            return 180d - alpha;
    }
    
    
        
    public static Vector2D ClosestPointOnLine(Double px, Double py, Double l1x, Double l1y, Double l2x, Double l2y)
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
    
    public static Double DistancePointToLine(Double px, Double py, Double l1x, Double l1y, Double l2x, Double l2y)
    {
        Vector2D p = new Vector2D(px,py);
        Vector2D l1 = new Vector2D(l1x,l1y);
        Vector2D l2 = new Vector2D(l2x,l2y);
        Vector2D l = l2.Minus(l1);

        if(l.getX() == 0 && l.getY() == 0) // (*)
            return l1.Minus(p).Length(); // l1==l2 so "the line" is actually just the point l1

        
        Vector2D perpendicular = ClosestPointOnLine(px,py,l1x,l1y,l2x,l2y);
        Double lScaleToPerpendicular = 0.0;
        if(l.getX() != 0)
            lScaleToPerpendicular = (perpendicular.getX() - l1.getX())/l.getX();
        else // if(l.getY() != 0) // true, because of (*)
            lScaleToPerpendicular = (perpendicular.getY() - l1.getY())/l.getY();
        
        
        if(lScaleToPerpendicular < 0)
            return l1.Minus(p).Length();
        if(lScaleToPerpendicular > 1)
            return l2.Minus(p).Length();
        return perpendicular.Minus(p).Length();
    }
}
