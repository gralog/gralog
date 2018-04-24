
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author meike
 * @also felix
 */
package gralog.gralogfx;
import java.util.concurrent.ThreadLocalRandom;
import gralog.events.*;
import gralog.rendering.*;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.gralogfx.*;

// import java.Arrays.*;



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
import javafx.scene.control.Button;
// import gralog.gralog-core.src.main.java.gralog.rendering.*;



public class Piping{

    private Process external;
    private BufferedReader in;
    private PrintStream out;
    private Structure structure;
    private StructurePane pane;

    private enum State{
        Null,
        Inintialized,
        InProgress,
        Paused
    }

    

    // private PipingMessageHandler eventHandler = 

    State state = State.Null;



    private <T> Boolean arrayContains(T[] array, T element){

        for (int i = 0; i < array.length; i ++){
            if (array[i].equals(element)){
                return true;
            }
        }
        return false;

    }

    public properGraphNames(String name){

        
        List<String> directed = ["d","directed","Directed"];
        List<String> undirected = ["u","undirected","Undirected"];
        List<String> buchi = ["b","buchi","buechi","b\xc3\xbcchi"];
        List<String> kripke = ["kripke","k"];
        List<String> parity = ["parity","p","Game"];
        List<String> automaton = ["a","automaton"];
        for (String piece : name.split(" ")){


            piece = piece.toLowerCase();
            if (directed.contains(piece)){
                return "Directed Graph";
            }
            if (undirected.contains(piece)){
                return "Undirected Graph";
            }
                
            if (buchi.contains(piece)){
                return "Buechi Automaton";
            }
                
            if (kripke.contains(piece)){
                return "Kripke Structure";
            }
                
            if (parity.contains(piece)){
                return "Parity Game";
            }
                
            if (automaton.contains(piece)){
                return "Automaton";
            }
        }
        return (String)null;
    }
            

