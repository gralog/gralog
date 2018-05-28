
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
import gralog.gralogfx.panels.PipingWindow;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.gralogfx.*;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Consumer;

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

    private List<PipingWindow> subscribers = new ArrayList<>();

    private Function<String,StructurePane> newGraphMethod;

    private int skipPausesWithIdGreaterThanOrEqualTo = Integer.MAX_VALUE;
    private int currentSkipValue = Integer.MAX_VALUE;


    private enum State{
        Null,
        Inintialized,
        InProgress,
        Paused
    }

    public void subscribe(PipingWindow sub){
        this.subscribers.add(sub);
    }

    private void aPauseOccured(String[] externalCommandSegments, boolean rankGiven){
        List<String[]> args = PipingMessageHandler.parsePauseVars(externalCommandSegments,rankGiven);
        subscribers.forEach(sub -> sub.notifyPauseRequested(this.structure,args));
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

   
    public Integer extractRankFromPause(String[] externalCommandSegments){
        System.out.println("where the rank would be : " + externalCommandSegments[1]);
        try{
            Integer rank = Integer.parseInt(externalCommandSegments[1]);
            System.out.println("parsd!: " + rank);
            return rank;
        }catch(Exception e){
            return (Integer)null;
        }
    }

    public void skipPressed(){
        this.skipPausesWithIdGreaterThanOrEqualTo = this.currentSkipValue;
        this.exec(Integer.toString(this.currentSkipValue));
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
                    String[] externalCommandSegments = line.split("#");

                    

                    System.out.println("current line: " + line);

                    //if pause, or graph init method, handle here
                    // else{
                    //     CommandForGralogToExecute currentCommand = PipingMessageHandler.handleCommand(externalCommandSegments);
                    // }


                    if (externalCommandSegments[0].equals("pauseUntilSpacePressed")){
                        


                        System.out.println("paused");
                        boolean withRank;

                        Integer rank = this.extractRankFromPause(externalCommandSegments);
                        withRank = (rank != null);
                        if (withRank){
                            rank = 0;
                        }
                        System.out.println("withrank: " + withRank + " rank: " + rank);
                        
                        if (rank < this.skipPausesWithIdGreaterThanOrEqualTo){
                            this.currentSkipValue = rank;
                            this.aPauseOccured(externalCommandSegments,withRank);

                            this.pane.requestRedraw();
                            this.state = State.Paused;
                            break;
                        }else{
                            out.println("skipped");
                            continue;
                        }

                        

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

                    CommandForGralogToExecute currentCommand;
                    try{
                        currentCommand = PipingMessageHandler.handleCommand(externalCommandSegments,this.getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                    }catch(Exception e){
                        currentCommand = new NotRecognizedCommand(externalCommandSegments,(Structure)null);
                    }
                    
                    if (!currentCommand.didFail()){
                        System.out.println("handling");
                        currentCommand.handle();
                        System.out.println("handled");
                        String response;
                        if (! currentCommand.didFail() && (response = currentCommand.getResponse()) != null){
                            System.out.println("no error, response is: |" + response + "|");
                            this.out.println(response);
                        }
                    }

                    if (currentCommand.didFail()){

                        ExceptionBox exbox = new ExceptionBox();
                        exbox.showAndWait(currentCommand.getError());
                    
                        // this.out.println(currentCommand.getError().toString());
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


