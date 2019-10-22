package gralog.gralogfx.piping.commands;
import gralog.structure.*;
import gralog.rendering.*;


public class DeleteVertexCommand extends CommandForGralogToExecute {
	

	int toDeleteVertexId;
	Vertex vertex;



	public DeleteVertexCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        
		try {    
            this.toDeleteVertexId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }
	}

	// public void 



	public void handle() {

        Vertex toDelete = this.structure.getVertexById(this.toDeleteVertexId);


            
        if (toDelete == null) {
            this.setConsoleMessage("tried to delete vertex " + this.toDeleteVertexId + ", which does not exist");
        	// this.error = new NonExistantVertexException("vertex " + this.toDeleteVertexId + " does not exist");
        	// this.fail();
            this.setResponse(null);
            return;
        }


        this.structure.removeVertex(toDelete);

        // return "ack";

        this.setResponse(null);
        return;


        // return v;
	}

}
