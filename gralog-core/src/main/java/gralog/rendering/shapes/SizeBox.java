package gralog.rendering.shapes;


import gralog.rendering.Vector2D;

/**
 * Describes a rectangle, contains two end points
 */
public class SizeBox {

    public Vector2D from;
    public Vector2D to;

    public Double angle;

    public SizeBox(Vector2D from, Vector2D to){
        this.from = from;
        this.to = to;
    }
}
