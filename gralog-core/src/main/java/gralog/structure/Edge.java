/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.annotations.DataField;
import gralog.events.EdgeEvent;
import gralog.events.EdgeListener;
import gralog.math.BezierUtilities;
import gralog.plugins.PluginManager;
import gralog.plugins.XmlMarshallable;
import gralog.plugins.XmlName;
import gralog.preferences.Configuration;


import gralog.rendering.Arrow;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.EdgeRenderer;
import gralog.rendering.Vector2D;
import gralog.structure.controlpoints.ControlPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@XmlName(name = "edge")
public class Edge extends XmlMarshallable implements IMovable, Serializable {

    public enum EdgeType {
        SHARP,
        ROUND, // TODO: choosing this in object inspector makes the edge invisible
        BEZIER
    }
    public static double edgeSelectionOffset = 0.02;
    public static double multiEdgeOffset = 0.2;
    //inspector visible
    @DataField(display = true)
    public String label = ""; //add this
    @DataField(display = true)
    public Double weight = 1.0d;
    @DataField(display = true)
    public Boolean isDirected = true;
    public Arrow arrowType = Arrow.TYPE2;

    public Double endPointDistance = 0d;   //how much distance is between endpoint and target
    public Double startPointDistance = 0d; //how much distance is between start point and source

    public double arrowHeadLength = 0.2d; // cm

    public double arrowHeadAngle = 40d; // degrees
    // @InspectorName(name = "thickness")
    @DataField(display = true)
    public Double thickness = 2.54 / 96; // cm
    @DataField(display = true)
    public GralogColor color = GralogColor.BLACK;
    @DataField(display = true)
    public GralogGraphicsContext.LineType type = GralogGraphicsContext.LineType.PLAIN;
    @DataField(display = true)
    public EdgeType edgeType = EdgeType.BEZIER; //TODO: switch to private and use annotations to mark insp vars
    public ArrayList<Edge> siblings = new ArrayList<>();
    public ArrayList<EdgeIntermediatePoint> intermediatePoints = new ArrayList<>();
    public ArrayList<ControlPoint> controlPoints = new ArrayList<>();
    @DataField(display = true, readOnly = true)
    Integer id = -1; //if not -1, then don't change the id
    Set<EdgeListener> listeners = new HashSet<>();
    private Vertex source = null;
    private Vertex target = null;
    public Edge() {

    }

    public Edge(Configuration config) {
        this();
        if (config != null) {
            initWithConfig(config);
        }
    }

    public static boolean isEdgeType(String type) {

        for (EdgeType et : EdgeType.values())
            if (et.name().equalsIgnoreCase(type))
                return true;
        return false;
    }

    protected void initWithConfig(Configuration config) {
        color = config.getValue("Edge_color", GralogColor::parseColor, GralogColor.BLACK);
        endPointDistance = config.getValue("Edge_endPointDistance", Double::parseDouble, 0.0);
        startPointDistance = config.getValue("Edge_startPointDistance", Double::parseDouble, 0.0);
        thickness = config.getValue("Edge_thickness", Double::parseDouble, 0.025);
    }



    public void setEdgeType(EdgeType e) {
        if (e == EdgeType.BEZIER && controlPoints.size() > 2) {

            Vector2D ctrl1 = Vector2D.zero(),
                    ctrl2 = Vector2D.zero();

            int offset = (controlPoints.size() + 1) % 2; //0 when uneven
            int middle = (controlPoints.size() - 1 - offset) / 2;

            for (int i = 0; i <= middle; i++) {
                ctrl1 = ctrl1.plus(controlPoints.get(i).getPosition());
            }
            for (int i = middle + offset; i < controlPoints.size(); i++) {
                ctrl2 = ctrl2.plus(controlPoints.get(i).getPosition());
            }
            ctrl1 = ctrl1.multiply(1d / (middle + 1));
            ctrl2 = ctrl2.multiply(1d / (middle + 1));
            ControlPoint c1 = new ControlPoint(ctrl1, this);
            ControlPoint c2 = new ControlPoint(ctrl2, this);
            controlPoints.clear();
            controlPoints.add(c1);
            controlPoints.add(c2);
        }

        this.edgeType = e;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }
    public int getControlPointCount() {
        return controlPoints.size();
    }

