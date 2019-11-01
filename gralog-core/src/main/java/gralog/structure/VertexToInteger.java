package gralog.structure;

import java.util.HashMap;

/**
 * Wrap class needed in MainWindow to accept the result of StronglyConnectedComponents
 * for the case distinction.*/

public class VertexToInteger {
    public HashMap<Vertex, Integer> vertexToInteger;
    public boolean hasRemovedVertices;

    public VertexToInteger(HashMap<Vertex, Integer> vertexToInteger, boolean hasRemovedVertices){
        this.vertexToInteger = vertexToInteger;
        this.hasRemovedVertices = hasRemovedVertices;
    }

    public VertexToInteger(HashMap<Vertex, Integer> vertexToInteger){
        this.vertexToInteger = vertexToInteger;
        this.hasRemovedVertices = false;
    }

    @Override
    public String toString() {
        String result = "Strongly connected components:\n\nvertex id\t\tcomponent number\n";
        for (Vertex v : vertexToInteger.keySet()){
            result += "\t" + v.getId() + "\t\t\t\t" + vertexToInteger.get(v) + "\n";
        }
        if (hasRemovedVertices)
            result += "\nSELECTED VERTICES WERE REMOVED BEFORE COMPUTING COMPONENTS!\n";
        return result;

    }
}
