package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;

public class ExistsEdgeCommand extends CommandForGralogToExecute {
	

	
    Edge anyEdge;
    boolean thereIsAnEdge = true;


	public ExistsEdgeCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try {
            this.anyEdge = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e) {
            this.thereIsAnEdge = false;
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }
        

	}


	public void handle() {

        this.setResponse(Boolean.toString(this.thereIsAnEdge));

        return;


        // return v;
	}

}
