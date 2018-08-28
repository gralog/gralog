package gralog.rendering;

import static gralog.rendering.GralogGraphicsContext.LineType;

import gralog.structure.Edge;
import gralog.structure.controlpoints.ControlPoint;

import java.util.List;


/**
 * This class is responsible for drawing Edges
 *
 * Can be used to draw different kinds of edges
 *
 * EdgeRenderer uses GralogGraphicsContext to draw the curve.
 */
public class EdgeRenderer {

    /**
     * Draws a bezier curve for the given Edge by using its control points
     *
     * @param e Has all the necessary information to render the curve
     * @param gc The graphics context used to draw the curve
     * @param c The color of the curve
     */
    public static void drawBezierEdge(Edge e, GralogGraphicsContext gc, GralogColor c){
        List<ControlPoint> controlPoints = e.controlPoints;

        if(controlPoints.isEmpty() || controlPoints.size() > 2){
            drawStraightEdge(e, gc, c);
            return;
        }

        GralogGraphicsContext.Bezier curve = new GralogGraphicsContext.Bezier();
        if(controlPoints.size() == 1){
            curve.ctrl1 = controlPoints.get(0).getPosition();
            curve.ctrl2 = controlPoints.get(0).getPosition();
        }else if(controlPoints.size() == 2){
            curve.ctrl1 = controlPoints.get(0).getPosition();
            curve.ctrl2 = controlPoints.get(1).getPosition();
        }

        //correction so that the arrow and line don't overlap at the end
        //corrections are always negative if the arrow model tip is at the origin
        double corr = e.arrowType.endPoint * e.arrowHeadLength;
        Vector2D sourceToCtrl1 = curve.ctrl1.minus(e.getSource().coordinates).normalized();
        Vector2D targetToCtrl2 = curve.ctrl2.minus(e.getTarget().coordinates).normalized();


        curve.source = e.getSource().shape.getEdgePoint(sourceToCtrl1.measureAngleX(), e.getSource().coordinates);
        curve.target = e.getTarget().shape.getEdgePoint(targetToCtrl2.measureAngleX(), e.getTarget().coordinates);

        //move away from the center of the target by the specified endPointDistance
        curve.target = curve.target.plus(curve.target.minus(e.getTarget().coordinates).multiply(e.endPointDistance));
        curve.source = curve.source.plus(curve.source.minus(e.getSource().coordinates).multiply(e.startPointDistance));

        if(e.isDirected){
            gc.arrow(targetToCtrl2.multiply(-1), curve.target, e.arrowType, e.arrowHeadLength, c);
        }else{
            corr = 0;
        }

        curve.target = curve.target.minus(targetToCtrl2.multiply(corr)); //correction for the arrow

        if(controlPoints.size() == 1){
            gc.drawQuadratic(curve, c, e.thickness, e.type);
        }else{
            gc.drawBezier(curve, c, e.thickness, e.type);
        }
    }

    /**
     * Draws a sharp piecewise-linear Edge by using its control points
     *
     * @param e Has all the necessary information to render the curve
     * @param gc The graphics context used to draw the curve
     * @param c The color of the curve
     */
    public static void drawSharpEdge(Edge e, GralogGraphicsContext gc, GralogColor c){
        List<ControlPoint> ctrl = e.controlPoints;

        if(ctrl.isEmpty()){
            drawStraightEdge(e, gc, c);
            return;
        }

        gc.line(e.getSource().coordinates, ctrl.get(0).getPosition(), c, e.thickness, e.type);

        for(int i = 1; i < ctrl.size(); i++){
            gc.line(ctrl.get(i-1).getPosition(), ctrl.get(i).getPosition(), c, e.thickness, e.type);
        }

        gc.line(ctrl.get(ctrl.size() - 1).getPosition(), e.getTarget().coordinates, c, e.thickness, e.type);
    }

    private static void drawStraightEdge(Edge e, GralogGraphicsContext gc, GralogColor c){
        double offset = e.getOffset();

        Vector2D diff = e.getTarget().coordinates.minus(e.getSource().coordinates);
        Vector2D perpendicularToEdge = diff.orthogonal(1).normalized().multiply(offset);

        Vector2D sourceOffset = e.getSource().coordinates.plus(perpendicularToEdge);
        Vector2D targetOffset = e.getTarget().coordinates.plus(perpendicularToEdge);

        Vector2D intersection = e.getTarget().shape.getIntersection(sourceOffset, targetOffset, e.getTarget().coordinates);
        intersection = intersection.minus(diff.normalized().multiply(e.endPointDistance / 2)); //no idea why I divide
        if(e.isDirected){
            Vector2D adjust = intersection.plus(diff.normalized().multiply(e.arrowType.endPoint * e.arrowHeadLength));
            gc.line(sourceOffset, adjust, c, e.thickness, e.type);
            gc.arrow(diff, intersection, e.arrowType, e.arrowHeadLength, c);
        }else{
            gc.line(sourceOffset, intersection, c, e.thickness, e.type);
        }
    }
}
