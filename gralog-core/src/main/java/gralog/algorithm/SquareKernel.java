
package gralog.algorithm;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.Status;
import gralog.preferences.Preferences;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AlgorithmDescription(
        name = "Square Kernel for Cluster Editing",
        text = "",
        url = ""
)
public class SquareKernel extends Algorithm {
    @Getter
    private Structure graph;
    private List<Pair<Integer, Integer>> permanentList;
    private List<Pair<Integer, Integer>> forbiddenList;
    private HashMap<Integer, List<Pair<Integer, Integer>>> commonList;
    private HashMap<Integer, List<Pair<Integer, Integer>>> nonCommonList;
    private int vertexesSize;
    private Integer[][] c;
    private Integer[][] n;
    private LinkedList<Edge> removedEdges;
    private HashSet<Edge> addedEdges;

    private HashMap<Integer, Integer> matrixIdToVertexId = new HashMap<>();
    private HashMap<Integer, Integer> vertexIdMatrixIdTo = new HashMap<>();

    //pointers not represented in Java

    private boolean[][] adjacencyMatrix;
    private Status[][] t;
    @Getter
    private int k;

    private boolean isNotSolveAble = false;

    public SquareKernel() {}


    @Override
    public AlgorithmParameters getParameters(Structure structure, Highlights highlights) {
        return new StringAlgorithmParameter("k", "3", "k limits the maximum amount of allowed edits.");
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p, Set<Object> selection,
                      ProgressHandler onprogress) throws Exception {

        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        k = Integer.parseInt(sp.parameter);

        this.graph = s;
        //this.k = 3;

        vertexesSize = graph.getVertices().size();

        c = new Integer[vertexesSize][vertexesSize]; //todo Frage : paper sagt [vertexesSize][(vertexesSize - 1) / 2] exp
        n = new Integer[vertexesSize][vertexesSize];
        t = new Status[vertexesSize][vertexesSize];
        //fill t
        for (var z : t) {
            for (var c : z) {
                c = Status.NORMAL;
            }
        }

        int i = 0;
        Collection<Vertex> vs =  s.getVertices();
        for(Vertex v : vs) {
            matrixIdToVertexId.put(i, v.getId());
            vertexIdMatrixIdTo.put(v.getId(), i);
            i++;
        }

        adjacencyMatrix = generateAdjacencyMatrix();
        permanentList = new LinkedList<>();
        forbiddenList = new LinkedList<>();

        commonList = new HashMap<>();
        nonCommonList = new HashMap<>();

        //initialize Lists
        for (int j = 0; j <= k; j++) {
            commonList.put(j, new LinkedList<>());
            nonCommonList.put(j, new LinkedList<>());
        }

        doKernel();

        return null;
    }

    public boolean isNotSolveAble() {
        return isNotSolveAble;
    }

    public void doKernel() {
        preprocessing();
        if (!isNotSolveAble) {
            algorithm();
        }
    }

    public Structure getGraph() {
        ////log.info("Returning Graph");
        return graph;
    }


    private void algorithm() {
        while ((!forbiddenList.isEmpty() || !permanentList.isEmpty()) && k >= 0) {
            updatePermanentList();
            updateForbiddenList();
            //System.out.println("running");
        }
    }

