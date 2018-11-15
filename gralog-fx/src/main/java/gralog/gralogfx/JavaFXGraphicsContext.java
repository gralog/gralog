/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.rendering.*;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;

/**
 *
 */
public class JavaFXGraphicsContext extends GralogGraphicsContext {

    protected GraphicsContext gc;
    protected StructurePane pane;

    public JavaFXGraphicsContext(GraphicsContext gc, StructurePane pane) {
        this.gc = gc;
        this.pane = pane;
    }

    @Override
    public void line(double x1, double y1, double x2, double y2, GralogColor c,
        double width, LineType type) {
        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(width * pane.zoomFactor * pane.screenResolutionX / 2.54);
        //TODO: pool line drawing
        if(type == LineType.DOTTED) {
            gc.setLineDashes(0.03 * pane.zoomFactor * pane.screenResolutionX / 2.54, 0.15 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }else if(type == LineType.DASHED) {
            gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }

        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        gc.setLineDashes(0);

    }

    public void arrow(Vector2D dir, Vector2D pos, Arrow arrowType, double scale, GralogColor color) {
        arrow(dir, pos, arrowType, scale, color, (2.54 / 96));
    }
    @Override
    public void polygon(double[] x, double[] y, int count, GralogColor c) {
        gc.setFill(Color.rgb(c.r, c.g, c.b));
        for (int i = 0; i < count; i++)
        {
            x[i] = pane.modelToScreenX(x[i]);
            y[i] = pane.modelToScreenY(y[i]);
        }
        gc.fillPolygon(x, y, count);
    }
    @Override
    public void lines(double[] x, double[] y, int count, GralogColor c, double lineWidth) {
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(lineWidth * pane.zoomFactor * (pane.screenResolutionX / 2.54));
        //gc.setLineWidth((2.54 / 96) * pane.zoomFactor * pane.screenResolutionX / 2.54);
        for (int i = 0; i < count; i++)
        {
            x[i] = pane.modelToScreenX(x[i]);
            y[i] = pane.modelToScreenY(y[i]);
        }
        gc.strokePolyline(x, y, count);
    }

    @Override
    public void loop(Loop l, double length, double correction, GralogColor c, double width, LineType type)
    {
        Vector2D a = pane.modelToScreen(l.start);
        Vector2D b = pane.modelToScreen(l.end.plus(l.tangentEnd.orthogonal().normalized().multiply(correction)));
        Vector2D ctrl1 = pane.modelToScreen(l.start.plus(l.tangentStart.orthogonal(1).normalized().multiply(length)));
        Vector2D ctrl2 = pane.modelToScreen(l.end.plus(l.tangentEnd.orthogonal(1).normalized().multiply(length)));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));

