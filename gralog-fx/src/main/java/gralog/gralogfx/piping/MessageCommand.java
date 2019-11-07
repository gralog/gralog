package gralog.gralogfx.piping;
import gralog.gralogfx.piping.commands.CommandForGralogToExecute;
import gralog.structure.*;
import gralog.rendering.*;


public class MessageCommand extends CommandForGralogToExecute {
	


    // String neighbourString;



	public MessageCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

	}


	public void handle() {
        return;
	}

}
