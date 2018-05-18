package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Diamond extends RenderingShape {

    public Diamond(SizeBox s) {
        super(s);
    }

    @Override
    public void render(GralogGraphicsContext gc, boolean h, Vector2D center) {
        double x1 = center.getX() - sizeBox.width/2;
        double y1 = center.getY() - sizeBox.height / 2;
        double x2 = center.getX() + sizeBox.width/2;
        double y2 = center.getY() + sizeBox.height/2;

        if (h){
            gc.strokeDiamond(x1 - strokeWidth/2, y1 - strokeWidth/2,
                    x2 + strokeWidth/2, y2 + strokeWidth/2,
                    strokeWidth, GralogColor.RED);
        }
        else{
            gc.strokeDiamond(x1 - strokeWidth/2, y1 - strokeWidth/2,
                    x2 + strokeWidth/2, y2 + strokeWidth/2,
                    strokeWidth, GralogColor.BLACK);
        }
        gc.fillDiamond(x1, y1, x2, y2, GralogColor.WHITE);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center){
        return false;
    }

    @Override
    public Vector2D getEdgePoint(double alpha, Vector2D center){
        double x;
        double y;
        alpha = alpha % 360;
        if(alpha < 0){
            alpha += 360;
        }
        if(alpha < 90){
            x = (1-alpha/90)*sizeBox.width/2;
            y = (alpha/90)*sizeBox.height/2;
        }else if(alpha < 180){
            alpha %= 90;
            x = -(alpha/90)*sizeBox.width/2;
            y = (1-alpha/90)*sizeBox.height/2;
        }else if(alpha < 270){
            alpha %= 90;
            x = -(1-alpha/90)*sizeBox.width/2;
            y = -(alpha/90)*sizeBox.height/2;
        }else{
            alpha %= 90;
            x = (alpha/90)*sizeBox.width/2;
            y = -(1-alpha/90)*sizeBox.height/2;
        }

        return new Vector2D(x, y).plus(center);
    }

    @Override
    public Vector2D getIntersection(Vector2D a, Vector2D b, Vector2D center) {
        //first, do a classification by angle between center and lineStart
        double alpha = a.minus(center).theta();
        double w = sizeBox.width/2;
        double h = sizeBox.height/2;

        Vector2D r;
        Vector2D dir;

        double coeff;

        if(alpha < 90){
            r = new Vector2D(center.getX() + w, center.getY());
            dir = new Vector2D(w, -h);
        }else if(alpha < 180){
            r = new Vector2D(center.getX() - w, center.getY());
            dir = new Vector2D(w, h);
        }else if(alpha < 270){
            r = new Vector2D(center.getX() - w, center.getY());
            dir = new Vector2D(w, -h);
        }else{
            r = new Vector2D(center.getX() + w, center.getY());
            dir = new Vector2D(w, h);
        }
        coeff = dir.getY()/dir.getX();
        double t = ((a.getY() - r.getY()) - coeff*(a.getX() - r.getX()))/
                (-b.getY() + a.getY() - coeff*(-b.getX() + a.getX()));

        return a.plus(b.minus(a).multiply(t)); //line defined as a + (b-a)t
    }
}
