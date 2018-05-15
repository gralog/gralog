package gralog.gralogfx.panels;

import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.gralogfx.Tabs;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.dockfx.DockNode;
import gralog.gralogfx.Piping;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;

public class PluginControlPanel extends ScrollPane implements PipingWindow{

    private ProgressBar pb;
    private Button pause,play,step;
    private Tabs tabs;
    private Piping pipeline;
    private List<Label> labels;
    private VBox vbox;

    public PluginControlPanel(Tabs tabs,Piping pipeline){
        setMinWidth(100);

        this.pipeline = pipeline;
        this.pipeline.subscribe(this);

        this.tabs = tabs;
        
        this.vbox = new VBox();
        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(this.widthProperty());
        vbox.prefWidthProperty().bind(this.widthProperty());
        

        play = createButton("\u25B6");


        step = createButton(">>");


        pause = createButton("||");

        pb = new ProgressBar(0f);

        

        labels = new ArrayList<Label>();

        Label label = new Label("foo");
        labels.add(label);
        labels.add(new Label("bar"));


        pb.prefWidthProperty().bind(vbox.widthProperty());
        pb.setPrefHeight(20);
        vbox.setPrefHeight(1000);


        hbox.getChildren().addAll(pause,play,step,pb);
        vbox.getChildren().addAll(hbox, pb);
        vbox.getChildren().addAll(labels);
        // this.foo();
        this.setPrefViewportHeight(1000);
        this.setContent(vbox);
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

    public void notifyPauseRequested(Structure structure, List<String[]> args){
        System.out.println("asuhhh" + args.get(0)[0] + " and has size: " + Integer.toString(args.size()));
        System.out.println("removing these labels: " + this.labels);
        System.out.println("where previously we were: " + this.vbox.getChildren());
        this.vbox.getChildren().removeAll(this.labels);
        System.out.println("but nowwww, well, now we are: " + this.vbox.getChildren());
        labels.clear();
        for (int i = 0; i < args.size(); i ++){
            String[] arg = args.get(i);
            this.labels.add(new Label(arg[0] + ": " + arg[1]));
            System.out.println("iter labels: " + this.labels);
        }
        System.out.println("labels: " + this.labels);
        this.tabs.requestRedraw();
        
        this.vbox.getChildren().addAll(this.labels);

        System.out.println("my children are: " + this.vbox.getChildren());
    }
    

}
