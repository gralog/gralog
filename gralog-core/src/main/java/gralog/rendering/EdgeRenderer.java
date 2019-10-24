/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

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
public final class EdgeRenderer {

    private EdgeRenderer() { }

    /**
     * Draws a bezier curve for the given Edge by using its control points
     *
     * @param e          Has all the necessary information to render the curve
     * @param gc         The graphics context used to draw the curve
     * @param isSelected The color of the curve
     */

    public static void drawBezierEdge(Edge e, GralogGraphicsContext gc, boolean isSelected, String label) {
        List<ControlPoint> controlPoints = e.controlPoints;

        if (controlPoints.isEmpty() || controlPoints.size() > 2) {
            drawStraightEdge(e, gc, isSelected, label);
            return;
        }

        GralogGraphicsContext.Bezier curve = new GralogGraphicsContext.Bezier();
        if (controlPoints.size() == 1) {
            curve.ctrl1 = controlPoints.get(0).getPosition();
            curve.ctrl2 = controlPoints.get(0).getPosition();
        } else if (controlPoints.size() == 2) {
            curve.ctrl1 = controlPoints.get(0).getPosition();
            curve.ctrl2 = controlPoints.get(1).getPosition();
        }

        //correction so that the arrow and line don't overlap at the end
        //corrections are always negative if the arrow model tip is at the origin
        double corr = e.arrowType.endPoint * e.arrowHeadLength;
        Vector2D sourceToCtrl1 = curve.ctrl1.minus(e.getSource().getCoordinates()).normalized();
        Vector2D targetToCtrl2 = curve.ctrl2.minus(e.getTarget().getCoordinates()).normalized();


        curve.source = e.getSource().shape.getEdgePoint(sourceToCtrl1.measureAngleX(), e.getSource().getCoordinates());
        curve.target = e.getTarget().shape.getEdgePoint(targetToCtrl2.measureAngleX(), e.getTarget().getCoordinates());

        //move away from the center of the target by the specified endPointDistance
        curve.target = curve.target.plus(curve.target.minus(e.getTarget().getCoordinates()).multiply(e.thickness)); //endPointDistance));
        curve.source = curve.source.plus(curve.source.minus(e.getSource().getCoordinates()).multiply(e.thickness)); //startPointDistance));

        corr = e.isDirected ? corr : 0;

        //curve.target = curve.target.minus(targetToCtrl2.multiply(corr)); //correction for the arrow

        if (e.isDirected) {
            if (controlPoints.size() == 1) {
                if (isSelected) {
                    gc.drawQuadratic(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                    gc.arrow(targetToCtrl2.multiply(-1), curve.target,
                            e.arrowType, e.arrowHeadLength, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
                }
                gc.drawQuadratic(curve, e.color, e.thickness, e.type);
                gc.arrow(targetToCtrl2.multiply(-1), curve.target, e.arrowType, e.arrowHeadLength, e.color, e.thickness + Edge.edgeSelectionOffset);
            } else {
                if (isSelected) {
                    gc.drawBezier(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                    gc.arrow(targetToCtrl2.multiply(-1), curve.target,
                            e.arrowType, e.arrowHeadLength, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
                }
                gc.drawBezier(curve, e.color, e.thickness, e.type);
                gc.arrow(targetToCtrl2.multiply(-1), curve.target, e.arrowType, e.arrowHeadLength, e.color, e.thickness + Edge.edgeSelectionOffset);
            }
        } else {
            if (controlPoints.size() == 1) {
                if (isSelected) {
                    gc.drawQuadratic(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                }
                gc.drawQuadratic(curve, e.color, e.thickness, e.type);
            } else {
                if (isSelected) {
                    gc.drawBezier(curve, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                }
                gc.drawBezier(curve, e.color, e.thickness, e.type);
            }

        }
        
    }

    /**
     * Draws a sharp piecewise-linear Edge by using its control points
     *
     * @param e  Has all the necessary information to render the curve
     * @param gc The graphics context used to draw the curve
     */
    public static void drawSharpEdge(Edge e, GralogGraphicsContext gc, boolean isSelected, String label) {
        List<ControlPoint> ctrl = e.controlPoints;

        if (ctrl.isEmpty()) {
            drawStraightEdge(e, gc, isSelected, label);
            return;
        }

        if (isSelected)
            gc.line(e.getSource().getCoordinates(), ctrl.get(0).getPosition(), GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
        gc.line(e.getSource().getCoordinates(), ctrl.get(0).getPosition(), e.color, e.thickness, e.type);

        for (int i = 1; i < ctrl.size(); i++) {
            if (isSelected) {
                gc.line(ctrl.get(i - 1).getPosition(), ctrl.get(i).getPosition(),
                        GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            }
            gc.line(ctrl.get(i - 1).getPosition(), ctrl.get(i).getPosition(), e.color, e.thickness, e.type);
        }
        if (isSelected) {
            gc.line(ctrl.get(ctrl.size() - 1).getPosition(), e.getTarget().getCoordinates(),
                    GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
        }
        if (e.isDirected()) {
        	double corr = e.arrowType.endPoint * e.arrowHeadLength;
        	Vector2D intersection = e.getTarget().shape.getIntersection(ctrl.get(ctrl.size() -1).getPosition(), e.getTarget().getCoordinates(), e.getTarget().getCoordinates().minus(e.thickness*2,e.thickness*2));//.minus(e.thickness*0.7,e.thickness*0.7) ;
        	//intersection = intersection.minus(diff.normalized().multiply(e.endPointDistance)); // option to put little distance btween arrow and vertex
        	if (isSelected) {
        		gc.line(ctrl.get(ctrl.size() - 1).getPosition(), intersection, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                gc.arrow(e.getTarget().getCoordinates().minus(ctrl.get(ctrl.size() -1).getPosition()), intersection, e.arrowType, e.arrowHeadLength, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
            }

            gc.line(ctrl.get(ctrl.size() - 1).getPosition(), intersection, e.color, e.thickness, e.type);
            gc.arrow(e.getTarget().getCoordinates().minus(ctrl.get(ctrl.size() -1).getPosition()), intersection, e.arrowType, e.arrowHeadLength, e.color, e.thickness + Edge.edgeSelectionOffset);
        } else {
        	Vector2D intersection = e.getTarget().shape.getIntersection(ctrl.get(ctrl.size() -1).getPosition(), e.getTarget().getCoordinates(), e.getTarget().getCoordinates()).minus(e.thickness*2,e.thickness*2) ;
        	gc.line(ctrl.get(ctrl.size() - 1).getPosition(), intersection, e.color, e.thickness, e.type);
        }
        
        if (ctrl.size()%2==0) { //even number of ctrl points - uneven number of edge parts
        	Vector2D start = ctrl.get(ctrl.size()/2-1).getPosition();
        	gc.putText(start.plus(e.thickness*10,e.thickness*10), label, e.thickness*10, GralogColor.BLACK);
        } else {
        	Vector2D start = ctrl.get((ctrl.size()-1)/2).getPosition();
        	gc.putText(start.plus(e.thickness*10,e.thickness*10), label, e.thickness*10, GralogColor.BLACK);
        }
    }

    private static void drawStraightEdge(Edge e, GralogGraphicsContext gc, boolean isSelected, String label) {
        double offset = e.getOffset();	// = 0 iff no multiegdes

        Vector2D diff = e.getTarget().getCoordinates().minus(e.getSource().getCoordinates());
        Vector2D perpendicularToEdge = diff.orthogonal(1).normalized().multiply(offset); // = 0 iff no multiedges

        Vector2D sourceOffset = e.getSource().getCoordinates().plus(perpendicularToEdge);
        Vector2D targetOffset = e.getTarget().getCoordinates().plus(perpendicularToEdge);

        Vector2D intersectionTarget = e.getTarget().shape.getIntersection(sourceOffset, targetOffset, e.getTarget().getCoordinates());
        Vector2D intersectionSource = e.getSource().shape.getIntersection(targetOffset, sourceOffset, e.getSource().getCoordinates());
        
        if (Double.isNaN(intersectionTarget.getX()) || Double.isNaN(intersectionTarget.getY())) {
            intersectionTarget = targetOffset.minus(diff.normalized().multiply(e.getTarget().shape.sizeBox.width / 2));
        }
        //intersection = intersection.minus(diff.normalized().multiply(e.endPointDistance)); // option to put little distance btween arrow and vertex
        Vector2D adjust = intersectionTarget.minus(diff.normalized().multiply(e.thickness));
        if (e.isDirected) {
            if (isSelected) {
                gc.line(intersectionSource, adjust, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
                gc.arrow(diff, adjust, e.arrowType, e.arrowHeadLength+e.thickness, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset);
            }

            gc.line(intersectionSource, adjust, e.color, e.thickness, e.type);
            gc.arrow(diff, adjust, e.arrowType, e.arrowHeadLength+e.thickness, e.color, e.thickness);
        } else {
            if (isSelected)
                gc.line(sourceOffset, adjust, GralogColor.RED, e.thickness + Edge.edgeSelectionOffset, e.type);
            gc.line(sourceOffset, adjust, e.color, e.thickness, e.type);
        }
        Vector2D center = sourceOffset.plus(intersectionTarget.minus(sourceOffset).multiply(0.5)).plus(e.thickness*10,e.thickness*10);
        gc.putText(center, label, e.thickness*10, GralogColor.BLACK);
    }
}
