package gralog.dialog;

import gralog.structure.*;

import java.util.*;


public class Dialog {

    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    private Map<String, ArrayList<Vertex>> vertexListS;
    private Map<String, ArrayList<Edge>> edgeListS;

    private String errorMsg = "";

    public Dialog() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
    }

    public boolean isID(String s) {
        return (vertexListS.containsKey(s) || edgeListS.containsKey(s));
    }

    public boolean isListID(String s) {
        return (vertexListS.containsKey(s) || edgeListS.containsKey(s));
    }

    public void performAction(DialogAction action, ArrayList<String> parameters) {

    }

    private void filterAllVertices(ArrayList<String> parameters, Structure structure) {
        ArrayList<Vertex> what = (ArrayList<Vertex>) structure.getVertices();
        ArrayList<Vertex> to;
        if (vertexListS.containsKey(parameters.get(parameters.size() - 1))) {
            to = vertexListS.get(parameters.get(parameters.size() - 1));
        } else
            to = new ArrayList<Vertex>();


    }

    private void filterVertices(ArrayList<Vertex> what, ArrayList<Vertex> to, ArrayList<String> parameters) {
        return;
    }

    private void filterEdges(ArrayList<Edge> what, ArrayList<Edge> to, ArrayList<String> parameters) {
        return;
    }


    private ArrayList<Vertex> getVertexTo(String s){
        if (vertexListS.containsKey(s))
            return vertexListS.get(s);
        else{
            vertexListS.put(s,new ArrayList<Vertex>());
            return (vertexListS.get(s));
        }
    }

    private ArrayList<Edge> getEdgeTo(String s){
        if (edgeListS.containsKey(s))
            return edgeListS.get(s);
        else{
            edgeListS.put(s,new ArrayList<Edge>());
            return (edgeListS.get(s));
        }
    }

    public void filter(ArrayList<String> parameters, Structure structure, Highlights highlights) {
        if (parameters.get(1).equals("VERTICES") || vertexListS.containsKey(parameters.get(0))){ // vertex list
            if (parameters.get(1).equals("VERTICES") && edgeListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as an edge list. Choose another name.\n";
                return;
            }
            ArrayList<Vertex> to = getVertexTo(parameters.get(parameters.size()-1));
            parameters.remove(0);
            parameters.remove(1);
            ArrayList<Vertex> what = new ArrayList<Vertex>();
            if (parameters.get(0).equals("SELECTED")) {
                parameters.remove(0);
                parameters.remove(1);
                for (Object v : highlights.getSelection())
                    if (v instanceof Vertex)
                        what.add((Vertex) v);
            }
            if (parameters.get(0).equals("ALL")) {
                what = (ArrayList<Vertex>) structure.getVertices();
                parameters.remove(0);
                parameters.remove(1);
            }
            filterVertices(what,to,parameters);
            return;
        }
        if (parameters.get(1).equals("EDGES") || edgeListS.containsKey(parameters.get(0))){ // edge list
            if (parameters.get(1).equals("EDGES") && vertexListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as a vertex list. Choose another name.\n";
                return;
            }
            ArrayList<Edge> to = getEdgeTo(parameters.get(parameters.size()-1));
            parameters.remove(0);
            parameters.remove(1);
            ArrayList<Edge> what = new ArrayList<Edge>();
            if (parameters.get(0).equals("SELECTED")) {
                parameters.remove(0);
                parameters.remove(1);
                for (Object v : highlights.getSelection())
                    if (v instanceof Edge)
                        what.add((Edge) v);
            }
            if (parameters.get(0).equals("ALL")) {
                what = (ArrayList<Edge>) structure.getEdges();
                parameters.remove(0);
                parameters.remove(1);
            }
            filterEdges(what,to,parameters);
            return;
        }

        errorMsg = "List " + parameters.get(0) + " not found.\n";
        return;
    }

    public String getErrorMsg(){
        return errorMsg;
    }

    public boolean addVertex(Vertex v) {
        return (vertices.add(v));
    }

    public boolean removeVertex(Vertex v) {
        return (vertices.remove(v));
    }

    public boolean addEdge(Edge e) {
        return edges.add(e);
    }

    public boolean removeEdge(Edge e) {
        return edges.remove(e);
    }

}
