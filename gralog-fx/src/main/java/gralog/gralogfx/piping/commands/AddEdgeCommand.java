package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.EdgeException;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;


public class AddEdgeCommand extends CommandForGralogToExecute {
	

	int sourceId;
    int targetId;
	Vertex sourceVertex;
    Vertex targetVertex;
    int id= -1;
    boolean isDirected;
    // String neighbourString;



	public AddEdgeCommand(String[] externalCommandSegments, Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try {
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

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

        try {
            this.id = Integer.parseInt(externalCommandSegments[4]);
        }catch(Exception e){
            //no id given; no matter
        }
	}


	public void handle() {

        Edge e = this.structure.addEdge(sourceVertex,targetVertex);

        if (e != null) // new selfloops are not added, in this case e==null
            this.setResponse(Integer.toString(e.getId()));
        else
            this.setResponse("");
        return;
	}

}
