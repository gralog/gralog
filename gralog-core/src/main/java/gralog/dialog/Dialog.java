package gralog.dialog;

import gralog.structure.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


public class Dialog  {

  private TreeSet<Vertex> vertices;
  private TreeSet<Edge> edges;

  private Set<TreeSet<Vertex>> vertexListS;
  private Set<TreeSet<Edge>> edgeListS;


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

  public void performAction(DialogAction action, ArrayList<String> parameters){

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
