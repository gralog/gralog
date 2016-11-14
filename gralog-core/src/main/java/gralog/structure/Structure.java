/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.structure;

import gralog.plugins.PluginManager;
import gralog.plugins.XmlMarshallable;
import gralog.events.*;
import gralog.rendering.*;

import java.util.*;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

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

    public void render(GralogGraphicsContext gc, Set<Object> highlights) {
        for (Edge e : edges)
            e.render(gc, highlights);
        for (Vertex v : vertices)
            v.render(gc, highlights);
    }

    public void snapToGrid(double GridSize) {
        for (Edge e : edges)
            e.snapToGrid(GridSize);
        for (Vertex v : vertices)
            v.snapToGrid(GridSize);
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
    abstract public V createVertex();

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
    abstract public E createEdge();

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
        HashMap<String, Vertex> VertexRegister = new HashMap<>();
        HashMap<Edge, Element> LoadedFrom = new HashMap<>();
        ArrayList<Edge> TempEdges = new ArrayList<>();

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
                VertexRegister.put(id, v);
                addVertex(v);
            }
            else if (obj instanceof Edge) {
                TempEdges.add((E) obj);
                LoadedFrom.put((E) obj, child);
            }
        }

        for (Edge e : TempEdges) {
            e.fromXml(LoadedFrom.get(e), VertexRegister);
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
