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
    
    public Vector2D Multiply(Double c)
    {
        return new Vector2D(this.x * c, this.y * c);
    }
    
    public Double length()
    {
        return Math.sqrt(x*x + y*y);
    }
    
}
