package gralog.structure;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class CurveControlPoint implements IMovable{

    protected static final double drawRadius = 0.05;
    protected static final double clickRadius = 0.2;

    public Vector2D position;

    public Vertex firstAnchor;
    public Vertex secondAnchor;
    public Edge parent;

    public boolean active;

    public CurveControlPoint() { }

    public CurveControlPoint(Vector2D position, Vertex firstAnchor, Vertex secondAnchor, Edge parent){
        this.firstAnchor = firstAnchor;
        this.secondAnchor = secondAnchor;
        this.position = position;
        this.parent = parent;
    }
    @Override
    public void move(Vector2D vec){
        position = position.plus(vec);
    }

    public Vector2D getPosition(){
        return position;
    }

    public void render(GralogGraphicsContext gc){
        gc.line(firstAnchor.coordinates, getPosition(), GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
        gc.line(secondAnchor.coordinates, getPosition(), GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
        gc.circle(getPosition(), drawRadius, GralogColor.RED);
    }

    public boolean containsCoordinate(double x, double y){
        Vector2D pos = getPosition();
        return Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2) < clickRadius * clickRadius; //squared
    }
}
