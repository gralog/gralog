/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.dialog;



import gralog.structure.Edge;
import gralog.structure.GraphOperations;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import gralog.utility.StringConverterCollection;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Comparator;

import static gralog.dialog.DialogParser.ANSI_GREEN;
import static gralog.dialog.DialogParser.ANSI_RESET;


public class Dialog {

    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    @Deprecated
    private Map<String, ArrayList<Vertex>> vertexListS;
    @Deprecated
    private Map<String, ArrayList<Edge>> edgeListS;

    private List<GralogList<Vertex>> allCollectionVertex;
    private List<GralogList<Edge>> allCollectionEdge;

    private String errorMsg = "";

    public Dialog(List<GralogList<Vertex>> allCollectionVertex, List<GralogList<Edge>> allCollectionEdge) {
        this();
        this.allCollectionVertex = allCollectionVertex;
        this.allCollectionEdge = allCollectionEdge;

    }

    public Dialog() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();

        vertexListS = new HashMap<>();
        edgeListS = new HashMap<>();

        allCollectionVertex = new ArrayList<GralogList<Vertex>>();
        allCollectionEdge = new ArrayList<GralogList<Edge>>();
    }

    @Deprecated
    public Map<String, ArrayList<Vertex>> getVertexListS() {
        return this.vertexListS;
    }

    @Deprecated
    public Map<String, ArrayList<Edge>> getEdgeListS() {
        return this.edgeListS;
    }

    public void printLists(ArrayList<String> parameters) {
        if (parameters.get(0).equals("PRINTALL")) {
            printAllVertexList();
            printAllEdgeList();
            return;
        }
        if (existsVertexList(parameters.get(0))) {
            printVertexList(parameters.get(0));
            return;
        }
        if (existsEdgeList(parameters.get(0))) {
            printEdgeList(parameters.get(0));
            return;
        }
        errorMsg = "No such list: " + parameters.get(0);
    }

    public void twoListsOp(ArrayList<String> parameters) {
        if (!existsVertexList(parameters.get(1))) {
            errorMsg = parameters.get(1) + " is not a name of a vertex list.\n";
            return;
        }
        if (!existsVertexList(parameters.get(2))) {
            errorMsg = parameters.get(2) + " is not a name of a vertex list.\n";
            return;
        }
        var gralogList = getTargetVertexList(parameters.get(3));
        ArrayList<Vertex> targetList = gralogList;

        var first = findVertexList(parameters.get(1));
        var second = findVertexList(parameters.get(2));

        switch (parameters.get(0)) { // union, intersection, difference, symmetric
            case "UNION":
                GraphOperations.unionLists(first, second, targetList);
                printVertexIdList(first);

                printVertexIdList(targetList);
                break;
            case "INTERSECTION":
                GraphOperations.intersectionLists(first, second, targetList);
                break;
            case "DIFFERENCE":
                GraphOperations.differenceLists(first, second, targetList);
                break;
            case "SYMMETRIC":
                GraphOperations.symmetricDifferenceLists(first, second, targetList);
                break;
        }

    }

    public void delete(ArrayList<String> parameters) {
        if (existsVertexList(parameters.get(0))) {
            allCollectionVertex.remove(findVertexList(parameters.get(0)));
            return;
        }
        if (existsEdgeList(parameters.get(0))) {
            allCollectionEdge.remove(findEdgeList(parameters.get(0)));
            return;
        }
        errorMsg = "No such list exists: " + parameters.get(0) + ".\n";
    }

    public void removeall(ArrayList<String> parameters, Structure structure){
        if (existsVertexList(parameters.get(0))) {
            ArrayList<Vertex> vertices = findVertexList(parameters.get(0));
            for (Vertex vertex : vertices)
                structure.removeVertex(vertex);
            allCollectionVertex.remove(findVertexList(parameters.get(0))); // also remove the list
        }
        if (existsEdgeList(parameters.get(0))) {
            ArrayList<Edge> edges = findEdgeList(parameters.get(0));
            for (Edge edge : edges)
                structure.removeEdge(edge);
            allCollectionEdge.remove(findEdgeList(parameters.get(0))); // also remove the list
            return;
        }
        errorMsg = "No such list exists: " + parameters.get(0) + ".\n";

    }

    private void addComplementVertexList(ArrayList<Vertex> sourceList, ArrayList<Vertex> allVertices, ArrayList<Vertex> targetList) {
        for (Vertex v : allVertices)
            if (!sourceList.contains(v))
                targetList.add(v);
    }

    private void addComplementEdgeList(ArrayList<Edge> sourceList, ArrayList<Edge> allVertices, ArrayList<Edge> targetList) {
        for (Edge v : allVertices)
            if (!sourceList.contains(v))
                targetList.add(v);
    }


    public void complement(ArrayList<String> parameters, Structure structure) {
        if (existsVertexList(parameters.get(0))) {
            ArrayList<Vertex> allVertices = new ArrayList<>(structure.getVertices());
            ArrayList<Vertex> sourceList = findVertexList(parameters.get(0));
            ArrayList<Vertex> targetList = getTargetVertexList(parameters.get(1));
            if (!targetList.isEmpty())
                errorMsg = "Note: target list " + parameters.get(1) + " is not empty!\n";
            addComplementVertexList(sourceList, allVertices, targetList);
            return;
        }
        if (existsEdgeList(parameters.get(0))) {
            ArrayList<Edge> allVertices = new ArrayList<Edge>(structure.getEdges());
            ArrayList<Edge> sourceList = findEdgeList(parameters.get(0));
            ArrayList<Edge> targetList = getTargetEdgeList(parameters.get(1));
            if (!targetList.isEmpty())
                errorMsg = "Note: target list " + parameters.get(1) + " is not empty!\n";
            addComplementEdgeList(sourceList, allVertices, targetList);
        }
    }

    public void sort(ArrayList<String> parameters) {
        String listname = parameters.get(0);
        if (!existsVertexList(listname)) {
            errorMsg = "No such list, cannot sort.\n";
            return;
        }
        var list = findVertexList(listname);
        switch (parameters.get(1)) {
            case "LEFTRIGHT":
                list.sort(new ComparatorLEFTRIGHT());
                break;
            case "RIGHTLEFT":
                list.sort(new ComparatorRIGHTLEFT());
                break;
            case "TOPDOWN":
                list.sort(new ComparatorTOPDOWN());
                break;
            case "BOTTOMUP":
                list.sort(new ComparatorBOTTOMUP());
                break;
            case "ID":
                if (parameters.get(2).equals("ASC"))
                    list.sort(new ComparatorIDasc());
                else
                    list.sort(new ComparatorIDdesc());
                break;
            case "LABEL":
                if (parameters.get(2).equals("ASC"))
                    list.sort(new ComparatorLabelAsc());
                else
                    list.sort(new ComparatorLabelDesc());
                break;
            default:
                errorMsg = parameters.get(1) + " is not a sort order.\n";
                break;
        }
    }

    private void filterEdges(ArrayList<Edge> sourceList, ArrayList<Edge> targetList, ArrayList<String> parameters) {
        LinkedHashMap<String, String> propertyValue = getConditions(parameters);
        if (propertyValue.isEmpty()) {
            targetList.addAll(sourceList);
            return;
        }

        for (Edge e : sourceList) {
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
                        if (!(e.weight == parseReal(propertyValue.get(property), "WEIGHT")))
                            filteredOut = true;
                            break;

                    case "EDGETYPE":
                        if (!(e.edgeType.name().equals(parseReal(propertyValue.get(property),
                            "EDGETYPE"))))
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
    private LinkedHashMap<String, String> getConditions(ArrayList<String> parameters) {
        LinkedHashMap<String, String> propertyValue = new LinkedHashMap<>();
        int i = 0;
        while (i < parameters.size()) {
            switch (parameters.get(i)) {
                case "STROKE":
                case "COLOR":
                case "FILL":
                    propertyValue.put(parameters.get(i), parameters.get(i + 1));
                    i += 2;
                    break;
                case "WIDTH":
                case "THICKNESS":
                case "HEIGHT":
                case "SIZE":
                case "WEIGHT":// double
                    if (parameters.get(i + 1).equals("<")) {
                        propertyValue.putIfAbsent(parameters.get(i) + "<", parameters.get(i + 2));
                        i += 3;
                    } else {
                        if (parameters.get(i + 1).equals("<")) {
                            propertyValue.putIfAbsent(parameters.get(i) + ">", parameters.get(i + 2));
                            i += 3;
                        } else {
                            propertyValue.putIfAbsent(parameters.get(i), parameters.get(i + 1));
                            i += 2;
                        }
                    }
                    break;
                case "ID":
                case "DEGREE":
                case "INDEGREE":
                case "OUTDEGREE":
                    if (parameters.get(i + 1).equals("<")) {
                        propertyValue.putIfAbsent(parameters.get(i) + "<", parameters.get(i + 2));
                        i += 3;
                    } else {
                        if (parameters.get(i + 1).equals(">")) {
                            propertyValue.putIfAbsent(parameters.get(i) + ">", parameters.get(i + 2));
                            i += 3;
                        } else {
                            propertyValue.putIfAbsent(parameters.get(i), parameters.get(i + 1));
                            i += 2;
                        }
                    }
                    break;
                case "SHAPE":
                    propertyValue.putIfAbsent(parameters.get(i), parameters.get(i + 1));
                    break;
                case "SELFLOOP":
                case "NOSELFLOOP":
                case "LABEL":
                case "NOLABEL":
                    propertyValue.put(parameters.get(i), "");
                    i += 1;
                    break;
                case "EDGETYPE":
                    propertyValue.put(parameters.get(i), parameters.get(i + 1));
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
    private Double parseReal(String s, String paramKey) {
        Double result = -1.0;
        try {
            result = Double.valueOf(s);
        } catch (NumberFormatException e) {
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
            return result;
        }
        return result;
    }

    // try to convert a string parameter s with key paramKey to Integer
    private Integer parseInt(String s, String paramKey) {
        Integer result = -1;
        try {
            result = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            errorMsg = "Could not recognise the value for " + paramKey + "; skipping this parameter.\n"; // this shouldn't happen
            return result;
        }
        return result;
    }

    // what: list to filter from, to: list to add items to (no copies)
    // the function iterates over parameters, in an iteration extracts the next parameter and filers according to it
    private void filterVertices(ArrayList<Vertex> sourceList, ArrayList<Vertex> targetList, ArrayList<String> parameters) {
        LinkedHashMap<String, String> propertyValue = getConditions(parameters);
        if (propertyValue.isEmpty()) {
            targetList.addAll(sourceList);
            return;
        }
        for (Vertex v : sourceList) {
            if (targetList.contains(v))
                continue;
            boolean filteredOut = false;
            for (String property : propertyValue.keySet()) {
                switch (property) {
                    case "STROKE": case "COLOR":
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
                        if (!v.label.isEmpty())
                            filteredOut = true;
                        break;
                    case "LABELCONTAINS":
                        if (!v.label.contains(propertyValue.get("LABELCONTAINS")))
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

    public void connectClique(ArrayList<String> parameters, Structure structure) {
        boolean undirected = true;
        try {
            undirected = structure.getDescription().name().equals("Undirected Graph");
        } catch (Exception e) {
            System.err.println("Could not determine the type of graph (undirected or not). Error: "
                    + e.toString());
        }

        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));
        for (Vertex v : list) {
            for (Vertex w : list) {
                if (v.id < w.id) {
                    if (undirected)
                        structure.addEdge(v, w);
                    else {
                        structure.addEdge(v, w);
                        structure.addEdge(w, v);
                    }
                }
            }
        }
    }

    public void connectTClosure(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));
        for (Vertex v : list) {
            for (Vertex w : list) {
                if (v.id < w.id) {
                    structure.addEdge(v, w);
                }
            }
        }
    }


    public void connectSelfloop(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));
        for (Vertex v : list)
            structure.addEdge(v, v);
    }

    public void connectPath(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));
        for (int i = 0; i < list.size() - 1; i++) {
            structure.addEdge(list.get(i), list.get(i + 1));
        }
    }

    public void connectCycle(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));
        for (int i = 0; i < list.size() - 1; i++) {
            structure.addEdge(list.get(i), list.get(i + 1));
        }
        structure.addEdge(list.get(list.size() - 1), list.get(0));
    }

    public void connectBiclique(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list1 = findVertexList(parameters.get(0));

        if (!existsVertexList(parameters.get(1))) {
            errorMsg = "No such vertex list: " + parameters.get(1);
            return;
        }
        ArrayList<Vertex> list2 = findVertexList(parameters.get(1));

        for (Vertex v : list1)
            for (Vertex w : list2)
                structure.addEdge(v, w);
    }

    public void connectMatching(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        if (!existsVertexList(parameters.get(1))) {
            errorMsg = "No such vertex list: " + parameters.get(1);
            return;
        }
        ArrayList<Vertex> list1;
        ArrayList<Vertex> list2;
        list1 = findVertexList(parameters.get(0));
        list2 = findVertexList(parameters.get(1));
        int len = Math.min(list1.size(), list2.size());
        for (int i = 0; i < len; i++) {
            structure.addEdge(list1.get(i), list2.get(i));
        }
    }

    public void connectFormula(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        ArrayList<Vertex> list = findVertexList(parameters.get(0));

        for (int i = 0; i < list.size(); i++) {
            net.objecthunter.exp4j.Expression expression =
                    new net.objecthunter.exp4j.ExpressionBuilder(parameters.get(0))
                            .variable("i")
                            .build()
                            .setVariable("i", i);
            double jDouble = expression.evaluate();
            int j = (int) Math.floor(jDouble);
            if (0 <= j && j < list.size())
                structure.addEdge(list.get(i), list.get(j));
            else {
                errorMsg = errorMsg + "Result of applying formula to "
                        + i + " out of list "
                        + parameters.get(0)
                        + ". Skipping " + i + ".\n";
            }

        }
    }

    // parameters are expected to be:
    // (0) name of the first list
    // (1) name of the second list
    // (2) arithmetic expression depending on i
    public void connect2ListsFormula(ArrayList<String> parameters, Structure structure) {
        if (!existsVertexList(parameters.get(0))) {
            errorMsg = "No such vertex list: " + parameters.get(0);
            return;
        }
        if (!existsVertexList(parameters.get(1))) {
            errorMsg = "No such vertex list: " + parameters.get(1);
            return;
        }
        ArrayList<Vertex> list1 = findVertexList(parameters.get(0));
        ArrayList<Vertex> list2 = findVertexList(parameters.get(1));
        // choose s.t. list1 is shorter than list2

        for (int i = 0; i < list1.size(); i++) {
            net.objecthunter.exp4j.Expression expression =
                    new net.objecthunter.exp4j.ExpressionBuilder(parameters.get(2))
                            .variable("i")
                            .build()
                            .setVariable("i", i);
            double jDouble = expression.evaluate();
            int j = (int) Math.floor(jDouble);
            if (0 <= j && j < list2.size())
                structure.addEdge(list1.get(i), list2.get(j));
            else {
                errorMsg = errorMsg + "Result of applying formula to "
                        + i + " out of list "
                        + parameters.get(1)
                        + ". Skipping " + i + ".\n";
            }

        }
    }


    public boolean existsVertexList(String s) {
        return findVertexList(s) != null;
    }

    public boolean existsEdgeList(String s) {
        return findEdgeList(s) != null;
    }

    public GralogList<Vertex> findVertexList(String s) {
        for (GralogList<Vertex> g : allCollectionVertex) {
            if (g.name.getValue().equalsIgnoreCase(s))
                return g;
        }
        return null;
    }

    public GralogList<Edge> findEdgeList(String s) {
        for (GralogList<Edge> g : allCollectionEdge) {
            if (g.name.getValue().equalsIgnoreCase(s))
                return g;
        }
        return null;
    }

    // returns list of vertices to save the result of filtering to
    // if the list with key s already exists, vertices are added to it (if not already there)
    private GralogList<Vertex> getTargetVertexList(String s) {
        var found = findVertexList(s);
        if (found != null) {
            return found;
        } else {
            GralogList<Vertex> v = new GralogList<>(s.toLowerCase(), StringConverterCollection.VERTEX_ID);
            allCollectionVertex.add(v);
            return v;
        }
    }

    // returns list of edges to save the result of filtering to
    // if the list with key s already exists, edges are added to it
    private GralogList<Edge> getTargetEdgeList(String s) {
        var found = findEdgeList(s);
        if (found != null) {
            return found;
        } else {
            GralogList<Edge> e = new GralogList<>(s.toLowerCase(), StringConverterCollection.EDGE_ID);
            allCollectionEdge.add(e);
            return e;
        }
    }

    private void printVertexList(String listName) {
        GralogList vertexList = findVertexList(listName);
        if (vertexList == null) {
            return;
        }
        System.out.print(ANSI_GREEN + "List " + listName + ": " + ANSI_RESET);
        System.out.println(vertexList.toString());
        System.out.println();
    }

    private void printEdgeList(String listName) {
        GralogList edgeList = findEdgeList(listName);
        if (edgeList == null) {
            System.out.println("List " + listName + " doesn't exist.");
            return;
        }
        System.out.print(ANSI_GREEN + "List " + listName + ": " + ANSI_RESET);
        System.out.println(edgeList.toString());
        System.out.println();
    }


    private void printAllVertexList() {
        for (GralogList g : allCollectionVertex) {
            System.out.println(g.name.getValue() + " : " + g.toString());
        }
    }

    private void printAllEdgeList() {
        for (GralogList g : allCollectionEdge) {
            System.out.println(g.name.getValue() + " : " + g.toString());
        }
    }

    // for debugging only
    private void printVertexIdList(ArrayList<Vertex> list) {
        for (Vertex v : list) {
            System.out.print(v.id + " ");
        }
        System.out.println();
    }

    // for debugging only
    private void printEdgeIdList(ArrayList<Edge> list) {
        for (Edge v : list) {
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

        if (parameters.get(1).equals("VERTICES") || existsVertexList(parameters.get(0))) { // vertex list

            // check error
            if (parameters.get(1).equals("VERTICES") && existsEdgeList(parameters.get(0))) {
                errorMsg = "List " + parameters.get(parameters.size() - 1) + " already exists as an edge list. Choose another name.\n";
                return;
            }

            // compute targetVertexList: if it doesn't exist, create a new one
            var targetVertexList = getTargetVertexList(parameters.get(parameters.size() - 1)); // where to filter to

            parameters.remove(parameters.size() - 1); // remove name of targetVertexList

            //compute sourceVertexList
            ArrayList<Vertex> sourceVertexList = new ArrayList<>(); // where to filter from
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Vertex)
                        sourceVertexList.add((Vertex) v);
            if (parameters.get(0).equals("ALL"))
                sourceVertexList = new ArrayList<>((Collection<? extends Vertex>) structure.getVertices());
            if (existsVertexList(parameters.get(0))) { // list already exists
                sourceVertexList = findVertexList(parameters.get(0));
            }

            // remove now unnecessary parameters
            if (parameters.get(1).equals("VERTICES")) {
                parameters.remove(1); // delete "vertices"
                parameters.remove(0); // delete "all" / "selected"
            } else
                parameters.remove(0); // delete the identifier of sourceList
            // filter
            filterVertices(sourceVertexList, targetVertexList, parameters);

            // clean parameters
            parameters.clear();

            return;
        }
        if (parameters.get(1).equals("EDGES") || existsEdgeList(parameters.get(0))) { // edge list
            // check error
            if (parameters.get(1).equals("EDGES") && existsVertexList(parameters.get(0))) {
                errorMsg = "List " + parameters.get(parameters.size() - 1) + " already exists as a vertex list. Choose another name.\n";
                return;
            }

            // compute targetEdgeList: if it doesn't exist, create a new one
            ArrayList<Edge> targetEdgeList = getTargetEdgeList(parameters.get(parameters.size() - 1));
            parameters.remove(parameters.size() - 1);

            // compute sourceEdgeList
            ArrayList<Edge> sourceEdgeList = new ArrayList<Edge>();
            if (parameters.get(0).equals("SELECTED"))
                for (Object v : highlights.getSelection())
                    if (v instanceof Edge)
                        sourceEdgeList.add((Edge) v);
            if (parameters.get(0).equals("ALL"))
                sourceEdgeList = new ArrayList<Edge>(structure.getEdges());
            if (existsEdgeList(parameters.get(0)))
                sourceEdgeList = findEdgeList(parameters.get(0));

            // remove now unnecessary parameters
            if (parameters.get(1).equals("EDGES")) {
                parameters.remove(1);
                parameters.remove(0);
            } else
                parameters.remove(0);
            // filter
            filterEdges(sourceEdgeList, targetEdgeList, parameters);

            // clean parameters
            parameters.clear();

            return;
        }

        errorMsg = "List " + parameters.get(0) + " not found.\n";
        return;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String s) {
        this.errorMsg = s;
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

class ComparatorLEFTRIGHT implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        if (v.getCoordinates().getX() > w.getCoordinates().getX())
            return 1;
        if (v.getCoordinates().getX() < w.getCoordinates().getX())
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object v) {
        if (v == this) {
            return true;
        }
        return false;
    }
    public int hashCode() {
        return 0;
    }
}

