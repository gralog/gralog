package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.concurrent.ThreadLocalRandom;


public class AddVertexCommand extends CommandForGralogToExecute {
	

	int newVertexId;
	Vertex vertex;

	public AddVertexCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.newVertexId = Integer.parseInt(this.externalCommandSegments[1]);
		this.structure = structure;
	}

	// public void 




	public void handle(){
		//////for later implementation
		// if (!this.stringId.equals(-1)){
		// 	this.vertex = this.structure.createVertex(Integer.parseInt(this.stringId));
		// }
		System.out.println("current vertices: " + this.structure.getVertices());
		this.vertex = this.structure.addVertex();
		System.out.println("and his id is: " + this.vertex.getId());
        this.vertex.setCoordinates(
            ThreadLocalRandom.current().nextInt(0, 10+1),
            ThreadLocalRandom.current().nextInt(0, 10+1)
        );
        this.vertex.fillColor = new GralogColor(204, 236, 53);

        this.newVertexId = this.vertex.getId();
        System.out.println("about to set response");
        this.setResponse(Integer.toString(this.vertex.getId()));

        // return v;
	}

}