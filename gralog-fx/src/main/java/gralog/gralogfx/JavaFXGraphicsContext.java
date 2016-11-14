/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx;

import gralog.rendering.*;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
        double width) {
        Point2D p1 = pane.modelToScreen(new Point2D(x1, y1));
        Point2D p2 = pane.modelToScreen(new Point2D(x2, y2));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(width * pane.zoomFactor * pane.screenResolutionX / 2.54);

        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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
    public void putText(double centerx, double centery, String text,
        double lineHeightCM, GralogColor c) {
        Point2D p1 = pane.modelToScreen(new Point2D(centerx, centery));

        Font font = gc.getFont();
        // I have no idea, why this is 1959.5... I hate magic numbers
        double newSize = 2.54d * lineHeightCM * pane.zoomFactor * 1959.5
            / (pane.screenResolutionY);
        gc.setFont(new Font(font.getName(), newSize));

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

}
