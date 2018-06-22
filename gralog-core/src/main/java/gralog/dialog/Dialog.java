package gralog.dialog;

import gralog.rendering.GralogColor;
import gralog.rendering.shapes.RenderingShape;
import gralog.structure.*;

import java.lang.reflect.Array;
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

    private void filterFill(ArrayList<Vertex> what, ArrayList<Vertex> to, String color){
        System.out.println("Strting... what = [" + what + "], to = [" + to + "], color = [" + color + "]");
        for (Vertex v : what){
            if (v.fillColor.name().equals(color)){
                to.add(v);
            }
        }
        System.out.println("Finished: what = [" + what + "], to = [" + to + "], color = [" + color + "]");
    }

    private void filterWidth(ArrayList<Vertex> what, ArrayList<Vertex> to, double width){
        for (Vertex v : what){
            if (v.radius == width){ // radius?
                to.add(v);
            }
        }
    }
    private void filterThickness(ArrayList<Vertex> what, ArrayList<Vertex> to, double thickness){
        for (Vertex v : what){
            if (v.strokeWidth == thickness){
                to.add(v);
            }
        }
    }
    private void filterHeight(ArrayList<Vertex> what, ArrayList<Vertex> to, double height){
        for (Vertex v : what){
            if (v.textHeight == height){ // textheight?
                to.add(v);
            }
        }
    }
    private void filterSize(ArrayList<Vertex> what, ArrayList<Vertex> to, double size){
        for (Vertex v : what){
            if (v.radius == size){ // size?
                to.add(v);
            }
        }
    }
    private void filterID(ArrayList<Vertex> what, ArrayList<Vertex> to, int id){
        for (Vertex v : what){
            if (v.id == id){
                to.add(v);
            }
        }

    }
    private void filterShape(ArrayList<Vertex> what, ArrayList<Vertex> to, String shape){
        for (Vertex v : what){
            if (v.shape.equals(shape)){
                to.add(v);
            }
        }
    }
    private void filterDegree(ArrayList<Vertex> what, ArrayList<Vertex> to, int degree){
        for (Vertex v : what){
            if (v.getDegree() == degree){
                to.add(v);
            }
        }
    }
    private void filterInDegree(ArrayList<Vertex> what, ArrayList<Vertex> to, int indegree){
        for (Vertex v : what){
            if (v.getInDegree() == indegree){
                to.add(v);
            }
        }
    }

    private void filterOutDegree(ArrayList<Vertex> what, ArrayList<Vertex> to, int outdegree){
        for (Vertex v : what){
            if (v.getOutDegree() == outdegree){
                to.add(v);
            }
        }
    }
    private void filterHasSelfloop(ArrayList<Vertex> what, ArrayList<Vertex> to){
        for (Vertex v : what){
            if (v.getNeighbours().contains(v) || v.getOutgoingNeighbours().contains(v)){ // how are selfloops stored?
                to.add(v);
            }
        }
    }
    private void filterHasNoSelfloop(ArrayList<Vertex> what, ArrayList<Vertex> to){
        for (Vertex v : what){
            if (!v.getNeighbours().contains(v) && !v.getOutgoingNeighbours().contains(v)){ // how are selfloops stored?
                to.add(v);
            }
        }
    }
    private void filterHasLabel(ArrayList<Vertex> what, ArrayList<Vertex> to){
        for (Vertex v : what){
            if (v.label.length() != 0){
                to.add(v);
            }
        }
    }
    private void filterHasNoLabel(ArrayList<Vertex> what, ArrayList<Vertex> to){
        for (Vertex v : what){
            if (v.label.length() > 0){
                to.add(v);
            }
        }
    }

    private void filterWeight(ArrayList<Edge> what, ArrayList<Edge> to, double weight){
        for (Edge e : what){
            if (e.weight == weight){
                to.add(e);
            }
        }
    }
    private void filterEdgeType(ArrayList<Edge> what, ArrayList<Edge> to, String edgeType){
        for (Edge e : what){
            if (e.edgeType.name().equals(edgeType)){
                to.add(e);
            }
        }
    }

    private void filterEdgeColor(ArrayList<Edge> what, ArrayList<Edge> to, String color){
        for (Edge e : what){
            if (e.color.equals(color)){
                to.add(e);
            }
        }
    }

    private void filterEdges(ArrayList<Edge> what, ArrayList<Edge> to, ArrayList<String> parameters){
        System.out.println("what = [" + what + "], to = [" + to + "], parameters = [" + parameters + "]");
        for (int i = 0; i < parameters.size(); i += 2) {
            switch (parameters.get(i)) {

                case "WEIGHT":
                    if (parameters.get(i + 1).matches("\\d*((\\.|,)\\d+)?")) {
                        errorMsg = "\"weight\" must be a number. Format: [0-9](.[0-9]+)?\n";
                        return;
                    }
                    Double weight;
                    try {
                        weight = Double.valueOf(parameters.get(i + 1));
                    } catch (NumberFormatException e) {
                        errorMsg = "Could not recognise the value for \"weight\".\n";
                        return;
                    }
                    filterWeight(what, to, weight);
                    break;
                case "EDGETYPE":
                    String edgeType = parameters.get(i + 1);
                    if (Edge.isEdgeType(edgeType)) {
                        errorMsg = "Could not recognise the value for \"shape\".\n";
                        return;
                    }
                    filterEdgeType(what, to, edgeType);
                    break;
                case "COLOR":
                    System.out.println("COLOR!err");
                    String color = parameters.get(i + 1);
                    if (!GralogColor.isColor(color)) {
                        errorMsg = "Could not recognise the value for \"color\".\n";
                        return;
                    }
                    filterEdgeColor(what, to, color);
                    break;


            }
        }
    }

    // what: list to filter from, to: list to add items to
    // the function iterates over parameters, in an iteration extracts the next parameter and filers according to it
    private void filterVertices(ArrayList<Vertex> what, ArrayList<Vertex> to, ArrayList<String> parameters) {
        // debugging
        System.out.println("what = [" + what + "], to = [" + to + "], parameters = [" + parameters + "]");
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
                    System.out.println(ANSI_RED + "filterVertices. Case FILL" + ANSI_RESET);
                    String fillColor = parameters.get(i+1);
                    if (GralogColor.isColor(fillColor)) {
                        System.out.println("Is color, entring filterFill");
                        filterFill(what, to, fillColor);
                    }
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
                case "NOCONDITION":
                    to.addAll(what);
                    return;
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
                case "DEGREE":
                    if (parameters.get(i+1).matches("\\d+")){
                        errorMsg = "\"degree\" must be a number. Format: [0-9]+\n";
                        return;
                    }
                    Integer degree;
                    try {degree= Integer.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"degree\".\n"; return;}
                    filterDegree(what,to, degree);
                    break;
                case "INDEGREE":
                    if (parameters.get(i+1).matches("\\d+")){
                        errorMsg = "\"indegree\" must be a number. Format: [0-9]+\n";
                        return;
                    }
                    Integer indegree;
                    try {indegree= Integer.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"indegree\".\n"; return;}
                    filterInDegree(what,to, indegree);
                    break;
                case "OUTDEGREE":
                    if (parameters.get(i+1).matches("\\d+")){
                        errorMsg = "\"outdegree\" must be a number. Format: [0-9]+\n";
                        return;
                    }
                    Integer outdegree;
                    try {outdegree= Integer.valueOf(parameters.get(i+1));}
                    catch (NumberFormatException e){errorMsg = "Could not recognise the value for \"outdegree\".\n"; return;}
                    filterOutDegree(what,to, outdegree);
                    break;
                case "SELFLOOP":
                    filterHasSelfloop(what,to);
                    break;

                case "NOSELFLOOP":
                    filterHasNoSelfloop(what,to);
                    break;
                case "LABEL":
                    filterHasLabel(what,to);
                    break;

                case "NOLABEL":
                    filterHasNoLabel(what,to);
                    break;


            }


            }

        return;
    }


    // returns list of vertices to save the result of filtering to
    // if the list with key s already exists, vertices are added to it
    private ArrayList<Vertex> getVertexTo(String s){
        if (vertexListS.containsKey(s))
            return vertexListS.get(s);
        else{
            vertexListS.put(s,new ArrayList<Vertex>());
            return (vertexListS.get(s));
        }
    }

    // returns list of edges to save the result of filtering to
    // if the list with key s already exists, edges are added to it
    private ArrayList<Edge> getEdgeTo(String s){
        if (edgeListS.containsKey(s))
            return edgeListS.get(s);
        else{
            edgeListS.put(s,new ArrayList<Edge>());
            return (edgeListS.get(s));
        }
    }

    // for debugging only
    private void printVertexIdList(ArrayList<Vertex> list){
        for (Vertex v : list){
            System.out.println(v.id);
        }
    }
    // for debugging only
    private void printEdgeIdList(ArrayList<Edge> list){
        for (Edge v : list){
            System.out.println(v.color);
        }
    }



    // the main filtering funciton
    public void filter(ArrayList<String> parameters, Structure structure, Highlights highlights) {
        System.out.println(ANSI_RED + "Entering filter. parameters = [" + parameters + "], structure = [" + structure + "], highlights = [" + highlights + "]" + ANSI_RESET);
        if (parameters.get(1).equals("VERTICES") || vertexListS.containsKey(parameters.get(0))){ // vertex list
            if (parameters.get(1).equals("VERTICES") && edgeListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as an edge list. Choose another name.\n";
                return;
            }
            ArrayList<Vertex> to = getVertexTo(parameters.get(parameters.size()-1)); // where to filter to
            parameters.remove(parameters.size()-1);
            ArrayList<Vertex> what = new ArrayList<Vertex>();; // where to filter from
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Vertex)
                        what.add((Vertex) v);
            if (parameters.get(0).equals("ALL"))
                what = new ArrayList<Vertex>(structure.getVertices());
            parameters.remove(1);
            parameters.remove(0);
            filterVertices(what,to,parameters);
            parameters.clear();
            // debug only
            System.out.println("Did filterVertices.\nWhat: ");
            printVertexIdList(what);
            System.out.println("\nto: ");
            printVertexIdList(to);
            System.out.println("\n");
            return;
        }
        if (parameters.get(1).equals("EDGES") || edgeListS.containsKey(parameters.get(0))){ // edge list
            if (parameters.get(1).equals("EDGES") && vertexListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as a vertex list. Choose another name.\n";
                return;
            }
            ArrayList<Edge> to = getEdgeTo(parameters.get(parameters.size()-1));
            parameters.remove(parameters.size()-1);
            ArrayList<Edge> what = new ArrayList<Edge>();
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Edge)
                        what.add((Edge) v);
            if (parameters.get(0).equals("ALL"))
                what = new ArrayList<Edge>(structure.getEdges());
            parameters.remove(1);
            parameters.remove(0);
            filterEdges(what,to,parameters);
            parameters.clear();
            // debug only
            System.out.println("Did filterEdges.\nWhat: ");
            printEdgeIdList(what);
            System.out.println("\nto: ");
            printEdgeIdList(to);
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
