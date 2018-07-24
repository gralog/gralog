package gralog.dialog;

import gralog.rendering.GralogColor;
import gralog.rendering.shapes.RenderingShape;
import gralog.structure.*;

import java.util.*;

import static gralog.dialog.DialogParser.ANSI_RED;
import static gralog.dialog.DialogParser.ANSI_RESET;

class ComparatorLEFTRIGHT implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        if (v.coordinates.getX() > w.coordinates.getX()) {
            return 1;
        }
        else {
            if (v.coordinates.getX() < w.coordinates.getX())
                return -1;
            else
                return 0;
        }
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorRIGHTLEFT implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        if (v.coordinates.getX() < w.coordinates.getX()) {
            return 1;
        }
        else {
            if (v.coordinates.getX() > w.coordinates.getX())
                return -1;
            else
                return 0;
        }
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorTOPDOWN implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        if (v.coordinates.getY() > w.coordinates.getY()) {
            return 1;
        }
        else {
            if (v.coordinates.getY() < w.coordinates.getY())
                return -1;
            else
                return 0;
        }
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorBOTTOMUP implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        if (v.coordinates.getY() < w.coordinates.getY()) {
            return 1;
        }
        else {
            if (v.coordinates.getY() > w.coordinates.getY())
                return -1;
            else
                return 0;
        }
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorIDasc implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        return v.id - w.id;
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorIDdesc implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        return w.id - v.id;
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorLabelAsc implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        return v.label.compareTo(w.label);
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

