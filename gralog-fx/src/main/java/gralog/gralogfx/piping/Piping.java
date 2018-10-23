
/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
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
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
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

    public enum MessageToConsoleFlag{
        Normal,
        Error,
        GPrint,
        Request,
        Notification,
        Warning
    }

    private Process external;
    private BufferedReader in;
    private PrintStream out;
    private BufferedReader processErrors;
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
    public CountDownLatch waitForSelection;
    private Lock pauseLock;
    public Condition canContinue;
    public MainWindow caller;
    public Supplier<Boolean> pauseFunction;
    public Supplier<Boolean> graphObjectSelectionFunction;
    public Supplier<String> receiveUserMessageFromConsole;
    private boolean pauseWasPressed = false;
    public boolean windowDoesCloseNow = false;
    protected BiConsumer sendMessageToConsole;
    private Class classSelectionIsWaitingFor = null;
    private IMovable selectedObject;
    private String consoleInputText;

    int nextStructurePaneId = 0;


    public enum State{
        Null,
        Inintialized,
        InProgress,
        Paused,
        WaitingForSelection,
        WaitingForConsoleInput
    }
    
    public State state = State.Null;

    private InjectiveMap<Integer,Structure> idGraphMap =  new InjectiveMap<Integer,Structure>();
    private InjectiveMap<Integer,StructurePane> idStructurePaneMap =  new InjectiveMap<Integer,StructurePane>();

    public void setClassSelectionIsWaitingFor(Class c){
        this.classSelectionIsWaitingFor = c;
    }


    public void subscribe(PipingWindow sub){
        this.subscribers.add(sub);
    }

    private void plannedPauseRequested(String[] externalCommandSegments, boolean rankGiven){
        this.sendMessageToConsole.accept("Pause requested!",MessageToConsoleFlag.Notification);
        List<String[]> args = PipingMessageHandler.parsePauseVars(externalCommandSegments,rankGiven);
        trackedVarArgs = args;
        this.pauseFunction.get();
        // subscribers.forEach(sub -> sub.notifyPauseRequested(this.structure,args));
    }

    public boolean isInitialized(){
        return (this.state != State.Null);
    }

    public Piping(BiFunction<String,Piping,StructurePane> newGraphMethod,StructurePane structurePane,CountDownLatch waitForPauseToBeHandled,Supplier<Boolean> pauseFunction,CountDownLatch waitForSelection,Supplier<Boolean> graphObjectSelectionFunction,BiConsumer<String,MessageToConsoleFlag> sendMessageToConsole){
        this.newGraphMethod = newGraphMethod;
        this.resetInitialPipingSkipVals();
        this.waitForPauseToBeHandled = waitForPauseToBeHandled;
        this.waitForSelection = waitForSelection;
        this.pauseFunction = pauseFunction;
        this.graphObjectSelectionFunction = graphObjectSelectionFunction;
        this.sendMessageToConsole = sendMessageToConsole;
        this.receiveUserMessageFromConsole = receiveUserMessageFromConsole;
        //check if structurePane is really necessary...
    }

    
    public String getNextLine() throws Exception{
        try{
            return this.in.readLine();
        }catch(Exception e){
            return null;
        }
    }
    // private PipingMessageHandler eventHandler = 

   
    public boolean externalProcessInit(String fileName,String initMessage){


        
            
        String line;
        String[] execStr = {fileName,initMessage};
        CountDownLatch execd = new CountDownLatch(1);
        
        try{
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","chmod u+x " + fileName});

            execd.countDown();
        }catch(Exception e){
            this.sendMessageToConsole.accept("The file was unable to be granted permission to be run",MessageToConsoleFlag.Error);
            execd.countDown();
        }
        try{
            execd.await();            
        }catch(Exception e){}

        try{
            this.external = Runtime.getRuntime().exec(execStr); //e.g. formatRequest
            this.in = new BufferedReader(new InputStreamReader(external.getInputStream()));
            this.processErrors = new BufferedReader(new InputStreamReader(this.external.getErrorStream()));
            this.out = new PrintStream(external.getOutputStream(),true);
        }catch(IOException e){
            // e.printStackTrace();

            this.sendMessageToConsole.accept("The file was unable to be run. Perhaps it needs to be given permission?",MessageToConsoleFlag.Error);
            return false;
        }
        this.state=State.Inintialized;

        return true;

    
    }

    public IMovable getSelectedObject(){
        return this.selectedObject;
    }

    public Class getClassSelectionIsWaitingFor(){
        return this.classSelectionIsWaitingFor;
    }

    public void profferSelectedObject(IMovable obj){
        
        this.selectedObject = obj;
        if (this.classSelectionIsWaitingFor != null && this.selectedObject.getClass() == this.classSelectionIsWaitingFor){
            this.waitForSelection.countDown();
            CountDownLatch newLatch = new CountDownLatch(1);
            this.setSelectionCountDownLatch(newLatch);

        }

        
    }

    public Boolean profferConsoleInput(String text){
        this.consoleInputText = text;
        Class c = this.classSelectionIsWaitingFor;
        if (c == String.class){
            this.waitForSelection.countDown();
            CountDownLatch newLatch = new CountDownLatch(1);
            this.setSelectionCountDownLatch(newLatch);
            return true;
        }else if (c == Double.class){
            try{
                Double.parseDouble(text); //if this doesn't fail the user has entered
                //a valid double, and the gralog command should proceed
                this.waitForSelection.countDown();
                CountDownLatch newLatch = new CountDownLatch(1);
                this.setSelectionCountDownLatch(newLatch);
                return true;
            }catch(Exception e){
                this.sendMessageToConsole.accept("Wrong class type - not a valid double!",MessageToConsoleFlag.Error);
            }
        }else if (c == Integer.class){
            try{
                Integer.parseInt(text); //if this doesn't fail the user has entered
                //a valid double, and the gralog command should proceed
                this.waitForSelection.countDown();
                CountDownLatch newLatch = new CountDownLatch(1);
                this.setSelectionCountDownLatch(newLatch);
                return true;
            }catch(Exception e){
                this.sendMessageToConsole.accept("Wrong class type - not a valid integer!",MessageToConsoleFlag.Error);
            }
        }else{
            return false;
        }
        return false;
    }

    public String getConsoleInput(){
        return this.consoleInputText;
    }

    public Structure getStructureWithId(int id){
        return this.idGraphMap.get(id);
    }
    public StructurePane getStructurePaneWithId(int id){
        return this.idStructurePaneMap.get(id);
    }

    public void pairLocalIdAndStructure(int id,Structure structure){
        this.idGraphMap.set(id,structure);
    }
    public void pairLocalIdAndStructurePane(int id,StructurePane structurePane){
        this.idStructurePaneMap.set(id,structurePane);
    }

    public int getIdWithStructure(Structure structure){
        return this.idGraphMap.getId(structure);
    }

    public int getIdWithStructurePane(StructurePane structurePane){
        return this.idStructurePaneMap.getId(structurePane);
    }

    
    private void resetInitialPipingSkipVals(){
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
    //     this.resetInitialPipingSkipVals();
    //     // newGraphMethod.apply(("hello world".split(" ")));

    //     System.out.println("starting exec");
    //     String exitMessage = this.exec(null);
        
    //     System.out.println("exec finished");
    //     return;
        
    // }

    public void killSelf(){
        this.windowDoesCloseNow = true;
        this.waitForPauseToBeHandled.countDown();
    }
   
    public Integer extractRankFromPause(String[] externalCommandSegments){
        // System.out.println("where the rank would be : " + externalCommandSegments[1]);
        try{
            Integer rank = Integer.parseInt(externalCommandSegments[1]);
            return rank;
        }catch(Exception e){
            return (Integer)null;
        }
    }

    public void pausePressed(){
        if (!(this.state == State.Paused)){
            this.pauseWasPressed = true;
        }
    }

    public void spontaneousStop(){
        this.external.destroyForcibly();
        this.windowDoesCloseNow = true;
        this.waitForPauseToBeHandled.countDown();
        this.makeNull();
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

    protected void redrawMyStructurePanes(){
        for (StructurePane sp : this.idStructurePaneMap.valueSet()){
            sp.requestRedraw();
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

    

    public void setPauseCountDownLatch(CountDownLatch latch){
        this.waitForPauseToBeHandled = latch;
    }
    public void setSelectionCountDownLatch(CountDownLatch latch){
        this.waitForSelection = latch;
    }

    public State getPipingState(){
        return this.state;
    }



    public void run() {

        if (this.state == State.Null){
            return;// "error: should not being execing as process has not been inintialized";
        }
        
        String result = "";



        try{
            String firstMessage = this.getFirstMessage();
            this.sendMessageToConsole.accept("Running external program",MessageToConsoleFlag.Notification);

            String line;

            if (!(firstMessage == null || firstMessage.length() == 0)){
                //send ack
                out.println(firstMessage);

            }
            // return "bla";
            this.setFirstMessage(null);



            
            while ((line = this.getNextLine()) != null){//while python has not yet terminated
                // System.out.println("current count: " + this.waitForPauseToBeHandled.getCount());
                if (this.windowDoesCloseNow){
                    return;
                }
                
                if (line.length() > 0){ // if not a bogus line
                    //handleLine()
                    this.state = State.InProgress;
                    String[] externalCommandSegments = line.split("#");
                    


                    if (this.pauseWasPressed){ //user input simulation
                        this.redrawMyStructurePanes();
                        this.state = State.Paused;
                        this.waitForPauseToBeHandled.await();
                        if (this.windowDoesCloseNow){
                            return;
                        }
                        this.pauseWasPressed = false;
                        this.state = State.InProgress;
                    }


                    

                    if (externalCommandSegments[0].equals("pauseUntilSpacePressed")){
                        


                        boolean withRank;

                        Integer rank = this.extractRankFromPause(externalCommandSegments);
                        withRank = (rank != null);
                        if (!withRank){
                            rank = 0;
                        }
                        
                        if (rank < this.skipPausesWithRankGreaterThanOrEqualTo){
                            this.currentSkipValue = rank;

                            this.plannedPauseRequested(externalCommandSegments,withRank);

                            
                            this.redrawMyStructurePanes();

                            this.state = State.Paused;
                            

                            this.waitForPauseToBeHandled.await();
                            if (this.windowDoesCloseNow){
                                return;
                            }
                            out.println("");
                            this.state = State.InProgress;
                            continue;

                            // break;
                        }else{
                            out.println("skipped");
                            continue;
                        }
                    } else if (externalCommandSegments[0].equals("message")) {
                    	this.sendMessageToConsole.accept(externalCommandSegments[2],MessageToConsoleFlag.Notification);
                    	continue;
                	}else if (externalCommandSegments[0].equals("useCurrentGraph")){ //user input simulation
                        
                        final String lineFinal = externalCommandSegments[0];
                        CountDownLatch graphWasInstantiated = new CountDownLatch(1);
                        Platform.runLater(
                            ()->{
                                StructurePane thisPane = this.newGraphMethod.apply(lineFinal,this);
                                this.pairLocalIdAndStructure(this.nextStructurePaneId,thisPane.getStructure());
                                this.pairLocalIdAndStructurePane(this.nextStructurePaneId,thisPane);

                                this.state = State.InProgress;
                                // System.out.println("about to return my structure with id: " + this.pane.getStructure().getId());
                                out.println(this.nextStructurePaneId);
                                this.nextStructurePaneId += 1;
                                graphWasInstantiated.countDown();
                            }
                        );
                        graphWasInstantiated.await();

                        

                        continue;

                    }else if ((line = PipingMessageHandler.properGraphNames(line)) != null){
                        final String lineFinal = line;
                        CountDownLatch graphWasInstantiated = new CountDownLatch(1);
                        Platform.runLater(
                            ()->{
                                StructurePane thisPane = this.newGraphMethod.apply(lineFinal,this);
                                this.pairLocalIdAndStructure(this.nextStructurePaneId,thisPane.getStructure());
                                this.pairLocalIdAndStructurePane(this.nextStructurePaneId,thisPane);
                                this.state = State.InProgress;
                                out.println(this.nextStructurePaneId);
                                this.nextStructurePaneId += 1;
                                graphWasInstantiated.countDown();
                            }
                        );
                        graphWasInstantiated.await();

                        continue;
                        

                    }

                    CommandForGralogToExecute currentCommand;
                    try{
                        // currentCommand = PipingMessageHandler.handleCommand(externalCommandSegments,this.getStructureWithId(Integer.parseInt(externalCommandSegments[1])),this);
                        int localStructureIndex = Integer.parseInt(PipingMessageHandler.extractNthPositionString(externalCommandSegments,1));
                        Structure structureOfCurrentCommand = this.getStructureWithId(localStructureIndex);
                        currentCommand = PipingMessageHandler.handleCommand(externalCommandSegments,structureOfCurrentCommand,this);
                        // mostRecentlyUsedStructurePane = currentCommand.getStructurePane();
                    }catch(Exception e){
                        e.printStackTrace();
                        currentCommand = new NotRecognizedCommand(externalCommandSegments,(Structure)null);
                    }
                    
                    if (!currentCommand.didFail()){
                        currentCommand.handle();
                        String response;
                        if (! currentCommand.didFail() && (response = currentCommand.getResponse()) != null){
                            this.out.println(response);
                        }
                    }
                    String msg;
                    if (!((msg = currentCommand.getConsoleMessage()) == null)){
                        this.sendMessageToConsole.accept(msg,MessageToConsoleFlag.Warning);
                    }



                    if (currentCommand.didFail()){
                        final String lineFinal = PipingMessageHandler.rejoinExternalCommandSegments(externalCommandSegments);
                        Exception e = currentCommand.getError();
                        this.sendMessageToConsole.accept(e.toString(),MessageToConsoleFlag.Error);

                        Platform.runLater(
                            () -> {
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("\"" + lineFinal + "\" is invalid:");
                                alert.setHeaderText(null);
                                alert.setContentText(e.toString());
                                alert.showAndWait();
                            }
                        ); 

                        if (currentCommand.getResponse() != null){
                            this.out.println(currentCommand.getResponse());
                        }
                    
                        // this.out.println(currentCommand.getError().toString());
                        this.state = State.Null;
                        this.redrawMyStructurePanes();
                        return;
                    }
                    
                    result = result + line;

                }

            }

            if (line == null){
                this.makeNull();
            }



            this.redrawMyStructurePanes();
            this.sendMessageToConsole.accept("External program terminated",MessageToConsoleFlag.Notification);
            


        }catch (Exception e){
            e.printStackTrace();
            return;// "error: there was an error";
        }

        
   
    }

    public void makeNull(){
        this.state = State.Null;
        System.out.println("makingue null");
        for (int i: idStructurePaneMap.keySet()){
            this.getStructurePaneWithId(i).setPiping(null);
        }
        String line;
        String wholeError = "";
        try{
            while (this.processErrors.ready() && ((line = this.processErrors.readLine())!= null)){
                wholeError += line + "\n";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            this.processErrors.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if (wholeError.length() > 0){
            wholeError = wholeError.substring(0,wholeError.length()-1);
        }

        if (!wholeError.equals("")){
            this.sendMessageToConsole.accept("Error in external Program: \n" + wholeError,MessageToConsoleFlag.Error);
        }
        System.out.println("trying to redraw");
        this.redrawMyStructurePanes();
        return;
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


