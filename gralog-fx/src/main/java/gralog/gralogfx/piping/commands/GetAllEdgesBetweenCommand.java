package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.util.stream.Collectors;

public class GetAllEdgesBetweenCommand extends CommandForGralogToExecute {
	
	Vertex sourceVertex;
    Vertex targetVertex;
    String edgeString;

	public GetAllEdgesBetweenCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
        try {
            this.sourceVertex = PipingMessageHandler.extractSourceFromEdge(externalCommandSegments,structure);
            this.targetVertex = PipingMessageHandler.extractTargetFromEdge(externalCommandSegments,structure);
            this.edgeString = PipingMessageHandler.extractNthPositionString(externalCommandSegments,2);
        }catch(NonExistantEdgeException e) {
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }

       

	}


	public void handle() {

        if (this.sourceVertex == null && this.targetVertex == null) {
            this.setResponse(null);
            return;
        }
        if (this.sourceVertex == null || this.targetVertex == null) {
            this.error = new NonExistantVertexException("The edge: " + this.edgeString + " exhibits wonky behaviour");
            this.fail();
            return;
        }
        Set<Edge> intersection = this.structure.edgesBetweenVertices(this.sourceVertex,this.targetVertex);
        
        String edgeString = "";
        
        for (Edge e : intersection){
            edgeString = edgeString + PipingMessageHandler.universalEdgeToTuple(e)+ "#";
        }
        if (edgeString.length() > 0 && null != edgeString){
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }


        this.setResponse(edgeString);

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
       


        return;


        // return v;
	}

}
