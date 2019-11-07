package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Iterator;

public class DeleteEdgeBetweenCommand extends CommandForGralogToExecute {
    

    int sourceId;
    int targetId;
    Vertex sourceVertex;
    Vertex targetVertex;

    public DeleteEdgeBetweenCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
        try {    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

        this.externalCommandSegments = externalCommandSegments;

        try {    
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("source vertex with id " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(this.targetId);

        if (this.targetVertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("target vertex with id " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

       

    }


    public void handle() {

        
        Set<Edge> intersection = this.structure.edgesBetweenVertices(this.sourceVertex,this.targetVertex);

        Iterator it = intersection.iterator();
        Edge edgeToDelete = it.hasNext() ? intersection.iterator().next() : null;
        for (Edge e : intersection) {
            if (edgeToDelete.getId() < e.getId()) {
                edgeToDelete = e;
            }
        }
        if (edgeToDelete != null) {
            this.structure.removeEdge(edgeToDelete);    
        }
        
        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
       

        this.setResponse(null);

        return;


        // return v;
    }

}
