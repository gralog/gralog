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
import gralog.gralogfx.StructurePane;

import gralog.importfilter.TrivialGraphFormatImport;
import gralog.importfilter.GralogTrivialGraphFormatImport;


public class SetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    String graphString;
    StructurePane currentStructurePane;
    int localStructureId;
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
        this.localStructureId = this.piping.getIdWithStructure(this.structure);
        this.currentStructurePane = this.piping.getStructurePaneWithId(this.localStructureId);
	}




	

	public void handle(){

        /* for testing*/
        this.graphString= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><graphml><graph edgedefault=\"directed\" type=\"digraph\"><node fillcolor=\"#CCEC35\" id=\"n1\" label=\"\" radius=\"0.7\" strokecolor=\"#000000\" strokewidth=\"0.026458333333333334\" textheight=\"0.4\" x=\"1.0\" y=\"2.0\"/><node fillcolor=\"#CCEC35\" id=\"n2\" label=\"\" radius=\"0.7\" strokecolor=\"#000000\" strokewidth=\"0.026458333333333334\" textheight=\"0.4\" x=\"2.0\" y=\"0.0\"/><node fillcolor=\"#CCEC35\" id=\"n3\" label=\"\" radius=\"0.7\" strokecolor=\"#000000\" strokewidth=\"0.026458333333333334\" textheight=\"0.4\" x=\"9.0\" y=\"6.0\"/><edge arrowheadlength=\"0.2\" color=\"#000000\" isdirected=\"false\" label=\"\" source=\"n2\" target=\"n3\" weight=\"1.0\" width=\"0.026458333333333334\"/><edge arrowheadlength=\"0.2\" color=\"#000000\" isdirected=\"false\" label=\"\" source=\"n1\" target=\"n2\" weight=\"1.0\" width=\"0.026458333333333334\"/><edge arrowheadlength=\"0.2\" color=\"#000000\" isdirected=\"false\" label=\"\" source=\"n1\" target=\"n3\" weight=\"1.0\" width=\"0.026458333333333334\"/></graph></graphml>";

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
                this.piping.pairLocalIdAndStructure(localStructureId,structureFromXml);
                this.currentStructurePane.setStructure(structureFromXml);
                System.out.println("setted the structure");

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
        }else if (this.format == GraphType.GTgf){
            String totalGraph = "";
            String firstLine;
            try{
                firstLine = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
            }catch(Exception e){
                doFail(e);
                return;
            }
            if (!firstLine.equals("$$")){
                doFail(new MessageFormatException("no multiline syntax!"));
            }
            String line = "";
            
            try{
                line = this.piping.getNextLine();
                while (!line.equals("$")){
                    System.out.println("Getsing the line: " + line);
                    totalGraph += line + "\n";
                    line = this.piping.getNextLine();
                }
            }catch(Exception e){
                this.doFail(e);
                return;
            }
            InputStream is = new ByteArrayInputStream(totalGraph.getBytes());
            GralogTrivialGraphFormatImport importer = new GralogTrivialGraphFormatImport();
            Structure structureFromGTGF;
            System.out.println("post ness we gots uselvs: " + totalGraph);
            try{
                structureFromGTGF = importer.importGraph(is,null);
            }catch(Exception e){
                this.doFail(e);
                return;
            }
            this.currentStructurePane.setStructure(structureFromGTGF);
            System.out.println("id: " + localStructureId);
            System.out.println("structure: " + structureFromGTGF);
            System.out.println("ol structure: " + this.structure);
            this.piping.pairLocalIdAndStructure(localStructureId,structureFromGTGF);

        }else{
            this.error = new MessageFormatException("The format " + this.format.toString() + " ain't no proper graph format!");
        }
        

        // int changeId;

        this.setResponse(null);


        return ;



        // return v;
	}

    public void doFail(Exception e){
        this.error = e;
        this.fail();
        return;
    }

}