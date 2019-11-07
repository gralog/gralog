package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.Piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import java.util.concurrent.CountDownLatch;



public class RequestEdgeCommand extends CommandForGralogToExecute {

    Edge edge;
    private CountDownLatch waitForSelection;
    private Supplier<Boolean> selectionFunction;
    private Piping piping;
  

	public RequestEdgeCommand(String[] externalCommandSegments,Structure structure,Piping piping) {
		
        this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        this.piping = piping;
        this.waitForSelection = piping.waitForSelection;
        this.selectionFunction = piping.graphObjectSelectionFunction;

	}

	public void handle() {

        this.selectionFunction.get();
        this.piping.state = Piping.State.WaitingForSelection;
        
        try {
            this.piping.redrawMyStructurePanes();
            this.piping.setClassSelectionIsWaitingFor(Edge.class);
            this.waitForSelection.await();
        }catch(Exception e) {
            e.printStackTrace();
        }
        this.piping.state = Piping.State.InProgress;
        Edge e = (Edge)this.piping.getSelectedObject();
        this.setResponse(Integer.toString(e.getId()));


        
        
	}

}
