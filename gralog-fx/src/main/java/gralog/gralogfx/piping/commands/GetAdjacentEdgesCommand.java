package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;



public class GetAdjacentEdgesCommand extends CommandForGralogToExecute {
	


	Vertex sourceVertex;

    Vertex targetVertex;
    // String neighbourString;



	public GetAdjacentEdgesCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {    
            Edge e = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
            this.sourceVertex = e.getSource();
            this.targetVertex = e.getTarget();
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }
       
	}


	

	public void handle() {

        // int changeId;
       
        
        

        Set<Edge> neighbouringEdges = this.sourceVertex.getIncidentEdges();
        neighbouringEdges.addAll(this.targetVertex.getIncidentEdges());


        String edgeString = "";
        for (Edge e : neighbouringEdges){
            edgeString = edgeString + PipingMessageHandler.universalEdgeToTuple(e)+ "#";
        }
        if (edgeString.length() > 0 && null != edgeString) {
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }


        this.setResponse(edgeString);

        return;


        // return v;
	}

}
