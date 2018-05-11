package gralog.rendering.shapes;

import gralog.rendering.GralogGraphicsContext;
import gralog.structure.Highlights;

public class Rectangle extends RenderingShape {

    public Rectangle(SizeBox s){
        super(s);
    }

    @Override
    public void render(GralogGraphicsContext gc, Highlights h) {
        //rendered shape is already given by sizeBox
    }
}
