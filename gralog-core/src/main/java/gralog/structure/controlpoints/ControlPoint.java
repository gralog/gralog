/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.structure.controlpoints;

import gralog.plugins.XmlMarshallable;
import gralog.plugins.XmlName;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.IMovable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

@XmlName(name = "controlpoint")
public class ControlPoint extends XmlMarshallable implements IMovable, Serializable {

    protected static final double DRAW_RADIUS = 0.05;
    protected static final double DRAW_RADIUS_SELECTED = 0.1;
    protected static final double CLICK_RADIUS = 0.3;

    public Vector2D position;

    public Edge parent;

    public boolean active;

    public ControlPoint() {
    }

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
        double radius = highlights.isSelected(this) ? DRAW_RADIUS_SELECTED : DRAW_RADIUS;
        gc.circle(getPosition(), radius, GralogColor.RED);
    }

    public void renderBezierHelpers(GralogGraphicsContext gc, Highlights highlights) {
        gc.line(parent.getSource().getCoordinates(), getPosition(),
                GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
        gc.line(parent.getTarget().getCoordinates(), getPosition(),
                GralogColor.BLACK, 0.02, GralogGraphicsContext.LineType.DASHED);
    }

    public boolean containsCoordinate(double x, double y) {
        Vector2D pos = getPosition();
        return Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2) < CLICK_RADIUS * CLICK_RADIUS; //squared
    }

    @Override
    public Element toXml(Document doc) throws Exception {
        Element enode = super.toXml(doc);
        enode.setAttribute("x", Double.toString(position.getX()));
        enode.setAttribute("y", Double.toString(position.getY()));
        return enode;
    }

    public void fromXml(Element enode) {
        double x = Double.parseDouble(enode.getAttribute("x"));
        double y = Double.parseDouble(enode.getAttribute("y"));
        position = new Vector2D(x, y);
    }

}
