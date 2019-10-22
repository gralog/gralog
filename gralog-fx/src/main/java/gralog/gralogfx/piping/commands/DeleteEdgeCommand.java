package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.gralogfx.piping.commands.CommandForGralogToExecute;
import gralog.structure.*;
import gralog.rendering.*;

public class DeleteEdgeCommand extends CommandForGralogToExecute {
	

	
    Edge edgeToDelete;


	public DeleteEdgeCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try {
            this.edgeToDelete = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e) {
            this.setConsoleMessage("(non-fatal) " + e.toString());
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }
        

	}


	public void handle() {

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
        if (this.edgeToDelete != null) {
            this.structure.removeEdge(this.edgeToDelete);
        }

        this.setResponse(null);

        return;


        // return v;
	}

}
