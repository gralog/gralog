package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.gralogfx.piping.commands.*;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.concurrent.ThreadLocalRandom;


public class AddVertexCommand extends CommandForGralogToExecute {
	

	int newVertexId = -1;
	Vertex vertex;
	double x;
	double y;

	public AddVertexCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		int lenCommand = externalCommandSegments.length;
		if (lenCommand == 3 || lenCommand == 5) {
			try {
				this.newVertexId =Integer.parseInt(PipingMessageHandler.extractNthPositionString(externalCommandSegments,2));
			}catch(Exception e) {
				this.error = e;
				this.fail();
				return;
			}
		}
		if (lenCommand > 3) {
			try {
				this.x =Double.parseDouble(PipingMessageHandler.extractNthPositionString(externalCommandSegments,lenCommand-2));
				this.y =Double.parseDouble(PipingMessageHandler.extractNthPositionString(externalCommandSegments,lenCommand-1));
			}catch(Exception e) {
				this.error = e;
				this.fail();
				return;
			}
		}else {
			this.x = ThreadLocalRandom.current().nextDouble(0, 10+1);
            this.y = ThreadLocalRandom.current().nextDouble(0, 10+1);
		}
		this.structure = structure;
	}

	// public void 




	public void handle() {
		//////for later implementation
		// if (!this.stringId.equals(-1)) {
		// 	this.vertex = this.structure.createVertex(Integer.parseInt(this.stringId));
		// }

		if (this.newVertexId == -1 || false){
			this.vertex = this.structure.addVertex();
		}else {
			this.vertex = this.structure.addVertex(null,this.newVertexId);
		}

        this.vertex.setCoordinates(
            this.x,
            this.y
        );


        this.newVertexId = this.vertex.getId();

        this.setResponse(Integer.toString(this.vertex.getId()));

        // return v;
	}

}
