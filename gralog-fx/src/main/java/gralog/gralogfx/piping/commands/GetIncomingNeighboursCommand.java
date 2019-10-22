package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;



public class GetIncomingNeighboursCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    // String neighbourString;



	public GetIncomingNeighboursCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try {
            this.sourceVertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }
	}


	

	public void handle() {

        // int changeId;
       
        
        Set<Vertex> neighbours = this.sourceVertex.getIncomingNeighbours();

        String neighbourString = "";
        for (Vertex v : neighbours) {
            neighbourString = neighbourString + Integer.toString(v.getId()) + "#";
        }
        if (neighbourString.length() > 0 && null != neighbourString) {
            neighbourString = neighbourString.substring(0,neighbourString.length()-1);
        }


        this.setResponse(neighbourString);

        return;


        // return v;
	}

}