    /**
     * The clickPosition determines where the edge was initially clicked to
     * add the control point. Depending on clickPosition, the correct edge segment
     * for adding the control point can be determined
     */
    public ControlPoint addControlPoint(Vector2D position, Vector2D clickPosition) {
        if (edgeType == EdgeType.BEZIER) {
            return addBezierControlPoint(position);
        } else {
            return addSharpControlPoint(position, clickPosition);
        }
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private ControlPoint addBezierControlPoint(Vector2D position) {
        if (controlPoints.size() >= 2) {
            return null;
        }

        ControlPoint c = new ControlPoint(position, this);

        if (controlPoints.size() == 1) {
            double c1Dist = c.getPosition().minus(target.coordinates).length();
            double c2Dist = controlPoints.get(0).getPosition().minus(target.coordinates).length();
            //distance is not the correct metric
            //TODO: project on edge and use x order
            controlPoints.add(c1Dist > c2Dist ? 0 : 1, c);
        } else if (controlPoints.isEmpty()) {
            controlPoints.add(c);
        } else { //can't add more than 2 bezier control points
            return null;
        }
        return c;
    }

    private ControlPoint addSharpControlPoint(Vector2D position, Vector2D clickPosition) {
        ControlPoint c = new ControlPoint(position, this);
        int idx = containsCoordinateSharp(clickPosition.getX(), clickPosition.getY());
        if (idx >= controlPoints.size()) {
            controlPoints.add(c);
        } else if (idx == 0) {
            controlPoints.add(0, c);
        } else {
            controlPoints.add(idx, c);
        }
        return c;
    }

    public ControlPoint removeControlPoint(ControlPoint c) {
        if (controlPoints.size() > 1) {
            controlPoints.remove(c);
            return controlPoints.get(0);
        } else {
            controlPoints.remove(0);
            return null;
        }

    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        if (this.source != null)
            this.source.disconnectEdge(this);

        this.source = source;
        if (source != null) {
            this.source.connectEdge(this);
        }
    }

    public boolean isDirected() {
        return this.isDirected;
    }

    public void setDirectedness(boolean directedness) {
        this.isDirected = directedness;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        if (this.target != null)
            this.target.disconnectEdge(this);
        this.target = target;
        if (target != null) {
            this.target.connectEdge(this);
        }
    }

    public boolean isLoop() {
        return getSource() == getTarget();
    }

    public boolean isSiblingTo(Edge other) { // TODO: rename to isAdjacent? Check if directed?
        return getTarget() == other.getTarget() || getTarget() == other.getSource();
    }

    public double maximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        return result;
    }

    @Override
    public void move(Vector2D offset) {
    	for (ControlPoint c : controlPoints)
    		c.move(offset);
    }

