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
     * @param isSelected The color of the curve
     */
    public static void drawBezierEdge(Edge e, GralogGraphicsContext gc, boolean isSelected){
        List<ControlPoint> controlPoints = e.controlPoints;

        if(controlPoints.isEmpty() || controlPoints.size() > 2){
            drawStraightEdge(e, gc, isSelected);
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

        corr = e.isDirected ? corr : 0;

        curve.target = curve.target.minus(targetToCtrl2.multiply(corr)); //correction for the arrow

        if(controlPoints.size() == 1){
            if(isSelected){
                gc.drawQuadratic(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            }
            gc.drawQuadratic(curve, e.color, e.thickness, e.type);
        }else{
            if(isSelected){
                gc.drawBezier(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            }
            gc.drawBezier(curve, e.color, e.thickness, e.type);
        }

        if(e.isDirected){
            if(isSelected){
                gc.arrow(targetToCtrl2.multiply(-1), curve.target,
                        e.arrowType, e.arrowHeadLength, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
            }
            gc.arrow(targetToCtrl2.multiply(-1), curve.target, e.arrowType, e.arrowHeadLength, e.color);
        }
    }

    /**
     * Draws a sharp piecewise-linear Edge by using its control points
     *
     * @param e Has all the necessary information to render the curve
     * @param gc The graphics context used to draw the curve
     */
    public static void drawSharpEdge(Edge e, GralogGraphicsContext gc, boolean isSelected){
        List<ControlPoint> ctrl = e.controlPoints;

        if(ctrl.isEmpty()){
            drawStraightEdge(e, gc, isSelected);
            return;
        }

        if(isSelected)
            gc.line(e.getSource().coordinates, ctrl.get(0).getPosition(), GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
        gc.line(e.getSource().coordinates, ctrl.get(0).getPosition(), e.color, e.thickness, e.type);

        for(int i = 1; i < ctrl.size(); i++){
            if(isSelected){
                gc.line(ctrl.get(i-1).getPosition(), ctrl.get(i).getPosition(),
                        GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            }
            gc.line(ctrl.get(i-1).getPosition(), ctrl.get(i).getPosition(), e.color, e.thickness, e.type);
        }
        if(isSelected){
            gc.line(ctrl.get(ctrl.size() - 1).getPosition(), e.getTarget().coordinates,
                    GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
        }

        gc.line(ctrl.get(ctrl.size() - 1).getPosition(), e.getTarget().coordinates, e.color, e.thickness, e.type);
    }

    private static void drawStraightEdge(Edge e, GralogGraphicsContext gc, boolean isSelected){
        double offset = e.getOffset();

        Vector2D diff = e.getTarget().coordinates.minus(e.getSource().coordinates);
        Vector2D perpendicularToEdge = diff.orthogonal(1).normalized().multiply(offset);

        Vector2D sourceOffset = e.getSource().coordinates.plus(perpendicularToEdge);
        Vector2D targetOffset = e.getTarget().coordinates.plus(perpendicularToEdge);

        Vector2D intersection = e.getTarget().shape.getIntersection(sourceOffset, targetOffset, e.getTarget().coordinates);
        intersection = intersection.minus(diff.normalized().multiply(e.endPointDistance)); //no idea why I divide
        if(e.isDirected){
            Vector2D adjust = intersection.plus(diff.normalized().multiply(e.arrowType.endPoint * e.arrowHeadLength));
            if(isSelected){
                gc.line(sourceOffset, adjust, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                gc.arrow(diff, intersection, e.arrowType, e.arrowHeadLength, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
            }

            gc.line(sourceOffset, adjust, e.color, e.thickness, e.type);
            gc.arrow(diff, intersection, e.arrowType, e.arrowHeadLength, e.color);
        }else{
            if(isSelected)
                gc.line(sourceOffset, intersection, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            gc.line(sourceOffset, intersection, e.color, e.thickness, e.type);
        }
    }
}
