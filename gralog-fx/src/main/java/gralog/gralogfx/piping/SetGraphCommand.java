package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import gralog.exportfilter.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import gralog.exportfilter.TrivialGraphFormatExport;


public class SetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    String graphString;
    // String neighbourString;
    Piping piping;


	public SetGraphCommand(String[] externalCommandSegments,Structure structure,Piping piping){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        this.piping= piping;
        

        try{
            this.format = PipingMessageHandler.properGraphFormats(externalCommandSegments[2]);
            this.graphString = (String)PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
        }catch(Exception e){
            this.error = e;
            this.fail();
            return;
        }


        if (this.format == GraphType.Null){
            this.fail();
            this.error = new Exception(this.format.toString() + " ain't no proper graph format");
            return;
        }
	}




	

	public void handle(){

        /* for testing*/
        this.graphString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><graphml><graph edgedefault=\"directed\" type=\"digraph\"><node fillcolor=\"#CCEC35\" id=\"n1\" label=\"\" radius=\"0.7\" strokecolor=\"#000000\" strokewidth=\"0.026458333333333334\" textheight=\"0.4\" x=\"0.0\" y=\"6.0\"/><node fillcolor=\"#CCEC35\" id=\"n2\" label=\"\" radius=\"0.7\" strokecolor=\"#000000\" strokewidth=\"0.026458333333333334\" textheight=\"0.4\" x=\"6.0\" y=\"5.0\"/><edge arrowheadlength=\"0.2\" color=\"#000000\" isdirected=\"false\" label=\"\" source=\"n1\" target=\"n2\" weight=\"1.0\" width=\"0.026458333333333334\"/></graph></graphml>";

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
        	System.out.println("Worked until here"+this);
            try{
                InputStream is = new ByteArrayInputStream(this.graphString.getBytes());
                Structure structureFromXml = Structure.loadFromStream(is);
                this.piping.setStructure(structureFromXml);
                System.out.println("set structure");

            }catch(Exception e){
                this.error =e;
                this.fail();
                return;
            }
            //parse the xml, possibly in a multi-line manner

        }else if (this.format == GraphType.Tgf){
            //parse the tgf, possibly in a multi-line manner
        }else if (this.format == GraphType.Tikz){
            //parse the tikz, possibly in a multi-line manner
        }else{
            this.error = new MessageFormatException("The format " + this.format.toString() + " ain't no proper graph format!");
        }
        

        // int changeId;

        this.setResponse(null);


        return ;



        // return v;
	}

}