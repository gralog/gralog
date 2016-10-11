/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author viktor
 */
public abstract class Structure<V extends Vertex, E extends Edge>
        extends XmlMarshallable implements IMovable {

    protected Set<Vertex> vertices;
    protected Set<Edge> edges;

    private final Set<StructureListener> listeners = new HashSet<>();

    public Structure() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public Set<Edge> getEdges() {
        return edges;
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

    abstract public V createVertex();

    public void addVertex(V v) {
        vertices.add(v);
    }

    public void removeVertex(Vertex v) {
        Set<Edge> deletedEdges = new HashSet<>();
        for (Edge e : edges)
            if (e.containsVertex(v))
                deletedEdges.add(e);
        for (Edge e : deletedEdges)
            removeEdge(e);

        vertices.remove(v);
    }

    abstract public E createEdge();

    public void addEdge(E e) {
        edges.add(e);
    }

    public void removeEdge(Edge e) {
        e.setSource(null);
        e.setTarget(null);
        edges.remove(e);
    }

    public E createEdge(V source, V target) {
        E edge = createEdge();
        edge.setSource(source);
        edge.setTarget(target);

        if (source == target && source != null) {
            edge.intermediatePoints.add(new EdgeIntermediatePoint(source.coordinates.get(0) + 0.6, source.coordinates.get(1) - 1.5d));
            edge.intermediatePoints.add(new EdgeIntermediatePoint(source.coordinates.get(0) - 0.6, source.coordinates.get(1) - 1.5d));
        }

        return edge;
    }

    public boolean adjacent(V a, V b) {
        for (Edge e : this.edges)
            if (e.containsVertex(a) && e.containsVertex(b))
                return true;
        return false;
    }

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
            edges.add(e);
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