class ComparatorRIGHTLEFT implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        if (v.getCoordinates().getX() < w.getCoordinates().getX()) {
            return 1;
        }
        if (v.getCoordinates().getX() > w.getCoordinates().getX()) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object v) {

        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }
}

class ComparatorTOPDOWN implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        if (v.getCoordinates().getY() > w.getCoordinates().getY())
            return 1;
        if (v.getCoordinates().getY() < w.getCoordinates().getY())
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object v) {
        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}


class ComparatorBOTTOMUP implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        if (v.getCoordinates().getY() < w.getCoordinates().getY())
            return 1;
        if (v.getCoordinates().getY() > w.getCoordinates().getY())
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object v) {

        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}

class ComparatorIDasc implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        return v.id - w.id;
    }

    @Override
    public boolean equals(Object v) {

        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}

class ComparatorIDdesc implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        return w.id - v.id;
    }

    @Override
    public boolean equals(Object v) {
        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}

class ComparatorLabelAsc implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        return v.label.compareTo(w.label);
    }

    @Override
    public boolean equals(Object v) {
        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}

class ComparatorLabelDesc implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v, Vertex w) {
        return w.label.compareTo(v.label);
    }

    @Override
    public boolean equals(Object v) {
        if (v == this)
            return true;
        return false;
    }
    public int hashCode() {
        return 0;
    }

}
