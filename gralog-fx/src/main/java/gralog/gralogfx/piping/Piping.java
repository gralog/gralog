
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author meike
 * @also felix
 */
package gralog.gralogfx.piping;
import java.util.concurrent.ThreadLocalRandom;
import gralog.events.*;
import gralog.rendering.*;
import gralog.gralogfx.panels.PipingWindow;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.gralogfx.*;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.*;

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



public class Piping extends Thread{

    private Process external;
    private BufferedReader in;
    private PrintStream out;
    private Structure structure;
    private StructurePane structurePane;

    private List<PipingWindow> subscribers = new ArrayList<>();

    private BiFunction<String,Piping,StructurePane> newGraphMethod;

    private int skipPausesWithRankGreaterThanOrEqualTo = Integer.MAX_VALUE;
    private int currentSkipValue = Integer.MAX_VALUE;
    private Thread t;
    private String firstMessage = null;
    public String[] commandWhichPromptedJoin;
    public List<String[]> trackedVarArgs;

    public CountDownLatch waitForPauseToBeHandled;
    private Lock pauseLock;
    public Condition canContinue;
    public MainWindow caller;
    public Function<Piping,Boolean> pauseFunction;
    private boolean pauseWasPressed = false;


    public enum State{
        Null,
        Inintialized,
        InProgress,
        Paused
    }
    
    public State state = State.Null;

    private HashMap<Integer,Structure> idGraphMap =  new HashMap<Integer,Structure>();
    private HashMap<Integer,StructurePane> idStructurePaneMap =  new HashMap<Integer,StructurePane>();



    public void subscribe(PipingWindow sub){
        this.subscribers.add(sub);
    }

    private void aPauseOccured(String[] externalCommandSegments, boolean rankGiven){
        List<String[]> args = PipingMessageHandler.parsePauseVars(externalCommandSegments,rankGiven);
        trackedVarArgs = args;
        this.pauseFunction.apply(this);
        // subscribers.forEach(sub -> sub.notifyPauseRequested(this.structure,args));
    }

    public boolean isInitialized(){
        return (this.state != State.Null);
    }

    public Piping(BiFunction<String,Piping,StructurePane> newGraphMethod,StructurePane structurePane,CountDownLatch waitForPauseToBeHandled,Function<Piping,Boolean> pauseFunction){
        this.newGraphMethod = newGraphMethod;
        this.resetInitialVals();
        this.structurePane = structurePane;
        this.idGraphMap.put(structurePane.getStructure().getId(),structurePane.getStructure());
        this.idStructurePaneMap.put(structurePane.getStructure().getId(),structurePane);
        this.waitForPauseToBeHandled = waitForPauseToBeHandled;
        this.pauseFunction = pauseFunction;
    }

    

