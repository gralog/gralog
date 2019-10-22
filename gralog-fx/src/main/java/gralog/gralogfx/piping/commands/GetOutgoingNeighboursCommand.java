package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;



public class GetOutgoingNeighboursCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    // String neighbourString;



	public GetOutgoingNeighboursCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("source vertex does not exist");
            return;
        }
	}


	

	public void handle() {

        // int changeId;
       
        
        Set<Vertex> neighbours = this.sourceVertex.getOutgoingNeighbours();

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
