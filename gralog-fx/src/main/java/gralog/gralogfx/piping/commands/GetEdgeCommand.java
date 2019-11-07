package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;


public class GetEdgeCommand extends CommandForGralogToExecute {

    Edge edge;
  

	public GetEdgeCommand(String[] externalCommandSegments,Structure structure) {
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

        
        try {
            this.setResponse(PipingMessageHandler.universalEdgeToGralogTuple(this.edge));
        }catch(Exception e) {
            this.fail();
            this.error = e;

        }

        
	}

}
