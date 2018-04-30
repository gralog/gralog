
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

import java.util.function.*;

import java.util.Arrays;

import java.util.HashMap;

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

    private Function<String,StructurePane> newGraphMethod;


    private enum State{
        Null,
        Inintialized,
        InProgress,
        Paused
    }

    

    // private PipingMessageHandler eventHandler = 

    State state = State.Null;


            //todo: hashmap<id,structure>

    private HashMap<Integer,Structure> idGraphMap;

    public boolean externalProcessInit(String fileName,String initMessage){


        idGraphMap = new HashMap<Integer,Structure>();
            
        System.out.println("external yo");
        String line;
        String[] execStr = {fileName,initMessage};
        try{
            this.external = Runtime.getRuntime().exec(execStr); //e.g. formatRequest
            this.in = new BufferedReader(new InputStreamReader(external.getInputStream()));
            this.out = new PrintStream(external.getOutputStream(),true);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        System.out.println("execd and shit");
        this.state=State.Inintialized;
        return true;

        
        // return "error smorgesbord";

        // if ((line = this.in.readLine()) != null){//assuming there is a valid line to be had
        //     // System.out.println("in while");
        //     // System.out.println("churnin");
        //     // return "error smorgesbord";
        //     if (line.length() > 0){ // if not a bogus line
        //         //handleLine()
                
        //         if (line.equals("useCurrentGraph")){ //user input simulation
        //             System.out.println("wants to use graph that is open");
        //             out.println("ack");
        //             this.state = State.Inintialized;
        //             return "useCurrentGraph";

        //         }
        //         System.out.println("before the grpahtypes");
                
        //         if ((line = PipingMessageHandler.properGraphNames(line)) != null){

        //             this.state = State.Inintialized;
        //             out.println("ack");
        //             return line;
        //         }
        //         in.close();
        //         out.close();
        //         return "error invalidType";
        //         // System.out.println("just heard: " + line);

        //     }

        // }
        // System.out.println("returnin");
        // return "error empty program";


        ///make first python call, get params :
        //new graph : Boolean
        //Automaton, Buechi, Directed, Kripke, Undirected
        //
        // exec(this.external,this.in,this.out,false);


   
    }

    private Structure getStructureWithId(int id){
        return this.idGraphMap.get(id);
    }

    public Object run(Function<String,StructurePane> newGraphMethod) throws
        Exception {
        // String[] commands
            // = {fileName, structure.xmlToString()};
        System.out.println("Gralog says: starting.");
        this.structure = structure;
        this.pane = pane;
        this.newGraphMethod = newGraphMethod;
        // newGraphMethod.apply(("hello world".split(" ")));

        System.out.println("starting exec");
        this.exec(null);
        System.out.println("exec finished");
        return null;
        
    }



  

    public String execWithAck(){
        
        if (this.state != State.Paused){
            return "error: not paused, therefore ack in jest";
        }
        return this.exec("ack");
    }



    private String exec(String firstMessage) {

        System.out.println("exec and state is: " + this.state);
        if (this.state == State.Null){
            return "error: should not being execing as process has not been inintialized";
        }
        
        System.out.println("140");
        String result = "";



        try{
            System.out.println("execing " + firstMessage);

            String line;

            if (!(firstMessage == null || firstMessage.length() == 0)){
                //send ack
                out.println(firstMessage);

            }else{
                System.out.println("null first message");
            }
            // return "bla";

            
            System.out.println("191");
            while ((line = this.in.readLine()) != null){//while python has not yet terminated
                System.out.println("in while");
                
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    this.state = State.InProgress;
                    String[] externalCommandSegments = line.split(" ");

                    CommandForGralogToExecute currentCommand;

                    System.out.println("current line: " + line);
                    
                    if (externalCommandSegments[0].equals("addVertex")){ //user input simulation
                        
                        currentCommand = new AddVertexCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));

                        System.out.println("structure gotten with id: " + Integer.parseInt(externalCommandSegments[1]) + " is: " + currentStructure);


                        // currentCommand.setStructure(currentStructure);

                        System.out.println("and the command thinks its structure is: " + currentCommand.structure.toString());

                        ///to generalize:::


                        System.out.println("received message to add vertex to graph #" + externalCommandSegments[1]);
                        // Vertex toAdd = PipingMessageHandler.handleAddVertex(externalCommandSegments,this.structure);
                        // this.out.println(Integer.toString(toAdd.getId()));
                    }else if (externalCommandSegments[0].equals("deleteVertex")){ //user input simulation
                        System.out.println("received message to delete vertex " + externalCommandSegments[2]);

                        currentCommand = new DeleteVertexCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // currentCommand.setStructure(currentStructure);
                        
                
                        // List<Vertex> vertexList = new ArrayList<Vertex>(this.structure.getVertices());
                        // PipingMessageHandler.handleDeleteVertex(externalCommandSegments,this.structure);

                        this.out.println("ack");


                    }else if (externalCommandSegments[0].equals("pauseUntilSpacePressed")){
                        

                        System.out.println("hello");
                        this.pane.requestRedraw();
                        this.state = State.Paused;
                        break;

                    }else if (externalCommandSegments[0].equals("setVertexFillColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
                        // PipingMessageHandler.handleSetVertexFillColor(externalCommandSegments,this.structure);
                        
                        currentCommand = new SetVertexFillColorCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // currentCommand.setStructure(currentStructure);

                        // this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("setVertexStrokeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
                        // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
                        
                        currentCommand = new SetVertexStrokeColorCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // currentCommand.setStructure(currentStructure);

                        // this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("setVertexRadius")){//format: setColor <vertexId> <newRadius>
                        // PipingMessageHandler.handleSetVertexRadius(externalCommandSegments,this.structure);
                        
                        currentCommand = new SetVertexRadiusCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // currentCommand.setStructure(currentStructure);

                        // this.out.println("ack");
                    }else if (externalCommandSegments[0].equals("getConnectedNeighbours")){//format: setColor <vertexId>
                        // String neighbourString = PipingMessageHandler.handleGetConnectedNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
                            
                        currentCommand = new GetConnectedNeighboursCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));


                        // this.out.println(neighbourString);
                    }else if (externalCommandSegments[0].equals("addEdge")){//format: addEdge <sourceId> <targetId> <directed?>
                        // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
                        
                        currentCommand = new AddEdgeCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));


                        // this.out.println(handleEdgeResponse);
                    }else if (externalCommandSegments[0].equals("deleteEdge")){//format: addEdge <sourceId> <targetId> <directed?>
                        // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
                        // System.out.println("")
                        currentCommand = new DeleteEdgeCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));


                        // this.out.println(handleEdgeResponse);
                    }else if (externalCommandSegments[0].equals("addEdgeLabel")){//format: addEdge <sourceId> <targetId> <directed?>
                        // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
                        // System.out.println("")
                        currentCommand = new AddEdgeLabelCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));


                        // this.out.println(handleEdgeResponse);
                    }else if (externalCommandSegments[0].equals("addVertexLabel")){//format: addEdge <sourceId> <targetId> <directed?>
                        // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
                        // System.out.println("")
                        currentCommand = new AddVertexLabelCommand(externalCommandSegments,getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));


                        // this.out.println(handleEdgeResponse);
                    }else if (externalCommandSegments[0].equals("useCurrentGraph")){ //user input simulation

                        this.pane = this.newGraphMethod.apply(externalCommandSegments[0]);
                        this.state = State.InProgress;
                        System.out.println("about to return my structure with id: " + this.pane.getStructure().getId());


                        idGraphMap.put(this.pane.getStructure().getId(),this.pane.getStructure());

                        // currentCommand = new NewGraphCommand(externalCommandSegments);
                        // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // currentCommand.setStructure(currentStructure);
                        out.println(Integer.toString(this.pane.getStructure().getId()));

                        continue;

                    }else if ((line = PipingMessageHandler.properGraphNames(line)) != null){

                        this.pane = this.newGraphMethod.apply(line);

                        idGraphMap.put(this.pane.getStructure().getId(),this.pane.getStructure());

                        this.state = State.InProgress;
                        System.out.println("about to return my structure with id: " + this.pane.getStructure().getId());
                        out.println(this.pane.getStructure().getId());

                        continue;
                    }


                    else{
                        System.out.println("error: not a recognized command dumbfuck did you not read the documentation");
                        // out.println(this.structure.xmlToString());
                        
                        this.out.println("error: not a recognized command");
                        continue;
                        // this.spacePressed= false;
                    }
                    // System.out.println("just heard: " + line);

                    

                    if (!currentCommand.didFail()){
                        currentCommand.handle();
                        if (! currentCommand.didFail()){
                            this.out.println(currentCommand.getResponse());    
                        }
                        
                    }else{
                        this.out.println(currentCommand.getError().toString());
                        System.out.println(currentCommand.getError().toString());
                    }
                    
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


