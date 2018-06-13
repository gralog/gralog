/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.math.BezierUtilities;
import gralog.plugins.*;
import gralog.events.*;
import gralog.rendering.*;

import java.util.*;

import gralog.structure.controlpoints.ControlPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
@XmlName(name = "edge")
public class Edge extends XmlMarshallable implements IMovable {

    public enum EdgeType{
        SHARP,
        ROUND,
        BEZIER
    }

    private int id = -1; //if not -1, then don't change the id

    public static double multiEdgeOffset = 0.2;

    Set<EdgeListener> listeners = new HashSet<>();

    //inspector visible
    public String label = ""; //add this
    public Double weight = 1.0d;

    public Boolean isDirected = true;
    public Arrow arrowType = Arrow.TYPE2;
    public double arrowHeadLength = 0.2d; // cm

    public double arrowHeadAngle = 40d; // degrees
    // @InspectorName(name = "thickness")

    public Double width = 2.54 / 96; // cm
    public GralogColor color = GralogColor.BLACK;

    public GralogGraphicsContext.LineType type = GralogGraphicsContext.LineType.PLAIN;
    public EdgeType edgeType = EdgeType.BEZIER; //TODO: switch to private and use annotations to mark insp vars

    //end

    public ArrayList<Edge> siblings = new ArrayList<>();
    public ArrayList<EdgeIntermediatePoint> intermediatePoints = new ArrayList<>();

    private Vertex source = null;
    private Vertex target = null;

    public ArrayList<ControlPoint> controlPoints = new ArrayList<>();

    public void setEdgeType(EdgeType e){
        if(e == EdgeType.BEZIER && controlPoints.size() > 2){

            Vector2D ctrl1 = Vector2D.zero(),
                    ctrl2 = Vector2D.zero();

            int offset = (controlPoints.size() + 1) % 2; //0 when uneven
            int middle = (controlPoints.size() - 1 - offset)/2;

            for(int i = 0; i <= middle; i++){
                ctrl1 = ctrl1.plus(controlPoints.get(i).getPosition());
            }
            for(int i = middle + offset; i < controlPoints.size(); i++){
                ctrl2 = ctrl2.plus(controlPoints.get(i).getPosition());
            }
            ctrl1 = ctrl1.multiply(1d/(middle + 1));
            ctrl2 = ctrl2.multiply(1d/(middle + 1));
            ControlPoint c1 = new ControlPoint(ctrl1, this);
            ControlPoint c2 = new ControlPoint(ctrl2, this);
            controlPoints.clear();
            controlPoints.add(c1);
            controlPoints.add(c2);
        }

        this.edgeType = e;
    }

    public EdgeType getEdgeType(){
        return edgeType;
    }

    public int getControlPointCount(){
        return controlPoints.size();
    }

    /**
     * The clickPosition determines where the edge was initially clicked to
     * add the control point. Depending on clickPosition, the correct edge segment
     * for adding the control point can be determined
     */
    public ControlPoint addControlPoint(Vector2D position, Vector2D clickPosition){
        if(edgeType == EdgeType.BEZIER){
            return addBezierControlPoint(position);
        }else{
            return addSharpControlPoint(position, clickPosition);
        }
    }


    public int getId(){
        return this.id;
    }

    public int setId(int id){
        this.id = id;
    }


    private ControlPoint addBezierControlPoint(Vector2D position){
        if(controlPoints.size() >= 2){
            return null;
        }

        ControlPoint c =  new ControlPoint(position, this);

        if(controlPoints.size() == 1){
            double c1Dist = c.getPosition().minus(target.coordinates).length();
            double c2Dist = controlPoints.get(0).getPosition().minus(target.coordinates).length();
            //distance is not the correct metric
            //TODO: project on edge and use x order
            controlPoints.add(c1Dist > c2Dist ? 0 : 1, c);
        }else if(controlPoints.isEmpty()){
            controlPoints.add(c);
        }else{ //can't add more than 2 bezier control points
            return null;
        }
        return c;
    }

    private ControlPoint addSharpControlPoint(Vector2D position, Vector2D clickPosition){
        ControlPoint c = new ControlPoint(position, this);
        int idx = containsCoordinateSharp(clickPosition.getX(), clickPosition.getY());
        if(idx >= controlPoints.size()){
            controlPoints.add(c);
        }
        else if(idx == 0){
            controlPoints.add(0,c);
        }else{
            controlPoints.add(idx, c);
        }
        return c;
    }

