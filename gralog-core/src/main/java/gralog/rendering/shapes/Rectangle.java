package gralog.rendering.shapes;

import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Rectangle extends RenderingShape {

    public Rectangle(SizeBox s){
        super(s);
    }

    @Override
    public void render(GralogGraphicsContext gc, boolean h, Vector2D center) {
        //rendered shape is already given by sizeBox
    }
}
