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

public class PluginControlPanel extends ScrollPane implements PipingWindow {

    private static ProgressBar pb;
    private static Button pause,play,step,stop;
    // private Tabs tabs;
    // private Piping pipeline;
    private static List<Label> labels;
    private static VBox boilerPlateVbox;
    private static VBox varBox;
    private static CheckBox wrapped;
    private static HBox wrappedHolder;
    private static HBox pauseOrPlay;
    private static List<Control> labelsAndSeparators;

        
    public PluginControlPanel() {
        setMinWidth(100);
        setMinHeight(200);

        // this.pipeline = pipeline;
        // this.pipeline.subscribe(this);

        // this.tabs = tabs;
        
        this.boilerPlateVbox = new VBox();
        this.varBox = new VBox();
        HBox hbox = new HBox();
        pauseOrPlay = new HBox();
        this.wrappedHolder = new HBox();
        this.setFitToWidth(true);

        hbox.prefWidthProperty().bind(this.widthProperty());
        wrappedHolder.prefWidthProperty().bind(this.widthProperty());

        this.wrapped = new CheckBox("Wrap text");
        
        Runnable checkBoxClickHandler = new Runnable() {
            public void run() {
                if (!wrapped.isSelected()) {
                    varBox.getChildren().clear();


                    for (Label x : PluginControlPanel.this.labels) {
                        x.setMinWidth(Region.USE_PREF_SIZE);
                        x.setWrapText(false);
                        
                    }
                    sourceVarBox();
                }else {
                    
                    varBox.getChildren().clear();


                    for (Label x : PluginControlPanel.this.labels) {

                        x.setMinWidth(100);
                        x.setWrapText(true);
                    }

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

        stop = createButton("\u25A0");

        pb = new ProgressBar(0.3);


        this.labels = new ArrayList<Label>();
        this.labelsAndSeparators = new ArrayList<Control>();
        


        pb.prefWidthProperty().bind(boilerPlateVbox.widthProperty());
        pb.setPrefHeight(20);
        pb.setStyle("-fx-accent:green;");





        hbox.getChildren().addAll(pauseOrPlay,stop,step);
        pauseOrPlay.getChildren().addAll(pause);
        
        sourceVarBox();
        varBox.getChildren().addAll(labelsAndSeparators);
        boilerPlateVbox.getChildren().addAll(hbox,pb,wrapped,varBox);

        // this.foo();
        // vbox.setFitToHeight(true);

  


        this.setContent(boilerPlateVbox);
    }
    public void setOnPlay(Runnable onPlay) {
        play.setOnMouseClicked(event -> onPlay.run());
    }
    public void setOnPause(Runnable onPause) {
        pause.setOnMouseClicked(event -> onPause.run());
    }
    public void setOnStep(Runnable onStep) {
        step.setOnMouseClicked(event -> onStep.run());
    }

    public void setOnStop(Runnable onStop) {
        stop.setOnMouseClicked(event -> onStop.run());
    }

    public void setOnWrappedClicked(Runnable onWrappedClicked) {
        wrapped.setOnMouseClicked(event -> onWrappedClicked.run());
    }



   

    public static void sourceVarBox() {
        interpolateSeparators();
        varBox.getChildren().addAll(labelsAndSeparators);
    }





    // public void foo() {
    //     this.tabs.subscribe(this::notifyPauseRequested);

    // }


    public void setProgress(double progress) {
        if(pb != null) {
            pb.setProgress(progress);
        }
    }

    private Button createButton(String label) {
        Button b = new Button(label);

        b.prefWidthProperty().bind(widthProperty().divide(3));

        return b;
    }

    public static void interpolateSeparators() {
        if (labelsAndSeparators == null) {
            labelsAndSeparators = new ArrayList<Control>();
        }
        labelsAndSeparators.clear();
        int i;
        for (i = 0; i < labels.size()-1; i ++) {
            labelsAndSeparators.add(labels.get(i));
            labelsAndSeparators.add(new Separator());
        }

        try {
            labelsAndSeparators.add(labels.get(i));
        }catch(Exception e) {
        }

    }

    public static void notifyPlannedPauseRequested(List<String[]> args) {
        varBox.getChildren().clear();

        pauseOrPlay.getChildren().clear();
        pauseOrPlay.getChildren().add(play);
    

        labels.clear();
        for (int i = 0; i < args.size(); i ++) {
            String[] arg = args.get(i);
            String labelString = arg[0] + ": " + arg[1];
            Label inter = new Label(labelString);
            inter.setMinWidth(Region.USE_PREF_SIZE);
            labels.add(inter);

        }

        sourceVarBox();
        
    }

    public static void notifySpontaneousPauseRequested() {
        
        pauseOrPlay.getChildren().clear();
        pauseOrPlay.getChildren().add(play);
        
    }

    public static void notifyPlayRequested() {
        pauseOrPlay.getChildren().clear();
        pauseOrPlay.getChildren().add(pause);
    }


}
