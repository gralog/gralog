/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.rendering;

/**
 *
 * @author viktor
 */
public abstract class GralogGraphicsContext {

    abstract public void Line(double x1, double y1, double x2, double y2, GralogColor color, double width);

    public void Arrow(double x1, double y1, double x2, double y2, double HeadAngle, double HeadLength, GralogColor color, double width)
    {
        Double ArrowHeadAngle = HeadAngle / 2.0d;
        Double ArrowHeadLength = HeadLength;

        Vector2D temp = new Vector2D(x2-x1, y2-y1);
        Double alpha = temp.Theta();

        Double arrowX1 = x2-ArrowHeadLength*Math.cos((alpha-ArrowHeadAngle)*Math.PI/180.0d);
        Double arrowY1 = y2-ArrowHeadLength*Math.sin((alpha-ArrowHeadAngle)*Math.PI/180.0d);
        Double arrowX2 = x2-ArrowHeadLength*Math.sin((90d-alpha-ArrowHeadAngle)*Math.PI/180.0d);
        Double arrowY2 = y2-ArrowHeadLength*Math.cos((90d-alpha-ArrowHeadAngle)*Math.PI/180.0d);

        Line(x1, y1, x2, y2, color, width);
        // arrow head
        Line(arrowX1, arrowY1, x2, y2, color, width);
        Line(arrowX2, arrowY2, x2, y2, color, width);
        Line(arrowX1, arrowY1, arrowX2, arrowY2, color, width);
    }
    
    abstract public void Circle(double centerx, double centery, double radius, GralogColor color);
    
    public void Rectangle(double x1, double y1, double x2, double y2, GralogColor color, double width) {
        Line(x1,y1,x2,y1,color,width);
        Line(x2,y1,x2,y2,color,width);
        Line(x2,y2,x1,y2,color,width);
        Line(x1,y2,x1,y1,color,width);
    }

    abstract public void FillRectangle(double x1, double y1, double x2, double y2, GralogColor color);
    
    public void PutText(double centerx, double centery, String text, double LineHeightCm, GralogColor color) {
    }

}
