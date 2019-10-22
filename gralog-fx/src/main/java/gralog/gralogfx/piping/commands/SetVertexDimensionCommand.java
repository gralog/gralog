package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.MessageFormatException;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.lang.reflect.Field;


public class SetVertexDimensionCommand extends CommandForGralogToExecute {
	

	int changeId;
	Vertex vertex;
    double newDimension;
    String dimension;



	public SetVertexDimensionCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        
		try {    
            this.changeId = Integer.parseInt(externalCommandSegments[2]);
        }catch(Exception e) {
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

        try {    
            this.dimension = PipingMessageHandler.extractNthPositionString(externalCommandSegments,4);
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }

        try {    
            this.newDimension = Double.parseDouble(PipingMessageHandler.extractNthPositionString(externalCommandSegments,3));
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }


        if (this.dimension.equals("height")) {
            vertex.shape.setHeight(this.newDimension);
        }else if(this.dimension.equals("width")) {
            vertex.shape.setWidth(this.newDimension);
        }else if (this.dimension.equals("radius")) {
            vertex.shape.setWidth(this.newDimension);
            vertex.shape.setHeight(this.newDimension);
        }else {
            this.error = new MessageFormatException("The dimension " + this.dimension + " is not currently supported by gralog! Soarry!");
            this.fail();
            return;
        }
        this.setResponse(null);
        return;


        // return v;
	}

}
