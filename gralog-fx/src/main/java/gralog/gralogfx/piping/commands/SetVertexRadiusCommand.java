package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;


public class SetVertexRadiusCommand extends CommandForGralogToExecute {
	

	int changeId;
	Vertex vertex;
    int newRadius;



	public SetVertexRadiusCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
		try {    
            this.changeId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }
    }  
	

	public void handle() {

        // int changeId;
       
        
        this.vertex = this.structure.getVertexById(this.changeId);

        if (this.vertex == null) {
            this.fail();

        	this.error = new NonExistantVertexException("vertex does not exist");
            return;
        }

        this.newRadius = Integer.parseInt(externalCommandSegments[3]);



        vertex.radius = this.newRadius;


        this.setResponse(null);
        return;


        // return v;
	}

}
