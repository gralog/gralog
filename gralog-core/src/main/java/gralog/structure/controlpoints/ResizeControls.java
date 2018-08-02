package gralog.structure.controlpoints;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import gralog.structure.IMovable;
import gralog.structure.Vertex;

import java.io.Serializable;

public class ResizeControls implements IMovable, Serializable
{

    public boolean active;

    public Vertex v;
    public Vector2D position;

    public ResizeControls(Vertex v){
        this.v = v;

    }

    @Override
    public void move(Vector2D vec){

    }

    public void render(GralogGraphicsContext gc){
        Vector2D pos = (v.coordinates.minus(v.shape.sizeBox.width/2, v.shape.sizeBox.height/2));
        Vector2D shape = (new Vector2D(v.shape.sizeBox.width, v.shape.sizeBox.height));

        gc.strokeRectangle(pos.getX(), pos.getY(),
                pos.getX() + shape.getX(), pos.getY() + shape.getY(), 0.01, GralogColor.BLACK);
        double ovl = 0.1;
        gc.fillOval(pos.getX(), pos.getY(), ovl, ovl);
        gc.fillOval(pos.getX() + shape.getX(), pos.getY(), ovl, ovl);
        gc.fillOval(pos.getX(), pos.getY() + shape.getY(), ovl, ovl);
        gc.fillOval(pos.getX() + shape.getX(),
                pos.getY() + shape.getY(), ovl, ovl);
    }

}
