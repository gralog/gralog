package gralog.structure;

import java.util.HashMap;

/**
 * Wrap class needed in MainWindow to accept the result of StronglyConnectedComponents
 * for the case distinction.*/

public class VertexToInteger {
    public HashMap<Vertex, Integer> vertexToInteger;

    public VertexToInteger(HashMap<Vertex, Integer> vertexToInteger){
        this.vertexToInteger = vertexToInteger;
    }
}
