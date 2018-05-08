package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;

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

        if (this.externalCommandSegments.length == 4){
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 3, 4));
        }else{
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 3, 6));
        }
        // this.changeColor = PipingMessageHandler.colorConversion(externalCommandSegments);
        
        this.vertex.strokeColor = changeColor;


        this.setResponse(null);
        return;


        // return v;
	}

}