    public ControlPoint removeControlPoint(ControlPoint c){
        if(controlPoints.size() > 1){
            controlPoints.remove(c);
            return controlPoints.get(0);
        }else{
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
        if (source != null){
            this.source.connectEdge(this);
        }
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        if (this.target != null)
            this.target.disconnectEdge(this);
        this.target = target;
        if (target != null){
            this.target.connectEdge(this);
        }
    }
    public boolean isLoop(){
        return getSource() == getTarget();
    }

    public boolean isSiblingTo(Edge other){
        return getTarget() == other.getTarget()|| getTarget() == other.getSource();
    }
    public double maximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        return result;
    }

    @Override
    public void move(Vector2D offset) {

    }

    public void collapse(Structure structure){
        //One edge that doesn't have the same direction as this edge
        Edge e = null;
        for(int i = 0; i < siblings.size(); i++){
            e = siblings.get(i);
            if(e != this && !e.sameOrientationAs(this)){
                break;
            }
            e = null;
        }
        for(int i = 0; i < siblings.size(); i++){
            if(siblings.get(i) != this && siblings.get(i) != e){
                structure.removeEdge(siblings.get(i), false);
            }
        }
        siblings.clear();
        siblings = new ArrayList<>();
        siblings.add(this);

        //correct siblings of edge e as well
        if(e != null){
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
        for(ControlPoint c : controlPoints){
            if(c.active && c.containsCoordinate(x,y)){
                return c;
            }
        }

        if (this.containsCoordinate(x, y)){
            return this;
        }

        return null;
    }
    private void renderLoop(GralogGraphicsContext gc, Highlights highlights){
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
        if(isDirected){
            gc.arrow(tangentToIntersection, intersection2, arrowType, arrowHeadLength, edgeColor);
        }else{
            correction = 0;
        }

        //Loop description, endpoints and tangents.
        GralogGraphicsContext.Loop l = new GralogGraphicsContext.Loop();
        l.start = intersection;
        l.end = intersection2;
        l.tangentStart = Vector2D.getVectorAtAngle(angleStart, 1).orthogonal();
        l.tangentEnd = Vector2D.getVectorAtAngle(angleEnd, 1).orthogonal();

        gc.loop(l,1.5, correction, edgeColor, width, type);

    }
    public void render(GralogGraphicsContext gc, Highlights highlights){

        GralogColor edgeColor = highlights.isSelected(this) ? GralogColor.RED : this.color;

        if(isLoop()){
            renderLoop(gc, highlights);
            return;
        }

        if(edgeType == EdgeType.BEZIER){
            EdgeRenderer.drawBezierEdge(this, gc, edgeColor);
        }else if(edgeType == EdgeType.SHARP){
            EdgeRenderer.drawSharpEdge(this, gc, edgeColor);
        }

    }

    public double getOffset(){
        double offset = 0;
        int index = siblings.indexOf(this);
        //offset both edges orthogonally, offsets differently when both face same direction
        if(siblings.size() == 2){
            offset = 0.5 * multiEdgeOffset;
            if(index == 1){
                if(siblings.get(0).sameOrientationAs(this)){
                    offset *= -1;
                }
            }

        }
        if(siblings.size() == 3){
            if(index == 1){
                offset = 0;
            }else if(index == 0){
                offset = multiEdgeOffset;
            }else if(index == 2){
                offset = (siblings.get(0).sameOrientationAs(this) ? -1 : 1) * multiEdgeOffset;
            }
        }
        if(siblings.size() == 4){
            int sameOrientationCount = 0;
            double offsetMultiplier;
            for (int i = 0; i < siblings.size(); i++)
            {
                if(i == index){
                    int correctedOffsetCounter = (sameOrientationCount >= 2 ? -(i - 1) : (sameOrientationCount + 1));
                    if(Math.abs(correctedOffsetCounter) > 1){
                        offsetMultiplier = 0.75;
                    }else{
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
    public boolean sameOrientationAs(Edge other){
        return getSource() == other.getSource();
    }
    public boolean containsCoordinate(double x, double y) {

        if(controlPoints.size() == 0){
            return containsCoordinateFlat(x, y) == 0;
        }
        if(edgeType == EdgeType.BEZIER){
            return containsCoordinateBezier(x, y);
        }else if(edgeType == EdgeType.SHARP){
            return containsCoordinateSharp(x, y) >= 0;
        }else{ //edgeType == EdgeType.ROUND
            return containsCoordinateRound(x, y);
        }
    }

    /**
     * Checks for contains() assuming there are no control points
     * @return 0 if it contains (x,  y), -1 otherwise
     */
    private int containsCoordinateFlat(double x, double y){
        Vector2D diff = target.coordinates.minus(source.coordinates);
        Vector2D perpendicularToDiff = diff.orthogonal(1).normalized().multiply(getOffset());

        Vector2D sourceOffset = source.coordinates.plus(perpendicularToDiff);
        Vector2D targetOffset = target.coordinates.plus(perpendicularToDiff);

        double fromX = sourceOffset.getX();
        double fromY = sourceOffset.getY();

        double toX = targetOffset.getX();
        double toY = targetOffset.getY();
        if(Vector2D.distancePointToLine(x, y, fromX, fromY, toX, toY) < multiEdgeOffset * 0.5){
            return 0;
        }else{
            return -1;
        }

    }
    private boolean containsCoordinateRound(double x, double y){
        return false;
    }

    /**
     * Checks if a given vector (x, y) is within a close margin of the
     * sharp edge (the form of the edge is given by its control points)
     * @param x x coordinate of the position to check
     * @param y y coordinate of the position to check
     * @return -1 if edge does not contain position, i>=0 otherwise (where [i-1, i] are the indices
     * of the control points that have been hit). If i==0, then [i-1] is the source vertex
     */
    private int containsCoordinateSharp(double x, double y){
        if(controlPoints.size() == 0){
            return containsCoordinateFlat(x, y);
        }
        double dist = Vector2D.distancePointToLine(x, y, source.coordinates, controlPoints.get(0).getPosition());

        if(dist < multiEdgeOffset * 0.5){
            return 0;
        }
        for(int i = 1; i < controlPoints.size(); i++){
            Vector2D a = controlPoints.get(i-1).getPosition();
            Vector2D b = controlPoints.get(i).getPosition();
            if(Vector2D.distancePointToLine(x, y, a, b) < multiEdgeOffset * 0.5){
                return i;
            }
        }
        Vector2D last = controlPoints.get(controlPoints.size() - 1).getPosition();
        dist = Vector2D.distancePointToLine(x, y, last, target.coordinates);

        return dist < multiEdgeOffset * 0.5 ? controlPoints.size(): -1;
    }

    private boolean containsCoordinateBezier(double x, double y){
        Vector2D m = new Vector2D(x,y);

        Vector2D ctrl1 = controlPoints.get(0).getPosition();
        Vector2D ctrl2 = controlPoints.size() < 2 ? ctrl1 : controlPoints.get(1).getPosition();

        //correction so that the arrow and line don't overlap at the end
        //corrections are always negative if the arrow model tip is at the origin
        double corr = arrowType.endPoint * arrowHeadLength;

        Vector2D sourceToCtrl1 = ctrl1.minus(source.coordinates).normalized();
        Vector2D targetToCtrl2 = ctrl2.minus(target.coordinates).normalized();

        if(!isDirected){
            corr = 0;
        }
        //TODO: replace with shape.getEdgePoint
        Vector2D source = this.source.coordinates.plus(sourceToCtrl1.multiply(this.source.radius));
        Vector2D target = this.target.coordinates.plus(targetToCtrl2.multiply(this.target.radius - corr));
        BezierUtilities.ProjectionResults projection;

        if(controlPoints.size() == 1){
            projection = BezierUtilities.pointProjectionQuadraticAlgebraic(m, source, ctrl1, target);
        }
        else if(controlPoints.size() == 2){
            projection = BezierUtilities.pointProjectionCubicAlgebraic(m, source, ctrl1, ctrl2, target);
        }else{
            return false;
        }

        if(projection.successful){
            return projection.result.minus(m).length() < multiEdgeOffset * 0.5;
        }else{
            return false;
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
        enode.setAttribute("width", Double.toString(width));
        enode.setAttribute("arrowheadlength", Double.toString(arrowHeadLength));
        enode.setAttribute("color", color.toHtmlString());

        for (EdgeIntermediatePoint p : intermediatePoints) {
            Element e = p.toXml(doc);
            if (e != null)
                enode.appendChild(e);
        }

        return enode;
    }

    public void setLabel(String label){
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

        if (enode.hasAttribute("width"))
            width = Double.parseDouble(enode.getAttribute("width"));

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
    public String toString(){
        return String.format("id:%d __ E(%d,%d)", id, this.getSource().getId(), this.getTarget().getId());
    }
}
