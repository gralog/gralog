package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Circle extends RenderingShape {

    public Circle(SizeBox s) {
        super(s);
    }

    public static Circle create(double radius){
        return new Circle(new SizeBox(2 * radius, 2 * radius));
    }
    @Override
    public void render(GralogGraphicsContext gc, boolean h, Vector2D center){
        double radius = sizeBox.width / 2;

        if (h)
            gc.circle(center, radius + 0.07, GralogColor.RED);

        gc.circle(center, radius, GralogColor.BLACK);
        gc.circle(center, radius - 2.54/96, GralogColor.WHITE);
        //TODO: add label etc
    }
}
