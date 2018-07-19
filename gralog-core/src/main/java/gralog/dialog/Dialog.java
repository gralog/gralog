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
            if (v.strokeColor.name().equals(color)){
                to.add(v);
            }
        }
    }

    private void filterFill(ArrayList<Vertex> what, ArrayList<Vertex> to, String color){
        for (Vertex v : what){
            if (v.fillColor.name().equals(color)){
                to.add(v);
            }
        }
    }

    private void filterWidth(ArrayList<Vertex> what, ArrayList<Vertex> to, double width){
        for (Vertex v : what){
            if (v.radius == width) // radius?
                to.add(v);
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

    // checks if parameters are correct: colours are colours, number are numbers and so on
    // returns a HashMap<property,value>
    // empty if "NOCONDITION" is a parameter
    private HashMap<String,String> getParameters(ArrayList<String> parameters){
        HashMap<String, String> propertyValue = new HashMap<>();
        for (int i = 0; i < parameters.size(); i += 2){
            switch (parameters.get(i)){
                case "STROKE": case "COLOR": case "FILL":
                    if (GralogColor.isColor(parameters.get(i+1)))
                        propertyValue.put(parameters.get(i),parameters.get(i+1));
                    else
                        errorMsg = "Warning: " + parameters.get(i+1) + " is not a color; skippping this parameter.\n";
                    break;
                case "WIDTH": case "THICKNESS": case "HEIGHT": case "SIZE": // double
                    if (! parameters.get(i+1).matches("\\d*((\\.|,)\\d+)?")) {
                        errorMsg = "\"width\" must be a number. Format: [0-9](.[0-9]+)?; skipping this parameter.\n";
                        break;
                    }
                    propertyValue.putIfAbsent(parameters.get(i),parameters.get(i+1));
                    break;
                case "ID": case "DEGREE": case "INDEGREE": case "OUTDEGREE":
                    if (parameters.get(i+1).matches("\\d+")) {
                        errorMsg = parameters.get(i) + " must be a number. Format: [0-9]+\n";
                        break;
                    }
                    propertyValue.put(parameters.get(i),parameters.get(i+1));
                    break;
                case "SHAPE":
                    if (!RenderingShape.isShape(parameters.get(i+1))){
                        errorMsg = "Could not recognise the value for \"shape\"; skipping this parameter.\n";
                        break;
                    }
                case "SELFLOOP": case "NOSELFLOOP": case "LABEL": case "NOLABEL":
                    propertyValue.put(parameters.get(i),"");
                case "NOCONDITION":
                    propertyValue.clear();
                    return propertyValue;

            }

        }

    }

    // try to convert a string parameter s with key paramKey to Double
    private Double parseReal(String s, String paramKey){
        Double result = -1.0;
        try {result = Double.valueOf(s);}
        catch (NumberFormatException e){
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
        return result;
    }
}

    // try to convert a string parameter s with key paramKey to Integer
    private Integer parseInt(String s, String paramKey){
        Integer result = -1;
        try {result = Integer.valueOf(s);}
        catch (NumberFormatException e){
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
            return result;
        }
    }

    // what: list to filter from, to: list to add items to
    // the function iterates over parameters, in an iteration extracts the next parameter and filers according to it
    private void filterVertices(ArrayList<Vertex> what, ArrayList<Vertex> to, ArrayList<String> parameters) {
        HashMap<String,String> propertyValue = getParameters(parameters);
        if (propertyValue.isEmpty()) {
            to.addAll(what);
            return;
        }
        for (Vertex v : what){
            boolean filteredOut = false;
            for (String property : propertyValue.keySet()){
                switch (property){
                    case "STROKE": case "COLOR":
                        if (!v.strokeColor.name().equals(propertyValue.get(property)))
                            to.add(v);
                        break;
                    case "FILL":
                        if (v.fillColor.name().equals(propertyValue.get(property)))
                            to.add(v);
                        break;
                    case "WIDTH":
                        if (v.radius == parseReal(propertyValue.get(property),"WIDTH")) { // TODO: check: radius?
                            to.add(v);
                            break;
                        }
                    case "THICKNESS":
                        if (v.strokeWidth == parseReal(propertyValue.get(property),"THICKNESS"))
                            to.add(v);
                        break;

                    case "HEIGHT":
                        if (v.textHeight == parseReal(propertyValue.get(property),"THICKNESS"))
                            to.add(v);
                        break;
                    case "SIZE":
                        if (v.radius == parseReal(propertyValue.get(property),"THICKNESS")) // TODO: check
                            to.add(v);
                        break;
                    case "ID":
                        if (v.id == parseInt(propertyValue.get(property),"ID"))
                            to.add(v);
                        break;
                    case "DEGREE":
                        if (v.getDegree() == parseInt(propertyValue.get(property),"DEGREE"))
                            to.add(v);
                        break;
                    case "INDEGREE":
                        if (v.getInDegree() == parseInt(propertyValue.get(property),"INDEGREE"))
                            to.add(v);
                        break;
                    case "OUTDEGREE":
                        if (v.getOutDegree() == parseInt(propertyValue.get(property),"OUTDEGREE"))
                            to.add(v);
                        break;
                    case "SHAPE":
                        if (v.shape.equals(propertyValue.get(property)))
                            to.add(v);
                        break;
                }

        }


        return;
    }


    // returns list of vertices to save the result of filtering to
    // if the list with key s already exists, vertices are added to it
    private ArrayList<Vertex> getVertexTo(String s){
        if (vertexListS.containsKey(s)) {
            System.out.println("Vertex list already exists.");
            return vertexListS.get(s);
        }
        else{
            vertexListS.put(s,new ArrayList<Vertex>());
            return (vertexListS.get(s));
        }
    }

    private void printVertexListS(){
        for (Map.Entry<String, ArrayList<Vertex>> pair : vertexListS.entrySet()){
            System.out.println(pair.getKey() + " ");
            for (Vertex v :  pair.getValue()){
                System.out.print(v.id + " ");
            }
            System.out.println("");
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
            System.out.print(v.id + " ");
        }
        System.out.println();
    }
    // for debugging only
    private void printEdgeIdList(ArrayList<Edge> list){
        for (Edge v : list){
            System.out.print(v.color + " ");
        }
        System.out.println();
    }



    // the main filtering funciton
    // parameters contains (1) definition of what to filter (2) conditions (3) definitions where to save
    // (1) is: selected/all vertices/edges
    //         or an identifier
    //         if the identifier is not found in the list of already defined identifiers: error message
    // (2) is: see description in gralog-core/.../dialog/actions.txt
    // (3) is: as (1), but if the identifier is not found in the list of already defined identifiers: create one
    public void filter(ArrayList<String> parameters, Structure structure, Highlights highlights) {
        System.out.println(ANSI_RED + "Entering filter. parameters = [" + parameters + "]" + ANSI_RESET);

        if (parameters.get(1).equals("VERTICES") || vertexListS.containsKey(parameters.get(0))){ // vertex list

            // check error
            if (parameters.get(1).equals("VERTICES") && edgeListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as an edge list. Choose another name.\n";
                return;
            }

            // compute to
            ArrayList<Vertex> to = getVertexTo(parameters.get(parameters.size()-1)); // where to filter to
            parameters.remove(parameters.size()-1); // remove name of to

            //compute what
            ArrayList<Vertex> what = new ArrayList<>(); // where to filter from
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Vertex)
                        what.add((Vertex) v);
            if (parameters.get(0).equals("ALL"))
                what = new ArrayList<Vertex>(structure.getVertices());
            if (vertexListS.containsKey(parameters.get(0))) { // list already exists
                System.out.println("Found vertex list " + parameters.get(0));
                what = vertexListS.get(parameters.get(0));
            }

            // remove now unnecessary parameters
            if (parameters.get(1).equals("VERTICES")) {
                parameters.remove(1); // delete "vertices"
                parameters.remove(0); // delete "all" / "selected"
            }
            else
                parameters.remove(0); // delete the identifier of what

            // filter
            filterVertices(what,to,parameters);

            // clean parameters
            parameters.clear();


            // debug only
            System.out.println("Did filterVertices.\nWhat: ");
            printVertexIdList(what);
            System.out.println("\nto: ");
            printVertexIdList(to);
            System.out.println("\n");
            System.out.println("vertexListS: ");
            printVertexListS();
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
            if (edgeListS.containsKey(parameters.get(0)))
                what = edgeListS.get(parameters.get(0));

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
