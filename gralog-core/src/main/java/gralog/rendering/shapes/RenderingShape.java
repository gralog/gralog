package gralog.rendering.shapes;

import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import gralog.structure.Highlights;


public abstract class RenderingShape {

    /**
     * Provides a rough measure of the size of a shape via a Rectangle,
     * applicable to every subclass of shape
     *
     * Two shapes with the same SizeBoxes should have roughly the same
     * actual size in the final render.
     */
    protected SizeBox sizeBox;


    public RenderingShape(Vector2D from, Vector2D to){
        this(new SizeBox(from, to));
    }

    /**
     * Creates a RenderingShape with sensible default values, so that the final size of
     * the rendered shape has similar dimensions as the rectangle of the given
     * SizeBox
     */
    public RenderingShape(SizeBox s){
        this.sizeBox = s;
    }

    /**
     * Renders a shape on the provided graphics context. RenderingShape will depend
     * on the internal state of the shape object (e.g. parameters such as
     * size, fill, color, etc..)
     *
     * @param gc The graphics context on which the shape is rendered
     */
    public abstract void render(GralogGraphicsContext gc, Highlights h);



}
