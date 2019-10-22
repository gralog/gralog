package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;

public class ExistsVertexCommand extends CommandForGralogToExecute {
	

	
    Vertex anyVertex;
    boolean thereIsAVertex = true;


	public ExistsVertexCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        try {
            this.anyVertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e) {
            this.thereIsAVertex = false;
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }
        

	}


	public void handle() {

        this.setResponse(Boolean.toString(this.thereIsAVertex));

        return;


        // return v;
	}

}
