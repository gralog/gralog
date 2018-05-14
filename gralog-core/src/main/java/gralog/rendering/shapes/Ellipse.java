package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Ellipse extends RenderingShape {

    public Ellipse(SizeBox s) {
        super(s);
    }

    public static Ellipse create(double width, double height){
        return new Ellipse(new SizeBox(width, height));
    }
    @Override
    public void render(GralogGraphicsContext gc, boolean h, Vector2D center) {
        if (h){
            gc.strokeOval(center, sizeBox.width + strokeWidth,
                    sizeBox.height + strokeWidth,
                    strokeWidth, GralogColor.RED);
        }else{
            gc.strokeOval(center, sizeBox.width + strokeWidth,
                    sizeBox.height + strokeWidth,
                    strokeWidth, GralogColor.BLACK);
        }
        gc.fillOval(center, sizeBox.width, sizeBox.height, GralogColor.WHITE);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center){
        return false;
    }
}