    private void updatePermanentList() {
        //do stuff for permanent list here
        //log.info("update updatePermanentList");
        if (permanentList.isEmpty()) {
            // //log.info("permanent list empty");
            return;
        }
        Pair<Integer, Integer> pair = permanentList.remove(0);
        int i = pair.getValue0();
        int j = pair.getValue1();
        //corrected
        //getGraph().addEdge(i, j);

        if (graphContainsEdgeWithMatrixId(i, j)) {
            return;
        }

        for (int l = 0; l < vertexesSize; l++) {
            if (l == i || j == l) {
                continue;
            }

            if (isCommon(i, j, l)) {
                addToN(i, l, -1); // N[i,l] := N[i,l] − 1
                addToC(i, l, +1); // C[i,l] := C[i,l] + 1
                addToN(j, l, -1); // N[j,l] := N[j,l] − 1
                addToC(j, l, +1); // C[j,l] := C[j,l] + 1
                continue;
            }
            if (isNeighbour(j, l)) {
                addToN(i, l, -1); // N[i,l] := N[i,l] − 1
                addToC(i, l, +1); // C[i,l] := C[i,l] + 1
                addToN(j, l, +1); // N[j,l] := N[j,l] + 1
                continue;
            }

            //wegen Without loss of generality
            if (isNeighbour(i, l)) {
                addToN(j, l, -1); // N[i,l] := N[i,l] − 1 {i <-> j intended}
                addToC(j, l, +1); // C[i,l] := C[i,l] + 1 {i <-> j intended}
                addToN(i, l, +1); // N[j,l] := N[j,l] + 1 {i <-> j intended}
                continue;
            }

            //no neighbour
            addToN(i, l, +1); // N[i,l] := N[i,l] + 1
            addToN(j, l, +1); // N[j,l] := N[j,l] + 1
        }
        permanentList.addAll(commonList.get(k) == null ? List.of() : commonList.get(k));
        forbiddenList.addAll(nonCommonList.get(k) == null ? List.of() : nonCommonList.get(k));

        graph.addEdge(matrixIdToVertexId.get(i), matrixIdToVertexId.get(j));
        adjacencyMatrix[i][j] = true;
        adjacencyMatrix[j][i] = true;
        k--;

        setT(i, j, Status.PERMANENT);
        //do b
        for (int l = 0; l < vertexesSize; l++) {
            if (l == i || j == l) {
                continue;
            }
            if (getT(i, l) == Status.PERMANENT && getT(j, l) == Status.NORMAL) {
                Pair<Integer, Integer> pairToAdd = Pair.with(j, l);
                if (!(permanentList.contains(pairToAdd) || forbiddenList.contains(pairToAdd))) {
                    permanentList.add(pairToAdd);
                }
            }
            if (getT(i, l) == Status.NORMAL && getT(j, l) == Status.PERMANENT) {
                Pair<Integer, Integer> pairToAdd = Pair.with(i, l);
                if (!(permanentList.contains(pairToAdd) || forbiddenList.contains(pairToAdd))) {
                    permanentList.add(pairToAdd);
                }
            }
        }
    }

    private void addToN(final int i, final int j, final int val) {
        setN(i, j, getN(i, j) + val);
        updateN(i, j, val);
    }

    private void addToC(final int i, final int j, final int val) {
        setC(i, j, getC(i, j) + val);
        updateC(i, j, val);
    }

    private void updateForbiddenList() {
        //do stuff for forbidden list here
        if (forbiddenList.isEmpty()) {
            ////log.info("forbidden list empty");
            return;
        }
        Pair<Integer, Integer> pair = forbiddenList.remove(0);
        int i = pair.getValue0();
        int j = pair.getValue1();

        if (!graphContainsEdgeWithMatrixId(i, j)) {
            return;
        }

        for (int l = 0; l < vertexesSize; l++) {
            if (l == i || j == l) {
                continue;
            }
            if (isCommon(i, j, l)) {
                addToN(i, l, +1); // N[i,l] := N[i,l] − 1 {+ <-> - intended}
                addToC(i, l, -1); // C[i,l] := C[i,l] + 1 {+ <-> - intended}
                addToN(j, l, +1); // N[j,l] := N[j,l] − 1 {+ <-> - intended}
                addToC(j, l, -1); // C[j,l] := C[j,l] + 1 {+ <-> - intended}
                continue;
            }
            if (isNeighbour(j, l)) {
                addToN(i, l, +1); // N[i,l] := N[i,l] − 1 {+ <-> - intended}
                addToC(i, l, -1); // C[i,l] := C[i,l] + 1 {+ <-> - intended}
                addToN(j, l, -1); // N[j,l] := N[j,l] + 1 {+ <-> - intended}
                continue;
            }

            //wegen Without loss of generality
            if (isNeighbour(i, l)) {
                addToN(j, l, +1); // N[i,l] := N[i,l] − 1 {i <-> j intended} {+ <-> - intended}
                addToC(j, l, -1); // C[i,l] := C[i,l] + 1 {i <-> j intended} {+ <-> - intended}
                addToN(i, l, -1); // N[j,l] := N[j,l] + 1 {i <-> j intended} {+ <-> - intended}
                continue;
            }

            //no neighbour
            addToN(i, l, -1); // N[i,l] := N[i,l] + 1 {+ <-> - intended}
            addToN(j, l, -1); // N[j,l] := N[j,l] + 1 {+ <-> - intended}
        }
        permanentList.addAll(commonList.get(k) == null ? List.of() : commonList.get(k));
        forbiddenList.addAll(nonCommonList.get(k) == null ? List.of() : nonCommonList.get(k));

        graph.removeEdge(getEdgeIdByMatrixStartAndEnd(i, j));
        adjacencyMatrix[i][j] = false;
        adjacencyMatrix[j][i] = false;
        k--;

        setT(i, j, Status.FORBIDDEN);
        //do b
        for (int l = 0; l < vertexesSize; l++) {
            if (l == i || j == l) {
                continue;
            }
            if (getT(i, l) == Status.FORBIDDEN && getT(j, l) == Status.NORMAL) {
                Pair<Integer, Integer> pairToAdd = Pair.with(j, l);
                if (!(permanentList.contains(pairToAdd) || forbiddenList.contains(pairToAdd))) {
                    forbiddenList.add(pairToAdd);
                }
            }
            if (getT(i, l) == Status.NORMAL && getT(j, l) == Status.FORBIDDEN) {
                Pair<Integer, Integer> pairToAdd = Pair.with(i, l);
                if (!(permanentList.contains(pairToAdd) || forbiddenList.contains(pairToAdd))) {
                    forbiddenList.add(pairToAdd);
                }
            }
        }
    }

