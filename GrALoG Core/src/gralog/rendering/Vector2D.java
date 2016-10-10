/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.rendering;

/**
 * A 2-dimensional immutable vector.
 */
public class Vector2D {
    private final double x, y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }
    
    public double get(int dimension)
    {
        switch(dimension)
        {
            case 0: return x;
            case 1: return y;
            default: throw new RuntimeException("Invalid dimension for a 2-dimensional vector: " + dimension);
        }
    }

    public int Dimensions()
    {
        return 2;
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
    
    public double Multiply(Vector2D other)
    {
        return this.x * other.x + this.y * other.y;
    }
    
    public Vector2D Normalized()
    {
        return this.Multiply(1d / this.Length());
    }
    
    public Vector2D Multiply(double c)
    {
        return new Vector2D(this.x * c, this.y * c);
    }
    
    public double Length()
    {
        return Math.sqrt(x*x + y*y);
    }
 
    public double Theta()
    {
        double alpha = Math.asin( this.Normalized().y );
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
        
    public static Vector2D ClosestPointOnLine(double px, double py, double l1x, double l1y, double l2x, double l2y)
    {
        Vector2D p = new Vector2D(px, py);
        Vector2D l1 = new Vector2D(l1x, l1y);
        Vector2D l2 = new Vector2D(l2x, l2y);
        Vector2D l = l2.Minus(l1);
        
        // normal-vector
        Vector2D n = l.Orthogonal();
        
        // lotfuß-punkt
        double k = Math.abs( l1.Minus(p).Multiply(n) ) / n.Multiply(n);
        Vector2D q = p.Plus(n.Multiply(k));
        
        return q;
    }
    
    public static double DistancePointToLine(double px, double py, double l1x, double l1y, double l2x, double l2y)
    {
        Vector2D p = new Vector2D(px, py);
        Vector2D l1 = new Vector2D(l1x, l1y);
        Vector2D l2 = new Vector2D(l2x, l2y);
        Vector2D l = l2.Minus(l1);

        if(l.getX() == 0 && l.getY() == 0) // (*)
            return l1.Minus(p).Length(); // l1==l2 so "the line" is actually just the point l1

        Vector2D perpendicular = ClosestPointOnLine(px, py, l1x, l1y, l2x, l2y);
        double lScaleToPerpendicular = 0.0;
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

    private static double snap(double GridSize, double value)
    {
        double newCoord = value + GridSize / 2d;
        double temp = newCoord % GridSize;
        if(temp < 0)
            temp += GridSize;
        return newCoord - temp;
    }

    public Vector2D SnapToGrid(double GridSize)
    {
        return new Vector2D(snap(GridSize, x), snap(GridSize, y));
    }

}
