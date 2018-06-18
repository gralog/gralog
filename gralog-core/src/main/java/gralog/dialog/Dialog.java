package gralog.dialog;

import gralog.rendering.GralogColor;
import gralog.rendering.shapes.RenderingShape;
import gralog.rendering.shapes.RenderingShape.*;
import gralog.structure.*;

import java.util.*;

import static gralog.dialog.DialogParser.ANSI_RED;
import static gralog.dialog.DialogParser.ANSI_RESET;


public class Dialog {

    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    private Map<String, ArrayList<Vertex>> vertexListS;
    private Map<String, ArrayList<Edge>> edgeListS;

    private String errorMsg = "";

    public Dialog() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        vertexListS = new HashMap();
        edgeListS = new HashMap();
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

    private void filterStroke(ArrayList<Vertex> what, ArrayList<Vertex> to, String color){
        for (Vertex v : what){
            if (v.strokeColor.equals(color)){
                to.add(v);
            }
        }
    }

    private void filterFill(ArrayList<Vertex> what, ArrayList<Vertex> to, String edgeType){}
    private void filterWidth(ArrayList<Vertex> what, ArrayList<Vertex> to, double width){}
    private void filterThickness(ArrayList<Vertex> what, ArrayList<Vertex> to, double thickness){}
    private void filterHeight(ArrayList<Vertex> what, ArrayList<Vertex> to, double height){}
    private void filterSize(ArrayList<Vertex> what, ArrayList<Vertex> to, double size){}
    private void filterID(ArrayList<Vertex> what, ArrayList<Vertex> to, int id){}
    private void filterShape(ArrayList<Vertex> what, ArrayList<Vertex> to, String shape){}
    private void filterEdgeType(ArrayList<Vertex> what, ArrayList<Vertex> to, Edge.EdgeType edgeType){}
    private void filterWeight(ArrayList<Vertex> what, ArrayList<Vertex> to, double weight){}




    private void filterVertices(ArrayList<Vertex> what, ArrayList<Vertex> to, ArrayList<String> parameters) {

        for (int i = 0; i < parameters.size(); i += 2){
            switch (parameters.get(i)){
                case "STROKE": case "COLOR":
                    String strokeColor = parameters.get(i+1);
                    if (GralogColor.isColor(strokeColor))
                        filterStroke(what,to, strokeColor);
                    else{
                        errorMsg = "\"(stroke) color\" must be a color.\n";
                        return;
                    }
                    break;

                case "FILL":
                    String fillColor = parameters.get(i+1);
                    if (GralogColor.isColor(fillColor))
                        filterFill(what,to, fillColor);
                    else{
                        errorMsg = "\"stroke\" must be a color.\n";
                        return;
                    }
                    break;
                case "WIDTH":
                    if (parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")){
                        errorMsg = "\"width\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double width;
                    try {width = Double.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"width\".\n"; return;}
                    filterWidth(what,to, width);
                    break;
                case "NOCONDITION": to = new ArrayList<Vertex>(what) ; break;
                case "THICKNESS":
                    if (parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")){
                        errorMsg = "\"thickness\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double thickness;
                    try {thickness = Double.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"thickness\".\n"; return;}
                    filterThickness(what,to, thickness);
                    break;
                case "HEIGHT":
                    if (parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")){
                        errorMsg = "\"height\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double height;
                    try {height = Double.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"height\".\n"; return;}
                    filterHeight(what,to, height);
                    break;
                case "SIZE":
                    if (parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")){
                        errorMsg = "\"size\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double size;
                    try {size = Double.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"size\".\n"; return;}
                    filterSize(what,to, size);
                    break;
                case "ID":
                    if (parameters.get(i+1).matches("\\d+")){
                        errorMsg = "\"id\" must be a number. Format: [0-9]+\n";
                        return;
                    }
                    Integer id;
                    try {id= Integer.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"id\".\n"; return;}
                    filterID(what,to, id);
                    break;
                case "SHAPE":
                    String shape = parameters.get(i+1);
                    if (!RenderingShape.isShape(shape)){
                        errorMsg = "Could not recognise the value for \"shape\".\n"; return;
                    }
                    filterShape(what,to,shape);
                    break;
                case "WEIGHT":
                    if (parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")){
                        errorMsg = "\"weight\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double weight;
                    try {weight = Double.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"weight\".\n"; return;}
                    filterWeight(what,to, weight);
                    break;
                case "EDGETYPE":
                    String edgeType= parameters.get(i+1);
                    if (Edge.isEdgeType(edgeType)){
                        errorMsg = "Could not recognise the value for \"shape\".\n"; return;
                    }
                    filterEdgeType(what,to,edgeType);
                    break;

            }


            }

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