    public void collapse(Structure structure) {
        //One edge that doesn't have the same direction as this edge
        Edge e = null;
        for (int i = 0; i < siblings.size(); i++) {
            e = siblings.get(i);
            if(e != this && !e.sameOrientationAs(this)){
                break;
            }
            e = null;
        }
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i) != this && siblings.get(i) != e) {
                structure.removeEdge(siblings.get(i), false);
            }
        }
        siblings.clear();
        siblings = new ArrayList<>();
        siblings.add(this);

        //correct siblings of edge e as well
        if (e != null) {
            siblings.add(e);

            e.siblings.clear();
            e.siblings = new ArrayList<>();
            e.siblings.add(this);
            e.siblings.add(e);
        }
    }

    public void snapToGrid(double gridSize) {

    }

    public IMovable findObject(double x, double y) {
        for (ControlPoint c : controlPoints) {
            if (c.active && c.containsCoordinate(x, y)) {
                return c;
            }
        }

        if (this.containsCoordinate(x, y)) {
            return this;
        }

        return null;
    }

    private void renderLoop(GralogGraphicsContext gc, Highlights highlights) {
        GralogColor edgeColor = highlights.isSelected(this) ? GralogColor.RED : this.color;


        double angleStart = source.loopAnchor - source.loopAngle;
        double angleEnd = source.loopAnchor + source.loopAngle;

        double r = source.radius;

        Vector2D intersection = source.shape.getEdgePoint(angleStart, source.coordinates);
        Vector2D intersection2 = source.shape.getEdgePoint(angleEnd, source.coordinates);

        Vector2D tangentToIntersection = Vector2D.getVectorAtAngle(angleEnd, 1).multiply(-1);

        //the correction retreats the endpoint of the bezier curve orthogonally from the vertex surface
        double correction = arrowType.endPoint * arrowHeadLength;

        //only draw arrow for directed graphs
        if (isDirected) {
            gc.arrow(tangentToIntersection, intersection2, arrowType, arrowHeadLength, edgeColor);
        } else {
            correction = 0;
        }

        //Loop description, endpoints and tangents.
        GralogGraphicsContext.Loop l = new GralogGraphicsContext.Loop();
        l.start = intersection;
        l.end = intersection2;
        l.tangentStart = Vector2D.getVectorAtAngle(angleStart, 1).orthogonal();
        l.tangentEnd = Vector2D.getVectorAtAngle(angleEnd, 1).orthogonal();

        gc.loop(l, 1.5, correction, edgeColor, thickness, type);

    }

    public void render(GralogGraphicsContext gc, Highlights highlights) {

        if (isLoop()) {
            renderLoop(gc, highlights);
            return;
        }

        if (edgeType == EdgeType.BEZIER) {
            EdgeRenderer.drawBezierEdge(this, gc, highlights.isSelected(this));
        } else if (edgeType == EdgeType.SHARP) {
            EdgeRenderer.drawSharpEdge(this, gc, highlights.isSelected(this));
        }

    }

    public double getOffset() {
        double offset = 0;
        int index = siblings.indexOf(this);
        //offset both edges orthogonally, offsets differently when both face same direction
        if (siblings.size() == 2) {
            offset = 0.5 * multiEdgeOffset;
            if(index == 1){
                if(siblings.get(0).sameOrientationAs(this)){
                    offset *= -1;
                }
            }

        }
        if (siblings.size() == 3) {
            if (index == 1) {
                offset = 0;
            } else if (index == 0) {
                offset = multiEdgeOffset;
            }else if(index == 2){
                offset = (siblings.get(0).sameOrientationAs(this) ? -1 : 1) * multiEdgeOffset;
            }
        }
        if (siblings.size() == 4) {
            int sameOrientationCount = 0;
            double offsetMultiplier;
            for (int i = 0; i < siblings.size(); i++) {
                if (i == index) {
                    int correctedOffsetCounter = (sameOrientationCount >= 2 ? -(i - 1) : (sameOrientationCount + 1));
                    if (Math.abs(correctedOffsetCounter) > 1) {
                        offsetMultiplier = 0.75;
                    } else {
                        offsetMultiplier = 0.5;
                    }
                    offset = offsetMultiplier * correctedOffsetCounter * multiEdgeOffset;
                    break;
                }
                if(siblings.get(i).sameOrientationAs(this)){
                    sameOrientationCount++;
                }

            }
        }
        return offset;
    }

    public boolean sameOrientationAs(Edge other) {
        return getSource() == other.getSource();
    }

    public boolean containsCoordinate(double x, double y) {

        if (controlPoints.size() == 0) {
            return containsCoordinateFlat(x, y) == 0;
        }
        if (edgeType == EdgeType.BEZIER) {
            return containsCoordinateBezier(x, y);
        } else if (edgeType == EdgeType.SHARP) {
            return containsCoordinateSharp(x, y) >= 0;
        } else { //edgeType == EdgeType.ROUND
            return containsCoordinateRound(x, y);
        }
    }

    /**
     * Checks for contains() assuming there are no control points
     *
     * @return 0 if it contains (x,  y), -1 otherwise
     */
    private int containsCoordinateFlat(double x, double y) {
        Vector2D diff = target.coordinates.minus(source.coordinates);
        Vector2D perpendicularToDiff = diff.orthogonal(1).normalized().multiply(getOffset());

        Vector2D sourceOffset = source.coordinates.plus(perpendicularToDiff);
        Vector2D targetOffset = target.coordinates.plus(perpendicularToDiff);

        double fromX = sourceOffset.getX();
        double fromY = sourceOffset.getY();

        double toX = targetOffset.getX();
        double toY = targetOffset.getY();
        if (Vector2D.distancePointToLine(x, y, fromX, fromY, toX, toY) < multiEdgeOffset * 0.5) {
            return 0;
        } else {
            return -1;
        }

    }

    private boolean containsCoordinateRound(double x, double y) {
        return false;
    }

    /**
     * Checks if a given vector (x, y) is within a close margin of the
     * sharp edge (the form of the edge is given by its control points)
     *
     * @param x x coordinate of the position to check
     * @param y y coordinate of the position to check
     * @return -1 if edge does not contain position, i>=0 otherwise (where [i-1, i] are the indices
     * of the control points that have been hit). If i==0, then [i-1] is the source vertex
     */
    private int containsCoordinateSharp(double x, double y) {
        if (controlPoints.size() == 0) {
            return containsCoordinateFlat(x, y);
        }
        double dist = Vector2D.distancePointToLine(x, y, source.coordinates, controlPoints.get(0).getPosition());

        if (dist < multiEdgeOffset * 0.5) {
            return 0;
        }
        for (int i = 1; i < controlPoints.size(); i++) {
            Vector2D a = controlPoints.get(i - 1).getPosition();
            Vector2D b = controlPoints.get(i).getPosition();
            if (Vector2D.distancePointToLine(x, y, a, b) < multiEdgeOffset * 0.5) {
                return i;
            }
        }
        Vector2D last = controlPoints.get(controlPoints.size() - 1).getPosition();
        dist = Vector2D.distancePointToLine(x, y, last, target.coordinates);

        return dist < multiEdgeOffset * 0.5 ? controlPoints.size() : -1;
    }

    private boolean containsCoordinateBezier(double x, double y) {
        Vector2D m = new Vector2D(x, y);

        Vector2D ctrl1 = controlPoints.get(0).getPosition();
        Vector2D ctrl2 = controlPoints.size() < 2 ? ctrl1 : controlPoints.get(1).getPosition();

        //correction so that the arrow and line don't overlap at the end
        //corrections are always negative if the arrow model tip is at the origin
        double corr = arrowType.endPoint * arrowHeadLength;

        Vector2D sourceToCtrl1 = ctrl1.minus(source.coordinates).normalized();
        Vector2D targetToCtrl2 = ctrl2.minus(target.coordinates).normalized();

        if (!isDirected) {
            corr = 0;
        }
        //TODO: replace with shape.getEdgePoint
        Vector2D source = getStartingPointSource();
        Vector2D target = getStartingPointTarget();
        BezierUtilities.ProjectionResults projection;

        if (controlPoints.size() == 1) {
            projection = BezierUtilities.pointProjectionQuadraticAlgebraic(m, source, ctrl1, target);
        } else if (controlPoints.size() == 2) {
            projection = BezierUtilities.pointProjectionCubicAlgebraic(m, source, ctrl1, ctrl2, target);
        } else {
            return false;
        }

        if (projection.successful) {
            return projection.result.minus(m).length() < multiEdgeOffset * 0.5;
        } else {
            return false;
        }
    }

    public Vector2D getStartingPointSource() {
        if (controlPoints.size() == 0) {
            return source.coordinates;
        }

        if (edgeType == EdgeType.BEZIER) {

            Vector2D ctrl1 = controlPoints.get(0).getPosition();
            Vector2D sourceToCtrl1 = ctrl1.minus(source.coordinates).normalized();

            return source.shape.getEdgePoint(sourceToCtrl1.theta(), source.coordinates);
        } else if (edgeType == EdgeType.SHARP) {
            return source.coordinates;
        } else if (edgeType == EdgeType.ROUND) {
            return source.coordinates;
        } else {
            return source.coordinates;
        }
    }

    public Vector2D getStartingPointTarget() {
        if (controlPoints.size() == 0) {
            return target.coordinates;
        }
        if (edgeType == EdgeType.BEZIER) {

            Vector2D ctrl2 = controlPoints.get(controlPoints.size() - 1).getPosition();
            Vector2D targetToCtrl1 = ctrl2.minus(target.coordinates).normalized();
            double corr = arrowType.endPoint * arrowHeadLength;
            if (isDirected) {
                corr = 0;
            }
            var x = target.shape.getEdgePoint(targetToCtrl1.theta(), target.coordinates);
            x = x.plus(targetToCtrl1.multiply(corr));
            return x;
        } else if (edgeType == EdgeType.SHARP) {
            return target.coordinates;
        } else if (edgeType == EdgeType.ROUND) {
            return target.coordinates;
        } else {
            return target.coordinates;
        }
    }

    public boolean containsVertex(Vertex v) {
        return source == v || target == v;
    }


    public double length() {
        Vector2D from = this.source.coordinates;
        Vector2D to = this.target.coordinates;
        //TODO: implement length for control points
        double result = 0.0;
        return result + to.minus(from).length();
    }

    public Element toXml(Document doc, HashMap<Vertex, String> ids) throws Exception {
        Element enode = super.toXml(doc);
        enode.setAttribute("source", ids.get(source));
        enode.setAttribute("target", ids.get(target));
        enode.setAttribute("isdirected", isDirected ? "true" : "false");
        enode.setAttribute("label", label);
        enode.setAttribute("weight", Double.toString(weight));
        enode.setAttribute("thickness", Double.toString(thickness));
        enode.setAttribute("arrowheadlength", Double.toString(arrowHeadLength));
        enode.setAttribute("color", color.toHtmlString());

        for (EdgeIntermediatePoint p : intermediatePoints) {
            Element e = p.toXml(doc);
            if (e != null)
                enode.appendChild(e);
        }

        for (var p : controlPoints){
            Element e = p.toXml(doc);
            if (e != null)
                enode.appendChild(e);
        }

        return enode;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void fromXml(Element enode, HashMap<String, Vertex> ids) throws Exception {
        setSource(ids.get(enode.getAttribute("source")));
        setTarget(ids.get(enode.getAttribute("target")));

        if (enode.hasAttribute("isdirected"))
            isDirected = enode.getAttribute("isdirected").equals("true");
        label = enode.getAttribute("label");
        if (enode.hasAttribute("weight"))
            weight = Double.parseDouble(enode.getAttribute("weight"));

        if (enode.hasAttribute("thickness"))
            thickness = Double.parseDouble(enode.getAttribute("thickness"));

        if (enode.hasAttribute("arrowheadlength"))
            arrowHeadLength = Double.parseDouble(enode.getAttribute("arrowheadlength"));
        if (enode.hasAttribute("color"))
            color = GralogColor.parseColor(enode.getAttribute("color"));

        NodeList children = enode.getChildNodes(); // load intermediate points
        for (int i = 0; i < children.getLength(); ++i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.instantiateClass(child.getTagName());
            if (obj instanceof EdgeIntermediatePoint) {
                EdgeIntermediatePoint p = (EdgeIntermediatePoint) obj;
                p.fromXml(child);
                this.intermediatePoints.add(p);
            }
            if (obj instanceof ControlPoint) {
                ControlPoint p = (ControlPoint) obj;
                p.fromXml(child);
                this.controlPoints.add(p);
            }

        }
    }

    protected void notifyEdgeListeners() {
        for (EdgeListener listener : listeners)
            listener.edgeChanged(new EdgeEvent(this));
    }

    public void addEdgeListener(EdgeListener listener) {
        listeners.add(listener);
    }

    public void removeEdgeListener(EdgeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        return String.format("id:%d __ E(%d,%d)", id, this.getSource().getId(), this.getTarget().getId());
    }

    public String gralogPipify() {
        Class<?> c = this.getClass();
        String ret = "";
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            boolean toBeSent = false;
            Annotation[] annotations = f.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof DataField) {
                    DataField dataField = (DataField) annotation;
                    toBeSent = dataField.display() && (!dataField.readOnly());
                }
            }
            if (toBeSent) {
                ret = ret + f.getName() + "=";
                try {
                    ret = ret + f.get(this).toString() + "|" + Structure.pythonifyClass(f.getDeclaringClass()) + "#";
                } catch (Exception e) {
                    //todo: to handle!!!
                }
            }

        }
        if (ret.length() > 0) {
            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;

    }
}
