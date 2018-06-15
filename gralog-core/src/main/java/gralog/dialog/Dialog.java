package gralog.dialog;

import gralog.structure.*;

import java.util.*;


public class Dialog  {

  private ArrayList<Vertex> vertices;
  private ArrayList<Edge> edges;

  private Map<String, ArrayList<Vertex>> vertexListS;
  private Map<String, ArrayList<Edge>> edgeListS;


  public Dialog() {
    vertices = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>();
  }

  public boolean isID(String s){
    return (vertexListS.containsKey(s) || edgeListS.containsKey(s));
  }

  public boolean isListID(String s){
    return (vertexListS.containsKey(s)|| edgeListS.containsKey(s));
  }

  public void performAction(DialogAction action, ArrayList<String> parameters){

  }

  private void filterAllVertices(ArrayList<String> parameters, Structure structure){
      ArrayList<Vertex> what = structure.getVertices();
      ArrayList<Vertex> to;
      if (vertexListS.containsKey(parameters.get(parameters.size()-1))){
          to = vertexListS.get(parameters.get(parameters.size()-1));
      }
      else
          to = new ArrayList<Vertex>();

      for (Object v : highlights.getSelection())
          if (v instanceof Vertex)
              to.add((Vertex)v);
  }

  public void filter(ArrayList<String> parameters, Structure structure, Highlights highlights){
      if (parameters.get(0).equals("ALL")){
          if (parameters.get(1).equals("VERTICES")) {
              parameters.remove(0);
              parameters.remove(1);
              filterAllVertices(parameters,structure);
          }
          if (parameters.get(1).equals("EDGES")){
              parameters.remove(0);
              parameters.remove(1);
              filterAllEdges(parameters);
          }
      }
      if (parameters.get(0).equals("SELECTED")){
          if (parameters.get(1).equals("VERTICES")) {
              parameters.remove(0);
              parameters.remove(1);
              filterSelectedVertices(parameters);
          }
          if (parameters.get(1).equals("EDGES")){
              parameters.remove(0);
              parameters.remove(1);
              filterSelectedEdges(parameters);
          }
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
