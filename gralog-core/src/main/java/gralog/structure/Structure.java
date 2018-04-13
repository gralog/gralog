/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import gralog.plugins.PluginManager;
import gralog.plugins.XmlMarshallable;
import gralog.events.*;
import gralog.rendering.*;

import java.util.*;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import javafx.geometry.Point2D;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;

/**
 *
 */
public abstract class Structure<V extends Vertex, E extends Edge>
    extends XmlMarshallable implements IMovable {

    protected Set<V> vertices;
    protected Set<E> edges;

    private final Set<StructureListener> listeners = new HashSet<>();

    public Structure() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }

    /**
     * @return An unmodifiable set of vertices.
     */
    public Set<V> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    /**
     * @return An unmodifiable set of edges.
     */
    public Set<E> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public void render(GralogGraphicsContext gc, Highlights highlights) {
        for (Edge e : edges)
            e.render(gc, highlights);
        for (Vertex v : vertices)
            v.render(gc, highlights);
    }

    public void snapToGrid(double gridSize) {
        for (Edge e : edges)
            e.snapToGrid(gridSize);
        for (Vertex v : vertices)
            v.snapToGrid(gridSize);
    }

    /**
     * @param dimension 0 for the x-dimension and 1 for the y-dimension.
     * @return The largest x or y value occurring in vertex/edges coordinates.
     */
    public double maximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        for (Vertex v : getVertices())
            result = Math.max(result, v.maximumCoordinate(dimension));
        for (Edge e : getEdges())
            result = Math.max(result, e.maximumCoordinate(dimension));
        return result;
    }

    @Override
    public void move(Vector2D offset) {
        for (Vertex v : getVertices())
            v.move(offset);
        for (Edge e : getEdges())
            e.move(offset);
    }

    /**
     * Creates a new vertex instance without adding it to the structure.
     *
     * @return The new vertex.
     */
    public abstract V createVertex();

    /**
     * Adds a vertex to the structure. Has no effect if the vertex already
     * exists in the structure.
     *
     * @param v The vertex to be added.
     */
    public void addVertex(V v) {
        vertices.add(v);
    }

    /**
     * Adds a set of vertices to the structure.
     *
     * @param vs The vertices to be added.
     */
    public void addVertices(Collection<V> vs) {
        vertices.addAll(vs);
    }

    /**
     * Clear the structure. Removes all vertices and all edges.
     */
    public void clear() {
        vertices.clear();
        edges.clear();
    }

    /**
     * Creates a new vertex with the given label and adds it to the structure.
     * This is a convenience function combining createVertex and addVertex.
     * Adding multiple vertices with the same name adds multiple vertices.
     *
     * @param label The label of the new vertex to be added.
     * @return The new vertex.
     */
    public V addVertex(String label) {
        V v = createVertex();
        v.label = label;
        addVertex(v);
        return v;
    }

    /**
     * Removes a vertex and its incident edges from the structure.
     *
     * @param v The vertex to be removed.
     */
    public void removeVertex(Vertex v) {
        Set<Edge> deletedEdges = new HashSet<>();
        for (Edge e : edges)
            if (e.containsVertex(v))
                deletedEdges.add(e);
        for (Edge e : deletedEdges)
            removeEdge(e);
        vertices.remove(v);
    }

    /**
     * Create a new edge instance without adding it to the structure.
     *
     * @return The new edge.
     */
    public abstract E createEdge();

    /**
     * Add an edge to the structure. Has no effect if the edge already exists in
     * the structure.
     *
     * @param e The edge to be added.
     */
    public void addEdge(E e) {
        edges.add(e);
    }

    /**
     * Adds a set of edges to the structure.
     *
     * @param es The edges to be added.
     */
    public void addEdges(Collection<E> es) {
        edges.addAll(es);
    }

    /**
     * Creates a new edge and adds it to the structure. This is a convenience
     * function combining createEdge and addEdge.
     *
     * @param source The tail of the new edge.
     * @param target The head of the new edge.
     * @return The new edge.
     */
    public E addEdge(V source, V target) {
        E e = createEdge(source, target);
        addEdge(e);
        return e;
    }

    /**
     * Removes an edge from the structure. Does not affect vertices, incident or
     * not.
     *
     * @param e The edge to be removed.
     */
    public void removeEdge(Edge e) {
        e.setSource(null);
        e.setTarget(null);
        edges.remove(e);
    }

    /**
     * Creates an edge without adding it to the graph.
     *
     * @param source The tail of the new edge.
     * @param target The head of the new edge.
     * @return The new edge.
     */
    public E createEdge(V source, V target) {
        E edge = createEdge();
        edge.setSource(source);
        edge.setTarget(target);

        if (source == target && source != null) {
            edge.intermediatePoints.add(
                new EdgeIntermediatePoint(source.coordinates.getX() + 0.6,
                    source.coordinates.getY() - 1.5d));
            edge.intermediatePoints.add(
                new EdgeIntermediatePoint(source.coordinates.getX() - 0.6,
                    source.coordinates.getY() - 1.5d));
        }
        return edge;
    }

    /**
     * @return True if the given two vertices are adjacent.
     * @param a The first vertex.
     * @param b The second vertex.
     */
    public boolean adjacent(V a, V b) {
        for (Edge e : this.edges)
            if (e.containsVertex(a) && e.containsVertex(b))
                return true;
        return false;
    }

    /**
     * Return an edge or vertex that lies at the given coordinates. If multiple
     * objects are stacked at the given location, then vertices win over edges.
     * If multiple edges or multiple vertices are stacked, then the top-most
     * object will be chosen in an unspecified way.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return An edge or vertex that lies at the given x/y coordinate.
     */
    public IMovable findObject(double x, double y) {
        IMovable result = null;

        for (Edge e : edges) {
            IMovable temp = e.findObject(x, y);
            if (temp != null)
                result = temp;
        }

        for (Vertex v : vertices)
            if (v.containsCoordinate(x, y))
                result = v;

        return result;
    }

    /**
     * Returns an array of movable objects that lie within bounds of a given rectangle.
     * TODO: Implement an overloaded findObjects that returns only filtered movable obj.
     * @param from one corner of the rectangle
     * @param to the corner diagonal to the first one
     * @return A collection of movables that are within bounds of the given rectangle
     */
    public List<IMovable> findObjects(Point2D from, Point2D to){
        List<IMovable> objects = new ArrayList<>();

        Vector2D vecFrom = new Vector2D(from.getX(), from.getY());
        Vector2D vecTo = new Vector2D(to.getX(), to.getY());
        double px = from.getX();
        double qx = to.getX();
        double py = from.getY();
        double qy = to.getY();

        double cx = qx - px;
        double cy = qy - py;

        for (Vertex v : vertices){
            double vx = v.coordinates.getX();
            double vy = v.coordinates.getY();
            if(insideSelection(qx, qy, cx, cy, vx, vy)){
                objects.add(v);
            }
        }

        for (Edge e : edges){
            Vector2D source = e.getSource().coordinates;
            Vector2D target = e.getTarget().coordinates;

            if(separatingAxisTest(vecFrom, vecTo, source, target)){
                continue;
            }
            Vector2D sel1 = new Vector2D(from.getX(), from.getY())  .minus(source);
            Vector2D sel2 = new Vector2D(to.getX(), from.getY())    .minus(source);
            Vector2D sel3 = new Vector2D(from.getX(), to.getY())    .minus(source);
            Vector2D sel4 = new Vector2D(to.getX(), to.getY())    .minus(source);

            Vector2D dir = target.minus(source);
            double lsquared = dir.length() * dir.length();

            Vector2D proj1 = dir.multiply(dir.multiply(sel1) / lsquared).minus(sel1);
            Vector2D proj2 = dir.multiply(dir.multiply(sel2) / lsquared).minus(sel2);
            Vector2D proj3 = dir.multiply(dir.multiply(sel3) / lsquared).minus(sel3);
            Vector2D proj4 = dir.multiply(dir.multiply(sel4) / lsquared).minus(sel4);

            if(!arePositivelyProportional(proj1, proj2)){
                objects.add(e);
            }else if(!arePositivelyProportional(proj1, proj3)){
                objects.add(e);
            }else if(!arePositivelyProportional(proj1, proj4)){
                objects.add(e);
            }

        }
        return objects;
    }

    //TODO: write comprehensive, more general SAT colision test
    /** Returns true if projections are separate
     */
    private boolean separatingAxisTest(Vector2D rectFrom, Vector2D rectTo, Vector2D edgeFrom, Vector2D edgeTo){
        //x axis
        if(Math.max(rectFrom.getX(), rectTo.getX()) < Math.min(edgeFrom.getX(), edgeTo.getX())){
            return true;
        }
        if(Math.min(rectFrom.getX(), rectTo.getX()) > Math.max(edgeFrom.getX(), edgeTo.getX())){
            return true;
        }
        //y axis
        if(Math.max(rectFrom.getY(), rectTo.getY()) < Math.min(edgeFrom.getY(), edgeTo.getY())){
            return true;
        }
        if(Math.min(rectFrom.getY(), rectTo.getY()) > Math.max(edgeFrom.getY(), edgeTo.getY())){
            return true;
        }

        return false;
    }
    private boolean arePositivelyProportional(Vector2D a, Vector2D b){
        double ax = a.getX();
        double ay = a.getY();

        double bx = b.getX();
        double by = b.getY();

        if(ax != 0){
            return Math.signum(ax) == Math.signum(bx);
        }else{
            return Math.signum(ay) == Math.signum(by);
        }
    }
    /* checking if x coordinate is in bounds of the rect (for x and y respectively)
     * |q-v|<|c|  and  sgn(c)|q-v|>0
     */
    private boolean insideSelection(double qx, double qy, double cx, double cy, double vx, double vy){
        return  Math.signum(cx) * (qx - vx) <= Math.abs(cx) &&
                Math.signum(cy) * (qy - vy) <= Math.abs(cy) &&
                Math.signum(cx) * (qx - vx) >= 0 &&
                Math.signum(cy) * (qy - vy) >= 0;
    }
    @Override
    public Element toXml(Document doc) throws Exception {
        Element snode = super.toXml(doc);

        HashMap<Vertex, String> ids = new HashMap<>();
        Integer i = 1;
        for (Vertex v : vertices) {
            String id = "n" + (i++);
            ids.put(v, id);
            Element vnode = v.toXml(doc, id);
            if (vnode != null)
                snode.appendChild(vnode);
        }

        for (Edge e : edges) {
            Element enode = e.toXml(doc, ids);
            if (enode != null)
                snode.appendChild(enode);
        }

        return snode;
    }

    public void writeToFile(String filename) throws Exception {
        writeToStream(new StreamResult(filename));
    }

    public void writeToStream(StreamResult stream) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("graphml");
        Element snode = toXml(doc);
        if (snode == null)
            throw new Exception("Error writing to XML");
        root.appendChild(snode);
        doc.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, stream);
    }

    public void fromXml(Element gnode) throws Exception {
        HashMap<String, Vertex> vertexRegister = new HashMap<>();
        HashMap<Edge, Element> loadedFrom = new HashMap<>();
        ArrayList<Edge> tempEdges = new ArrayList<>();

        NodeList children = gnode.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.instantiateClass(child.getTagName());
            if (obj instanceof Vertex) {
                V v = (V) obj;
                String id = v.fromXml(child);
                vertexRegister.put(id, v);
                addVertex(v);
            } else if (obj instanceof Edge) {
                tempEdges.add((E) obj);
                loadedFrom.put((E) obj, child);
            }
        }

        for (Edge e : tempEdges) {
            e.fromXml(loadedFrom.get(e), vertexRegister);
            edges.add((E) e);
        }
    }

    public static Structure loadFromFile(String fileName) throws Exception {
        return loadFromStream(new FileInputStream(fileName));
    }

    public static Structure loadFromStream(InputStream stream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!root.getTagName().equalsIgnoreCase("graphml"))
            throw new Exception("Not a GraphML file");

        NodeList children = root.getChildNodes();

        for (int i = children.getLength() - 1; i >= 0; --i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element child = (Element) childNode;

            Object result = PluginManager.instantiateClass(child.getTagName());
            if (result == null)
                continue;

            if (result instanceof Structure) {
                ((Structure) result).fromXml(child);
                return (Structure) result;
            }
        }

        return null;
    }

    protected void notifyStructureListeners() {
        for (StructureListener listener : listeners)
            listener.structureChanged(new StructureEvent(this));
    }

    public void addStructureListener(StructureListener listener) {
        listeners.add(listener);
    }

    public void removeStructureListener(StructureListener listener) {
        listeners.remove(listener);
    }

    public StructureDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(StructureDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @StructureDescription Annotation");
        return this.getClass().getAnnotation(StructureDescription.class);
    }
}