class ComparatorLabelDesc implements Comparator<Vertex>{
    @Override
    public int compare(Vertex v, Vertex w){
        return w.label.compareTo(v.label);
    }
    @Override
    public boolean equals(Object v) {
        return false;
    }
}

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

    public void unionLists (ArrayList source1, ArrayList source2, ArrayList target){
        HashSet<Object> tmp = new HashSet<Object> (source1);
        for (Object x : source2)
            tmp.add(x);
        target.addAll(tmp);
    }

    // note: source1 and source2 are guaranteed to have every element only once
    public void intersectionLists(ArrayList source1, ArrayList source2, ArrayList target){
        HashSet tmp = new HashSet(source1);
        for (Object x : source2)
            if (tmp.contains(x))
                target.add(x);
    }

    public void symmetricDifferenceLists(ArrayList source1, ArrayList source2, ArrayList target){
        HashSet tmp1 = new HashSet(source1);
        HashSet tmp2 = new HashSet(source2);
        for (Object x : source1)
            if (! tmp2.contains(x))
                target.add(x);
        for (Object x : source2)
            if (! tmp1.contains(x))
                target.add(x);
    }

    public void differenceLists(ArrayList source1, ArrayList source2, ArrayList target){
        HashSet tmp = new HashSet(source2);
        for (Object x : source1)
            if (! tmp.contains(x))
                target.add(x);
    }

    public void twoListsOp(ArrayList<String> parameters){
        if (! vertexListS.containsKey(parameters.get(1))){
            errorMsg = parameters.get(1) + " is not a name of a vertex list.\n";
            return;
        }
        if (! vertexListS.containsKey(parameters.get(2))){
            errorMsg = parameters.get(2) + " is not a name of a vertex list.\n";
            return;
        }
        ArrayList<Vertex> targetList = getTargetVertexList(parameters.get(3));
        switch (parameters.get(0)){ // union, intersection, difference, symmetric
            case "UNION":         unionLists(vertexListS.get(parameters.get(1)),vertexListS.get(parameters.get(2)),targetList);
            case "INTERSECTION":  intersectionLists(vertexListS.get(parameters.get(1)),vertexListS.get(parameters.get(2)),targetList);
            case "DIFFERENCE":    differenceLists(vertexListS.get(parameters.get(1)),vertexListS.get(parameters.get(2)),targetList);
            case "SYMMETRIC":     symmetricDifferenceLists(vertexListS.get(parameters.get(1)),vertexListS.get(parameters.get(2)),targetList);
        }

    }

    public void delete(ArrayList<String> parameters){
        if (vertexListS.containsKey(parameters.get(0))){
            vertexListS.remove(parameters.get(0));
            return;
        }
        if (edgeListS.containsKey(parameters.get(0))){
            edgeListS.remove(parameters.get(0));
            return;
        }
        errorMsg = "No such list exists: " + parameters.get(0) + ".\n";
    }

    private void addComplementVertexList(ArrayList<Vertex> sourceList, ArrayList<Vertex> allVertices, ArrayList<Vertex> targetList){
        for (Vertex v : allVertices)
            if (! sourceList.contains(v))
                targetList.add(v);
    }

    private void addComplementEdgeList(ArrayList<Edge> sourceList, ArrayList<Edge> allVertices, ArrayList<Edge> targetList){
        for (Edge v : allVertices)
            if (! sourceList.contains(v))
                targetList.add(v);
    }


    public void complement(ArrayList<String> parameters, Structure structure){
        if (vertexListS.containsKey(parameters.get(0))){
            ArrayList<Vertex> allVertices = new ArrayList<Vertex>(structure.getVertices());
            ArrayList<Vertex> sourceList = vertexListS.get(parameters.get(0));
            ArrayList<Vertex> targetList = getTargetVertexList(parameters.get(1));
            if (! targetList.isEmpty())
                errorMsg = "Note: target list " + parameters.get(1)  + " is not empty!\n";
            addComplementVertexList(sourceList, allVertices, targetList);
            return;
        }
        if (edgeListS.containsKey(parameters.get(0))){
            ArrayList<Edge> allVertices = new ArrayList<Edge>(structure.getEdges());
            ArrayList<Edge> sourceList = edgeListS.get(parameters.get(0));
            ArrayList<Edge> targetList = getTargetEdgeList(parameters.get(1));
            if (! targetList.isEmpty())
                errorMsg = "Note: target list " + parameters.get(1)  + " is not empty!\n";
            addComplementEdgeList(sourceList, allVertices, targetList);
        }
    }
    
    public void sort(ArrayList<String> parameters){
        String listname = parameters.get(0);
        if (! vertexListS.containsKey(listname)){
            errorMsg = "No such list, cannot sort.\n";
            return;
        }
        switch (parameters.get(1)){
            case "LEFTTORIGHT":
                Collections.sort(vertexListS.get(listname),new ComparatorLEFTRIGHT());
            case "RIGHTTOLEFT":
                Collections.sort(vertexListS.get(listname),new ComparatorRIGHTLEFT());
            case "TOPDOWN":
                Collections.sort(vertexListS.get(listname),new ComparatorTOPDOWN());
            case "BOTTOMUP":
                Collections.sort(vertexListS.get(listname),new ComparatorBOTTOMUP());
            case "ID":
                if (parameters.get(2).equals("ASC"))
                    Collections.sort(vertexListS.get(listname),new ComparatorIDasc());
                else
                    Collections.sort(vertexListS.get(listname),new ComparatorIDdesc());
            case "LABEL":
                if (parameters.get(2).equals("ASC"))
                    Collections.sort(vertexListS.get(listname), new ComparatorLabelAsc());
                else
                    Collections.sort(vertexListS.get(listname),new ComparatorLabelDesc());
        }
    }

    private void filterEdges(ArrayList<Edge> sourceList, ArrayList<Edge> targetList, ArrayList<String> parameters){
        LinkedHashMap<String,String> propertyValue = getConditions(parameters);
        if (propertyValue.isEmpty()) {
            targetList.addAll(sourceList);
            return;
        }
        for (Edge e : sourceList){
            if (targetList.contains(e))
                continue;
            boolean filteredOut = false;
            for (String property : propertyValue.keySet()) {
                switch (property) {
                    case "COLOR":
                        if (!e.color.name().equals(propertyValue.get(property)))
                            filteredOut = true;
                        break;
                    case "WEIGHT":
                        if (!(e.weight == parseReal(propertyValue.get(property), "WEIGHT"))) {
                            filteredOut = true;
                            break;
                        }
                    case "EDGETYPE":
                        if (!(e.edgeType.name().equals(parseReal(propertyValue.get(property), "EDGETYPE"))))
                            filteredOut = true;
                        break;
                }
            }
            if (!filteredOut)
                targetList.add(e);
        }
        return;
    }

    // checks if condiitons are correct: colours are colours, number are numbers and so on
    // returns a HashMap<property,value>
    // empty if "NOCONDITION" is a condition
    private LinkedHashMap<String,String> getConditions(ArrayList<String> parameters){
        LinkedHashMap<String, String> propertyValue = new LinkedHashMap<>();
        int i = 0;
        while (i < parameters.size()) {
            switch (parameters.get(i)){
                case "STROKE": case "COLOR": case "FILL":
                    propertyValue.put(parameters.get(i),parameters.get(i+1));
                    i += 2;
                    break;
                case "WIDTH": case "THICKNESS": case "HEIGHT": case "SIZE": case "WEIGHT":// double
                    if (parameters.get(i+1).equals("<")) {
                        propertyValue.putIfAbsent(parameters.get(i) + "<", parameters.get(i + 2));
                        i += 3;
                    }
                    else {
                        if (parameters.get(i+1).equals("<")) {
                            propertyValue.putIfAbsent(parameters.get(i) + ">", parameters.get(i + 2));
                            i += 3;
                        }
                        else {
                            propertyValue.putIfAbsent(parameters.get(i), parameters.get(i + 1));
                            i += 2;
                        }
                    }
                    break;
                case "ID": case "DEGREE": case "INDEGREE": case "OUTDEGREE":
                    if (parameters.get(i+1).equals("<")) {
                        propertyValue.putIfAbsent(parameters.get(i) + "<", parameters.get(i + 2));
                        i += 3;
                    }
                    else {
                        if (parameters.get(i+1).equals("<")) {
                            propertyValue.putIfAbsent(parameters.get(i) + ">", parameters.get(i + 2));
                            i += 3;
                        }
                        else {
                            propertyValue.putIfAbsent(parameters.get(i), parameters.get(i + 1));
                            i += 2;
                        }
                    }
                    break;
                case "SHAPE":
                    propertyValue.putIfAbsent(parameters.get(i),parameters.get(i+1));
                    break;
                case "SELFLOOP": case "NOSELFLOOP": case "LABEL": case "NOLABEL":
                    i += 2;
                    propertyValue.put(parameters.get(i),"");
                    break;
                case "EDGETYPE":
                    propertyValue.put(parameters.get(i),parameters.get(i+1));
                    i += 2;
                    break;
                case "NOCONDITION":
                    propertyValue.clear();
                    return propertyValue;
            }

        }
        return propertyValue;

    }

    // try to convert a string parameter s with key paramKey to Double
    private Double parseReal(String s, String paramKey){
        Double result = -1.0;
        try {result = Double.valueOf(s);}
        catch (NumberFormatException e){
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
            return result;
        }
        return result;
    }

    // try to convert a string parameter s with key paramKey to Integer
    private Integer parseInt(String s, String paramKey){
        Integer result = -1;
        System.out.println("parseInt: s = [" + s + "], paramKey = [" + paramKey + "]");
        try {result = Integer.valueOf(s);}
        catch (NumberFormatException e){
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
            return result;
        }
        return result;
    }

    // what: list to filter from, to: list to add items to (no copies)
    // the function iterates over parameters, in an iteration extracts the next parameter and filers according to it
    private void filterVertices(ArrayList<Vertex> sourceList, ArrayList<Vertex> targetList, ArrayList<String> parameters) {
        LinkedHashMap<String,String> propertyValue = getConditions(parameters);
        if (propertyValue.isEmpty()) {
            targetList.addAll(sourceList);
            return;
        }
        for (Vertex v : sourceList){
            if (targetList.contains(v))
                continue;
            boolean filteredOut = false;
            for (String property : propertyValue.keySet()) {
                switch (property) {
                    case "STROKE":
                    case "COLOR":
                        if (!v.strokeColor.name().equals(propertyValue.get(property)))
                            filteredOut = true;
                        break;
                    case "FILL":
                        if (!v.fillColor.name().equals(propertyValue.get(property)))
                            filteredOut = true;
                        break;
                    case "WIDTH":
                        if (!(v.radius == parseReal(propertyValue.get(property), "WIDTH"))) { // TODO: check: radius?
                            filteredOut = true;
                            break;
                        }
                    case "WIDTH<":
                        if (!(v.radius < parseReal(propertyValue.get(property), "WIDTH"))) { // TODO: check: radius?
                            filteredOut = true;
                            break;
                        }
                    case "WIDTH>":
                        if (!(v.radius > parseReal(propertyValue.get(property), "WIDTH"))) { // TODO: check: radius?
                            filteredOut = true;
                            break;
                        }
                    case "THICKNESS":
                        if (!(v.strokeWidth == parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "THICKNESS<":
                        if (!(v.strokeWidth < parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "THICKNESS>":
                        if (!(v.strokeWidth > parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "HEIGHT":
                        if (!(v.textHeight == parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "HEIGHT<":
                        if (!(v.textHeight < parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "HEIGHT>":
                        if (!(v.textHeight > parseReal(propertyValue.get(property), "THICKNESS")))
                            filteredOut = true;
                        break;
                    case "SIZE":
                        if (!(v.radius == parseReal(propertyValue.get(property), "THICKNESS"))) // TODO: check
                            filteredOut = true;
                        break;
                    case "SIZE<":
                        if (!(v.radius < parseReal(propertyValue.get(property), "THICKNESS"))) // TODO: check
                            filteredOut = true;
                        break;
                    case "SIZE>":
                        if (!(v.radius > parseReal(propertyValue.get(property), "THICKNESS"))) // TODO: check
                            filteredOut = true;
                        break;
                    case "ID":
                        if (!(v.id == parseInt(propertyValue.get(property), "ID")))
                            filteredOut = true;
                        break;
                    case "ID<":
                        if (!(v.id < parseInt(propertyValue.get(property), "ID")))
                            filteredOut = true;
                        break;
                    case "ID>":
                        if (!(v.id > parseInt(propertyValue.get(property), "ID")))
                            filteredOut = true;
                        break;
                    case "DEGREE":
                        if (!(v.getDegree() == parseInt(propertyValue.get(property), "DEGREE")))
                            filteredOut = true;
                        break;
                    case "DEGREE<":
                        if (!(v.getDegree() < parseInt(propertyValue.get(property), "DEGREE")))
                            filteredOut = true;
                        break;
                    case "DEGREE>":
                        if (!(v.getDegree() > parseInt(propertyValue.get(property), "DEGREE")))
                            filteredOut = true;
                        break;
                    case "INDEGREE":
                        if (!(v.getInDegree() == parseInt(propertyValue.get(property), "INDEGREE")))
                            filteredOut = true;
                        break;
                    case "INDEGREE<":
                        if (!(v.getInDegree() < parseInt(propertyValue.get(property), "INDEGREE")))
                            filteredOut = true;
                        break;
                    case "INDEGREE>":
                        if (!(v.getInDegree() > parseInt(propertyValue.get(property), "INDEGREE")))
                            filteredOut = true;
                        break;
                    case "OUTDEGREE":
                        if (!(v.getOutDegree() == parseInt(propertyValue.get(property), "OUTDEGREE")))
                            filteredOut = true;
                        break;
                    case "OUTDEGREE<":
                        if (!(v.getOutDegree() < parseInt(propertyValue.get(property), "OUTDEGREE")))
                            filteredOut = true;
                        break;
                    case "OUTDEGREE>":
                        if (!(v.getOutDegree() > parseInt(propertyValue.get(property), "OUTDEGREE")))
                            filteredOut = true;
                        break;
                    case "SHAPE":
                        if ((!v.shape.equals(propertyValue.get(property))))
                            filteredOut = true;
                        break;
                    case "SELFLOOP":
                        if (!v.getOutgoingNeighbours().contains(v))
                            filteredOut = true;
                        break;
                    case "NOSELFLOOP":
                        if (v.getOutgoingNeighbours().contains(v))
                            filteredOut = true;
                        break;
                    case "LABELEMPTY":
                        if (! v.label.isEmpty())
                            filteredOut = true;
                        break;
                    case "LABELCONTAINS":
                        if (! v.label.contains(propertyValue.get("LABELCONTAINS")))
                            filteredOut = true;
                    case "NOLABEL":
                        if (!v.label.isEmpty())
                            filteredOut = true;
                        break;
                }
            }
            if (!filteredOut)
                targetList.add(v);
        }
        return;
    }


    // returns list of vertices to save the result of filtering to
    // if the list with key s already exists, vertices are added to it (if not already there)
    private ArrayList<Vertex> getTargetVertexList(String s){
        if (vertexListS.containsKey(s)) {
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
    private ArrayList<Edge> getTargetEdgeList(String s){
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

            // compute targetVertexList: if it doesn't exist, create a new one
            ArrayList<Vertex> targetVertexList = getTargetVertexList(parameters.get(parameters.size()-1)); // where to filter to
            parameters.remove(parameters.size()-1); // remove name of targetVertexList

            //compute sourceVertexList
            ArrayList<Vertex> sourceVertexList = new ArrayList<>(); // where to filter from
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Vertex)
                        sourceVertexList.add((Vertex) v);
            if (parameters.get(0).equals("ALL"))
                sourceVertexList = new ArrayList<Vertex>(structure.getVertices());
            if (vertexListS.containsKey(parameters.get(0))) { // list already exists
                sourceVertexList = vertexListS.get(parameters.get(0));
            }

            // remove now unnecessary parameters
            if (parameters.get(1).equals("VERTICES")) {
                parameters.remove(1); // delete "vertices"
                parameters.remove(0); // delete "all" / "selected"
            }
            else
                parameters.remove(0); // delete the identifier of sourceList

            // filter
            filterVertices(sourceVertexList,targetVertexList,parameters);

            // clean parameters
            parameters.clear();


            // debug only
            System.out.println("Did filterVertices.\nsourceList: ");
            printVertexIdList(sourceVertexList);
            System.out.println("\nto: ");
            printVertexIdList(targetVertexList);
            System.out.println("\n");
            System.out.println("vertexListS: ");
            printVertexListS();
            return;
        }
        if (parameters.get(1).equals("EDGES") || edgeListS.containsKey(parameters.get(0))){ // edge list

            // check error
            if (parameters.get(1).equals("EDGES") && vertexListS.containsKey(parameters.get(0))){
                errorMsg = "List " + parameters.get(parameters.size()-1) + " already exists as a vertex list. Choose another name.\n";
                return;
            }

            // compute targetEdgeList: if it doesn't exist, create a new one
            ArrayList<Edge> targetEdgeList = getTargetEdgeList(parameters.get(parameters.size()-1));
            parameters.remove(parameters.size()-1);

            // compute sourceEdgeList
            ArrayList<Edge> sourceEdgeList = new ArrayList<Edge>();
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Edge)
                        sourceEdgeList.add((Edge) v);
            if (parameters.get(0).equals("ALL"))
                sourceEdgeList = new ArrayList<Edge>(structure.getEdges());
            if (edgeListS.containsKey(parameters.get(0)))
                sourceEdgeList = edgeListS.get(parameters.get(0));

            // remove now unnecessary parameters
            if (parameters.get(1).equals("EGDES")) {
                parameters.remove(1);
                parameters.remove(0);
            }
            else
                parameters.remove(0);

            // filter
            filterEdges(sourceEdgeList,targetEdgeList,parameters);

            // clean parameters
            parameters.clear();

            // debug only
            System.out.println("Did filterEdges.\nsourceList: ");
            printEdgeIdList(sourceEdgeList);
            System.out.println("\nto: ");
            printEdgeIdList(targetEdgeList);
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
