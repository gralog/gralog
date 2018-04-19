
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author meike
 * @also felix
 */
package gralog.algorithm;
import java.util.concurrent.ThreadLocalRandom;

import gralog.rendering.*;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;

import java.util.Set;

import java.io.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
// import javafx.scene.control.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
// import gralog.gralog-core.src.main.java.gralog.rendering.*;

@AlgorithmDescription(
    name = "Felix Piping",
    text = "text",
    url = "https://url.com"
)

public class Piping2 extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return null;
    }

    public Object run(Structure structure, AlgorithmParameters params,
        Set<Object> selection, ProgressHandler onprogress) throws
        Exception {
        String[] commands
            = {"/Users/f002nb9/Documents/f002nb9/kroozing/gralog/FelixTest.py", structure.xmlToString()};
        System.out.println("Gralog says: starting.");
        String output = this.exec(commands, structure);
        System.out.println("Finished exec.");

        // Platform.runLater(
        //     () ->{
        //         Label label2 = new Label("Name Runlater:");
        //         TextField textField2 = new TextField ();
        //         HBox hb2 = new HBox();
        //         hb2.getChildren().addAll(label2, textField2);
        //         hb2.setSpacing(10);
        //         // hb2.show();
        //     }
        // );

        // Label label1 = new Label("Name Reg:");
        // TextField textField = new TextField ();
        // HBox hb = new HBox();
        // hb.getChildren().addAll(label1, textField);
        // hb.setSpacing(10);

       


        Platform.runLater(
            () -> {
                System.out.println("alerting result");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("result after exec\n:" + output);
                alert.showAndWait();
            }
        );
        return null;
    }

    private String exec(String[] execStr, Structure structure) {
        Platform.runLater(
            () -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("executing:" + execStr[0]);
                alert.showAndWait();
            }
        );
        System.out.println("92");
        String result = "";






        try{

            String line;
            // String result;
            Process external = Runtime.getRuntime().exec(execStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(external.getInputStream()));
            PrintStream out = new PrintStream(external.getOutputStream(),true);
            
            
            // System.out.println("110");
            while ((line = in.readLine()) != null){//while python has not yet terminated
                // System.out.println("in while");
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    String[] externalCommandSegments = line.split(" ");
                    if (externalCommandSegments[0].equals("addVertex")){ //user input simulation
                        System.out.println("received message to add vertex " + externalCommandSegments[1]);
                        out.println("ack");


                        Vertex v = structure.createVertex();
                        v.coordinates = new Vector2D(
                            ThreadLocalRandom.current().nextInt(0, 10+1),
                            ThreadLocalRandom.current().nextInt(0, 10+1)
                        );
                        v.fillColor = new GralogColor(204, 136, 153);
                

                        structure.addVertex(v);

                    }else if (externalCommandSegments[0].equals("deleteVertex")){ //user input simulation
                        System.out.println("received message to delete vertex " + externalCommandSegments[1]);
                        out.println("ack");

                        
                        
                
                        List<Vertex> vertexList = new ArrayList<Vertex>(structure.getVertices());
                        structure.removeVertex(vertexList.get(Integer.parseInt(externalCommandSegments[1])));

                    }else{
                        out.println(structure.xmlToString());
                    }
                    // System.out.println("just heard: " + line);
                    result = result + line;
                }

            }
            // System.out.println("done with while");
            
            return result;

            


        }catch (Exception e){
            System.out.println(e);
        }
        return "hooray";
    //     try {
    //         String line;
    //         Process p = Runtime.getRuntime().exec(execStr);
    //         BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
    //         BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    //         PrintStream out = new PrintStream(p.getOutputStream());
    //         out.println("Hello!\n");
    //         System.out.println("102");
    //         out.flush();
    //         result = bri.readLine();
    //         System.out.println("104" + result);
    //         out.println("ack\n");
            

    //         System.out.println("108");
    //         out.flush();
    //         while ((line = bri.readLine()) != null) {
    //             result = result + line;
    //             System.out.println("reading up tha pythonzzz" + line);
    //         }
    //         bri.close();
    //         while ((line = bre.readLine()) != null) {
    //             // System.out.println(line);
    //             System.out.println("wubadubdub");
    //         }
    //         bre.close();
    //         p.waitFor();
    //         System.out.println("Done.");
    //     } catch (Exception err) {
    //         System.out.println("nooo");
    //         err.printStackTrace();
    //     }
    //     System.out.println("returing result = : " + result);
    //     return result;
    // }
    }
}