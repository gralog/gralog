package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;


public class SetEdgeColorCommand extends CommandForGralogToExecute {
	

    Edge edge;
    int edgeId;
	GralogColor changeColor;



	public SetEdgeColorCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        //get source, target vertex id's, terminate program if the number is not a number
    

        try{
            this.edge = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e){
            this.setConsoleMessage("(non-fatal) " + e.toString());
        }catch(Exception e){
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }

        try{
            String color = this.externalCommandSegments[3];
            if (color.substring(0,3).equals("hex")){
                this.changeColor = PipingMessageHandler.colorConversionHex(color.substring(4,color.length()-1));
            }else if(color.substring(0,3).equals("rgb")){

                this.changeColor = PipingMessageHandler.colorConversionRGB(color.substring(4,color.length()-1));
            }
        }catch(Exception e){
            this.error = e;
            this.fail();
            return;
        }
        

        


	}

	public void handle(){

        // int changeId;
        if (this.edge != null){
            this.edge.color = changeColor;
        }
        
        this.setResponse(null);
        return;


        // return v;
	}

}