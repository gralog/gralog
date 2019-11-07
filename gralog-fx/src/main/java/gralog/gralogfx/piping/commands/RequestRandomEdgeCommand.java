package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;


public class RequestRandomEdgeCommand extends CommandForGralogToExecute {

    Edge edge;
  

	public RequestRandomEdgeCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

	}

	public void handle() {

        try {
            this.edge = this.structure.getRandomEdge();
            if (this.edge != null) {
                this.setResponse(Integer.toString(this.edge.getId()));
                return;
            }
            this.fail();
            this.error = new NonExistantEdgeException("Unable to pick a random edge because there are no edges in the graph@!");
            this.setResponse(null);
        }catch(Exception e) {
            this.fail();
            this.error = e;
            this.setResponse(null);
        }
        
	}

}