    private void updateC(final Integer i,
                         final Integer l, final Integer change) {
        //update(i, l, c, commonList, permanentList);

        final int size = getC(i, l);

        //log.info("Updating common list for {}, {}", i, l);

        int r = size - change;
        if(r < 0 || r > k){
            return;
        }
        Optional<Pair<Integer, Integer>> pairOptional = Optional.empty();

        pairOptional =
                commonList.get(r)
                        .stream()
                        .filter(pair -> (pair.getValue0() == i && pair.getValue1() == l)
                                || (pair.getValue0() == l && pair.getValue1() == i))
                        .findFirst();



        if (!pairOptional.isPresent()) {
            return; // Element isn't in common any more
        }
        //todo this is done with pointer in c++ possible in

        commonList.get(r).remove(pairOptional.get());
        if (size > k) {
            permanentList.add(pairOptional.get());
        } else {
            commonList.get(size).add(pairOptional.get());
        }

    }

    private void updateN(final Integer i,
                         final Integer l, final Integer change) {
        //update(i, l, n, nonCommonList, forbiddenList);

        int size = getN(i, l);

        ////log.info("Updating non-common list for {}, {}, size = {}", i, l, size);

        int r = size-change;
        if(r < 0 || r > k){
            return;
        }
        Optional<Pair<Integer, Integer>> pairOptional = Optional.empty();

        pairOptional =
                nonCommonList.get(r)
                        .stream()
                        .filter(pair -> (pair.getValue0() == i && pair.getValue1() == l)
                                || (pair.getValue0() == l && pair.getValue1() == i))
                        .findFirst();


        if (!pairOptional.isPresent()) {
            return; // Element isn't in common any more
        }
        //todo this is done with pointer in c++ possible in

        nonCommonList.get(r).remove(pairOptional.get());
        if (size > k) {
            forbiddenList.add(pairOptional.get());
        } else {
            nonCommonList.get(size).add(pairOptional.get());
        }
    }

    private void preprocessing() {
        for (int i = 0; i < vertexesSize; i++) {
            for (int j = i + 1; j < vertexesSize; j++) {
                //////log.info("Knotenpaar: {} {}", i, j);
                List<Integer> common = new LinkedList<>();
                List<Integer> nonCommon = new LinkedList<>();
                getCommonAndNonCommon(i, j, common, nonCommon);
                //verify that Kernel might have Solution after Rule 1.3
                int commonSize = common.size();
                int nonCommonSize = nonCommon.size();

                if (verify1_3(commonSize, nonCommonSize)) {
                    return;
                }

                //add to matrix
                setC(i, j, commonSize);
                setN(i, j, nonCommonSize);

                ////log.info("Common: {} Uncommon: {}", commonSize, nonCommonSize);
                //add to Lists
                Pair<Integer, Integer> pair = Pair.with(i, j);
                if (commonSize > k) {
                    permanentList.add(pair);
                    setT(i, j, Status.PERMANENT);
                } else {
                    commonList.get(commonSize).add(pair);
                }
                if (nonCommonSize > k) {
                    forbiddenList.add(pair);
                    setT(i, j, Status.FORBIDDEN);
                } else {
                    //corrected
                    nonCommonList.get(nonCommonSize).add(pair);
                }
                ////log.info("Knotenpaar: {} {} abgearbeitet", i, j);
            }
        }
    }

