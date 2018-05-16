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
import gralog.gralogfx.Piping;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;

public class PluginControlPanel extends ScrollPane implements PipingWindow{

    private ProgressBar pb;
    private Button pause,play,step,n00b;
    private Tabs tabs;
    private Piping pipeline;
    private List<Label> labels;
    private VBox vbox;
    private CheckBox wrapped;
    private HBox wrappedHolder;


        
    


    public PluginControlPanel(Tabs tabs,Piping pipeline){
        setMinWidth(100);
        setMinHeight(200);

        this.pipeline = pipeline;
        this.pipeline.subscribe(this);

        this.tabs = tabs;
        
        this.vbox = new VBox();
        HBox hbox = new HBox();
        this.wrappedHolder = new HBox();

        hbox.prefWidthProperty().bind(this.widthProperty());
        wrappedHolder.prefWidthProperty().bind(this.widthProperty());

        this.wrapped = new CheckBox("Wrap text");
        

        Runnable borf = new Runnable(){
            public void run(){
                if (!wrapped.isSelected()){
                    System.out.println("is now unchecked");
                    // System.out.println("before he was " + vbox.getChildren().get(3).getWrapText());
                    vbox.getChildren().removeAll(labels);


                    for (Label x : labels){
                        x.setMinWidth(Region.USE_PREF_SIZE);
                        x.setWrapText(false);
                        
                        vbox.getChildren().add(x);
                    }
                    // System.out.println("after readding I've got: " + vbox.getChildren().get(3));
                }else{
                    System.out.println("is now checked");
                    
                    vbox.getChildren().removeAll(labels);
                    System.out.println("after removal I've got: " + vbox.getChildren());


                    for (Label x : labels){

                        x.setMinWidth(100);
                        x.setWrapText(true);
                        vbox.getChildren().add(x);
                    }

                    System.out.println("after readding I've got: " + vbox.getChildren());
                
                }

            }
        };

        
        this.setOnWrappedClicked(borf);


        vbox.prefWidthProperty().bind(this.widthProperty());
        

        play = createButton("\u25B6");


        step = createButton(">>");


        pause = createButton("||");

        n00b = createButton("hello");

        pb = new ProgressBar(0f);

        

        labels = new ArrayList<Label>();

        Label label = new Label("foo");

        labels.add(label);
        labels.add(new Label("bar"));

        for (Label x : labels){
            x.setWrapText(true);
        }



        pb.prefWidthProperty().bind(vbox.widthProperty());
        pb.setPrefHeight(20);





        hbox.getChildren().addAll(pause,play,step,pb);
        
        vbox.getChildren().addAll(hbox,pb,wrapped);
        vbox.getChildren().addAll(labels);

        // this.foo();
        // vbox.setFitToHeight(true);

  


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

    public void setOnWrappedClicked(Runnable onWrappedClicked){
        wrapped.setOnMouseClicked(event -> onWrappedClicked.run());
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
        this.vbox.getChildren().removeAll(this.labels);

        labels.clear();
        for (int i = 0; i < args.size(); i ++){
            String[] arg = args.get(i);
            Label inter = new Label(arg[0] + ": " + arg[1]);
            inter.setMinWidth(Region.USE_PREF_SIZE);
            this.labels.add(inter);

        }

        this.tabs.requestRedraw();
        
        this.vbox.getChildren().addAll(this.labels);


    }
    

}