    public String externalProcessInit(String fileName,String initMessage){

        try{

            
            System.out.println("external yo");
            String line;
            String[] execStr = {fileName,initMessage};
            this.external = Runtime.getRuntime().exec(execStr); //e.g. formatRequest
            this.in = new BufferedReader(new InputStreamReader(external.getInputStream()));
            this.out = new PrintStream(external.getOutputStream(),true);
            System.out.println("execd and shit");
            
            // return "error smorgesbord";

            if ((line = this.in.readLine()) != null){//assuming there is a valid line to be had
                // System.out.println("in while");
                // System.out.println("churnin");
                // return "error smorgesbord";
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    
                    if (line.equals("useCurrentGraph")){ //user input simulation
                        System.out.println("wants to use graph that is open");
                        out.println("ack");
                        this.state = State.Inintialized;
                        return "useCurrentGraph";

                    }
                    System.out.println("before the grpahtypes");
                    
                    if (properGraphNames(line) != null){

                        this.state = State.Inintialized;
                        return line;
                    }
                    in.close();
                    out.close();
                    return "error invalidType";
                    // System.out.println("just heard: " + line);

                }

            }
            System.out.println("returnin");
            return "error empty program";


            ///make first python call, get params :
            //new graph : Boolean
            //Automaton, Buechi, Directed, Kripke, Undirected
            //
            // exec(this.external,this.in,this.out,false);


        }catch(Exception e){
            e.printStackTrace();
        }
        return "error";

    }

    public Object run(Structure structure, StructurePane pane) throws
        Exception {
        // String[] commands
            // = {fileName, structure.xmlToString()};
        System.out.println("Gralog says: starting.");
        this.structure = structure;
        this.pane = pane;
        // String output = this.init(commands);
        this.exec(false);
        System.out.println("Finished exec.");

      

       


      
        return null;
    }



  

    public String execWithAck(){
        
        if (this.state != State.Paused){
            return "error: not paused, therefore ack in jest";
        }
        return this.exec(true);
    }



    private String exec(Boolean sendAck) {

        System.out.println("exec and state is: " + this.state);
        if (this.state == State.Null){
            return "error: should not being execing as process has not been inintialized";
        }
        
        System.out.println("140");
        String result = "";



        try{
            System.out.println("execing " + sendAck);

            String line;

            if (sendAck){
                //send ack
                out.println("ack");

            }

            
            // System.out.println("110");
            while ((line = this.in.readLine()) != null){//while python has not yet terminated
                System.out.println("in while");
                
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    this.state = State.InProgress;
                    String[] externalCommandSegments = line.split(" ");

                    System.out.println("current line: " + line);
                    
                    if (externalCommandSegments[0].equals("addVertex")){ //user input simulation
                        System.out.println("received message to add vertex " + externalCommandSegments[1]);
                        Vertex toAdd = this.handleAddVertex(externalCommandSegments);
                        this.out.println(Integer.toString(toAdd.getId()));
                    }else if (externalCommandSegments[0].equals("deleteVertex")){ //user input simulation
                        System.out.println("received message to delete vertex " + externalCommandSegments[1]);

                        
                        
                
                        // List<Vertex> vertexList = new ArrayList<Vertex>(this.structure.getVertices());
                        this.handleDeleteVertex(externalCommandSegments);

                        this.out.println("ack");


                    }else if (externalCommandSegments[0].equals("pauseUntilSpacePressed")){
                        
                        System.out.println("hello");
                        this.pane.requestRedraw();
                        this.state = State.Paused;
                        break;

                    }else if (externalCommandSegments[0].equals("setVertexFillColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
                        this.handleSetVertexFillColor(externalCommandSegments);
                        this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("setVertexStrokeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
                        this.handleSetVertexStrokeColor(externalCommandSegments);
                        this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("setVertexRadius")){//format: setColor <vertexId> <newRadius>
                        this.handleSetVertexRadius(externalCommandSegments);
                        this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("getConnectedNeighbours")){//format: setColor <vertexId>
                        String neighbourString = this.handleGetConnectedNeighbours(externalCommandSegments);///get to know yo neighba
                        this.out.println(neighbourString);
                    }else if (externalCommandSegments[0].equals("addEdge")){//format: addEdge <sourceId> <targetId> <directed?>
                        String handleEdgeResponse = this.handleAddEdge(externalCommandSegments);///get to know yo neighba
                        System.out.println("ackers: " + handleEdgeResponse);
                        this.out.println(handleEdgeResponse);
                        System.out.println("fini w add edge");
                        System.out.println(this.structure.getEdges());
                    }else{
                        System.out.println("error: not a recognized command dumbfuck did you not read the documentation");
                        out.println(this.structure.xmlToString());
                        
                        this.out.println("ack");
                        // this.spacePressed= false;
                    }
                    // System.out.println("just heard: " + line);
                    result = result + line;

                }

            }

            if (line == null){
                this.state = State.Null;
                System.out.println("makingue null");
            }else{
                System.out.println("line is not null rather: " + line);
            }


            System.out.println("reqing redraw");
            this.pane.requestRedraw();
            return result;

            


        }catch (Exception e){
            e.printStackTrace();
            return "error: there was an error";
        }
        
   
    }

    public Vertex handleAddVertex(String[] externalCommandSegments){
        Vertex v = this.structure.createVertex();
        v.coordinates = new Vector2D(
            ThreadLocalRandom.current().nextInt(0, 10+1),
            ThreadLocalRandom.current().nextInt(0, 10+1)
        );
        v.fillColor = new GralogColor(204, 136, 153);

        this.structure.addVertex(v);

        return v;

        
    }

    public String handleDeleteVertex(String[] externalCommandSegments){
        int deleteId;
        try{    
            deleteId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex toDelete = structure.getVertexById(deleteId);
            
        if (toDelete == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        System.out.println("toDelete: " + toDelete);
        this.structure.removeVertex(toDelete);

        return "ack";

        
    }

    public GralogColor colorConversion(String[] externalCommandSegments){
        GralogColor changeColor;
        if (externalCommandSegments.length == 3){
            //hex notation
            if (externalCommandSegments[2].length() == 7){
                externalCommandSegments[2] = externalCommandSegments[2].substring(1);
            }
            changeColor = new GralogColor(Integer.parseInt(externalCommandSegments[2],16));
            return changeColor;
        }else if (externalCommandSegments.length == 5){
            int r = Integer.parseInt(externalCommandSegments[2]);
            int g = Integer.parseInt(externalCommandSegments[3]);
            int b = Integer.parseInt(externalCommandSegments[4]);
            return new GralogColor(r,g,b);
        }
        return (GralogColor)null;
        
    }

    public String handleSetVertexFillColor(String[] externalCommandSegments){
        int changeId;
        try{    
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }
        
        Vertex changeVertex = this.structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }
        GralogColor changeColor = colorConversion(externalCommandSegments);
        
        changeVertex.fillColor = changeColor;

        return "ack";
    }

    public String handleSetVertexStrokeColor(String[] externalCommandSegments){
        int changeId;
        try{    
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }
        
        Vertex changeVertex = this.structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }
        GralogColor changeColor = colorConversion(externalCommandSegments);
        
        changeVertex.strokeColor = changeColor;

        return "ack";
    }

    public String handleSetVertexRadius(String[] externalCommandSegments){
        int changeId;
        try{    
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex changeVertex = this.structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        int newRadius = Integer.parseInt(externalCommandSegments[2]);

        changeVertex.radius = newRadius;

        return "ack";
    }

    public String handleGetConnectedNeighbours(String[] externalCommandSegments){
        int sourceId;
        try{    
            sourceId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex sourceVertex = this.structure.getVertexById(sourceId);

        if (sourceVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        Set<Vertex> connectedNeighbours = sourceVertex.getConnectedNeighbours();

        String neighbourString = "";
        for (Vertex v : connectedNeighbours){
            neighbourString = neighbourString + Integer.toString(v.getId()) + " ";
        }
        if (neighbourString.length() > 0 && null != neighbourString){
            neighbourString = neighbourString.substring(0,neighbourString.length()-1);
        }
        return neighbourString;
    }

    public String handleAddEdge(String[] externalCommandSegments){
        System.out.println("in add edge");
        int sourceId;
        try{    
            sourceId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex sourceVertex = this.structure.getVertexById(sourceId);

        int targetId;
        try{    
            targetId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex targetVertex = this.structure.getVertexById(targetId);

        Edge e = this.structure.createEdge(sourceVertex,targetVertex);
        this.structure.addEdge(e);
        // e.setSource(sourceVertex);
        // e.setTarget(targetVertex);
        e.isDirected = (externalCommandSegments[3].equals("true"));

        return "ack";
    }

    
}





/* some potentially useful commented out things:

// Platform.runLater(
//     () -> {
//         Alert alert = new Alert(AlertType.INFORMATION);
//         alert.setTitle("Information Dialog");
//         alert.setHeaderText(null);
//         alert.setContentText("executing:" + execStr[0]);
//         alert.showAndWait();
//     }
// );


#######



  // private String init(String[] execStr){

    //     // String result;
    //     try{

    //         Platform.runLater(
    //             () -> {
    //                 Alert alert = new Alert(AlertType.INFORMATION);
    //                 alert.setTitle("Information Dialog");
    //                 alert.setHeaderText(null);
    //                 alert.setContentText("executing:" + execStr[0]);
    //                 alert.showAndWait();
    //             }
    //         );
    //         this.external = Runtime.getRuntime().exec(execStr);
    //         this.in = new BufferedReader(new InputStreamReader(external.getInputStream()));
    //         this.out = new PrintStream(external.getOutputStream(),true);
    //         exec(this.external,this.in,this.out,false);


    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }

    //     return "hello";

    // }



######




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


*/


