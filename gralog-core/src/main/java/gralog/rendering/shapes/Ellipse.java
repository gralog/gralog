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

    @Override
    public Vector2D getEdgePoint(double alpha, Vector2D center){
        //TODO: also for ellipse
        alpha = Math.toRadians(alpha);
        return new Vector2D(sizeBox.width/2 * Math.cos(alpha),
                sizeBox.height/2 * Math.sin(alpha)).plus(center);
    }

    @Override
    public Vector2D getIntersection(Vector2D a, Vector2D b, Vector2D center) {

        //TODO: do a reduced calculation if the sizebox is a square
        //(which is a default behaviour for many graphs)

        //move origin to center
        a = a.minus(center);
        b = b.minus(center);
        //parameters that define the ellipse
        final double w2 = Math.pow(sizeBox.width,  2);
        final double h2 = Math.pow(sizeBox.height, 2);

        //slope
        double d;

        if(a.getX() != b.getX()){
            d = (a.getY() - b.getY())/(a.getX() - b.getX());
        }else{
            //special case where a is on top or below b
            double y = Math.signum(a.getY()) * Math.sqrt(h2/4 - a.getX() * a.getX() * h2/w2);
            return new Vector2D(a.getX(), y).plus(center);
        }
        //constant factor for linear function of a + (b-a)t
        double c;

        if(b.getX() != 0){
            c = b.getY() - (d * b.getX());
        }else{
            c = b.getY();
        }

        //pq formula
        final double bracketTerm = (4/w2 + 4*d*d/h2);
        final double p = (8 * d * c)/( bracketTerm * h2);
        final double q = (4 * c * c * (1 / h2) - 1)/bracketTerm;

        final double x1 = -p/2 + Math.sqrt(p * p /4 -q);
        final double x2 = -p/2 - Math.sqrt(p * p /4 -q);

        if(a.getX() > b.getX()){
            if(x1 > b.getX() && x1 < a.getX()){
                return new Vector2D(x1, d * x1 + c).plus(center);
            }else{
                return new Vector2D(x2, d * x2 + c).plus(center);
            }
        }else if(a.getX() < b.getX()){
            if(x1 > a.getX() && x1 < b.getX()){
                return new Vector2D(x1, d * x1 + c).plus(center);
            }else{
                return new Vector2D(x2, d * x2 + c).plus(center);
            }
        }else{
            if(a.getY() > b.getY()){
                double y1 = d * x1 + c;
                if(y1 > b.getY() && y1 < a.getY()){
                    return new Vector2D(x1, y1).plus(center);
                }else{
                    return new Vector2D(x2, d * x2 + c).plus(center);
                }
            }else{
                double y1 = d * x1 + c;
                if(y1 > a.getY() && y1 < b.getY()){
                    return new Vector2D(x1, y1).plus(center);
                }else{
                    return new Vector2D(x2, d * x2 + c).plus(center);
                }
            }
        }
    }
}
