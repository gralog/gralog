package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Rectangle extends RenderingShape {

    public Rectangle(SizeBox s){
        super(s);
    }

    @Override
    public void render(GralogGraphicsContext gc, boolean h, Vector2D center) {
        //rendered shape is already given by sizeBox
        double x1 = center.getX() - sizeBox.width/2;
        double y1 = center.getY() - sizeBox.height / 2;
        double x2 = center.getX() + sizeBox.width/2;
        double y2 = center.getY() + sizeBox.height/2;

        if (h){
            gc.strokeRectangle(x1 - strokeWidth/2, y1 - strokeWidth/2,
                    x2 + strokeWidth/2, y2 + strokeWidth/2,
                    strokeWidth, GralogColor.RED);
        }
        else{
            gc.strokeRectangle(x1 - strokeWidth/2, y1 - strokeWidth/2,
                    x2 + strokeWidth/2, y2 + strokeWidth/2,
                    strokeWidth, GralogColor.BLACK);
        }

        gc.fillRectangle(x1, y1, x2, y2, GralogColor.WHITE);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center){
        return false;
    }
}
