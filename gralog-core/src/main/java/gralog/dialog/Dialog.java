package gralog.dialog;

import gralog.structure.*;

import java.util.*;


public class Dialog  {

    private TreeSet<Vertex> vertices;
    private TreeSet<Edge> edges;

    private Map<String, TreeSet<Vertex>> vertexListS;
    private Map<String, TreeSet<Edge>> edgeListS;


    public Dialog() {
        vertices = new TreeSet<Vertex>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex a, Vertex b) {
                if (a.id == b.id)
                    return 0;
                else {
                    if (a.id < b.id) {
                        return -1;
                    } else return 1;
                }
            }
        });

        edges = new TreeSet<Edge>(new Comparator<Edge>() {
            @Override
            public int compare(Edge a, Edge b) {
                if (a.id == b.id)
                    return 0;
                else {
                    if (a.id < b.id) {
                        return -1;
                    } else return 1;
                }
            }
        });

    }

    public boolean isID(String s){
        return (vertexListS.containsKey(s) || edgeListS.containsKey(s));
    }

    public boolean isListID(String s){
        return (vertexListS.containsKey(s)|| edgeListS.containsKey(s));
    }

    public void performAction(DialogAction action, ArrayList<String> parameters){

    }

    public void filter(ArrayList<String> parameters){
        ArrayList<Vertex> what_v;
        ArrayList<Edge> what_e;
        if ((parameters.get(0).equals("ALL") && parameters.get(1).equals("VERTICES"))){
            what_v = new ArrayList<Vertex>();
        }



    }

    public boolean addVertex(Vertex v){
        return (vertices.add(v));
    }

    public boolean removeVertex(Vertex v){
        return (vertices.remove(v));
    }

    public boolean addEdge(Edge e){
        return edges.add(e);
    }

    public boolean removeEdge(Edge e){
        return edges.remove(e);
    }

}
