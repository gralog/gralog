package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PreGralogException;
import gralog.structure.*;
import gralog.rendering.*;


public class PreGralogErrorCommand extends CommandForGralogToExecute {
	


    // String neighbourString;



	public PreGralogErrorCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;


        this.fail();
        this.error = new PreGralogException("error: <" +externalCommandSegments[2] + ">");

	}


	public void handle() {
        return;
	}

}
