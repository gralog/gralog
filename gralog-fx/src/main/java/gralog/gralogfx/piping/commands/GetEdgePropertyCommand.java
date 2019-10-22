package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;


public class GetEdgePropertyCommand extends CommandForGralogToExecute {

    Edge edge;
	String propertyString;
  

	public GetEdgePropertyCommand(String[] externalCommandSegments,Structure structure) {
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

        //extract the property to be searched for, if it doesn't exist, terminate program
        try {

            this.propertyString = externalCommandSegments[3];
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }

	}

	public void handle() {

        // int changeId;

        boolean found = false;
        Class<?> c = edge.getClass();
        for (Field f : c.getFields()) {
            if (f.getName().equals(this.propertyString)) {
                try {
                    this.setResponse(PipingMessageHandler.universalEdgeToGralogTuple(this.edge));
                }catch(Exception e) {
                    this.fail();
                    this.error = e;

                }
                return;
            }
        }
        this.fail();
        this.error = new Exception("class Edge does not have property : " + this.propertyString);
        return;



        // return v;
	}

}
