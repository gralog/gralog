package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;



public class GetOutgoingEdgesCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    // String neighbourString;



	public GetOutgoingEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new NonExistantVertexException("error: source vertex does not exist");
            return;
        }
	}


	

	public void handle(){

        // int changeId;
       
        
        

        Set<Edge> connectedEdges = this.sourceVertex.getOutgoingEdges();

        String edgeString = "";
        for (Edge e : connectedEdges){
            edgeString = edgeString + PipingMessageHandler.universalEdgeToTuple(e)+ "#";
        }
        if (edgeString.length() > 0 && null != edgeString){
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }


        this.setResponse(edgeString);

        return;


        // return v;
	}

}