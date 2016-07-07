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
public abstract class Structure<V extends Vertex, E extends Edge> extends XmlMarshallable {
    protected Set<Vertex> Vertices;
    protected Set<Edge> Edges;
    
    Set<StructureListener> listeners = new HashSet<StructureListener>();
    
    public Structure() {
        Vertices = new HashSet<Vertex>();
        Edges = new HashSet<Edge>();
    }
    

    public Set<Vertex> getVertices() {
        return Vertices;
    }
    public Set<Edge> getEdges() {
        return Edges;
    }            
    
    
    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        for(Edge e : Edges)
            e.Render(gc, highlights);
        for(Vertex v : Vertices)
            v.Render(gc, highlights);
    }

    
    public void SnapToGrid(Double GridSize) {
        for(Edge e : Edges)
            e.SnapToGrid(GridSize);
        for(Vertex v : Vertices)
            v.SnapToGrid(GridSize);
    }
    
    
    public Double MaximumCoordinate(int dimension) {
        Double result = Double.NEGATIVE_INFINITY;
        for(Vertex v : getVertices())
            result = Math.max(result, v.MaximumCoordinate(dimension));
        for(Edge e : getEdges())
            result = Math.max(result, e.MaximumCoordinate(dimension));
        return result;
    }
    
    public void Move(Vector<Double> offset) {
        for(Vertex v : getVertices())
            v.Move(offset);
        for(Edge e : getEdges())
            e.Move(offset);
    }
    
    
    abstract public V CreateVertex();
    public void AddVertex(V v) {
        Vertices.add(v);
    }
    public void RemoveVertex(Vertex v) {
        Set<Edge> deletedEdges = new HashSet<Edge>();
        for(Edge e : Edges)
            if(e.ContainsVertex(v))
                deletedEdges.add(e);
        for(Edge e : deletedEdges)
            RemoveEdge(e);
        
        Vertices.remove(v);
    }

    
    abstract public E CreateEdge();
    public void AddEdge(E e) {
        Edges.add(e);
    }
    public void RemoveEdge(Edge e) {
        e.setSource(null);
        e.setTarget(null);
        Edges.remove(e);
    }
    public E CreateEdge(V source, V target)
    {
        E edge = CreateEdge();
        edge.setSource(source);
        edge.setTarget(target);
        
        if(source == target && source != null)
        {
            edge.intermediatePoints.add(new EdgeIntermediatePoint(source.Coordinates.get(0) + 0.6, source.Coordinates.get(1)-1.5d));
            edge.intermediatePoints.add(new EdgeIntermediatePoint(source.Coordinates.get(0) - 0.6, source.Coordinates.get(1)-1.5d));
        }
        
        return edge;
    }
    
    
    
    public boolean Adjacent(V a, V b) {
        for(Edge e : this.Edges)
            if(e.ContainsVertex(a) && e.ContainsVertex(b))
                return true;
        return false;
    }
    public IMovable FindObject(Double x, Double y)
    {
        IMovable result = null;

        for(Edge e : Edges)
        {
            IMovable temp = e.FindObject(x,y);
            if(temp != null)
                result = temp;
        }
        
        for(Vertex v : Vertices)
            if(v.ContainsCoordinate(x,y))
                result = v;

        return result;
    }
    
    
    
    public Element ToXml(Document doc) throws Exception {
        Element snode = super.ToXml(doc);

        HashMap<Vertex, String> ids = new HashMap<Vertex, String>();
        Integer i = 1;
        for(Vertex v : Vertices)
        {
            String id = "n"+(i++);
            ids.put(v, id);
            Element vnode = v.ToXml(doc, id);
            if(vnode != null)
                snode.appendChild(vnode);
        }
        
        for(Edge e : Edges)
        {
            Element enode = e.ToXml(doc, ids);
            if(enode != null)
                snode.appendChild(enode);
        }        
        
        return snode;
    }
    
    public void WriteToFile(String filename) throws Exception {
        WriteToStream(new StreamResult(filename));
    }
    
    public void WriteToStream(StreamResult stream) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("graphml");
        Element snode = ToXml(doc);
        if(snode == null)
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
    
    public void FromXml(Element gnode) throws Exception {
        
        HashMap<String,Vertex> VertexRegister = new HashMap<>();
        HashMap<Edge, Element> LoadedFrom = new HashMap<>();
        Vector<Edge> TempEdges = new Vector<Edge>();
        
        NodeList children = gnode.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != childNode.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.InstantiateClass(child.getTagName());
            if(obj instanceof Vertex)
            {
                V v = (V)obj;
                String id = v.FromXml(child);
                VertexRegister.put(id, v);
                AddVertex(v);
            }
            else if(obj instanceof Edge)
            {
                TempEdges.add((E)obj);
                LoadedFrom.put((E)obj, child);
            }
        }
        
        for(Edge e : TempEdges)
        {
            e.FromXml(LoadedFrom.get(e), VertexRegister);
            Edges.add(e);
        }
    }

    public static Structure LoadFromFile(String fileName) throws Exception
    {
        return LoadFromStream(new FileInputStream(fileName));
    }
    
    public static Structure LoadFromStream(InputStream stream) throws Exception
    {
     	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(stream);
	doc.getDocumentElement().normalize();
        
        Element root = doc.getDocumentElement();
        if(!root.getTagName().equalsIgnoreCase("graphml"))
            throw new Exception("Not a GraphML file");
        
        NodeList children = root.getChildNodes();
        
        for(int i = children.getLength()-1; i >= 0; --i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != childNode.ELEMENT_NODE)
                continue;
            Element child = (Element)childNode;
            
            Object result = PluginManager.InstantiateClass(child.getTagName());
            if(result == null)
                continue;
            
            if(result instanceof Structure)
            {
                ((Structure)result).FromXml(child);
                return (Structure)result;
            }
        }

        return null;
    }
    
    
    protected void notifyStructureListeners() {
        for(StructureListener listener : listeners)
            listener.StructureChanged(new StructureEvent(this));
    }
    public void addStructureListener(StructureListener listener) {
        listeners.add(listener);
    }
    public void removeStructureListener(StructureListener listener) {
        listeners.remove(listener);
    }
    
    
    public StructureDescription getDescription() throws Exception {
        if(!this.getClass().isAnnotationPresent(StructureDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @StructureDescription Annotation");
        return this.getClass().getAnnotation(StructureDescription.class);
    }
    
}

