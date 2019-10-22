package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;


public class GetEdgeIdFromEndpointsCommand extends CommandForGralogToExecute {

    Edge edge;
  

	public GetEdgeIdFromEndpointsCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

        try {
            this.edge = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }


	}

	public void handle() {

        // int changeId;

        this.setResponse(Integer.toString(this.edge.getId()));
	}

}
