package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteAllEdgesCommand extends CommandForGralogToExecute {
	

	int sourceId;
    int targetId;
	Vertex sourceVertex;
    Vertex targetVertex;

	public DeleteAllEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.externalCommandSegments = externalCommandSegments;

        try{    
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new NonExistantVertexException("source vertex with id " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(this.targetId);

        if (this.targetVertex == null){
            this.fail();
            this.error = new NonExistantVertexException("target vertex with id " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

       

	}


	public void handle(){

        
        Set<Edge> intersection = this.structure.edgesBetweenVertices(this.sourceVertex,this.targetVertex);
        System.out.println("intersection: " + intersection);
        System.out.println("target: " + targetVertex);
        System.out.println("inc: " + targetVertex.getIncomingEdges());
        System.out.println("outg: " + sourceVertex.getOutgoingEdges());
        System.out.println("meanwhile all edges: " + this.structure.getEdges());
        

        for (Edge e : intersection){
            this.structure.removeEdge(e);
        }

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
       

        this.setResponse(null);

        return;


        // return v;
	}

}