    // private PipingMessageHandler eventHandler = 

   
    public boolean externalProcessInit(String fileName,String initMessage){

        // System.out.println("creating new pipeline, it has pane: " + this.pane);

        
            
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

    private StructurePane getStructurePaneWithId(int id){
        return this.idStructurePaneMap.get(id);
    }

    private void resetInitialVals(){
        this.skipPausesWithRankGreaterThanOrEqualTo = Integer.MAX_VALUE;
        this.currentSkipValue = Integer.MAX_VALUE;
    }

    // public void run(BiFunction<String,Piping,StructurePane> newGraphMethod) throws
    //     Exception {
    //     // String[] commands
    //         // = {fileName, structure.xmlToString()};
    //     System.out.println("Gralog says: starting.");
    //     // this.structure = structure;
    //     // this.pane = pane;
    //     // System.out.println("running, it has pane: " + this.pane);
    //     this.newGraphMethod = newGraphMethod;
    //     this.resetInitialVals();
    //     // newGraphMethod.apply(("hello world".split(" ")));

    //     System.out.println("starting exec");
    //     String exitMessage = this.exec(null);
        
    //     System.out.println("exec finished");
    //     return;
        
    // }

   
    public Integer extractRankFromPause(String[] externalCommandSegments){
        // System.out.println("where the rank would be : " + externalCommandSegments[1]);
        try{
            Integer rank = Integer.parseInt(externalCommandSegments[1]);
            System.out.println("parsd!: " + rank);
            return rank;
        }catch(Exception e){
            return (Integer)null;
        }
    }

    public void pausePressed(){
        this.pauseWasPressed = true;
    }

    public void skipPressed(){
        this.skipPausesWithRankGreaterThanOrEqualTo = this.currentSkipValue;
    }
  

    // public String execWithAck(){
        
    //     if (this.state != State.Paused){
    //         return "error: not paused, therefore ack in jest";
    //     }
    //     return this.exec("ack");
    // }

    private void redrawMyStructurePanes(){
        for (int i : this.idStructurePaneMap.keySet()){
            this.idStructurePaneMap.get(i).requestRedraw();
        }
    }

    public void start(){
        if (this.t == null){
            this.t =new Thread(this,this.toString());
            this.firstMessage = null;
            t.start();
        }
    }

    public void setFirstMessage(String firstMessage){
        this.firstMessage = firstMessage;
    }
    public String getFirstMessage(){
        return this.firstMessage;
    }

    public void addIdStructurePane(int id, StructurePane structurePane){
        this.idStructurePaneMap.put(id,structurePane);
    }

    public void setCountDownLatch(CountDownLatch latch){
        this.waitForPauseToBeHandled = latch;
    }



    public void run() {

        // System.out.println("exeqing, it has pane: " + this.pane);

        System.out.println("exec and state is: " + this.state);
        if (this.state == State.Null){
            return;// "error: should not being execing as process has not been inintialized";
        }
        
        System.out.println("140");
        String result = "";



        try{
            String firstMessage = this.getFirstMessage();
            System.out.println("execing " + firstMessage);

            String line;

            if (!(firstMessage == null || firstMessage.length() == 0)){
                //send ack
                out.println(firstMessage);

            }else{
                System.out.println("null first message");
            }
            // return "bla";
            this.setFirstMessage(null);



            
            System.out.println("191");
            while ((line = this.in.readLine()) != null){//while python has not yet terminated
                System.out.println("in while");
                // System.out.println("current count: " + this.waitForPauseToBeHandled.getCount());

                
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    this.state = State.InProgress;
                    String[] externalCommandSegments = line.split("#");

                    

                    System.out.println("current line: " + line);

                    if (this.pauseWasPressed){ //user input simulation
                        
                        this.waitForPauseToBeHandled.await();
                    }


                    if (externalCommandSegments[0].equals("pauseUntilSpacePressed")){
                        


                        System.out.println("paused");
                        boolean withRank;

                        Integer rank = this.extractRankFromPause(externalCommandSegments);
                        withRank = (rank != null);
                        if (!withRank){
                            rank = 0;
                        }
                        System.out.println("withrank: " + withRank + " rank: " + rank);
                        
                        if (rank < this.skipPausesWithRankGreaterThanOrEqualTo){
                            this.currentSkipValue = rank;

                            this.aPauseOccured(externalCommandSegments,withRank);

                            
                            this.redrawMyStructurePanes();

                            this.state = State.Paused;
                            
                            System.out.println("ok it's been a paused");

                            this.waitForPauseToBeHandled.await();
                            out.println("");
                            System.out.println("waited for ack, now it's done!");
                            continue;

                            // break;
                        }else{
                            out.println("skipped");
                            continue;
                        }
                    }else if (externalCommandSegments[0].equals("useCurrentGraph")){ //user input simulation
                        
                        // StructurePane thisPane = this.newGraphMethod.apply(externalCommandSegments[0],this);
                        // this.state = State.InProgress;
                        // // System.out.println("about to return my structure with id: " + this.pane.getStructure().getId());


                        // idGraphMap.put(thisPane.getStructure().getId(),thisPane.getStructure());
                        // idStructurePaneMap.put(thisPane.getStructure().getId(),thisPane);

                        // // currentCommand = new NewGraphCommand(externalCommandSegments);
                        // // Structure currentStructure = getStructureWithId(Integer.parseInt(externalCommandSegments[1]));
                        // // currentCommand.setStructure(currentStructure);

                        out.println(Integer.toString(this.structurePane.getStructure().getId()));

                        continue;

                    }else if ((line = PipingMessageHandler.properGraphNames(line)) != null){

                        StructurePane thisPane = this.newGraphMethod.apply(line,this);

                        idGraphMap.put(thisPane.getStructure().getId(),thisPane.getStructure());
                        idStructurePaneMap.put(thisPane.getStructure().getId(),thisPane);

                        this.state = State.InProgress;
                        // System.out.println("about to return my structure with id: " + this.pane.getStructure().getId());
                        out.println(thisPane.getStructure().getId());

                        continue;
                    }

                    CommandForGralogToExecute currentCommand;
                    try{
                        currentCommand = PipingMessageHandler.handleCommand(externalCommandSegments,this.getStructureWithId(Integer.parseInt(externalCommandSegments[1])));
                        // mostRecentlyUsedStructurePane = currentCommand.getStructurePane();
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
                for (int i: idStructurePaneMap.keySet()){
                    idStructurePaneMap.get(i).setPiping(null);
                }
            }else{
                System.out.println("line is not null rather: " + line);
            }


            System.out.println("reqing redraw");

            this.redrawMyStructurePanes();
            System.out.println("redr000");
            


        }catch (Exception e){
            e.printStackTrace();
            return;// "error: there was an error";
        }

        System.out.println("returning");
        
   
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


