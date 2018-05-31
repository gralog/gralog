package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;

public class DeleteVertexCommand extends CommandForGralogToExecute {
	

	int toDeleteVertexId;
	Vertex vertex;



	public DeleteVertexCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure=structure;
		try{    
            this.toDeleteVertexId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }
	}

	// public void 



	public void handle(){

        Vertex toDelete = this.structure.getVertexById(this.toDeleteVertexId);


            
        if (toDelete == null){
        	this.error = new Exception("error: vertex " + this.toDeleteVertexId + " does not exist");
        	this.fail();
            return;
        }

        System.out.println("toDelete: " + toDelete);
        this.structure.removeVertex(toDelete);

        // return "ack";

        this.setResponse(null);
        return;


        // return v;
	}

}