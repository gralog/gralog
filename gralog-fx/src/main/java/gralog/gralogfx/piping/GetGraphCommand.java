package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import gralog.exportfilter.*;

import gralog.exportfilter.TrivialGraphFormatExport;


public class GetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    // String neighbourString;



	public GetGraphCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        System.out.println("init GetGraphCommand");
        
        this.format = PipingMessageHandler.properGraphFormats(externalCommandSegments[2]);
        System.out.println("this.format: " + this.format.toString());

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
            try{
                String xml = this.structure.xmlToString();
                this.setResponse(xml);
            }catch(Exception e){
                this.setResponse(e.toString());
            }
            
            return;
        }

        if (this.format == GraphType.Tgf){
            try{
                //String tgf = TrivialGraphFormatExport.exportToString(this.structure);
                // tgf = PipingPresets.multiLineIfyGraphString(tgf);
                // this.setResponse(tgf);



                ByteArrayOutputStream result = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(result);

                TrivialGraphFormatExport tgfExport = new TrivialGraphFormatExport();
                tgfExport.export(this.structure,out,null);
                out.flush();
                String tgf = result.toString();
                tgf = PipingPresets.multiLineIfyGraphString(tgf);
                this.setResponse(tgf);


            }catch(Exception e){
                this.setResponse(e.toString());
            }
            
            return;
        }

        if (this.format == GraphType.Tikz){
            try{
                String tgf = "";

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(result);

                TikZExport tikzExport = new TikZExport();
                tikzExport.export(this.structure,out,null);
                out.flush();

                String tikz = result.toString();

                tikz = PipingPresets.multiLineIfyGraphString(tikz);
                this.setResponse(tikz);
            }catch(Exception e){
                this.setResponse(e.toString());
            }
            
            return;
        }
        

        // int changeId;
        this.setResponse("User wanted it in format : " + this.format.toString());
    


        return ;



        // return v;
	}

}