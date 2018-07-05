package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import gralog.exportfilter.*;


import gralog.exportfilter.TrivialGraphFormatExport;


public class SetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    // String neighbourString;



	public SetGraphCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        
        this.format = PipingMessageHandler.properGraphFormats(externalCommandSegments[2]);


        if (this.format == GraphType.Null){
            this.fail();
            this.error = new Exception("error: " + this.format.toString() + " ain't no proper format");
            
        }
	}




	

	public void handle(){

        /*Gralog messages are in the format:

        $$
        Bla 
        Bla
        bla
        #
        bla
        $


        In other words, if a message is going to have multiple lines, there is 
        a $$ in front of the first line, then a $ at the end before the last line
        */

        /* let it be known that xml comes without whitespace between elements*/


        if (this.format == GraphType.Xml){
            //parse the xml, possibly in a multi-line manner
        }

        if (this.format == GraphType.Tgf){
            //parse the tgf, possibly in a multi-line manner
        }

        if (this.format == GraphType.Tikz){
            //parse the tikz, possibly in a multi-line manner
        }
        

        // int changeId;

        this.setResponse(null);


        return ;



        // return v;
	}

}