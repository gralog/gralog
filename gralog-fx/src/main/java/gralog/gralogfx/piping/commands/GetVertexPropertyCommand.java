package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;


public class GetVertexPropertyCommand extends CommandForGralogToExecute {

    Vertex vertex;
	String propertyString;
  

	public GetVertexPropertyCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

        try {
            this.vertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }

        //extract the property to be searched for, if it doesn't exist, terminate program
        try {

            this.propertyString = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }

	}

	public void handle() {

        // int changeId;

        Class<?> c = this.vertex.getClass();
        for (Field f : c.getFields()) {
            if (f.getName().equals(this.propertyString)) {
                try {
                    this.setResponse(this.vertex.gralogPipify());
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
