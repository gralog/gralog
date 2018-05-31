package gralog.gralogfx.panels;

import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.gralogfx.Tabs;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.dockfx.DockNode;
import gralog.gralogfx.piping.Piping;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;

public class PluginControlPanel extends ScrollPane implements PipingWindow{

    private ProgressBar pb;
    private Button pause,play,step,n00b;
    private Tabs tabs;
    private Piping pipeline;
    private List<Label> labels;
    private VBox boilerPlateVbox;
    private VBox varBox;
    private CheckBox wrapped;
    private HBox wrappedHolder;
    private List<Control> labelsAndSeparators;

        
    


    public PluginControlPanel(Tabs tabs,Piping pipeline){
        setMinWidth(100);
        setMinHeight(200);

        this.pipeline = pipeline;
        this.pipeline.subscribe(this);

        this.tabs = tabs;
        
        this.boilerPlateVbox = new VBox();
        this.varBox = new VBox();
        HBox hbox = new HBox();
        this.wrappedHolder = new HBox();
        this.setFitToWidth(true);

        hbox.prefWidthProperty().bind(this.widthProperty());
        wrappedHolder.prefWidthProperty().bind(this.widthProperty());

        this.wrapped = new CheckBox("Wrap text");
        
        Runnable checkBoxClickHandler = new Runnable(){
            public void run(){
                if (!wrapped.isSelected()){
                    System.out.println("is now unchecked");
                    // System.out.println("before he was " + vbox.getChildren().get(3).getWrapText());
                    varBox.getChildren().clear();


                    for (Label x : PluginControlPanel.this.labels){
                        x.setMinWidth(Region.USE_PREF_SIZE);
                        x.setWrapText(false);
                        
                    }
                    sourceVarBox();
                    // System.out.println("after readding I've got: " + vbox.getChildren().get(3));
                }else{
                    System.out.println("is now checked");
                    
                    varBox.getChildren().clear();
                    System.out.println("after removal I've got: " + varBox.getChildren());


                    for (Label x : PluginControlPanel.this.labels){

                        x.setMinWidth(100);
                        x.setWrapText(true);
                    }

                    System.out.println("after readding I've got: " + varBox.getChildren());
                    sourceVarBox();
                }

            }
        };

        
        this.setOnWrappedClicked(checkBoxClickHandler);


        varBox.prefWidthProperty().bind(this.widthProperty());
        boilerPlateVbox.prefWidthProperty().bind(this.widthProperty());
        

        play = createButton("\u25B6");


        step = createButton(">>");


        pause = createButton("||");

        n00b = createButton("hello");

        pb = new ProgressBar(0.3);

        

        this.labels = new ArrayList<Label>();
        this.labelsAndSeparators = new ArrayList<Control>();
        


        pb.prefWidthProperty().bind(boilerPlateVbox.widthProperty());
        pb.setPrefHeight(20);
        pb.setStyle("-fx-accent:green;");





        hbox.getChildren().addAll(pause,play,step);
        
        sourceVarBox();
        varBox.getChildren().addAll(labelsAndSeparators);
        boilerPlateVbox.getChildren().addAll(hbox,pb,wrapped,varBox);

        // this.foo();
        // vbox.setFitToHeight(true);

  


        this.setContent(boilerPlateVbox);
    }
    public void setOnPlay(Runnable onPlay){
        play.setOnMouseClicked(event -> onPlay.run());
    }
    public void setOnPause(Runnable onPause){
        pause.setOnMouseClicked(event -> onPause.run());
    }
    public void setOnStep(Runnable onStep){
        step.setOnMouseClicked(event -> onStep.run());
    }

    public void setOnWrappedClicked(Runnable onWrappedClicked){
        wrapped.setOnMouseClicked(event -> onWrappedClicked.run());
    }

    public void sourceVarBox(){
        interpolateSeparators();
        this.varBox.getChildren().addAll(this.labelsAndSeparators);
    }

    



    // public void foo(){
    //     this.tabs.subscribe(this::notifyPauseRequested);

    // }


    public void setProgress(double progress){
        if(pb != null){
            pb.setProgress(progress);
        }
    }

    private Button createButton(String label){
        Button b = new Button(label);

        b.prefWidthProperty().bind(widthProperty().divide(3));

        return b;
    }

    public void interpolateSeparators(){
        if (this.labelsAndSeparators == null){
            this.labelsAndSeparators = new ArrayList<Control>();
        }
        this.labelsAndSeparators.clear();
        int i;
        for (i = 0; i < this.labels.size()-1; i ++){
            this.labelsAndSeparators.add(this.labels.get(i));
            this.labelsAndSeparators.add(new Separator());
        }

        try{
            this.labelsAndSeparators.add(this.labels.get(i));
        }catch(Exception e){
            System.out.println("buppo");
        }

    }

    public void notifyPauseRequested(Structure structure, List<String[]> args){
        this.varBox.getChildren().clear();

        labels.clear();
        for (int i = 0; i < args.size(); i ++){
            String[] arg = args.get(i);
            Label inter = new Label(arg[0] + ": " + arg[1]);
            inter.setMinWidth(Region.USE_PREF_SIZE);
            this.labels.add(inter);

        }

        sourceVarBox();

        this.tabs.requestRedraw();
        
    }


}
