/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import javafx.util.StringConverter;
import org.reflections.Reflections;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;


public abstract class RenderingShape implements Serializable {



    //used to restrict the size box, can cause performance problems otherwise
    protected static final double MAX_WIDTH = 300;
    protected static final double MAX_HEIGHT = 300;
    public static LinkedList<Class<? extends RenderingShape>> renderingShapeClasses;

    /*
     * Initializes a list of rendering shapes
     */
    static {
        Reflections reflections = new Reflections("gralog.rendering.shapes");
        Set<Class<? extends RenderingShape>> classes = reflections.getSubTypesOf(RenderingShape.class);
        renderingShapeClasses = new LinkedList<>(classes);
    }

    public Double strokeWidth = 3.6d / 96; //cm
    /**
     * Provides a rough measure of the size of a shape via a Rectangle,
     * applicable height every subclass of shape
     *
     * Two shapes with the same SizeBoxes should have roughly the same
     * actual size in the final render.
     */
    public SizeBox sizeBox;
    /**
     * Creates a RenderingShape with sensible default values, so that the final size of
     * the rendered shape has similar dimensions as the rectangle of the given
     * SizeBox
     */
    public RenderingShape(SizeBox s) {
        this.sizeBox = s;
    }

    public static boolean isShape(String s) {
        for (PossibleShapes ps : PossibleShapes.values())
            if (ps.name().equalsIgnoreCase(s))
                return true;
        return false;
    }

    public void setWidth(double width) {
        sizeBox.width = Math.min(width, MAX_WIDTH);
    }

    public void setHeight(double height) {
        sizeBox.height = Math.min(height, MAX_HEIGHT);
    }

    /**
     * Renders a shape on the provided graphics context. RenderingShape will depend
     * on the internal state of the shape object (e.g. parameters such as
     * size, fill, color, etc..)
     *
     * @param gc    The graphics context on which the shape is rendered
     * @param label
     * @param s     Stroke color
     * @param f     Filling color
     */
    public void render(GralogGraphicsContext gc, Vector2D center, String label, GralogColor s, GralogColor f) {
        double avg = (sizeBox.width + sizeBox.height) / 2;
        gc.putText(center, label, avg / 4, f.inverse());
    }

    public void render(GralogGraphicsContext gc, Vector2D center, GralogColor s, GralogColor f) {
        render(gc, center, "", s, f);
    }

    /**
     * Returns true if the given vector is overlapping with this shape
     *
     * @param point Vector that is being tested for overlapping
     * @return True if the shape contains
     */
    public abstract boolean containsCoordinate(Vector2D point, Vector2D center);

    /**
     * Calculates the (closest) point X on the edge of the shape so that (X-center)
     * has an euler angle of alpha.
     *
     * @param alpha  The angle of the point in degrees
     * @param center The point of reference for calculating X
     * @return The closest such point as a Vector2D
     */
    public abstract Vector2D getEdgePoint(double alpha, Vector2D center);

    /**
     * Calculates the point of intersection between a line and the shape
     * (Basically a RayCast)
     *
     * @param lineStart Start of the line
     * @param lineEnd   The end of the line INSIDE the shape. For a normal edge calculation, lineEnd = center
     * @param center    The center of the object with this shape
     */
    public abstract Vector2D getIntersection(Vector2D lineStart, Vector2D lineEnd, Vector2D center);

    // if changed, also change enum PossibleShapes in gralog-core.gralog.dialog
    private enum PossibleShapes {
        // TODO: SQUARE,
        CYCLE,
        ELLIPSE,
        RECTANGLE,
        DIAMOND
    }

    /**
     * StringConverter class. Useful for any class that relies on redefining the toString() method
     * of classes that extend the RenderingShape
     *
     * @see javafx.scene.control.ChoiceBox e.g. setConverter()
     */
    public static final class ShapeConverter extends StringConverter<Class<? extends RenderingShape>> {

        @Override
        public String toString(Class<? extends RenderingShape> object) {
            return object.getSimpleName();
        }

        @Override
        public Class<? extends RenderingShape> fromString(String string) {
            return null;
        }
    }

}
