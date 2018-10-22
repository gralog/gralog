package gralog.structure.controlpoints;

import gralog.plugins.XmlMarshallable;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.IMovable;
import gralog.structure.Vertex;

import java.io.Serializable;

public class ControlPoint extends XmlMarshallable implements IMovable, Serializable {

    protected static final double drawRadius = 0.05;
    protected static final double drawRadiusSelected = 0.1;
    protected static final double clickRadius = 0.3;

    public Vector2D position;

    public Edge parent;

    public boolean active;

    public ControlPoint() { }

    public ControlPoint(Vector2D position, Edge parent) {
        this.position = position;
        this.parent = parent;
    }

    @Override
    public void move(Vector2D vec) {
        position = position.plus(vec);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void render(GralogGraphicsContext gc, Highlights highlights) {
        double radius = highlights.isSelected(this) ? drawRadiusSelected : drawRadius;
        gc.circle(getPosition(), radius, GralogColor.RED);
    }
    public void renderBezierHelpers(GralogGraphicsContext gc, Highlights highlights) {
        gc.line(parent.getSource().coordinates, getPosition(), GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
        gc.line(parent.getTarget().coordinates, getPosition(), GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
    }

    public boolean containsCoordinate(double x, double y) {
        Vector2D pos = getPosition();
        return Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2) < clickRadius * clickRadius; //squared
    }
}
