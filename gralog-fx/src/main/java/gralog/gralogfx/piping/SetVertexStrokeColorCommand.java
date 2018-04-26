package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;

public class SetVertexStrokeColorCommand extends CommandForGralogToExecute {
	

	int changeId;
	Vertex vertex;
    GralogColor changeColor;


	public SetVertexStrokeColorCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure=structure;
		try{    
            this.changeId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }
	}

	// public void 


	public void handle(){

        // int changeId;
       
        
        this.vertex = this.structure.getVertexById(this.changeId);

        if (this.vertex == null){

            this.fail();
        	this.error = new Exception("error: vertex does not exist");
            return;
        }
        this.changeColor = PipingMessageHandler.colorConversion(externalCommandSegments);
        
        this.vertex.strokeColor = changeColor;


        this.setResponse("ack");
        return;


        // return v;
	}

}