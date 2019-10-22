package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;


public class SetVertexFillColorCommand extends CommandForGralogToExecute {
	

	int changeId;
	Vertex vertex;
	GralogColor changeColor;



	public SetVertexFillColorCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
		try {    
            this.changeId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

        this.vertex = this.structure.getVertexById(this.changeId);

        if (this.vertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("vertex with id: " + this.changeId + " does not exist");
            return;
        }

        try {
            String color = this.externalCommandSegments[3];
            if (color.substring(0,3).equals("hex")) {
                this.changeColor = PipingMessageHandler.colorConversionHex(color.substring(4,color.length()-1));
            }else if(color.substring(0,3).equals("rgb")) {
                this.changeColor = PipingMessageHandler.colorConversionRGB(color.substring(4,color.length()-1));
            }
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }
	}

	public void handle() {
        
        this.vertex.fillColor = changeColor;


        this.setResponse(null);
        return;


        // return v;
	}

}
