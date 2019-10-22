package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;



public class GetIncomingEdgesCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    // String neighbourString;



	public GetIncomingEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try{
            this.sourceVertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(Exception e){
            this.error = e;
            this.fail();
            return;
        }
	}


	

	public void handle(){

        // int changeId;
       
        
        

        Set<Edge> connectedEdges = this.sourceVertex.getIncomingEdges();

        

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