    private boolean verify1_3(final int commonSize, final int nonCommonSize) {
        if (commonSize > k && nonCommonSize > k) {
            isNotSolveAble = true;
            ////log.info("Cluster Editing can not have an solution due to Rule 1.3");
            return true;
        }
        return false;
    }

    /**
     * calculates common and non common vertexes for given vertex pair i,j.
     *
     * @param i         vertex i
     * @param j         vertex j
     * @param common    the List where all common Vertexes l (indexMatrix) are in
     * @param nonCommon the List where all non common Vertexes l (indexMatrix) are in
     */
    private void getCommonAndNonCommon(final int i,
                                       final int j,
                                       final List<Integer> common,
                                       final List<Integer> nonCommon) {
        for (int indexMatrix = 0; indexMatrix < vertexesSize; indexMatrix++) {
            if (indexMatrix == i || j == indexMatrix) {
                continue;
            }
            if (isCommon(i, j, indexMatrix)) {
                common.add(indexMatrix);
                ////log.info("Common with: {}", indexMatrix);
            }

            if (isNonCommon(i, j, indexMatrix)) {
                nonCommon.add(indexMatrix);
                ////log.info("Uncommon with: {}", indexMatrix);
            }
        }
    }

    private boolean[][] generateAdjacencyMatrix() {
        boolean[][] adjacencyMatrix = new boolean[vertexesSize][vertexesSize];
        //todo fix performance
        for (Edge edge : (Collection<Edge>)graph.getEdges()) {
            adjacencyMatrix[vertexIdMatrixIdTo.get(edge.getSource().getId())][vertexIdMatrixIdTo.get(edge.getTarget().getId())] = true;
            adjacencyMatrix[vertexIdMatrixIdTo.get(edge.getTarget().getId())][vertexIdMatrixIdTo.get(edge.getSource().getId())] = true;
        }
        return adjacencyMatrix;
    }

    /**
     * Is Vertex Pair i,j common with vertex v?.
     *
     * @param i vertex i
     * @param j vertex j
     * @param l vertex l
     * @return boolean
     */
    private boolean isCommon(final Integer i, final Integer j, final Integer l) {
        return adjacencyMatrix[i][l] && adjacencyMatrix[j][l];
    }

    private boolean isNonCommon(final Integer i, final Integer j, final Integer l) {
        return (adjacencyMatrix[i][l] || adjacencyMatrix[j][l])
                && !(adjacencyMatrix[i][l] && adjacencyMatrix[j][l])
                && i != l
                && j != l;
    }

    private boolean isNeighbour(final Integer i, final Integer l) {
        return adjacencyMatrix[i][l];
    }

    public void setC(final int i, final int j, final int val) {
        if (i < j) {
            c[i][j] = val;
        } else {
            c[j][i] = val;
        }
    }

    public int getC(final int i, final int j) {
        return i < j ? c[i][j] : c[j][i];
    }

    public void setN(final int i, final int j, final int val) {
        if (i < j) {
            n[i][j] = val;
        } else {
            n[j][i] = val;
        }
    }

    public int getN(final int i, final int j) {
        return i < j ? n[i][j] : n[j][i];
    }

    public void setT(final int i, final int j, final Status s) {
        if (i < j) {
            t[i][j] = s;
        } else {
            t[j][i] = s;
        }
    }

    public Status getT(final int i, final int j) {
        return i < j ? t[i][j] : t[j][i];
    }

    public int getEdgeIdByMatrixStartAndEnd(final int i, final int j){
        int mi = matrixIdToVertexId.get(i);
        int mj = matrixIdToVertexId.get(j);
        return graph.getEdgeByVertexIds(mi, mj).getId();
    }

    public boolean graphContainsEdgeWithMatrixId(final int i, final int j){
        int mi = matrixIdToVertexId.get(i);
        int mj = matrixIdToVertexId.get(j);
        return graph.getEdgeByVertexIds(mi, mj) != null;
    }


}
