package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;

public class SetVertexFillColorCommand extends CommandForGralogToExecute {
	

	int changeId;
	Vertex vertex;
	GralogColor changeColor;



	public SetVertexFillColorCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
		try{    
            this.changeId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.vertex = this.structure.getVertexById(this.changeId);

        if (this.vertex == null){
            this.fail();
            this.error = new Exception("error: vertex with id: " + this.changeId + " does not exist");
            return;
        }
	}

	public void handle(){

        // int changeId;
       
        
        
        if (this.externalCommandSegments.length == 4){
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 3, 4));
        }else{
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 3, 6));
        }
        
        this.vertex.fillColor = changeColor;


        this.setResponse(null);
        return;


        // return v;
	}

}