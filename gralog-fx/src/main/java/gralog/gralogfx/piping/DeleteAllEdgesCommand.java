package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteAllEdgesCommand extends CommandForGralogToExecute {
	
	Vertex sourceVertex;
    Vertex targetVertex;
    String edgeString;

	public DeleteAllEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
        try{
            this.sourceVertex = PipingMessageHandler.extractSourceFromEdge(externalCommandSegments,structure);
            this.targetVertex = PipingMessageHandler.extractTargetFromEdge(externalCommandSegments,structure);
            this.edgeString = PipingMessageHandler.extractNthPositionString(externalCommandSegments,2);
        }catch(NonExistantEdgeException e){
        }catch(Exception e){
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }

       

	}


	public void handle(){

        if (this.sourceVertex == null && this.targetVertex == null){
            this.setResponse(null);
            return;
        }
        if (this.sourceVertex == null || this.targetVertex == null){
            this.error = new NonExistantVertexException("The edge: " + this.edgeString + " exhibits wonky behaviour");
            this.fail();
            return;
        }
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