        if(type == LineType.DOTTED) {
            gc.setLineDashes(0.03 * pane.zoomFactor * pane.screenResolutionX / 2.54, 0.15 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }else if(type == LineType.DASHED) {
            gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }
        gc.setLineWidth(width * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.beginPath();
        gc.moveTo(a.getX(), a.getY());
        gc.bezierCurveTo(ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), b.getX(), b.getY());
        gc.stroke();
        gc.closePath();
        gc.setLineDashes(0);
    }
    public void drawBezier(Bezier curve, GralogColor color, double width, LineType type) {
        Vector2D a = pane.modelToScreen(curve.source);
        Vector2D b = pane.modelToScreen(curve.target);
        Vector2D ctrl1 = pane.modelToScreen(curve.ctrl1);
        Vector2D ctrl2 = pane.modelToScreen(curve.ctrl2);

        gc.setStroke(Color.rgb(color.r, color.g, color.b));
        //setting line type
        if(type == LineType.DOTTED) {
            gc.setLineDashes(0.03 * pane.zoomFactor * pane.screenResolutionX / 2.54, 0.15 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }else if(type == LineType.DASHED) {
            gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }
        gc.setLineWidth(width * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.beginPath();
        gc.moveTo(a.getX(), a.getY());
        gc.bezierCurveTo(ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), b.getX(), b.getY());
        gc.stroke();
        gc.closePath();
        gc.setLineDashes(0);
    }
    public void drawQuadratic(Bezier curve, GralogColor color, double width, LineType type) {
        Vector2D a = pane.modelToScreen(curve.source);
        Vector2D b = pane.modelToScreen(curve.target);
        Vector2D ctrl1 = pane.modelToScreen(curve.ctrl1);

        gc.setStroke(Color.rgb(color.r, color.g, color.b));
        //setting line type
        if(type == LineType.DOTTED) {
            gc.setLineDashes(0.03 * pane.zoomFactor * pane.screenResolutionX / 2.54, 0.15 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }else if(type == LineType.DASHED) {
            gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }
        gc.setLineWidth(width * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.beginPath();
        gc.moveTo(a.getX(), a.getY());
        gc.quadraticCurveTo(ctrl1.getX(), ctrl1.getY(), b.getX(), b.getY());
        gc.stroke();
        gc.closePath();
        gc.setLineDashes(0);
    }


    @Override
    public void circle(double centerx, double centery, double radius,
        GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(centerx - radius, centery - radius));
        Point2D p2 = pane.modelToScreen(new Point2D(centerx + radius, centery + radius));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(1);

        gc.fillOval(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }
    @Override
    public void strokeRectangle(double x1, double y1, double x2, double y2, double strokeWidth, GralogColor color) {

        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setStroke(Color.rgb(color.r, color.g, color.b));
        gc.setLineWidth(strokeWidth * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.strokeRect(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }

    @Override
    public void strokeRectangle(double x1, double y1, double x2, double y2, double strokeWidth, LineType type) {

        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));
        GralogColor color = GralogColor.BLACK;

        if(type == LineType.DOTTED) {
            gc.setLineDashes(0.08 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }else if(type == LineType.DASHED) {
            gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);gc.setLineDashes(0.2 * pane.zoomFactor * pane.screenResolutionX / 2.54);
        }

        gc.setStroke(Color.rgb(color.r, color.g, color.b));
        gc.setLineWidth(strokeWidth * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.strokeRect(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
        gc.setLineDashes(0);
    }

    @Override
    public void strokeDiamond(double x1, double y1, double x2, double y2, double strokeWidth, GralogColor color) {

        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setStroke(Color.rgb(color.r, color.g, color.b));
        gc.setLineWidth(strokeWidth * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.strokePolygon(new double[] {p1.getX(), (p2.getX() + p1.getX())/2, p2.getX(), (p2.getX() + p1.getX())/2},
                        new double[] {(p2.getY() + p1.getY())/2, p1.getY(), (p2.getY() + p1.getY())/2, p2.getY()},
                4);
    }

    @Override
    public void fillDiamond(double x1, double y1, double x2, double y2, GralogColor color) {

        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setFill(Color.rgb(color.r, color.g, color.b));
        gc.fillPolygon(new double[] {p1.getX(), (p2.getX() + p1.getX())/2, p2.getX(), (p2.getX() + p1.getX())/2},
                new double[] {(p2.getY() + p1.getY())/2, p1.getY(), (p2.getY() + p1.getY())/2, p2.getY()},
                4);
    }

    @Override
    public void strokeOval(double x, double y, double width, double height, double strokeWidth,
                           GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(x - width/2, y - height/2));
        Point2D p2 = pane.modelToScreen(new Point2D(x + width/2, y + height/2));

        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(strokeWidth * pane.zoomFactor * pane.screenResolutionX / 2.54);
        gc.strokeOval(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());

    }

    @Override
    public void fillOval(double x, double y, double width, double height,
                           GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(x - width/2, y - height/2));
        Point2D p2 = pane.modelToScreen(new Point2D(x + width/2, y + height/2));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(1);
        gc.fillOval(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());

    }

    @Override
    public void putText(double centerx, double centery, String text,
        double lineHeightCM, GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(centerx, centery));


        // I have no idea, why this is 1959.5... I hate magic numbers
        double newSize = 2.54d * lineHeightCM * pane.zoomFactor * 1959.5
            / (pane.screenResolutionY);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, newSize));

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.rgb(c.r, c.g, c.b));

        gc.fillText(text, p1.getX(), p1.getY());
    }

    @Override
    public void fillRectangle(double x1, double y1, double x2, double y2,
        GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setFill(Color.rgb(c.r, c.g, c.b));

        gc.fillRect(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }

    @Override
    public void selectionRectangle(Point2D from, Point2D to, Color color) {

        Point2D topLeft = new Point2D(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()));
        double width = Math.abs(from.getX() - to.getX());
        double height = Math.abs(from.getY() - to.getY());

        gc.setFill(color);
        gc.fillRect(topLeft.getX(), topLeft.getY(), width, height);
        //stroke outline
        gc.setStroke(Color.BLACK);
        gc.strokeRect(topLeft.getX(), topLeft.getY(), width, height);
    }
}
